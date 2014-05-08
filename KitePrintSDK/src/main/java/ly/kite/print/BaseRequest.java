package ly.kite.print;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by deonbotha on 02/02/2014.
 */
public class BaseRequest {
    public static interface BaseRequestListener {
        void onSuccess(int httpStatusCode, JSONObject json);
        void onError(Exception ex);
    }

    public static enum HttpMethod {
        POST("POST"), GET("GET"), PATCH("PATCH");

        private final String methodName;
        private HttpMethod(String method) {
            this.methodName = method;
        }
    };

    private static class HttpPatch extends HttpPost {
        public static final String METHOD_PATCH = "PATCH";

        public HttpPatch(final String url) {
            super(url);
        }

        @Override
        public String getMethod() {
            return METHOD_PATCH;
        }
    }

    private static class JSONHttpResponse {
        private Exception error;
        private int httpStatusCode;
        private JSONObject json;
    }

    private final HttpMethod method;
    private final String url;
    private final Map<String, String> headers;
    private final String body;
    private AsyncTask<Void, Void, JSONHttpResponse> requestTask;

    public BaseRequest(HttpMethod method, String url, Map<String, String> headers, String body) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
    }

    public void cancel() {
        if (this.requestTask != null) {
            this.requestTask.cancel(true);
            this.requestTask = null;
        }
    }

    public void start(final BaseRequestListener listener) {
        assert requestTask == null : "Oops a request has previously been started";

        requestTask = new AsyncTask<Void, Void, JSONHttpResponse>() {
            @Override
            protected JSONHttpResponse doInBackground(Void... voids) {
                JSONHttpResponse jsonResponse = new JSONHttpResponse();

                HttpClient httpclient = new DefaultHttpClient();
                HttpRequestBase request = null;
                if (method == HttpMethod.GET) {
                    request = new HttpGet(url);
                } else if (method == HttpMethod.POST || method == HttpMethod.PATCH) {
                    HttpPost postReq = method == HttpMethod.POST ? new HttpPost(url) : new HttpPatch(url);
                    postReq.setHeader("Content-Type", "application/json");
                    try {
                        postReq.setEntity(new StringEntity(body));
                    } catch (UnsupportedEncodingException e) {
                        jsonResponse.error = e;
                        return jsonResponse;
                    }

                    request = postReq;
                }

                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        request.setHeader(entry.getKey(), entry.getValue());
                    }
                }

                request.setHeader("Authorization", "ApiKey " + KitePrintSDK.getAPIKey() + ":");

                try {
                    HttpResponse response = httpclient.execute(request);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String line = null; (line = reader.readLine()) != null;) {
                        builder.append(line).append("\n");
                    }

                    JSONTokener t = new JSONTokener(builder.toString());
                    jsonResponse.json = new JSONObject(t);
                    jsonResponse.httpStatusCode = response.getStatusLine().getStatusCode();
                } catch (Exception e) {
                    jsonResponse.error = e;
                }

                return jsonResponse;
            }

            @Override
            protected void onPostExecute(JSONHttpResponse response) {
                if (response.error != null) {
                    listener.onError(response.error);
                } else {
                    listener.onSuccess(response.httpStatusCode, response.json);
                }
            }
        };

        requestTask.execute();
    }





}
