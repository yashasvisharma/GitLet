package gitlet.commands;

import gitlet.Utils;
import gitlet.core.GitFile;
import gitlet.core.GitFileSet;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

public class RmCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        execute(model, parseArguments(arguments));
    }

    private static Options parseArguments(String[] arguments) throws GitletException {
        if (arguments.length != 1) {
            throw new GitletException("Arguments for Rm command invalid.");
        }
        return new Options(arguments[0]);
    }

    private void execute(GitletModel model, Options options) throws GitletException {
        GitFileSet stagedFiles = model.getStagedFiles();
        GitFile stagedFile = stagedFiles.lookupFile(options.fileName);
        boolean isStaged = stagedFile != null && stagedFile.getHash() != null;

        GitFileSet headCommitFiles = model.getCurrentBranch().getHeadCommit().getAllFiles();
        GitFile committedFile = headCommitFiles.lookupFile(options.fileName);
        boolean isCommitted = committedFile != null && committedFile.getHash() != null;

        if (!isStaged && !isCommitted) {
            throw new GitletException("No reason to remove the file.");
        }

        if (isStaged) {
            stagedFiles.removeFile(options.fileName);
        }

        // If the file  is tracked, mark it for untracking
        if (isCommitted) {
            GitFile deletedFile = new GitFile(options.fileName, null);
            stagedFiles.addFile(deletedFile);
            Utils.restrictedDelete(model.getWorkingDirectoryFile(options.fileName));
        }
    }

    private static class Options {
        final String fileName;

        private Options(String name) {
            this.fileName = name;
        }
    }
}
