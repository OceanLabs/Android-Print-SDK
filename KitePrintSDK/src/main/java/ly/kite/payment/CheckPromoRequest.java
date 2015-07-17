package ly.kite.payment;

import android.content.Context;

import org.json.JSONObject;

import java.math.BigDecimal;

import ly.kite.print.BaseRequest;
import ly.kite.KiteSDK;
import ly.kite.KiteSDKException;
import ly.kite.print.PrintJob;
import ly.kite.print.PrintOrder;

/**
 * Created by deonbotha on 17/02/2014.
 */
public class CheckPromoRequest {

    private BaseRequest req;

    public void checkPromoCode( Context context, String promoCode, PrintOrder order, final CheckPromoCodeRequestListener listener) {
        if (req != null) throw new AssertionError("only one check promo code request can be in progress at a time");

        StringBuilder templateCostBreakdown = new StringBuilder();

        for (PrintJob j : order.getJobs()) {
            if (templateCostBreakdown.length() > 0) templateCostBreakdown.append(",");
            templateCostBreakdown.append(String.format("%s:%s", j.getProductId(), j.getCost(order.getCurrencyCode()).toString()));
        }

        String url = String.format("%s/promo_code/check?code=%s&templates=%s&currency=%s", KiteSDK.getInstance( context ).getPrintAPIEndpoint(), promoCode, templateCostBreakdown.toString(), order.getCurrencyCode());
        req = new BaseRequest(context, BaseRequest.HttpMethod.GET, url, null, null);
        req.start(new BaseRequest.BaseRequestListener() {
            @Override
            public void onSuccess(int httpStatusCode, JSONObject json) {
                try {
                    if (httpStatusCode >= 200 && httpStatusCode <= 299) {
                        BigDecimal discount = new BigDecimal(json.getDouble("discount"));
                        listener.onDiscount(discount);
                    } else {
                        JSONObject error = json.getJSONObject("error");
                        String message = error.getString("message");
                        listener.onError(new KiteSDKException(message));
                    }
                } catch (Exception ex) {
                    listener.onError(ex);
                }
            }

            @Override
            public void onError(Exception ex) {
                listener.onError(ex);
            }
        });
    }

    public void cancel() {
        if (req != null) {
            req.cancel();
            req = null;
        }
    }

}
