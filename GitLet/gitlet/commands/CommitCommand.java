package gitlet.commands;

import gitlet.core.Commit;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

public class CommitCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        execute(model, parseArguments(arguments));
    }

    private static Options parseArguments(String[] arguments) throws GitletException {
        if (arguments.length > 1) {
            throw new GitletException("Commit command needs one argument.");
        }
        if (arguments.length == 0 || arguments[0].isEmpty()) {
            throw new GitletException("Please enter a commit message.");
        }
        return new Options(arguments[0]);
    }

    private void execute(GitletModel model, Options options) throws GitletException {
        if (model.getStagedFiles().isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }

        Commit newCommit = new Commit(options.message, model.getCurrentBranch().getHeadCommit(),
                model.getStagedFiles());
        model.getCommitGraph().addCommit(newCommit);
        model.getStagedFiles().clear();
        model.getCurrentBranch().setHeadCommit(newCommit);
    }

    private static class Options {
        final String message;
        private Options(String message) {
            this.message = message;
        }
    }
}
