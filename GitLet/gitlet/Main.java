package gitlet;

import gitlet.commands.AddCommand;
import gitlet.commands.BranchCommand;
import gitlet.commands.CheckoutCommand;
import gitlet.commands.Command;
import gitlet.commands.CommitCommand;
import gitlet.commands.FindCommand;
import gitlet.commands.GlobalLogCommand;
import gitlet.commands.InitCommand;
import gitlet.commands.LogCommand;
import gitlet.commands.MergeCommand;
import gitlet.commands.ResetCommand;
import gitlet.commands.RmBranchCommand;
import gitlet.commands.RmCommand;
import gitlet.commands.StatusCommand;
import gitlet.core.Branch;
import gitlet.core.Commit;
import gitlet.core.CommitGraph;
import gitlet.core.GitFileSet;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;
import gitlet.storage.GitletModelPersistenceManager;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Map<String, Command> COMMANDS =  new HashMap<>();
    private static final boolean DEBUG_MODE = false;

    // Static constructor to add the valid commands we support.
    static {
        COMMANDS.put("init", new InitCommand());
        COMMANDS.put("add", new AddCommand());
        COMMANDS.put("commit", new CommitCommand());
        COMMANDS.put("rm", new RmCommand());
        COMMANDS.put("log", new LogCommand());
        COMMANDS.put("global-log", new GlobalLogCommand());
        COMMANDS.put("find", new FindCommand());
        COMMANDS.put("status", new StatusCommand());
        COMMANDS.put("checkout", new CheckoutCommand());
        COMMANDS.put("branch", new BranchCommand());
        COMMANDS.put("rm-branch", new RmBranchCommand());
        COMMANDS.put("reset", new ResetCommand());
        COMMANDS.put("merge", new MergeCommand());
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("You have not provided the valid arguments yet.");
        }

        try {
            File workingDirectory = new File("").getAbsoluteFile();
            File gitletDirectory = new File(workingDirectory, ".gitlet");
            GitletModelPersistenceManager storage = new GitletModelPersistenceManager();

            GitletModel model;
            if (!gitletDirectory.exists()) {
                if ("init".equals(args[0])) {
                    model = initializeGitletRepository(workingDirectory, gitletDirectory);
                } else {
                    throw new GitletException("You don't have a valid .gitlet repository");
                }
            } else {
                model = storage.loadFromDisk(workingDirectory, gitletDirectory);
                Command command = getCommand(args[0]);
                command.execute(model, copyArguments(args));
            }

            storage.saveToDisk(model);
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            if (DEBUG_MODE) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    private static GitletModel initializeGitletRepository(File workingDirectory,
                                                          File gitletDirectory)
            throws GitletException {
        Commit newCommit = new Commit("initial commit", null, new GitFileSet());
        CommitGraph commitGraph = new CommitGraph(newCommit);
        GitFileSet stagedFiles = new GitFileSet();
        List<Branch> branches = Collections.singletonList(new Branch("master", newCommit));
        return new GitletModel(workingDirectory, gitletDirectory, commitGraph, stagedFiles,
                branches, "master");
    }

    private static Command getCommand(String arg) throws GitletException {
        Command command = COMMANDS.get(arg);
        if (command == null) {
            throw new GitletException(arg + " is not valid command!");
        }
        return command;
    }

    private static String[] copyArguments(String[] args) {
        String[] arguments = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            arguments[i - 1] = args[i];
        }
        return arguments;
    }
}
