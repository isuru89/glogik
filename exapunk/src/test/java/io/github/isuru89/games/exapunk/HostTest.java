package io.github.isuru89.games.exapunk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HostTest {

    @Test
    void hostShouldBeUniqueById() {
        Set<Host> hosts = new HashSet<>();
        Assertions.assertTrue(hosts.add(new Host("800", 3)));
        Assertions.assertTrue(hosts.add(new Host("798", 3)));
        Assertions.assertTrue(hosts.add(new Host("797", 3)));
        Assertions.assertFalse(hosts.add(new Host("797", 9)));
    }

    @Test
    void hostLinking_ShouldFailIfSameLinkIdUsed() {
        var host = new Host("800", 3);
        var hostX1 = new Host("799", 6);
        var hostX2 = new Host("798", 2);

        host.addNeighbor(799, hostX1);
        host.addNeighbor(798, hostX2);
        Assertions.assertThrows(RuntimeException.class, () -> host.addNeighbor(798, hostX1));
    }

    @Test
    void hostLinking_ShouldFailAddingSameHostRecursively() {
        var host = new Host("800", 3);
        Assertions.assertThrows(RuntimeException.class, () -> host.addNeighbor(799, host));
    }


    @Test
    void hostLinking_ShouldAbleToAddMultipleLinksForSameHost() {
        var host = new Host("800", 3);
        var hostX1 = new Host("799", 6);

        host.addNeighbor(799, hostX1);
        host.addNeighbor(798, hostX1);
    }

    @Test
    void addExa_ShouldFailIfSameExaAddedTwiceOrMore() {
        var host = new Host("800", 8);
        var exa = new ExA("exa-1", defaultProgram());

        host.addExA(exa);
        Assertions.assertThrows(RuntimeException.class, () -> host.addExA(exa));
        Assertions.assertThrows(RuntimeException.class, () -> host.addExA(new ExA("exa-1", defaultProgram())));
    }

    @Test
    void addExa_ShouldOccupyOneSpaceBySingleExa() {
        var host = new Host("800", 8);
        var exa = new ExA("exa-1", defaultProgram());

        assertEquals(8, host.getTotalSpaces());
        assertEquals(8, host.getRemainingSpaces());
        host.addExA(exa);
        assertEquals(8, host.getTotalSpaces());
        assertEquals(7, host.getRemainingSpaces());
    }

    @Test
    void addExa_ShouldNotChangeFreeSpaceIfExaAddingFailed() {
        var host = new Host("800", 8);
        var exa = new ExA("exa-1", defaultProgram());

        assertEquals(8, host.getRemainingSpaces());
        host.addExA(exa);
        assertEquals(7, host.getRemainingSpaces());
        Assertions.assertThrows(RuntimeException.class, () -> host.addExA(exa));
        assertEquals(7, host.getRemainingSpaces());
    }

    @Test
    void addExa_ShouldFailIfNotEnoughSpacesWhenAddingExa() {
        var host = new Host("800", 3);

        assertEquals(3, host.getRemainingSpaces());
        host.addExA(new ExA("exa-1", defaultProgram()));
        assertEquals(2, host.getRemainingSpaces());
        host.addExA(new ExA("exa-2", defaultProgram()));
        assertEquals(1, host.getRemainingSpaces());
        host.addExA(new ExA("exa-3", defaultProgram()));
        assertEquals(0, host.getRemainingSpaces());
        Assertions.assertThrows(RuntimeException.class, () -> host.addExA(new ExA("exa-4", defaultProgram())));
    }


    @Test
    void removeExa_ShouldAvailableAdditionalSpaceWhenRemoved() {
        var host = new Host("800", 3);

        host.addExA(new ExA("exa-1", defaultProgram()));
        host.addExA(new ExA("exa-2", defaultProgram()));
        assertEquals(1, host.getRemainingSpaces());

        host.removeExA(new ExA("exa-1", defaultProgram()));
        assertEquals(2, host.getRemainingSpaces());
    }

    @Test
    void removeExa_ShouldFailWhenTriedNonExistenceExa() {
        var host = new Host("800", 3);

        host.addExA(new ExA("exa-1", defaultProgram()));
        host.addExA(new ExA("exa-2", defaultProgram()));

        Assertions.assertThrows(RuntimeException.class, () -> host.removeExA(new ExA("exa-9", defaultProgram())));
    }

    @Test
    void addFile_ShouldOccupyOneSpaceBySingleFile() {
        var host = new Host("800", 3);

        assertEquals(3, host.getRemainingSpaces());
        host.addFile(aFile("201"));
        assertEquals(2, host.getRemainingSpaces());
    }


    @Test
    void addFile_ShouldFailIfSameFileTriedToAddTwiceOrMore() {
        var host = new Host("800", 3);

        host.addFile(aFile("201"));
        assertEquals(2, host.getRemainingSpaces());
        Assertions.assertThrows(RuntimeException.class, () -> host.addFile(aFile("201")));
        assertEquals(2, host.getRemainingSpaces());
    }


    @Test
    void addFile_ShouldFailIfNotEnoughSpaceAvailable() {
        var host = new Host("800", 3);

        host.addFile(aFile("201"));
        host.addFile(aFile("202"));
        host.addFile(aFile("203"));
        assertEquals(0, host.getRemainingSpaces());

        Assertions.assertThrows(RuntimeException.class, () -> host.addFile(aFile("204")));
        assertEquals(0, host.getRemainingSpaces());
    }


    @Test
    void removeFile_ShouldAvailableAdditionalSpaceOnceRemoved() {
        var host = new Host("800", 3);

        host.addFile(aFile("201"));
        host.addFile(aFile("202"));
        host.addFile(aFile("203"));
        assertEquals(0, host.getRemainingSpaces());

        host.removeFile(aFile("202"));
        assertEquals(1, host.getRemainingSpaces());
    }

    @Test
    void removeFile_ShouldFailWhenTriedNonExistenceFile() {
        var host = new Host("800", 3);

        host.addFile(aFile("201"));
        Assertions.assertThrows(RuntimeException.class, () -> host.removeFile(aFile("299")));
    }

    private Program defaultProgram() {
        return new Program(List.of());
    }

    private File aFile(String id) {
        return new File(id);
    }
}