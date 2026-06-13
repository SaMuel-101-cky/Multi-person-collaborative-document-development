package com.example.db_document.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserContextTest {

    @Test
    void testSetAndGetUserId() {
        Long testUserId = 1L;
        UserContext.setUserId(testUserId);

        Long retrievedUserId = UserContext.getUserId();
        assertEquals(testUserId, retrievedUserId);
    }

    @Test
    void testRemove() {
        UserContext.setUserId(1L);
        UserContext.remove();

        Long userId = UserContext.getUserId();
        assertNull(userId);
    }

    @Test
    void testThreadLocalIsolation() throws InterruptedException {
        UserContext.setUserId(1L);

        Thread thread = new Thread(() -> {
            UserContext.setUserId(2L);
            assertEquals(2L, UserContext.getUserId());
        });

        thread.start();
        thread.join();

        Long mainThreadUserId = UserContext.getUserId();
        assertEquals(1L, mainThreadUserId);
    }

    @Test
    void testMultipleSetOperations() {
        UserContext.setUserId(1L);
        assertEquals(1L, UserContext.getUserId());

        UserContext.setUserId(2L);
        assertEquals(2L, UserContext.getUserId());

        UserContext.setUserId(3L);
        assertEquals(3L, UserContext.getUserId());

        UserContext.remove();
        assertNull(UserContext.getUserId());
    }

    @Test
    void testInitialState() {
        UserContext.remove(); // Ensure clean state
        Long userId = UserContext.getUserId();
        assertNull(userId);
    }
}
