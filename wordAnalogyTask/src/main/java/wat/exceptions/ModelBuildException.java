package wat.exceptions;

public class ModelBuildException extends Exception {

    public ModelBuildException(String message) {

        super(message);
    }

    public ModelBuildException(Throwable cause) {

        super(cause);
    }
}
