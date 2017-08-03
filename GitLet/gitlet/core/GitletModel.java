package gitlet.core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GitletModel {
    /** Filter out all but plain files. */
    private static final FilenameFilter PLAIN_FILES = (dir, name) -> new File(dir, name).isFile();

    private final File workingDirectory;
    private final File gitletDirectory;
    private final BlobManager blobManager;
    private final CommitGraph commitGraph;
    private final GitFileSet stagedFiles;
    private final Map<String, Branch> branches = new HashMap<>();
    private Branch currentBranch; // This is the only mutable field

    public GitletModel(File workingDirectory, File gitletDirectory, CommitGraph commitGraph,
            GitFileSet stagedFiles, List<Branch> branches, String currentBranch)
                throws GitletException {
        this.workingDirectory = workingDirectory;
        this.gitletDirectory = gitletDirectory;
        this.blobManager = new BlobManager(workingDirectory, gitletDirectory);
        this.commitGraph = commitGraph;
        this.stagedFiles = stagedFiles;
        for (Branch branch : branches) {
            this.branches.put(branch.getName(), branch);
        }
        this.setCurrentBranch(currentBranch);
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public File getWorkingDirectoryFile(String fileName) {
        return new File(workingDirectory, fileName);
    }

    public File getGitletDirectory() {
        return gitletDirectory;
    }

    public BlobManager getBlobManager() {
        return blobManager;
    }

    public CommitGraph getCommitGraph() {
        return commitGraph;
    }

    public GitFileSet getStagedFiles() {
        return stagedFiles;
    }

    public Map<String, Branch> getBranches() {
        return branches;
    }

    public Branch getCurrentBranch() {
        return currentBranch;
    }

    public GitletModel setCurrentBranch(String newCurrentBranch) throws GitletException {
        if (this.branches.get(newCurrentBranch) == null) {
            throw new GitletException("You can not set a null branch.");
        }
        this.currentBranch = this.branches.get(newCurrentBranch);
        return this;
    }

    /** Returns a list of the names of all plain files in the working directory. */
    public Set<String> getWorkingDirectoryFileNames() {
        String[] files = this.workingDirectory.list(PLAIN_FILES);
        if (files == null) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(Arrays.asList(files));
        }
    }
}
