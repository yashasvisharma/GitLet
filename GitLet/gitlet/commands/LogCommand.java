package gitlet.commands;

import gitlet.core.Commit;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

import java.text.SimpleDateFormat;

public class LogCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        execute(model, parseArguments(arguments));
    }

    private static Options parseArguments(String[] arguments) throws GitletException {
        return new Options();
    }

    private void execute(GitletModel model, Options options) throws GitletException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Commit currentCommit = model.getCurrentBranch().getHeadCommit();

        while (currentCommit != null) {
            System.out.println("===");
            System.out.println("Commit " + currentCommit.getHash());
            System.out.println(sdf.format(currentCommit.getDate()));
            System.out.println(currentCommit.getMessage() + "\n");

            currentCommit = currentCommit.getParent();
        }
    }

    private static class Options {
        private Options() { }
    }
}
