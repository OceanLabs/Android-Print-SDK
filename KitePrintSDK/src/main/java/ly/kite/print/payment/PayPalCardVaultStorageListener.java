package ly.kite.print.payment;

/**
 * Created by deonbotha on 16/02/2014.
 */
public interface PayPalCardVaultStorageListener {
    void onStoreSuccess(PayPalCard card);
    void onError(PayPalCard card, Exception ex);
}
