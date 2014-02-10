package co.oceanlabs.pssdk;

/**
 * Created by deonbotha on 09/02/2014.
 */
public interface PrintOrderSubmissionListener {
    void onProgress(PrintOrder printOrder, int totalAssetsToUpload, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite, long totalBytesWritten, long totalBytesExpectedToWrite);
    void onSubmissionComplete(PrintOrder printOrder, String orderIdReceipt);
    void onError(PrintOrder printOrder, Exception error);
}
