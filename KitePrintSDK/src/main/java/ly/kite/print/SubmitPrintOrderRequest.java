package ly.kite.print;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by deonbotha on 09/02/2014.
 */
class SubmitPrintOrderRequest {
    private final PrintOrder printOrder;
    private BaseRequest req;

    public SubmitPrintOrderRequest(PrintOrder printOrder) {
        this.printOrder = printOrder;
    }

    public void submitForPrinting(final SubmitPrintOrderRequestListener listener) {
        assert req == null : "you can only submit a request once";

        JSONObject json = printOrder.getJSONRepresentation();
        String url = String.format("%s/v1/print", KitePrintSDK.getEnvironment().getPrintAPIEndpoint());
        req = new BaseRequest(BaseRequest.HttpMethod.POST, url, null, json.toString());
        req.start(new BaseRequest.BaseRequestListener() {
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
                            listener.onError(SubmitPrintOrderRequest.this, new KitePrintSDKException(message));
                        }
                    }
                } catch (JSONException ex) {
                    listener.onError(SubmitPrintOrderRequest.this, ex);
                }
            }

            @Override
            public void onError(Exception ex) {
                listener.onError(SubmitPrintOrderRequest.this, ex);
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
