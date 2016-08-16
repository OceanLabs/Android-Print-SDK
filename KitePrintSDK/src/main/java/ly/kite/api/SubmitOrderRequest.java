package ly.kite.api;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ly.kite.KiteSDKException;
import ly.kite.KiteSDK;
import ly.kite.ordering.Order;
import ly.kite.util.HTTPJSONRequest;

/**
 * Created by deonbotha on 09/02/2014.
 */
public class SubmitOrderRequest
  {
  static private final String  LOG_TAG                  = "SubmitOrderRequest";

  static private final boolean DISPLAY_PRINT_ORDER_JSON = false;


    private final Order printOrder;
    private KiteAPIRequest req;

    public SubmitOrderRequest( Order printOrder) {
        this.printOrder = printOrder;
    }

    public void submitForPrinting(Context context, final IProgressListener listener) {
        assert req == null : "you can only submit a request once";

        JSONObject json = printOrder.getJSONRepresentation( context );

    if ( DISPLAY_PRINT_ORDER_JSON ) Log.d( LOG_TAG, "Print Order JSON:\n" + json.toString() );

        String url = String.format("%s/print", KiteSDK.getInstance( context ).getAPIEndpoint());
        req = new KiteAPIRequest( context, KiteAPIRequest.HttpMethod.POST, url, null, json.toString());
        req.start(new HTTPJSONRequest.IJSONResponseListener() {
            @Override
            public void onSuccess(int httpStatusCode, JSONObject json) {

        if ( DISPLAY_PRINT_ORDER_JSON )Log.d( LOG_TAG, "Print Order response JSON:\n" + json.toString() );

                try {
                    if (httpStatusCode >= 200 && httpStatusCode <= 299) {
                        String orderId = json.getString("print_order_id");
                        listener.onSubmissionComplete(SubmitOrderRequest.this, orderId);
                    } else {
                        JSONObject error = json.getJSONObject("error");
                        String message = error.getString("message");
                        String errorCode = error.getString("code");
                        if (errorCode.equalsIgnoreCase("20")) {
                            // this error code indicates an original success response for the request. It's handy to report a success in this
                            // case as it may be that the client never received the original success response.
                            String orderId = json.getString("print_order_id");
                            listener.onSubmissionComplete(SubmitOrderRequest.this, orderId);
                        } else {
                            listener.onError(SubmitOrderRequest.this, new KiteSDKException(message));
                        }
                    }
                } catch (JSONException ex) {
                    listener.onError(SubmitOrderRequest.this, ex);
                }
            }

            @Override
            public void onError(Exception exception ) {
                listener.onError(SubmitOrderRequest.this, exception );
            }
        });
    }

    public void cancelSubmissionForPrinting() {
        if (req != null) {
            req.cancel();
            req = null;
        }
    }


  public interface IProgressListener
    {
    public void onSubmissionComplete( SubmitOrderRequest req, String orderId );

    public void onError( SubmitOrderRequest req, Exception error );
    }

  }
