package ly.kite.print;

/**
 * Created by deonbotha on 02/02/2014.
 */
public class KitePrintSDKException extends Exception {

    static enum ErrorCode {
        GENERIC_ERROR
    };

    private final ErrorCode code;

    public KitePrintSDKException(String message) {
        this(message, ErrorCode.GENERIC_ERROR);
    }

    public KitePrintSDKException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }
}
