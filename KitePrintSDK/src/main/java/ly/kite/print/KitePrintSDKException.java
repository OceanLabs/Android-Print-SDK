package ly.kite.print;

import java.io.Serializable;

/**
 * Created by deonbotha on 02/02/2014.
 */
public class KitePrintSDKException extends RuntimeException {

    public static enum ErrorCode {
        GENERIC_ERROR,
        TEMPLATE_NOT_FOUND
    };

    private final ErrorCode code;

    public KitePrintSDKException(String message) {
        this(message, ErrorCode.GENERIC_ERROR);
    }

    public KitePrintSDKException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
