package io.github.isuru89.games.exapunk;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ExA {

    private final String id;
    private final AtomicInteger subIds = new AtomicInteger(0);
    private final Map<String, String> registers = new HashMap<>();
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

        this.registers.putAll(Map.of(
                "X", "0",
                "T", "0",
                "F", "0",
                "M", "0"));
    }

    public boolean isLocal() {
        return local;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }

    public BlockContext prepareCycle() {
        return null;
    }

    public BlockContext executeCycle() {
        String line = program.getNextInstruction();
        String[] ops = line.split("[\\s+]");
        String cmd = ops[0];

        var executor = new Executor(this, ops);

        // execute
        switch (cmd) {
            case "link":
                return executor.commandLink();
            case "copy":
                return executor.commandCopy();
            default:
                throw new RuntimeException("unknown command!");
        }

    }

    Value setRegisterValue(String register, String value) {
        var reg = register.toUpperCase();
        if (!registers.containsKey(reg)) {
            throw new RuntimeException("no such registers found in exa!");
        }

        if ("M".equals(reg)) {
            mustHaveALevelAndHost();

            if (this.local) {
                return currentHost.getLocalM().write(value);
            } else {
                return currentLevel.getGlobalM().write(value);
            }
        }

        registers.put(register, value);
        return Value.nonBlocked(value);
    }

    boolean isRegisterAddress(String val) {
        return registers.containsKey(val.toUpperCase());
    }

    boolean canValueRead(String register) {
        var reg = register.toUpperCase();

        if ("M".equals(reg)) {
            mustHaveALevelAndHost();

            if (this.local) {
                return currentHost.getLocalM().peek().isPresent();
            } else {
                return currentLevel.getGlobalM().peek().isPresent();
            }
        }

        return isRegisterAddress(reg);
    }

    boolean canValueWrite(String register) {
        var reg = register.toUpperCase();

        if ("M".equals(reg)) {
            mustHaveALevelAndHost();

            if (this.local) {
                return currentHost.getLocalM().peek().isEmpty();
            } else {
                return currentLevel.getGlobalM().peek().isEmpty();
            }
        }

        return isRegisterAddress(reg);
    }

    Value getRegisterValue(String register) {
        var reg = register.toUpperCase();
        if (!registers.containsKey(reg)) {
            throw new RuntimeException("no such registers found in exa!");
        }

        if ("M".equals(reg)) {
            mustHaveALevelAndHost();

            if (this.local) {
                return currentHost.getLocalM().read();
            } else {
                return currentLevel.getGlobalM().read();
            }
        }

        return Value.nonBlocked(registers.get(reg));
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

    public String getCurrentHostId() {
        mustHaveALevelAndHost();

        return currentHost.getId();
    }

    public void grabFile(File file) {
        mustNotHoldAFile();

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
        mustHaveALevelAndHost();
        mustHoldAFile();

        var fileOwner = currentFile.getOwnedBy().orElseThrow(() -> new RuntimeException("file is not owned by any exa!"));
        if (!this.equals(fileOwner)) {
            throw new RuntimeException("file does not owned by this exa!");
        }

        mustHaveEnoughSpaceInHost();

        currentHost.addFile(currentFile);
        currentFile.setOwnedBy(null);
        currentFile = null;
    }

    public ExA replicate(String label) {
        mustHaveEnoughSpaceInHost();

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
        mustHoldAFile();

        currentFile.setOwnedBy(null);
        currentFile = null;
    }

    public void make() {
        mustNotHoldAFile();
        mustHaveALevelAndHost();

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

    BlockContext createNonBlockingCtx() {
        return new BlockContext(BlockType.NONE, this.local);
    }

    private void mustHaveALevelAndHost() {
        if (currentLevel == null) {
            throw new RuntimeException("this exa does not belong to a level");
        }
        if (currentHost == null) {
            throw new RuntimeException("exa is not in a host!");
        }
    }

    private void mustHaveEnoughSpaceInHost() {
        if (!currentHost.hasEnoughSpace()) {
            throw new RuntimeException("not enough space on the current host");
        }
    }

    private void mustHoldAFile() {
        if (currentFile == null) {
            throw new RuntimeException("no file is held by this exa");
        }
    }

    private void mustNotHoldAFile() {
        if (currentFile != null) {
            throw new RuntimeException("exa is holding a file already");
        }
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
