package com.example.db_document.handler;

import com.example.db_document.pojo.DocUpdate;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.service.DocUpdateBuffer;
import com.example.db_document.service.DocUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentSocketHandlerTest {

    private DocUpdateService docUpdateService;
    private DocUpdateBuffer docUpdateBuffer;
    private DocumentSocketHandler handler;

    @BeforeEach
    void setUp() {
        docUpdateService = mock(DocUpdateService.class);
        docUpdateBuffer = mock(DocUpdateBuffer.class);
        handler = new DocumentSocketHandler();

        ReflectionTestUtils.setField(handler, "docUpdateService", docUpdateService);
        ReflectionTestUtils.setField(handler, "docUpdateBuffer", docUpdateBuffer);

        Object userSessions = ReflectionTestUtils.getField(DocumentSocketHandler.class, "userSessions");
        if (userSessions instanceof Map<?, ?> map) {
            map.clear();
        }
    }

    @Test
    void handleBinaryMessage_syncStep1_sendsHistoryOnce() throws Exception {
        long docId = 1L;

        WebSocketSession session = mock(WebSocketSession.class);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("docId", docId);
        attrs.put("role", PermissionType.EDITOR);
        when(session.getAttributes()).thenReturn(attrs);
        when(session.isOpen()).thenReturn(true);

        DocUpdate u1 = new DocUpdate();
        u1.setUpdateData(new byte[]{9});
        DocUpdate u2 = new DocUpdate();
        u2.setUpdateData(new byte[]{10});
        when(docUpdateService.listByDocumentId(docId)).thenReturn(List.of(u1, u2));
        when(docUpdateBuffer.snapshotPendingPayloads(docId)).thenReturn(List.of(new byte[]{11}));

        handler.handleBinaryMessage(session, new BinaryMessage(ByteBuffer.wrap(new byte[]{0, 0})));

        assertEquals(true, attrs.get("historySent"));

        ArgumentCaptor<BinaryMessage> msgCaptor = ArgumentCaptor.forClass(BinaryMessage.class);
        verify(session, times(3)).sendMessage(msgCaptor.capture());
        List<BinaryMessage> sent = msgCaptor.getAllValues();
        assertArrayEquals(new byte[]{9}, sent.get(0).getPayload().array());
        assertArrayEquals(new byte[]{10}, sent.get(1).getPayload().array());
        assertArrayEquals(new byte[]{11}, sent.get(2).getPayload().array());
        verify(docUpdateBuffer, never()).enqueueUpdate(anyLong(), any());
    }

    @Test
    void handleBinaryMessage_viewerIgnoresNonAwarenessMessages() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("docId", 1L);
        attrs.put("role", PermissionType.VIEWER);
        when(session.getAttributes()).thenReturn(attrs);

        handler.handleBinaryMessage(session, new BinaryMessage(ByteBuffer.wrap(new byte[]{0, 2, 1, 2})));

        verifyNoInteractions(docUpdateService);
        verifyNoInteractions(docUpdateBuffer);
        verify(session, never()).sendMessage(any());
    }

    @Test
    void handleBinaryMessage_awarenessIsBroadcastAndStored() throws Exception {
        long docId = 2L;

        WebSocketSession sender = mock(WebSocketSession.class);
        when(sender.getId()).thenReturn("s1");
        when(sender.isOpen()).thenReturn(true);
        Map<String, Object> senderAttrs = new HashMap<>();
        senderAttrs.put("docId", docId);
        senderAttrs.put("role", PermissionType.EDITOR);
        when(sender.getAttributes()).thenReturn(senderAttrs);

        WebSocketSession other = mock(WebSocketSession.class);
        when(other.getId()).thenReturn("s2");
        when(other.isOpen()).thenReturn(true);
        Map<String, Object> otherAttrs = new HashMap<>();
        otherAttrs.put("docId", docId);
        otherAttrs.put("role", PermissionType.EDITOR);
        when(other.getAttributes()).thenReturn(otherAttrs);

        Object documentSessions = ReflectionTestUtils.getField(handler, "documentSessions");
        assertTrue(documentSessions instanceof Map<?, ?>);
        @SuppressWarnings("unchecked")
        Map<Long, Set<WebSocketSession>> docSessionsMap = (Map<Long, Set<WebSocketSession>>) documentSessions;
        docSessionsMap.put(docId, new CopyOnWriteArraySet<>(List.of(sender, other)));

        byte[] payload = new byte[]{1, 7, 8};
        handler.handleBinaryMessage(sender, new BinaryMessage(ByteBuffer.wrap(payload)));

        verify(other).sendMessage(any(BinaryMessage.class));

        Object latestAwarenessByDoc = ReflectionTestUtils.getField(handler, "latestAwarenessByDoc");
        assertTrue(latestAwarenessByDoc instanceof Map<?, ?>);
        @SuppressWarnings("unchecked")
        Map<Long, Map<String, byte[]>> awareness = (Map<Long, Map<String, byte[]>>) latestAwarenessByDoc;
        assertArrayEquals(payload, awareness.get(docId).get("s1"));
    }

    @Test
    void afterConnectionClosed_flushesBufferWhenRoomBecomesEmpty() throws Exception {
        long docId = 3L;
        long userId = 4L;

        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("s1");
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("docId", docId);
        attrs.put("userId", userId);
        when(session.getAttributes()).thenReturn(attrs);

        @SuppressWarnings("unchecked")
        Map<Long, Set<WebSocketSession>> documentSessions =
                (Map<Long, Set<WebSocketSession>>) ReflectionTestUtils.getField(handler, "documentSessions");
        documentSessions.put(docId, new CopyOnWriteArraySet<>(List.of(session)));

        @SuppressWarnings("unchecked")
        Map<Long, Set<WebSocketSession>> userSessions =
                (Map<Long, Set<WebSocketSession>>) ReflectionTestUtils.getField(DocumentSocketHandler.class, "userSessions");
        userSessions.put(userId, new CopyOnWriteArraySet<>(List.of(session)));

        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(docUpdateBuffer).flushAsync(docId);
        assertFalse(documentSessions.containsKey(docId));
        assertFalse(userSessions.containsKey(userId));
    }
}

