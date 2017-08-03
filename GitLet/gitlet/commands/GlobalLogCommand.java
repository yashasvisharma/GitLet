package gitlet.commands;

import gitlet.core.Commit;
import gitlet.core.CommitGraph;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

import java.text.SimpleDateFormat;

public class GlobalLogCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        execute(model, parseArguments(arguments));
    }

    private static Options parseArguments(String[] arguments) throws GitletException {
        return new Options();
    }

    private void execute(GitletModel model, Options options) throws GitletException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CommitGraph commitGraph = model.getCommitGraph();

        for (Commit commit : commitGraph.getAllCommits()) {
            System.out.println("===");
            System.out.println("Commit " + commit.getHash());
            System.out.println(sdf.format(commit.getDate()));
            System.out.println(commit.getMessage() + "\n");
        }
    }

    private static class Options {
        private Options() {
        }
    }
}
