package io.github.isuru89.games.exapunk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class FileTest {

    @Test
    void fileIterationFromStartToEnd() {
        File file = File.createWithContent("201", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals("a", it.next());
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals("b", it.next());
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals("c", it.next());
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals("d", it.next());
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals("e", it.next());
        Assertions.assertFalse(it.hasNext());
    }


    @Test
    void fileIterationSeekForward() {
        File file = File.createWithContent("202", Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
        File.ContentIterator it = new File.ContentIterator(file);
        it.seek(2);
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals("c", it.next());
        Assertions.assertTrue(it.hasPrevious());
        Assertions.assertEquals("d", it.next());

        it.seek(2);
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals("g", it.next());
    }

    @Test
    void fileIterationSeekToEnd() {
        File file = File.createWithContent("203", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);
        it.seek(999);
        Assertions.assertFalse(it.hasNext());
        Assertions.assertTrue(it.hasPrevious());
        Assertions.assertEquals("e", it.previous());
    }

    @Test
    void fileIterationSeekToBeginning() {
        File file = File.createWithContent("204", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);
        it.seek(4);
        Assertions.assertTrue(it.hasNext());
        Assertions.assertTrue(it.hasPrevious());
        Assertions.assertEquals("e", it.next());

        it.seek(-999);
        Assertions.assertTrue(it.hasNext());
        Assertions.assertFalse(it.hasPrevious());
        Assertions.assertEquals("a", it.next());
    }

}