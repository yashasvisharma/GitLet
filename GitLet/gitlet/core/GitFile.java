package gitlet.core;

import gitlet.Utils;

import java.io.File;
import java.io.Serializable;

public class GitFile implements Serializable {
    private static final long serialVersionUID = 913448143310365102L;

    private final String name;
    private final String hash;

    /**
     * @param hash - if null, means the file has been deleted
     */
    public GitFile(String name, String hash) {
        this.name = name;
        this.hash = hash;
    }

    public static GitFile create(File workingDirectory, String name) throws GitletException {
        File file = new File(workingDirectory, name);
        if (!file.exists()) {
            throw new GitletException("File does not exist.");
        }
        String hash = Utils.sha1(file);
        return new GitFile(name, hash);
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }
}
