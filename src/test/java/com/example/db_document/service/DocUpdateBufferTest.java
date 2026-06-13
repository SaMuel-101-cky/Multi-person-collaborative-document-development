package com.example.db_document.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocUpdateBufferTest {

    private DocUpdateService docUpdateService;
    private DocUpdateBuffer buffer;

    @BeforeEach
    void setUp() {
        docUpdateService = mock(DocUpdateService.class);
        buffer = new DocUpdateBuffer();
        ReflectionTestUtils.setField(buffer, "docUpdateService", docUpdateService);
    }

    @AfterEach
    void tearDown() {
        buffer.shutdown();
    }

    @Test
    void enqueueUpdate_flushesImmediatelyWhenMaxBatchSizeReached() throws Exception {
        ReflectionTestUtils.setField(buffer, "maxBatchSize", 1);
        ReflectionTestUtils.setField(buffer, "flushIntervalMs", 60000L);

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(docUpdateService).createDocUpdatesBatch(any());

        buffer.enqueueUpdate(1L, new byte[]{1, 2});

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        verify(docUpdateService).createDocUpdatesBatch(any());
    }

    @Test
    void snapshotPendingPayloads_returnsPendingPayloads() {
        ReflectionTestUtils.setField(buffer, "maxBatchSize", 10);
        ReflectionTestUtils.setField(buffer, "flushIntervalMs", 60000L);

        buffer.enqueueUpdate(2L, new byte[]{9});
        List<byte[]> payloads = buffer.snapshotPendingPayloads(2L);

        assertEquals(1, payloads.size());
        assertArrayEquals(new byte[]{9}, payloads.get(0));
    }

    @Test
    void enqueueUpdate_ignoresNullInputs() {
        assertDoesNotThrow(() -> buffer.enqueueUpdate(null, new byte[]{1}));
        assertDoesNotThrow(() -> buffer.enqueueUpdate(1L, null));
    }
}

