package co.oceanlabs.pssdk;

/**
 * Created by deonbotha on 08/02/2014.
 */
interface AssetGetBytesLengthListener {
    void onBytesLength(Asset asset, long byteLength);
    void onError(Asset asset, Exception ex);
}
