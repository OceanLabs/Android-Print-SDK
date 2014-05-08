package ly.kite.print;

/**
 * Created by deonbotha on 09/02/2014.
 */
public interface PrintOrderSubmissionListener {
    void onProgress(PrintOrder printOrder, int totalAssetsUploaded, int totalAssetsToUpload, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite, long totalBytesWritten, long totalBytesExpectedToWrite);
    void onSubmissionComplete(PrintOrder printOrder, String orderIdReceipt);
    void onError(PrintOrder printOrder, Exception error);
}
