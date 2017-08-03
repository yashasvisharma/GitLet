package gitlet.commands;

import gitlet.core.GitFile;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

import java.util.Objects;

public class AddCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        execute(model, parseArguments(arguments));
    }

    private static Options parseArguments(String[] arguments) throws GitletException {
        if (arguments.length != 1) {
            throw new GitletException("Add command needs one argument.");
        }
        return new Options(arguments[0]);
    }

    private void execute(GitletModel model, Options options) throws GitletException {
        String filename = options.filename;
        GitFile workingFile = GitFile.create(model.getWorkingDirectory(), filename);

        GitFile commitFile = model.getCurrentBranch().getHeadCommit().lookupFile(filename);
        GitFile existingFile = model.getStagedFiles().lookupFile(filename);
        if (existingFile == null) {
            existingFile = commitFile;
        }

        if (existingFile == null
                || !Objects.equals(existingFile.getHash(), workingFile.getHash())) {
            model.getBlobManager().saveFile(workingFile);
            if (commitFile == null
                    || !Objects.equals(commitFile.getHash(), workingFile.getHash())) {
                model.getStagedFiles().addFile(workingFile);
            } else {
                model.getStagedFiles().removeFile(filename);
            }
        }
    }

    private static class Options {
        final String filename;

        private Options(String filename) {
            this.filename = filename;
        }
    }
}
