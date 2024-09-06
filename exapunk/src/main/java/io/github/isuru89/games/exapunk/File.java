package io.github.isuru89.games.exapunk;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;

public class File {

    public static final String EOF = "";

    private final String id;
    private final List<String> contents = new ArrayList<>();
    private Host placedHost;
    private ExA ownedBy;

    public File(String id) {
        this.id = id;
    }

    private File(String id, List<String> content) {
        this.id = id;
        this.contents.addAll(content);
    }

    public static File create(String id) {
        return new File(id);
    }

    public static File createWithContent(String id, List<String> content) {
        return new File(id, content);
    }

    public String getId() {
        return id;
    }

    public Optional<Host> getPlacedHost() {
        return Optional.ofNullable(placedHost);
    }

    void setPlacedHost(Host placedHost) {
        this.placedHost = placedHost;
    }

    public Optional<ExA> getOwnedBy() {
        return Optional.ofNullable(ownedBy);
    }

    void setOwnedBy(ExA ownedBy) {
        this.ownedBy = ownedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(id, file.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "File{" +
                "contents=" + contents +
                '}';
    }

    static class ContentIterator implements ListIterator<String> {
        private final ListIterator<String> it;

        ContentIterator(File file) {
            this.it = file.contents.listIterator();
        }

        public void seek(int pos) {
            int cur = 0;
            if (pos > 0) {
                while (cur < pos && it.hasNext()) {
                    it.next();
                    cur++;
                }
            } else {
                while (cur < Math.abs(pos) && it.hasPrevious()) {
                    it.previous();
                    cur++;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public String next() {
            return it.next();
        }

        @Override
        public boolean hasPrevious() {
            return it.hasPrevious();
        }

        @Override
        public String previous() {
            return it.previous();
        }

        @Override
        public int nextIndex() {
            return it.nextIndex();
        }

        @Override
        public int previousIndex() {
            return it.previousIndex();
        }

        @Override
        public void remove() {
            if (it.hasNext()) {
                it.next();
                it.previous();
                it.remove();
            } else {
                throw new RuntimeException("cannot remove from end of file!");
            }
        }

        @Override
        public void set(String s) {
            if (it.hasNext()) {
                it.next();
                it.set(s);
                it.previous();
            } else {
                add(s);
            }
        }

        @Override
        public void add(String s) {
            if (!it.hasNext()) {
                it.add(s);
            } else {
                throw new RuntimeException("cannot add elements in the middle of file");
            }
        }
    }

}
