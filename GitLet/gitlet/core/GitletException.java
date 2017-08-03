package gitlet.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@SuppressWarnings("unused")

public class GitletException extends Exception {
    public GitletException() {
    }

    public GitletException(String message) {
        super(message);
    }

    public GitletException(String message, Throwable cause) {
        super(message, cause);
    }

    public void handleExceptions() throws GitletException {
        try {
            new FileOutputStream("myfile.txt");
        } catch (FileNotFoundException exception) {
            throw new GitletException("Oh damn! Failed to open my file",
                    exception); // Message, Exception
        }
    }


}
