package ly.kite.print.payment;

/**
 * Created by deonbotha on 16/02/2014.
 */
public interface PayPalCardChargeListener {
    void onChargeSuccess(PayPalCard card, String proofOfPayment);
    void onError(PayPalCard card, Exception ex);
}
