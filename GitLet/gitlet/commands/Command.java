package gitlet.commands;

import gitlet.core.GitletException;
import gitlet.core.GitletModel;

@SuppressWarnings("unused")

/* A component that implements a Command that operates on a GitletModel.
 * The execution of the command is configured by a collection of arguments.
 */
public interface Command {
    void execute(GitletModel model, String[] arguments) throws GitletException;
}







