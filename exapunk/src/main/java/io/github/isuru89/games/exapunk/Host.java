package io.github.isuru89.games.exapunk;

import java.util.*;

public class Host {

    private final String id;
    private final int totalSpaces;
    private final Set<ExA> exAs = new HashSet<>();
    private final Set<Host> neighbors = new HashSet<>();
    private final Set<File> files = new HashSet<>();
    private final Map<Integer, Host> hostLinks = new HashMap<>();
    private int remainingSpaces;

    public Host(String id, int totalSpaces) {
        this.id = id;
        this.totalSpaces = totalSpaces;
        this.remainingSpaces = totalSpaces;
    }

    public void addFile(File file) {
        if (remainingSpaces == 0) {
            throw new RuntimeException("not enough space in this host!");
        }

        if (!files.add(file)) {
            throw new RuntimeException("file already exists in this host!");
        }

        file.setPlacedHost(this);
        remainingSpaces--;
    }

    public void removeFile(File file) {
        if (!files.remove(file)) {
            throw new RuntimeException("file does not exists in this host!");
        }
        file.setPlacedHost(null);
        remainingSpaces++;
    }

    public Optional<File> findFileById(String fileId) {
        return files.stream().filter(f -> fileId.equals(f.getId())).findFirst();
    }

    public void removeExA(ExA exA) {
        if (exAs.remove(exA)) {
            remainingSpaces++;
        } else {
            throw new RuntimeException("exa not exists in this host!");
        }
    }

    public Optional<Host> getLinkedHost(int hostId) {
        return Optional.ofNullable(hostLinks.get(hostId));
    }

    Optional<ExA> getAnotherExa(ExA toIgnore) {
        return exAs.stream().filter(e -> !e.equals(toIgnore)).findFirst();
    }

    public void addExA(ExA exa) {
        if (remainingSpaces == 0) {
            throw new RuntimeException("not enough space in this host!");
        }

        if (!exAs.add(exa)) {
            throw new RuntimeException("exa already exists in this host!");
        }

        remainingSpaces--;
        exa.setCurrentHost(this);
    }

    boolean containsExa(ExA exA) {
        return exAs.contains(exA);
    }

    boolean hasEnoughSpace() {
        return remainingSpaces > 0;
    }

    public void addNeighbor(int linkId, Host host) {
        if (host.equals(this)) {
            throw new RuntimeException("cannot add same host recursively!");
        }

        if (hostLinks.containsKey(linkId)) {
            throw new RuntimeException("the link already exists!");
        }
        hostLinks.put(linkId, host);
    }

    public int getRemainingSpaces() {
        return remainingSpaces;
    }

    public int getTotalSpaces() {
        return totalSpaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Host host = (Host) o;
        return Objects.equals(id, host.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
