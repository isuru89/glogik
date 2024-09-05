package io.github.isuru89.games.exapunk;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Level {

    private final Set<File> allFiles = new HashSet<>();
    private final Set<ExA> allExAs = new HashSet<>();
    private final Set<Host> allHosts = new HashSet<>();

    public Optional<File> findFileById(String fileId) {
        return allFiles.stream().filter(f -> f.getId().equals(fileId)).findFirst();
    }

    public Optional<ExA> findAnyOtherExa(ExA ignore) {
        return allExAs.stream().filter(e -> !e.equals(ignore)).findFirst();
    }

    public void addFile(File file) {
        if (!allFiles.add(file)) {
            throw new RuntimeException("file with same id exists in this level!");
        }
    }

    public void addExa(ExA exA) {
        if (!allExAs.add(exA)) {
            throw new RuntimeException("exa with same is exists in this level");
        }
        exA.setCurrentLevel(this);
    }

    public void removeExa(ExA exA) {
        allExAs.remove(exA);
        exA.setCurrentLevel(null);
    }
}
