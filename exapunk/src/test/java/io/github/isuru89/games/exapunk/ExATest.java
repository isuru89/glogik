package io.github.isuru89.games.exapunk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ExATest {


    @Test
    void move_ShouldFailIfExaIsNotInAHost() {
        var exa = new ExA("exa-1", defaultProgram());

        Assertions.assertThrows(RuntimeException.class, () -> exa.moveToHost(800));
    }

    @Test
    void move_ShouldFailThereIsNoExistingHostLinked() {
        var host = new Host("800", 3);
        var exa = new ExA("exa-1", defaultProgram());

        host.addExA(exa);

        Assertions.assertThrows(RuntimeException.class, () -> exa.moveToHost(801));
    }

    @Test
    void move_ShouldSucceedMovingToALinkedHost() {
        var host = new Host("800", 3);
        var hostNext = new Host("801", 2);
        var exa = new ExA("exa-1", defaultProgram());

        host.addNeighbor(801, hostNext);
        host.addExA(exa);

        Assertions.assertEquals(2, host.getRemainingSpaces());
        Assertions.assertEquals(2, hostNext.getRemainingSpaces());
        Assertions.assertDoesNotThrow(() -> exa.moveToHost(801));
        Assertions.assertEquals(1, hostNext.getRemainingSpaces());
        Assertions.assertEquals(3, host.getRemainingSpaces());

        Assertions.assertEquals(hostNext, exa.getCurrentHost().orElseThrow());
        Assertions.assertFalse(host.containsExa(exa));
        Assertions.assertTrue(hostNext.containsExa(exa));
    }

    @Test
    void move_ShouldFailIfDestinationHasNotEnoughSpace() {
        var host = new Host("800", 3);
        var hostNext = new Host("801", 2);
        var exa = new ExA("exa-1", defaultProgram());

        host.addNeighbor(801, hostNext);
        host.addExA(exa);
        hostNext.addExA(new ExA("exa-2", defaultProgram()));
        hostNext.addExA(new ExA("exa-3", defaultProgram()));

        Assertions.assertEquals(2, host.getRemainingSpaces());
        Assertions.assertEquals(0, hostNext.getRemainingSpaces());
        Assertions.assertThrows(RuntimeException.class, () -> exa.moveToHost(801));
        Assertions.assertEquals(0, hostNext.getRemainingSpaces());
        Assertions.assertEquals(2, host.getRemainingSpaces());

        Assertions.assertEquals(host, exa.getCurrentHost().orElseThrow());
        Assertions.assertFalse(hostNext.containsExa(exa));
        Assertions.assertTrue(host.containsExa(exa));
    }

    @Test
    void grabFile_ShouldFailIfFileOwnedByAnotherExa() {
        var exa = new ExA("exa-1", defaultProgram());
        var file = File.create("201");

        Assertions.assertThrows(RuntimeException.class, () -> exa.grabFile(file));
    }

    @Test
    void grabFile_ShouldFailIfFileIsInDifferentHostThanExa() {
        var hostFile = new Host("800", 3);
        var hostExa = new Host("801", 2);
        var exa = new ExA("exa-1", defaultProgram());
        var file = File.create("201");

        hostFile.addFile(file);
        hostExa.addExA(exa);

        Assertions.assertThrows(RuntimeException.class, () -> exa.grabFile(file));
    }

    @Test
    void grabFile_ShouldFailIfExaIsNotAnyHostYet() {
        var hostFile = new Host("800", 3);
        var exa = new ExA("exa-1", defaultProgram());
        var file = File.create("201");

        hostFile.addFile(file);

        Assertions.assertThrows(RuntimeException.class, () -> exa.grabFile(file));
    }

    @Test
    void grabFile_ShouldFailIfFileIsNotAnyHostYet() {
        var hostExa = new Host("801", 2);
        var exa = new ExA("exa-1", defaultProgram());
        var file = File.create("201");

        hostExa.addExA(exa);

        Assertions.assertThrows(RuntimeException.class, () -> exa.grabFile(file));
    }

    @Test
    void grabFile_ShouldFailIfExaAlreadyOwnedADifferentFile() {
        var host = new Host("800", 3);
        var exa = new ExA("exa-1", defaultProgram());
        var file = File.create("201");
        var fileAnother = File.create("202");

        host.addFile(file);
        host.addFile(fileAnother);
        host.addExA(exa);

        exa.grabFile(fileAnother);
        Assertions.assertThrows(RuntimeException.class, () -> exa.grabFile(file));
    }

    @Test
    void grabFile_ShouldFailIfExaTriedToGrabSameFileTwiceWhileOwningTheSame() {
        var host = new Host("800", 3);
        var exa = new ExA("exa-1", defaultProgram());
        var file = File.create("201");

        host.addFile(file);
        host.addExA(exa);
        exa.grabFile(file);

        Assertions.assertThrows(RuntimeException.class, () -> exa.grabFile(file));
    }

    @Test
    void grabFile_ShouldTakeSpaceFromHostOnceGrabbed() {
        var host = new Host("800", 3);
        var exa = new ExA("exa-1", defaultProgram());
        var file = File.create("201");

        host.addFile(file);
        host.addExA(exa);
        Assertions.assertEquals(1, host.getRemainingSpaces());
        Assertions.assertDoesNotThrow(() -> exa.grabFile(file));

        Assertions.assertEquals(exa, file.getOwnedBy().orElseThrow());
        Assertions.assertTrue(file.getPlacedHost().isEmpty());
        Assertions.assertEquals(2, host.getRemainingSpaces());
    }

    @Test
    void dropFile_ShouldFailIfExaDoesNotOwnAFile() {
        var exa = new ExA("exa-1", defaultProgram());

        Assertions.assertThrows(RuntimeException.class, () -> exa.dropFile());
    }
//
//    @Test
//    void dropFile_ShouldFailIfExaTryToDropNoOneOwnedFile() {
//        var host = new Host("800", 3);
//        var exa = new ExA("exa-1", defaultProgram());
//        var file = File.create("201");
//        var fileOther = File.create("202");
//
//        host.addFile(file);
//        host.addExA(exa);
//        exa.grabFile(file);
//
//        Assertions.assertThrows(RuntimeException.class, () -> exa.dropFile());
//    }
//
//
//    @Test
//    void dropFile_ShouldFailIfExaTryToDropFileOwnedByAnotherExa() {
//        var host = new Host("800", 5);
//        var exa = new ExA("exa-1", defaultProgram());
//        var exaOther = new ExA("exa-2", defaultProgram());
//        var file = File.create("201");
//        var fileOther = File.create("202");
//
//        host.addFile(file);
//        host.addFile(fileOther);
//        host.addExA(exa);
//        host.addExA(exaOther);
//        exaOther.grabFile(fileOther);
//        exa.grabFile(file);
//
//        Assertions.assertThrows(RuntimeException.class, () -> exa.dropFile());
//    }

    @Test
    void dropFile_ShouldFailIfExaCannotDropFileInFullyOccupiedHost() {
        var host = new Host("800", 3);
        var exa = new ExA("exa-1", defaultProgram());
        var exaOther = new ExA("exa-2", defaultProgram());
        var exaSecond = new ExA("exa-3", defaultProgram());
        var file = File.create("201");

        host.addFile(file);
        host.addExA(exa);
        host.addExA(exaOther);
        exa.grabFile(file);
        host.addExA(exaSecond);

        Assertions.assertThrows(RuntimeException.class, () -> exa.dropFile());
    }

    @Test
    void dropFile_ShouldSucceedGrabbedFileDroppedImmediately() {
        var host = new Host("800", 3);
        var exa = new ExA("exa-1", defaultProgram());
        var exaOther = new ExA("exa-2", defaultProgram());
        var file = File.create("201");

        host.addFile(file);
        host.addExA(exa);
        host.addExA(exaOther);
        exa.grabFile(file);

        Assertions.assertDoesNotThrow(() -> exa.dropFile());
    }

    @Test
    void dropFile_ShouldSucceedIfHostHasEnoughSpace() {
        var host = new Host("800", 5);
        var exa = new ExA("exa-1", defaultProgram());
        var file = File.create("201");

        host.addFile(file);
        host.addExA(exa);
        exa.grabFile(file);

        Assertions.assertDoesNotThrow(() -> exa.dropFile());

        Assertions.assertTrue(file.getOwnedBy().isEmpty());
        Assertions.assertTrue(exa.getCurrentFile().isEmpty());
        Assertions.assertEquals(host, file.getPlacedHost().orElseThrow());
    }

    @Test
    void replicate_ShouldFailIfNotEnoughSpace() {
        var host = new Host("800", 3);
        var exa = new ExA("exa-1", defaultProgram());

        host.addExA(exa);
        host.addExA(new ExA("exa-2", defaultProgram()));
        host.addExA(new ExA("exa-3", defaultProgram()));

        Assertions.assertEquals(0, host.getRemainingSpaces());
        Assertions.assertThrows(RuntimeException.class, () -> exa.replicate("TODO"));
        Assertions.assertEquals(0, host.getRemainingSpaces());
    }

    @Test
    void replicate_ClonedExAShouldHaveCorrectLabelAsStartingPoint() {

    }

    private Program defaultProgram() {
        return new Program(List.of(), 0);
    }
}