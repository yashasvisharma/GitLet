package gitlet.commands;

import gitlet.core.GitFile;
import gitlet.core.GitFileSet;
import gitlet.core.GitletException;
import gitlet.core.GitletModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class StatusCommand implements Command {
    @Override
    public void execute(GitletModel model, String[] arguments) throws GitletException {
        printBranches(model);
        printStageArea(model);
        printModifications(model);
    }

    private void printBranches(GitletModel model) {
        List<String> branchNames = new ArrayList<>();
        for (String branchName : model.getBranches().keySet()) {
            branchNames.add(branchName);
        }
        Collections.sort(branchNames);

        System.out.println("=== Branches ===");
        for (String branchName : branchNames) {
            if (model.getCurrentBranch().getName().equals(branchName)) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }
        System.out.println();
    }

    private void printStageArea(GitletModel model) {
        List<String> stagedFileNames = new ArrayList<>();
        List<String> removedFileNames = new ArrayList<>();

        for (GitFile file : model.getStagedFiles()) {
            if (file.getHash() == null) {
                removedFileNames.add(file.getName());
            } else {
                stagedFileNames.add(file.getName());
            }
        }
        Collections.sort(stagedFileNames);
        Collections.sort(removedFileNames);

        System.out.println("=== Staged Files ===");
        for (String fileName : stagedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String fileName : removedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    private void printModifications(GitletModel model) throws GitletException {
        GitFileSet commitFiles = model.getCurrentBranch().getHeadCommit().getAllFiles();
        GitFileSet combinedRepoFiles = commitFiles.combine(model.getStagedFiles());

        List<GitFile> modifiedFiles = new ArrayList<>();
        Set<String> workingDirectoryFileNames = model.getWorkingDirectoryFileNames();
        for (GitFile repoFile : combinedRepoFiles) {
            if (workingDirectoryFileNames.contains(repoFile.getName())) {
                GitFile workingFile = GitFile.create(model.getWorkingDirectory(),
                        repoFile.getName());
                if (!workingFile.getHash().equals(repoFile.getHash())) {
                    modifiedFiles.add(workingFile);
                }
                workingDirectoryFileNames.remove(repoFile.getName());
            } else if (repoFile.getHash() != null) { // deleted
                modifiedFiles.add(new GitFile(repoFile.getName(), null));
            }
        }
        modifiedFiles.sort(Comparator.comparing(GitFile::getName));
        List<String> untrackedFiles = new ArrayList<>(workingDirectoryFileNames);
        Collections.sort(untrackedFiles);

        System.out.println("=== Modifications Not Staged For Commit ===");
        for (GitFile modifiedFile : modifiedFiles) {
            System.out.print(modifiedFile.getName());
            System.out.println(modifiedFile.getHash() != null ? " (modified)" : " (deleted)");
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String untrackedFile : untrackedFiles) {
            System.out.println(untrackedFile);
        }
    }
}
