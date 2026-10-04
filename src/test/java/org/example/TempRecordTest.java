package org.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TempRecordTest {
    @Test
    void storesNewRecordValues() {
        TempRecord record = new TempRecord(12.5, 54.5, 1, 2);

        assertEquals(12.5, record.getInputValue());
        assertEquals(54.5, record.getOutputValue());
        assertEquals(1, record.getFromUnitId());
        assertEquals(2, record.getToUnitId());
    }

    @Test
    void storesPersistedRecordMetadata() {
        LocalDateTime createdAt = LocalDateTime.now();
        TempRecord record = new TempRecord(4, 1, 2, 1, 3, createdAt);

        assertEquals(4, record.getId());
        assertEquals(createdAt, record.getCreatedAt());
    }
}
