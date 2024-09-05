package io.github.isuru89.games.exapunk;

import java.util.*;

public class Program {

    private final List<String> lines;
    private final Map<String, Integer> labelPos = new HashMap<>();
    private ExA owner;
    private int currentPosition;

    public Program(List<String> codeLines) {
        this(codeLines, 0);
    }

    public Program(List<String> codeLines, int currPos) {
        lines = new ArrayList<>(codeLines);
        this.currentPosition = currPos;

        this.scanLinesForLabelPositions();
    }

    void scanLinesForLabelPositions() {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            var lblOpt = getLabelName(line);
            if (lblOpt.isPresent()) {
                labelPos.put(lblOpt.get(), i);
            }
        }
    }

    void setOwner(ExA owner) {
        this.owner = owner;
    }

    private Optional<String> getLabelName(String line) {
        if (line.toLowerCase().startsWith("mark")) {
            return Optional.of(line.substring(5).trim());
        }
        return Optional.empty();
    }

    private int findNextExecutableStatement(int curPos) {
        String line;
        int pos;
        do {
            pos = curPos++ % lines.size();
            line = lines.get(pos).trim();
        } while (!isExecutableStatement(line));

        return pos;
    }

    public void execute() {
        int nextPos = findNextExecutableStatement(currentPosition);
        String line = lines.get(nextPos).trim();
        currentPosition = nextPos + 1;

        String[] args = line.split("[\\s+]");
        String cmd = args[0];

        switch (cmd) {
            case "link":
                owner.moveToHost(Integer.parseInt(args[1]));
                break;
            case "grab":
                var host = owner.getCurrentHost().orElseThrow(() -> new RuntimeException("exa is in invalid host"));
                owner.grabFile(host.findFileById(args[1]).orElseThrow(() -> new RuntimeException("no such file by given id")));
                break;
            case "drop":
                owner.dropFile();
                break;
            case "repl":
                owner.replicate(args[1]);
            default:
                throw new RuntimeException("unknown command");
        }
    }

    private boolean isExecutableStatement(String line) {
        return !line.isBlank() && !line.startsWith("#") && !line.endsWith(":");
    }

    int getLabelPosition(String label) {
        return labelPos.getOrDefault(label, -1);
    }

    void gotoLabel(String label) {
        if (!labelPos.containsKey(label)) {
            throw new RuntimeException("no label exists in the program");
        }

        int pos = labelPos.get(label);
        currentPosition = findNextExecutableStatement(pos);
    }

    public Program copy(int withNewPosition) {
        return new Program(List.of(), withNewPosition);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
