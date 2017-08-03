package gitlet.commands;

import gitlet.core.GitletException;
import gitlet.core.GitletModel;


public class InitCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        throw new GitletException("A gitlet version-control system already "
               + "exists in the current directory.");
    }
}
