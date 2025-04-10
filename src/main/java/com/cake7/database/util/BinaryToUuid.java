package com.cake7.database.util;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

@Component
public class BinaryToUuid {

    public UUID convertBytesToUuid(byte[] bytes) {
        if (bytes == null || bytes.length != 16) {
            throw new IllegalArgumentException("UUID 바이트 배열은 16바이트여야 합니다.");
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long high = buffer.getLong();  // 앞의 8바이트
        long low = buffer.getLong();   // 뒤의 8바이트

        return new UUID(high, low);
    }

    public static byte[] convertUuidToBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }
}
