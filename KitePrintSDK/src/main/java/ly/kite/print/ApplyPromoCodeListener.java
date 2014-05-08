package ly.kite.print;

import java.math.BigDecimal;

/**
 * Created by deonbotha on 18/02/2014.
 */
public interface ApplyPromoCodeListener {
    void onPromoCodeApplied(PrintOrder order, BigDecimal discount);
    void onError(PrintOrder order, Exception ex);
}
