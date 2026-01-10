package fileindexer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

public class FileIndex {
    private final ConcurrentHashMap<String, FileData> index = new ConcurrentHashMap<>();

    // Dodavanje datoteke u indeks
    public boolean add(FileData data) {
        return index.putIfAbsent(data.getPath(), data) == null;
    }

    public Collection<FileData> allFiles() {
        return index.values();
    }

    public int size() {
        return index.size();
    }
}
