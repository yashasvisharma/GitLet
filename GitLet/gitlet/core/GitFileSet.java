package gitlet.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GitFileSet implements Iterable<GitFile>, Serializable {
    private static final long serialVersionUID = 4428205857098378614L;

    private final Map<String, GitFile> files;

    public GitFileSet() {
        this(Collections.emptyList());
    }

    public GitFileSet(Iterable<GitFile> files) {
        this(buildMap(files));
    }

    private GitFileSet(Map<String, GitFile> files) {
        this.files = files;
    }

    public GitFileSet combine(GitFileSet other) {
        Map<String, GitFile> copiedFiles = new HashMap<>(this.files);
        copiedFiles.putAll(other.files);
        return new GitFileSet(copiedFiles.values());
    }

    public GitFile lookupFile(String filename) {
        return this.files.get(filename);
    }

    @Override
    public Iterator<GitFile> iterator() {
        return files.values().iterator();
    }

    public Set<String> getFileNames() {
        return this.files.keySet();
    }

    public GitFileSet addFile(GitFile file) {
        this.files.put(file.getName(), file);
        return this;
    }

    public GitFileSet addFiles(GitFile... newFiles) {
        for (GitFile file : newFiles) {
            addFile(file);
        }
        return this;
    }

    public GitFileSet addFiles(Iterable<GitFile> newFiles) {
        for (GitFile file : newFiles) {
            addFile(file);
        }
        return this;
    }

    public GitFileSet removeFile(String filename) {
        this.files.remove(filename);
        return this;
    }

    public GitFileSet clear() {
        this.files.clear();
        return this;
    }

    /**
     * Substracts the given file set from this file set and returns a copy. The resulting
     * file set can be used to transform the source file configuration into this file
     * configuration.
     */
    public GitFileSet diff(GitFileSet sourceFileSet) {
        GitFileSet destinationFileSet = new GitFileSet(this);
        for (GitFile file : sourceFileSet) {
            GitFile destFile = destinationFileSet.lookupFile(file.getName());
            if (destFile != null) {
                if (Objects.equals(destFile.getHash(), file.getHash())) {
                    destinationFileSet.removeFile(destFile.getName());
                } // else: do not do anything.
            } else {
                destinationFileSet.addFile(new GitFile(file.getName(), null));
            }
        }
        return destinationFileSet;
    }

    private static Map<String, GitFile> buildMap(Iterable<GitFile> files) {
        Map<String, GitFile> fileMap = new HashMap<>();
        for (GitFile file : files) {
            fileMap.put(file.getName(), file);
        }
        return fileMap;
    }

    public int size() {
        return this.files.size();
    }

    public boolean isEmpty() {
        return this.files.isEmpty();
    }
}
