package gitlet.commands;

import gitlet.core.Branch;
import gitlet.core.Commit;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

public class BranchCommand implements Command {
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
        if (model.getBranches().get(options.branchName) != null) {
            throw new GitletException("A branch with that name already exists.");
        }
        Commit headCommit = model.getCurrentBranch().getHeadCommit();
        Branch newBranch = new Branch(options.branchName, headCommit);
        model.getBranches().put(options.branchName, newBranch);
    }

    private static class Options {
        final String branchName;

        private Options(String branchName) {
            this.branchName = branchName;
        }
    }
}
