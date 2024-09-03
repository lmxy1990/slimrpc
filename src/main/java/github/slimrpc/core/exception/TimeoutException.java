package github.slimrpc.core.exception;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TimeoutException extends RuntimeException {


    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Read object (Deserialization).
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Restore num.
    }

    private static final long serialVersionUID = 1L;

    public TimeoutException(String msg) {
        super(msg);
    }


}
