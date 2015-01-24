package ly.kite.print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by alibros on 06/01/15.
 */
public class SyncTemplateRequest {


    private BaseRequest req;

    public SyncTemplateRequest() {

    }

    public void sync(final SyncTemplateRequestListener listener) {
        assert req == null : "you can only submit a request once";

        String url = String.format("%s/v1/template/", KitePrintSDK.getEnvironment().getPrintAPIEndpoint());
        req = new BaseRequest(BaseRequest.HttpMethod.GET, url, null, null);
        req.start(new BaseRequest.BaseRequestListener() {
            @Override
            public void onSuccess(int httpStatusCode, JSONObject json) {
                try {
                    if (httpStatusCode >= 200 && httpStatusCode <= 299) {
                        JSONArray templates = json.getJSONArray("objects");
                        ArrayList<Template> templateObjects = new ArrayList<Template>();
                        for (int i = 0; i < templates.length(); ++i) {
                            JSONObject jsonObject = templates.getJSONObject(i);
                            Template template = Template.parseTemplate(templates.getJSONObject(i));
                            templateObjects.add(template);
                        }

                        listener.onSyncComplete(templateObjects);
                    } else {
                        JSONObject error = json.getJSONObject("error");
                        String message = error.getString("message");
                        String errorCode = error.getString("code");
                        listener.onError(new KitePrintSDKException(message));

                    }
                } catch (JSONException ex) {
                    listener.onError(ex);
                }
            }
            @Override
            public void onError(Exception ex) {
                listener.onError(ex);
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
