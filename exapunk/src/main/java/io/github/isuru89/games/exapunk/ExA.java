package io.github.isuru89.games.exapunk;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ExA {

    private final String id;
    private final AtomicInteger subIds = new AtomicInteger(0);
    private Program program;
    private Host currentHost;
    private File currentFile;
    private Level currentLevel;
    private boolean local;
    private BlockContext blockContext;

    public ExA(String id, Program program) {
        this.id = id;
        this.local = false;
        this.setProgram(program);
        this.blockContext = new BlockContext(BlockType.NONE, this.local);
    }

    void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }

    void setProgram(Program program) {
        this.program = program;
        this.program.setOwner(this);
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

    public void dropFile() {
        if (currentHost == null) {
            throw new RuntimeException("exa is not in a host!");
        }

        if (currentFile == null) {
            throw new RuntimeException("exa does not owning a file!");
        }

        var fileOwner = currentFile.getOwnedBy().orElseThrow(() -> new RuntimeException("file is not owned by any exa!"));
        if (!this.equals(fileOwner)) {
            throw new RuntimeException("file does not owned by this exa!");
        }

        if (currentHost.getRemainingSpaces() <= 0) {
            throw new RuntimeException("not enough space to drop file!");
        }

        currentHost.addFile(currentFile);
        currentFile.setOwnedBy(null);
        currentFile = null;
    }

    public ExA replicate(String label) {
        if (!currentHost.hasEnoughSpace()) {
            throw new RuntimeException("not enough space on the current host");
        }

        if (program.getLabelPosition(label) < 0) {
            throw new RuntimeException("no such label exists by given name");
        }

        String newId = id + ":" + subIds.getAndIncrement();
        int spawnPos = program.getLabelPosition(label);
        Program copiedProgram = program.copy(spawnPos);

        var newExa = new ExA(newId, copiedProgram);
        currentHost.addExA(newExa);

        return newExa;
    }

    public void wipe() {
        if (currentFile == null) {
            throw new RuntimeException("no file is held by this exa");
        }

        currentFile.setOwnedBy(null);
        currentFile = null;
    }

    public void make() {
        if (currentFile != null) {
            throw new RuntimeException("exa is holding a file already");
        }

        if (currentLevel == null) {
            throw new RuntimeException("this exa does not belong to a level");
        }

        int fId = 200;
        while (currentLevel.findFileById(String.valueOf(fId)).isPresent()) {
            fId++;
        }

        var file = new File(String.valueOf(fId));
        currentLevel.addFile(file);
        currentHost.addFile(file);
        file.setOwnedBy(this);
    }

    public void modeChange() {
        this.local = !this.local;
    }

    public Optional<ExA> findAnotherExaInThisHost() {
        return currentHost.getAnotherExa(this);
    }

    public Optional<ExA> findAnotherExaInLevel() {
        return currentLevel.findAnyOtherExa(this);
    }

    public void halt() {
        if (currentFile != null) {
            currentHost.addFile(currentFile);
            currentFile.setOwnedBy(null);
            currentFile = null;
        }

        currentHost.removeExA(this);
        currentLevel.removeExa(this);
    }

    public void kill() {
        var other = currentHost.getAnotherExa(this);
        if (other.isPresent()) {
            other.get().halt();

        } else {
            throw new RuntimeException("no exas to kill");
        }
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

    static enum BlockType {
        NONE, IO
    }

    static record BlockContext(BlockType blockType, boolean local) {
    }
}
