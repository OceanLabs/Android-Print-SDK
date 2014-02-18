package co.oceanlabs.pssdk.payment;

import junit.framework.Assert;

import org.json.JSONObject;

import java.math.BigDecimal;

import co.oceanlabs.pssdk.BaseRequest;
import co.oceanlabs.pssdk.PSPrintSDK;
import co.oceanlabs.pssdk.PSPrintSDKException;
import co.oceanlabs.pssdk.PrintJob;
import co.oceanlabs.pssdk.PrintOrder;

/**
 * Created by deonbotha on 17/02/2014.
 */
public class CheckPromoRequest {

    private BaseRequest req;

    public void checkPromoCode(String promoCode, PrintOrder order, final CheckPromoCodeRequestListener listener) {
        if (req != null) throw new AssertionError("only one check promo code request can be in progress at a time");

        StringBuilder templateCostBreakdown = new StringBuilder();
        for (PrintJob j : order.getJobs()) {
            if (templateCostBreakdown.length() > 0) templateCostBreakdown.append(",");
            templateCostBreakdown.append(String.format("%s:%s", j.getTemplateName(), j.getCost().toString()));
        }

        String url = String.format("%s/v1/promo_code/check?code=%s&templates=%s&currency=GBP", PSPrintSDK.getEnvironment().getPrintAPIEndpoint(), promoCode, templateCostBreakdown.toString());
        req = new BaseRequest(BaseRequest.HttpMethod.GET, url, null, null);
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
                        listener.onError(new PSPrintSDKException(message));
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
