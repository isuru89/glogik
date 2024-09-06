package io.github.isuru89.games.exapunk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test
    void fileIterationUpdateFirstElement() {
        File file = File.createWithContent("204", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);

        it.set("updated-a");
        Assertions.assertEquals("updated-a", it.next());
        System.out.println(file);
    }

    @Test
    void fileIterationUpdateMiddleElement() {
        File file = File.createWithContent("204", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);
        it.seek(1);
        Assertions.assertEquals("b", it.next());
        it.set("updated-c");
        Assertions.assertEquals("updated-c", it.next());
    }


    @Test
    void fileIterationUpdateEndElement() {
        File file = File.createWithContent("204", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);
        it.seek(999);
        it.previous();
        it.set("updated-e");
        Assertions.assertEquals("updated-e", it.next());
    }


    @Test
    void fileIterationAddShouldFailUnlessCursorIsAtTheEnd() {
        File file = File.createWithContent("204", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);

        Assertions.assertThrows(RuntimeException.class, () -> it.add("x"));
        assertFileContent(file, Arrays.asList("a", "b", "c", "d", "e"));
        it.seek(2);
        Assertions.assertThrows(RuntimeException.class, () -> it.add("x"));
        assertFileContent(file, Arrays.asList("a", "b", "c", "d", "e"));
    }


    @Test
    void fileIterationAddShouldSucceedOnlyAtTheEnd() {
        File file = File.createWithContent("204", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);

        it.seek(999);
        Assertions.assertDoesNotThrow(() -> it.add("x"));

        assertFileContent(file, List.of("a", "b", "c", "d", "e", "x"));
    }


    @Test
    void fileIterationAddShouldBeAbleToCallContinuouslyAtTheEnd() {
        File file = File.createWithContent("204", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);

        it.seek(999);
        Assertions.assertDoesNotThrow(() -> it.add("x"));
        Assertions.assertDoesNotThrow(() -> it.add("y"));
        Assertions.assertDoesNotThrow(() -> it.add("z"));

        assertFileContent(file, List.of("a", "b", "c", "d", "e", "x", "y", "z"));
    }

    @Test
    void fileIterationRemoveShouldSucceedFirstElement() {
        File file = File.createWithContent("204", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);

        Assertions.assertDoesNotThrow(it::remove);

        assertFileContent(file, List.of("b", "c", "d", "e"));
    }


    @Test
    void fileIterationRemoveShouldSucceedMiddleElement() {
        File file = File.createWithContent("204", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);

        it.seek(2);
        Assertions.assertDoesNotThrow(it::remove);

        assertFileContent(file, List.of("a", "b", "d", "e"));
    }

    @Test
    void fileIterationRemoveShouldFailAtEndOfFile() {
        File file = File.createWithContent("204", Arrays.asList("a", "b", "c", "d", "e"));
        File.ContentIterator it = new File.ContentIterator(file);

        it.seek(999);
        Assertions.assertThrows(RuntimeException.class, it::remove);

        assertFileContent(file, List.of("a", "b", "c", "d", "e"));
    }

    private void assertFileContent(File file, List<String> expected) {
        var it = new File.ContentIterator(file);
        var actual = new ArrayList<String>();

        while (it.hasNext()) {
            actual.add(it.next());
        }

        Assertions.assertEquals(expected, actual);
    }

}