package ly.kite.print.payment;

import java.math.BigDecimal;

/**
 * Created by deonbotha on 17/02/2014.
 */
public interface CheckPromoCodeRequestListener {
    void onDiscount(BigDecimal discount);
    void onError(Exception ex);
}
