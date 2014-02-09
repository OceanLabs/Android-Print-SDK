package co.oceanlabs.pssdk;

/**
 * Created by deonbotha on 02/02/2014.
 */
public class PSPrintSDKException extends Exception {

    static enum ErrorCode {
        GENERIC_ERROR
    };

    private final ErrorCode code;

    public PSPrintSDKException(String message) {
        this(message, ErrorCode.GENERIC_ERROR);
    }

    public PSPrintSDKException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }
}
