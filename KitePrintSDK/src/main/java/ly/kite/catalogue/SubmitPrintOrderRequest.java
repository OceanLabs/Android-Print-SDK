package ly.kite.catalogue;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ly.kite.KiteSDKException;
import ly.kite.KiteSDK;
import ly.kite.util.HTTPJSONRequest;

/**
 * Created by deonbotha on 09/02/2014.
 */
class SubmitPrintOrderRequest {
    private final PrintOrder printOrder;
    private HTTPJSONRequest req;

    public SubmitPrintOrderRequest(PrintOrder printOrder) {
        this.printOrder = printOrder;
    }

    public void submitForPrinting(Context context, final SubmitPrintOrderRequestListener listener) {
        assert req == null : "you can only submit a request once";

        JSONObject json = printOrder.getJSONRepresentation();
        String url = String.format("%s/print", KiteSDK.getInstance( context ).getAPIEndpoint());
        req = new HTTPJSONRequest( context, HTTPJSONRequest.HttpMethod.POST, url, null, json.toString());
        req.start(new HTTPJSONRequest.HTTPJSONRequestListener() {
            @Override
            public void onSuccess(int httpStatusCode, JSONObject json) {
                try {
                    if (httpStatusCode >= 200 && httpStatusCode <= 299) {
                        String orderId = json.getString("print_order_id");
                        listener.onSubmissionComplete(SubmitPrintOrderRequest.this, orderId);
                    } else {
                        JSONObject error = json.getJSONObject("error");
                        String message = error.getString("message");
                        String errorCode = error.getString("code");
                        if (errorCode.equalsIgnoreCase("20")) {
                            // this error code indicates an original success response for the request. It's handy to report a success in this
                            // case as it may be that the client never received the original success response.
                            String orderId = json.getString("print_order_id");
                            listener.onSubmissionComplete(SubmitPrintOrderRequest.this, orderId);
                        } else {
                            listener.onError(SubmitPrintOrderRequest.this, new KiteSDKException(message));
                        }
                    }
                } catch (JSONException ex) {
                    listener.onError(SubmitPrintOrderRequest.this, ex);
                }
            }

            @Override
            public void onError(Exception exception ) {
                listener.onError(SubmitPrintOrderRequest.this, exception );
            }
        });
    }

    public void cancelSubmissionForPrinting() {
        if (req != null) {
            req.cancel();
            req = null;
        }
    }

}
