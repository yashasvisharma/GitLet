package gitlet.commands;

import gitlet.Utils;
import gitlet.core.BlobManager;
import gitlet.core.Branch;
import gitlet.core.Commit;
import gitlet.core.GitFile;
import gitlet.core.GitFileSet;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

import java.util.HashSet;
import java.util.Set;

public class MergeCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        execute(model, parseArguments(arguments));
    }

    private static Options parseArguments(String[] arguments) throws GitletException {
        if (arguments.length != 1) {
            throw new GitletException("Arguments for Merge command invalid.");
        }
        return new Options(arguments[0]);
    }

    private void execute(GitletModel model, Options options) throws GitletException {
        if (!model.getStagedFiles().isEmpty()) {
            throw new GitletException("You have uncommitted changes.");
        }

        Branch currentBranch = model.getCurrentBranch();
        if (currentBranch.getName().equals(options.branchName)) {
            throw new GitletException("Cannot merge a branch with itself.");
        }
        Commit currentCommit = currentBranch.getHeadCommit();

        Branch givenBranch = model.getBranches().get(options.branchName);
        if (givenBranch == null) {
            throw new GitletException("A branch with that name does not exist.");
        }

        Commit givenCommit = givenBranch.getHeadCommit();
        Commit splitPointCommit = findSplitPoint(currentCommit, givenCommit);

        // Handle trivial merge cases.
        if (splitPointCommit.equals(givenCommit)) {
            throw new GitletException("Given branch is an ancestor of the current branch.");
        } else if (splitPointCommit.equals(currentCommit)) {
            currentBranch.setHeadCommit(givenBranch.getHeadCommit());
            throw new GitletException("Current branch fast-forwarded.");
        }

        if (mergeCommit(model, splitPointCommit, currentCommit, givenCommit)) {
            // Commit if the merge was clean!
            Commit newCommit = new Commit("Merged " + currentBranch.getName()
                    + " with " + givenBranch.getName() + ".",
                    model.getCurrentBranch().getHeadCommit(), model.getStagedFiles());
            model.getCommitGraph().addCommit(newCommit);
            model.getStagedFiles().clear();
            model.getCurrentBranch().setHeadCommit(newCommit);
        } else {
            throw new GitletException("Encountered a merge conflict.");
        }
    }

    /**
     * Merge commits and return true if there were not conflicts.
     */
    private boolean mergeCommit(GitletModel model, Commit base, Commit current, Commit given) {
        // First, find the changes from the base to each one of the commits.
        GitFileSet diffToCurrent = current.getAllFiles().diff(base.getAllFiles());
        GitFileSet diffToGiven = given.getAllFiles().diff(base.getAllFiles());

        // Then, obtain the conflicting changes as an intersection of both file sets.
        Set<String> filesInConflict = new HashSet<>(diffToCurrent.getFileNames());
        filesInConflict.retainAll(diffToGiven.getFileNames());

        // Stage all files that are not in conflicts.
        BlobManager blobManager = model.getBlobManager();
        GitFileSet stagedFiles = model.getStagedFiles();
        for (GitFile gitFile : diffToGiven) {
            if (!filesInConflict.contains(gitFile.getName())) {
                blobManager.restoreFile(gitFile);
                stagedFiles.addFile(gitFile);
            }
        }

        // Create a conflict file for all the files in conflict.
        for (String fileName : filesInConflict) {
            createConflictFile(model, fileName, diffToCurrent.lookupFile(fileName),
                    diffToGiven.lookupFile(fileName));
        }

        return filesInConflict.isEmpty();
    }

    private void createConflictFile(GitletModel model, String fileName, GitFile currentFile,
            GitFile givenFile) {
        String currentFileContents = currentFile.getHash() != null
                ? new String(model.getBlobManager().loadData(currentFile.getHash())) : null;
        String givenFileContents = givenFile.getHash() != null
                ? new String(model.getBlobManager().loadData(givenFile.getHash())) : null;
        String conflictFileContents = "<<<<<<< HEAD\n"
                + (currentFileContents != null ? currentFileContents + "\n" : "")
                + "=======\n"
                + (givenFileContents != null ? givenFileContents + "\n" : "")
                + ">>>>>>>";
        Utils.writeContents(model.getWorkingDirectoryFile(fileName),
                conflictFileContents.getBytes());
    }

    private Commit findSplitPoint(Commit commit1, Commit commit2) throws GitletException {
        Set<Commit> seen = new HashSet<>();
        while (commit1 != null || commit2 != null) {
            if (commit1 != null) {
                if (seen.contains(commit1))  {
                    return commit1;
                }
                seen.add(commit1);
                commit1 = commit1.getParent();
            }
            if (commit2 != null) {
                if (seen.contains(commit2))  {
                    return commit2;
                }
                seen.add(commit2);
                commit2 = commit2.getParent();
            }
        }
        throw new GitletException("Split point not found.");
    }

    private static class Options {
        final String branchName;

        private Options(String branchName) {
            this.branchName = branchName;
        }
    }
}
