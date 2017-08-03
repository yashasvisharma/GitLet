package gitlet.commands;

import gitlet.core.Branch;
import gitlet.core.Commit;
import gitlet.core.GitFile;
import gitlet.core.GitFileSet;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

import java.util.Set;

public class CheckoutCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        execute(model, parseArguments(arguments));
    }

    private static Options parseArguments(String[] arguments) throws GitletException {
        if (arguments.length == 1) {
            return new Options(arguments[0]);
        } else if (arguments.length == 2) {
            if (!"--".equals(arguments[0])) {
                throw  new GitletException("Incorrect operands.");
            }
            return new Options(null, arguments[1]);
        } else if (arguments.length == 3) {
            if (!"--".equals(arguments[1])) {
                throw  new GitletException("Incorrect operands.");
            }
            return new Options(arguments[0], arguments[2]);
        } else {
            throw new GitletException("too many arguments");
        }
    }

    private void execute(GitletModel model, Options options) throws GitletException {
        if (options.branchName == null) {
            checkoutFile(model, options.commitHash, options.fileName);
        } else {
            checkoutBranch(model, options.branchName);
        }
    }

    private void checkoutBranch(GitletModel model, String branchName) throws GitletException {
        if (branchName.equals(model.getCurrentBranch().getName())) {
            throw new GitletException("No need to checkout the current branch.");
        }
        Branch branch = model.getBranches().get(branchName);
        if (branch == null) {
            throw new GitletException("No such branch exists.");
        }

        checkoutCommit(model, branch.getHeadCommit());

        // Update current branch
        model.setCurrentBranch(branchName);
    }

    private void checkoutFile(GitletModel model, String commitHash, String fileName)
            throws GitletException {
        Commit commit = commitHash != null ? model.getCommitGraph().lookupCommit(commitHash)
                : model.getCurrentBranch().getHeadCommit();
        if (commit == null) {
            throw new GitletException("No commit with that id exists.");
        }

        GitFile file = commit.lookupFile(fileName);
        if (file == null) {
            throw new GitletException("File does not exist in that commit.");
        }
        model.getBlobManager().restoreFile(file);
    }

    protected void checkoutCommit(GitletModel model, Commit commit) throws GitletException {
        // Obtain the files to modify in the checkout.
        GitFileSet destCommitFiles = commit.getAllFiles();
        GitFileSet currentCommitFiles = model.getCurrentBranch().getHeadCommit().getAllFiles();
        GitFileSet diffFileSet = destCommitFiles.diff(currentCommitFiles);

        // Fail if any untracked files in the workspace will be overwritten.
        Set<String> workingDirectoryFileNames = model.getWorkingDirectoryFileNames();
        for (GitFile destFile : diffFileSet) {
            String fileName = destFile.getName();
            if (currentCommitFiles.lookupFile(fileName) == null
                    && workingDirectoryFileNames.contains(fileName)) {
                throw new GitletException("There is an untracked file in the way; "
                        + "delete it or add it first.");
            }
        }

        // Finally, update the workspace.
        model.getBlobManager().restoreFiles(diffFileSet);
    }

    private static class Options {
        private final String commitHash;
        private final String branchName;
        private final String fileName;

        private Options(String branchName) {
            this.branchName = branchName;
            this.commitHash = null;
            this.fileName = null;
        }

        Options(String commitHash, String fileName) {
            this.commitHash = commitHash;
            this.fileName = fileName;
            this.branchName = null;
        }
    }
}
