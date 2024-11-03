package io.github.isuru89.games.exapunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Level implements Iterable<String> {

    private final Set<File> allFiles = new HashSet<>();
    private final Set<ExA> allExAs = new HashSet<>();
    private final Set<Host> allHosts = new HashSet<>();
    private final Map<Host, Map<Integer, Host>> hostConnections = new HashMap<>();

    private final BufferValue globalM = new BufferValue();

    public BufferValue getGlobalM() {
        return globalM;
    }

    public Optional<File> findFileById(String fileId) {
        return allFiles.stream().filter(f -> f.getId().equals(fileId)).findFirst();
    }

    public Optional<ExA> findAnyOtherExa(ExA ignore) {
        return allExAs.stream().filter(e -> !e.equals(ignore)).findFirst();
    }

    public void addHost(Host host) {
        if (!allHosts.add(host)) {
            throw new RuntimeException("host with same id exists");
        }
    }

    public boolean areHostsConnected(String fromHostId, String toHostId) {
        Set<String> traversedHosts = new HashSet<>();
        var fromHost = findHostById(fromHostId).orElseThrow(() -> new RuntimeException("no host found by id " + fromHostId));
        var toHost = findHostById(toHostId).orElseThrow(() -> new RuntimeException("no host found by id " + toHostId));

        traversedHosts.add(fromHostId);

        return isConnected(fromHost, toHost, traversedHosts);
    }

    private boolean isConnected(Host fromHost, Host toHost, Set<String> traversedHosts) {
        var connected = hostConnections.getOrDefault(fromHost, Map.of());
        if (connected.isEmpty()) {
            return false;
        }

        for (var next : connected.values()) {
            if (toHost.equals(next)) {
                return true;
            }

            traversedHosts.add(next.getId());
            if (isConnected(next, toHost, traversedHosts)) {
                return true;
            }
            traversedHosts.remove(next.getId());
        }

        return false;
    }

    public void linkHosts(Host from, Host to, int linkId) {
        var connections = hostConnections.computeIfAbsent(from, (key) -> new HashMap<>());
        if (connections.containsKey(linkId)) {
            throw new RuntimeException("the link id already exists with this from host");
        }

        connections.put(linkId, to);
    }

    public void linkHosts(Host from, Host to, int linkId, int reverseLinkId) {
        var connectionsTo = hostConnections.computeIfAbsent(from, (key) -> new HashMap<>());
        if (connectionsTo.containsKey(linkId)) {
            throw new RuntimeException("link id already exists between from->to");
        }
        var connectionsFrom = hostConnections.computeIfAbsent(to, (key) -> new HashMap<>());
        if (connectionsFrom.containsKey(reverseLinkId)) {
            throw new RuntimeException("reverse link id already exists between to->from");
        }

        connectionsTo.put(linkId, to);
        connectionsFrom.put(reverseLinkId, from);
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

    public Optional<Host> findHostById(String id) {
        return allHosts.stream().filter(h -> id.equals(h.getId())).findFirst();
    }

    @Override
    public Iterator<String> iterator() {
        return new Execution();
    }

    private class Execution implements Iterator<String> {

        private int cycles;

        @Override
        public boolean hasNext() {
            return !allExAs.isEmpty();
        }

        @Override
        public String next() {
            for (var exa : allExAs) {
                exa.prepareCycle();
            }

            for (var exa : allExAs) {
                exa.executeCycle();
            }


            cycles++;
            return null;
        }
    }
}
