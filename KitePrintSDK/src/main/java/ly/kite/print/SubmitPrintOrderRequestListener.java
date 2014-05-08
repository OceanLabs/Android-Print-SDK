package ly.kite.print;

/**
 * Created by deonbotha on 09/02/2014.
 */
interface SubmitPrintOrderRequestListener {
    void onSubmissionComplete(SubmitPrintOrderRequest req, String orderIdReceipt);
    void onError(SubmitPrintOrderRequest req, Exception error);
}
