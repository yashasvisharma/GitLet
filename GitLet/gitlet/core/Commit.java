package gitlet.core;

import gitlet.Utils;

import java.io.Serializable;
import java.util.Date;

public class Commit implements Serializable {
    private static final long serialVersionUID = -6945042295117097166L;

    private final String hash;
    private final Date date;
    private final String message;
    private final Commit parent;
    private final GitFileSet allFiles;

    public Commit(String message, Commit parent, GitFileSet changedFiles) {
        this.date = new Date();
        this.message = message;
        this.parent = parent;
        this.allFiles = parent != null
                ? parent.getAllFiles().combine(changedFiles) : new GitFileSet(changedFiles);
        this.hash = Utils.sha1(date.toString(), message, parent != null ? parent.getHash() : "");
    }

    public String getHash() {
        return hash;
    }

    public Date getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public Commit getParent() {
        return parent;
    }

    public GitFileSet getAllFiles() {
        return allFiles;
    }

    public GitFile lookupFile(String filename) {
        return allFiles.lookupFile(filename);
    }
}
