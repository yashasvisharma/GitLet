package gitlet.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CommitGraph implements Serializable {
    private static final long serialVersionUID = -2121844053787120370L;

    private final Map<String, Commit> commits = new HashMap<>();

    public CommitGraph() { }

    public CommitGraph(Commit... commits) {
        for (Commit commit : commits) {
            this.commits.put(commit.getHash(), commit);
        }
    }

    public Commit lookupCommit(String hash) throws GitletException {
        if (hash.length() == 40) {
            return commits.get(hash);
        }
        // Shorter commit hashes.
        Commit foundCommit = null;
        for (Commit commit : commits.values()) {
            if (commit.getHash().startsWith(hash)) {
                if (foundCommit != null) {
                    throw new GitletException("Ambiguous commit hash prefix.");
                }
                foundCommit = commit;
            }
        }
        return foundCommit;
    }

    public Iterable<Commit> getAllCommits() {
        return commits.values();
    }

    public void addCommit(Commit commit) {
        commits.put(commit.getHash(), commit);
    }
}
