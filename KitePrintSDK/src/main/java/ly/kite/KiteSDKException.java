package ly.kite;

/**
 * Created by deonbotha on 02/02/2014.
 */
public class KiteSDKException extends RuntimeException {

    public static enum ErrorCode {
        GENERIC_ERROR,
        TEMPLATE_NOT_FOUND
    };

    private final ErrorCode code;

    public KiteSDKException( String message ) {
        this(message, ErrorCode.GENERIC_ERROR);
    }

    public KiteSDKException( String message, ErrorCode code ) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
