package com.example.db_document.service;

import com.example.db_document.model.dto.DocUpdateCreateRequest;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class DocUpdateBuffer {                //这个类并发写得很厉害
    private static final class BufferState {                     //内部类
        private final Object lock = new Object();          //锁
        private final List<DocUpdateCreateRequest> pending = new ArrayList<>();          //刚收到的，还没来得及写入库的更新
        private final List<DocUpdateCreateRequest> inFlight = new ArrayList<>();
        private ScheduledFuture<?> scheduledFlush;
    }

    private final Map<Long, BufferState> buffers = new ConcurrentHashMap<>();               //根据文档id，对每个文档的缓冲池进行隔离
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);    //单线程的定时任务线程池，负责落库

    @Value("${doc-update.flush-interval-ms:500}")
    private long flushIntervalMs;                      //时间间隔

    @Value("${doc-update.max-batch-size:10}")
    private int maxBatchSize;                             //最大批次数量

    @Autowired
    private DocUpdateService docUpdateService;

    public void enqueueUpdate(Long documentId, byte[] payload) {        //收到一个更新请求，先放到内存里，等积累到一定数量或者过了一段时间再批量落库
        if (documentId == null || payload == null) {
            return;
        }

        DocUpdateCreateRequest req = new DocUpdateCreateRequest();
        req.setDocumentId(documentId);
        req.setVectorClock(String.valueOf(System.currentTimeMillis()));
        req.setUpdateData(payload);
        req.setIsSnapshot(false);
        req.setParentUpdateId(null);

        BufferState state = buffers.computeIfAbsent(documentId, k -> new BufferState());
        List<DocUpdateCreateRequest> toFlush = null;
        synchronized (state.lock) {       //异步写入
            state.pending.add(req);
            if (state.pending.size() >= maxBatchSize) {       //超过10个，写入
                toFlush = drainPendingLocked(state);
                cancelScheduledFlushLocked(state);
            } else if (state.scheduledFlush == null || state.scheduledFlush.isDone()) {
                state.scheduledFlush = executor.schedule(() -> flushDocument(documentId), flushIntervalMs, TimeUnit.MILLISECONDS);
            }
        }

        if (toFlush != null) {
            List<DocUpdateCreateRequest> finalToFlush = toFlush;
            executor.execute(() -> flushRequests(documentId, finalToFlush));
        }
    }

    public List<byte[]> snapshotPendingPayloads(Long documentId) {        //获取暂未落库的更新数据
        if (documentId == null) {
            return List.of();
        }
        BufferState state = buffers.get(documentId);
        if (state == null) {
            return List.of();
        }

        synchronized (state.lock) {
            if (state.pending.isEmpty() && state.inFlight.isEmpty()) {
                return List.of();
            }
            List<byte[]> payloads = new ArrayList<>(state.pending.size() + state.inFlight.size());
            for (DocUpdateCreateRequest req : state.pending) {
                payloads.add(req.getUpdateData());
            }
            for (DocUpdateCreateRequest req : state.inFlight) {
                payloads.add(req.getUpdateData());
            }
            return payloads;
        }
    }

    public void flushAsync(Long documentId) {
        if (documentId == null) {
            return;
        }
        executor.execute(() -> flushDocument(documentId));
    }

    private void flushDocument(Long documentId) {
        BufferState state = buffers.get(documentId);
        if (state == null) {
            return;
        }

        List<DocUpdateCreateRequest> toFlush;
        synchronized (state.lock) {
            toFlush = drainPendingLocked(state);
            state.scheduledFlush = null;
        }

        if (toFlush.isEmpty()) {
            return;
        }
        flushRequests(documentId, toFlush);
    }

    private List<DocUpdateCreateRequest> drainPendingLocked(BufferState state) {
        if (state.pending.isEmpty()) {
            return List.of();
        }
        state.inFlight.clear();
        state.inFlight.addAll(state.pending);
        List<DocUpdateCreateRequest> drained = new ArrayList<>(state.pending);
        state.pending.clear();
        return drained;
    }

    private void cancelScheduledFlushLocked(BufferState state) {
        if (state.scheduledFlush != null && !state.scheduledFlush.isDone()) {
            state.scheduledFlush.cancel(false);
        }
        state.scheduledFlush = null;
    }

    private void flushRequests(Long documentId, List<DocUpdateCreateRequest> reqs) {
        try {
            docUpdateService.createDocUpdatesBatch(reqs);             //批量落库
            BufferState state = buffers.get(documentId);
            if (state != null) {
                synchronized (state.lock) {
                    state.inFlight.clear();
                }
            }
        } catch (Exception e) {
            BufferState state = buffers.get(documentId);
            if (state != null) {
                synchronized (state.lock) {
                    List<DocUpdateCreateRequest> retry = new ArrayList<>(reqs.size() + state.pending.size());
                    retry.addAll(reqs);
                    retry.addAll(state.pending);
                    state.pending.clear();
                    state.pending.addAll(retry);
                    if (state.scheduledFlush == null || state.scheduledFlush.isDone()) {
                        state.scheduledFlush = executor.schedule(() -> flushDocument(documentId), flushIntervalMs, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        for (Long documentId : buffers.keySet()) {
            try {
                flushDocument(documentId);
            } catch (Exception e) {
            }
        }
        executor.shutdown();
    }
}
