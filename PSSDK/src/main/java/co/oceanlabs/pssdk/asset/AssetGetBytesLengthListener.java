package co.oceanlabs.pssdk.asset;

/**
 * Created by deonbotha on 08/02/2014.
 */
public interface AssetGetBytesLengthListener {
    void onBytesLength(Asset asset, long byteLength);
    void onError(Asset asset, Exception ex);
}
