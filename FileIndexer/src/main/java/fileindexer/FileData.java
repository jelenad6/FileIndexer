package fileindexer;

import java.io.File;

public class FileData {
    private final String path;
    private final long size;
    private final long lastModified;
    private final String extension;

    public FileData(File f) {
        this.path = f.getAbsolutePath();
        this.size = f.length();
        this.lastModified = f.lastModified();
        String name = f.getName();
        int dot = name.lastIndexOf('.');
        this.extension = (dot == -1) ? "" : name.substring(dot+1);
    }

    public String getPath() { return path; }
    public long getSize() { return size; }
    public long getLastModified() { return lastModified; }
    public String getExtension() { return extension; }

    @Override
    public String toString() {
        return path + " (" + size + " B, " + extension + ")";
    }
}
