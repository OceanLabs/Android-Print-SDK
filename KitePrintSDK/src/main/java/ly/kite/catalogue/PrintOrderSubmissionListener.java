package ly.kite.catalogue;

/**
 * Created by deonbotha on 09/02/2014.
 */
public interface PrintOrderSubmissionListener {
    void onProgress(PrintOrder printOrder, int primaryProgressPercent, int secondaryProgressPercent );
    void onSubmissionComplete(PrintOrder printOrder, String orderIdReceipt);
    void onError(PrintOrder printOrder, Exception error);
}
