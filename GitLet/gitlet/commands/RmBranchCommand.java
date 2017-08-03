package gitlet.commands;

import gitlet.core.GitletException;
import gitlet.core.GitletModel;

import java.util.Map;

public class RmBranchCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        execute(model, parseArguments(arguments));
    }

    private static Options parseArguments(String[] arguments) throws GitletException {
        if (arguments.length != 1) {
            throw new GitletException("Branch command needs one argument.");
        }
        return new Options(arguments[0]);
    }

    private void execute(GitletModel model, Options options) throws GitletException {
        Map branches = model.getBranches();
        if (!branches.containsKey(options.branchName)) {
            throw new GitletException("A branch with that name does not exist.");
        }
        if (model.getCurrentBranch().getName().equals(options.branchName)) {
            throw new GitletException("Cannot remove the current branch.");
        }
        branches.remove(options.branchName);
    }

    private static class Options {
        final String branchName;

        private Options(String branchName) {
            this.branchName = branchName;
        }
    }
}
