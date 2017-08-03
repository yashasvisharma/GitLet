package gitlet.commands;

import gitlet.core.Commit;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

public class FindCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        execute(model, parseArguments(arguments));
    }

    private static Options parseArguments(String[] arguments) throws GitletException {
        if (arguments.length != 1) {
            throw new GitletException("Find command with bad arguments.");
        }
        String message = arguments[0].toLowerCase();
        return new Options(message);
    }

    private void execute(GitletModel model, Options options) throws GitletException {
        boolean foundCommit = false;
        for (Commit commit : model.getCommitGraph().getAllCommits()) {
            if (commit.getMessage().toLowerCase().contains(options.message)) {
                System.out.println(commit.getHash());
                foundCommit = true;
            }
        }
        if (!foundCommit) {
            throw new GitletException("Found no commit with that message.");
        }
    }

    private static class Options {
        final String message;
        private Options(String message) {
            this.message = message;
        }
    }
}
