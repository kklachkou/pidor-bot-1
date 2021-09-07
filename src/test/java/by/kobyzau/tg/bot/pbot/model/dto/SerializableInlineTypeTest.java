package by.kobyzau.tg.bot.pbot.model.dto;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SerializableInlineTypeTest {

    @Test
    public void assertDuplicateIndexes() {
        //when
        long count = Arrays.stream(SerializableInlineType.values()).map(SerializableInlineType::getIndex).count();
        long uniqueCount = Arrays.stream(SerializableInlineType.values()).map(SerializableInlineType::getIndex).distinct().count();

        //then
        assertEquals(uniqueCount, count);
    }

}