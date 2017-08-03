package gitlet.commands;

import gitlet.core.Commit;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

public class ResetCommand extends CheckoutCommand {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        execute(model, parseArguments(arguments));
    }

    private static Options parseArguments(String[] arguments) throws GitletException {
        if (arguments.length != 1) {
            throw new GitletException("Reset command needs one argument.");
        }
        return new Options(arguments[0]);
    }

    private void execute(GitletModel model, Options options) throws GitletException {
        Commit commit = model.getCommitGraph().lookupCommit(options.commitHash);
        if (commit == null) {
            throw new GitletException("No commit with that id exists.");
        }

        checkoutCommit(model, commit);
        model.getStagedFiles().clear();
        model.getCurrentBranch().setHeadCommit(commit);
    }

    private static class Options {
        private final String commitHash;

        private Options(String commitHash) {
            this.commitHash = commitHash;
        }
    }
}
