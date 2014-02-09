package co.oceanlabs.pssdk.asset;

import java.util.List;

/**
 * Created by deonbotha on 09/02/2014.
 */
public interface AssetUploadRequestListener {
    void onProgress(AssetUploadRequest req, int totalAssetsUploaded, int totalAssetsToUpload, long bytesWritten, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite);
    void onUploadComplete(AssetUploadRequest req, List<Asset> assets);
    void onError(AssetUploadRequest req, Exception error);
}
