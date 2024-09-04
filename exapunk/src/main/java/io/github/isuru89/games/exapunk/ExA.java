package io.github.isuru89.games.exapunk;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ExA {

    private final String id;
    private final AtomicInteger subIds = new AtomicInteger(0);
    private final Program program;
    private Host currentHost;
    private File currentFile;

    public ExA(String id, Program program) {
        this.id = id;
        this.program = program;
    }

    public void moveToHost(int hostId) {
        var linkedHost = currentHost.getLinkedHost(hostId).orElseThrow(() -> new RuntimeException("no such host!"));

        if (linkedHost.hasEnoughSpace()) {
            currentHost.removeExA(this);
            linkedHost.addExA(this);
        } else {
            throw new RuntimeException("destination does not have enough space!");
        }
    }

    public void grabFile(File file) {
        if (currentFile != null) {
            throw new RuntimeException("exa already own another file!");
        }

        if (file.getOwnedBy().isPresent()) {
            throw new RuntimeException("file is already owned by some other exa!");
        }

        var fileHost = file.getPlacedHost().orElseThrow(() -> new RuntimeException("file does not placed in a host!"));
        if (!fileHost.equals(currentHost)) {
            throw new RuntimeException("exa and file must be in same host!");
        }

        fileHost.removeFile(file);

        file.setOwnedBy(this);
        currentFile = file;
    }

    public void dropFile(File file) {
        if (currentHost == null) {
            throw new RuntimeException("exa is not in a host!");
        }

        if (currentFile == null) {
            throw new RuntimeException("exa does not owning a file!");
        }

        var fileOwner = file.getOwnedBy().orElseThrow(() -> new RuntimeException("file is not owned by any exa!"));
        if (!this.equals(fileOwner)) {
            throw new RuntimeException("file does not owned by this exa!");
        }

        if (currentHost.getRemainingSpaces() <= 0) {
            throw new RuntimeException("not enough space to drop file!");
        }

        currentHost.addFile(file);
        file.setOwnedBy(null);
        currentFile = null;
    }

    public ExA replicate() {
        String newId = id + ":" + subIds.getAndIncrement();
        Program copiedProgram = program.copy(program.getCurrentPosition());

        return new ExA(newId, copiedProgram);
    }

    public Optional<File> getCurrentFile() {
        return Optional.ofNullable(currentFile);
    }

    public Optional<Host> getCurrentHost() {
        return Optional.ofNullable(currentHost);
    }

    public void setCurrentHost(Host host) {
        currentHost = host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExA exA = (ExA) o;
        return Objects.equals(id, exA.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
