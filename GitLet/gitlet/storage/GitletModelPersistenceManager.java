package gitlet.storage;

import gitlet.Utils;
import gitlet.core.Branch;
import gitlet.core.CommitGraph;
import gitlet.core.GitFileSet;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GitletModelPersistenceManager {
    private static final String STAGED_FILES = "STAGED_FILES";
    private static final String BRANCHES = "BRANCHES";
    private static final String COMMIT_GRAPH = "COMMIT_GRAPH";
    private static final String CURRENT_BRANCH = "CURRENT_BRANCH";

    public GitletModel loadFromDisk(File workingDirectory, File gitletDirectory)
            throws GitletException {
        if (!gitletDirectory.exists()) {
            throw new GitletException("The .gitlet directory does not exist.");
        } else if (!gitletDirectory.isDirectory()) {
            throw new GitletException(".gitlet is not a directory");
        }

        CommitGraph commitGraph = Utils.deserialize(new File(gitletDirectory, COMMIT_GRAPH));
        GitFileSet stagedFiles = Utils.deserialize(new File(gitletDirectory, STAGED_FILES));

        Map<String, String> branchHashes = Utils.deserialize(new File(gitletDirectory, BRANCHES));
        List<Branch> branches = new ArrayList<>();
        for (Map.Entry<String, String> entry : branchHashes.entrySet()) {
            String branchName = entry.getKey();
            String branchHash = entry.getValue();
            branches.add(new Branch(branchName, commitGraph.lookupCommit(branchHash)));
        }

        String currentBranchName = Utils.deserialize(new File(gitletDirectory, CURRENT_BRANCH));
        return new GitletModel(workingDirectory, gitletDirectory, commitGraph, stagedFiles,
                branches, currentBranchName);
    }

    public void saveToDisk(GitletModel model) throws GitletException {
        File gitletDirectory = model.getGitletDirectory();
        if (!gitletDirectory.exists()) {
            if (!gitletDirectory.mkdir()) { // create new directory
                throw new GitletException("Can't create a new Gitlet directory.");
            }
        } else if (!gitletDirectory.isDirectory()) {
            throw new GitletException(".gitlet is not a directory");
        }

        Utils.serialize(model.getCommitGraph(), new File(gitletDirectory, COMMIT_GRAPH));
        Utils.serialize(model.getStagedFiles(), new File(gitletDirectory, STAGED_FILES));
        Utils.serialize(model.getCurrentBranch().getName(), new File(gitletDirectory,
                CURRENT_BRANCH));

        Map<String, String> branchHashes = new HashMap<>();
        for (Map.Entry<String, Branch> entry : model.getBranches().entrySet()) {
            branchHashes.put(entry.getKey(), entry.getValue().getHeadCommit().getHash());
        }
        Utils.serialize(branchHashes, new File(gitletDirectory, BRANCHES));
    }
}
