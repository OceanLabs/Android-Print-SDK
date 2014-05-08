package ly.kite.print;

/**
 * Created by deonbotha on 08/02/2014.
 */
interface AssetGetBytesListener {
    void onBytes(Asset asset, byte[] bytes);
    void onError(Asset asset, Exception ex);
}
