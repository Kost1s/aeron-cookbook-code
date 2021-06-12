package com.aeroncookbook.agrona;

import org.agrona.concurrent.CachedEpochClock;
import org.agrona.concurrent.SnowflakeIdGenerator;
import org.agrona.concurrent.SystemEpochClock;
import org.junit.jupiter.api.Test;

import static org.agrona.concurrent.SnowflakeIdGenerator.MAX_NODE_ID_AND_SEQUENCE_BITS;
import static org.agrona.concurrent.SnowflakeIdGenerator.NODE_ID_BITS_DEFAULT;
import static org.agrona.concurrent.SnowflakeIdGenerator.SEQUENCE_BITS_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SnowflakeTests
{
    @Test
    void shouldGenerateId()
    {
        final var snowflake = new SnowflakeIdGenerator(1L);
        final var nextId = snowflake.nextId();
        assertNotEquals(0L, nextId);
    }

    @Test
    void allowsExtractionOfTimeNodeSequence()
    {
        final var clock = new CachedEpochClock();
        final var timestampOffset = 1609459200000L; //January 1, 2021 12:00:00 AM
        clock.update(SystemEpochClock.INSTANCE.time());
        final var snowflake = new SnowflakeIdGenerator(NODE_ID_BITS_DEFAULT, SEQUENCE_BITS_DEFAULT,
            1L, timestampOffset, clock);

        //extract the time, node and sequence
        final var nextId = snowflake.nextId();
        assertEquals(clock.time() - timestampOffset, nextId >>> MAX_NODE_ID_AND_SEQUENCE_BITS);
        assertEquals(1L, (nextId >>> SEQUENCE_BITS_DEFAULT) & snowflake.maxNodeId());
        assertEquals(0L, nextId & snowflake.maxSequence());

        final var nextNextId = snowflake.nextId();
        assertEquals(clock.time() - timestampOffset, nextNextId >>> MAX_NODE_ID_AND_SEQUENCE_BITS);
        assertEquals(1L, (nextNextId >>> SEQUENCE_BITS_DEFAULT) & snowflake.maxNodeId());
        assertEquals(1L, nextNextId & snowflake.maxSequence());
    }


}
