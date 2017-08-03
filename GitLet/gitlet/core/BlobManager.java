package gitlet.core;

import gitlet.Utils;

import java.io.File;

/*
BlobManager - a component that manages file blobs on disk (commits, files...)
Blobs are not loaded in memory - they are on disk and looked up on demand.
Manages a directory of blobs and maps SHAs to files.
Operations:
saveFile(filename) - copies a file into the blob directory and returns its SHA (to use on commit)
restoreFile(filename, SHA) - copies a file from the blob directory to a given location
    (to use on checkout)
saveFile(byte[]) - creates a blob from a byte array and returns a SHA (may be used for
    storage of commits)
loadData(SHA) - reads a blob to memory as a byte array
 */
public class BlobManager {
    private final File workDirectory;
    private final File blobDirectory;

    /**
     *
     * @param workDirectory: gitlet directory
     * @param gitletDirectory: directory for the gitlet repository
     */
    public BlobManager(File workDirectory, File gitletDirectory) {
        this.workDirectory = workDirectory;
        this.blobDirectory = new File(gitletDirectory, "objects");
        if (!this.blobDirectory.exists()) {
            this.blobDirectory.mkdirs();
        }
    }

    /**
     * It creates a new copy in the blob directory of a file from
     * the current working directory.
     * @param file - file in the working directory
     * @return Hash of the created file
     */
    public String saveFile(GitFile file) throws GitletException {
        File newFile = new File(this.workDirectory, file.getName());
        byte[] fileData = Utils.readContents(newFile);
        String hash = saveData(fileData);
        if (!hash.equals(file.getHash())) {
            throw new GitletException("File hash mismatch when saving file.");
        }
        return hash;
    }

    /**
     * Creates a new file in the working directory given the
     * filename and hash of a file in the Blob Directory.
     * @param file - file you want to create in the working directory
     */
    public void restoreFile(GitFile file) {
        File newFile = new File(this.workDirectory, file.getName());
        if (file.getHash() == null) {
            newFile.delete();
        } else {
            byte[] fileData = loadData(file.getHash());
            Utils.writeContents(newFile, fileData);
        }
    }

    /**
     * Restores all the files in the given set.
     */
    public void restoreFiles(GitFileSet files) {
        for (GitFile file : files) {
            restoreFile(file);
        }
    }

    /**
     * Creates a new file into the blob directory given
     * an arbitrary data array.
     * @param data - array of bytes to be written to disk
     * @return Hash of the created Blob
     */
    public String saveData(byte[] data) {
        String hash = Utils.sha1(data);
        File newBlobFile = new File(this.blobDirectory, hash); // Use the hash as filename
        Utils.writeContents(newBlobFile, data);
        return hash;
    }

    public byte[] loadData(String hash) {
        File blobFile = new File(this.blobDirectory, hash);
        return Utils.readContents(blobFile);
    }
}
