package ly.kite.print;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by deonbotha on 07/02/2014.
 */
class AssetUploadRequest {

    private boolean cancelled;
    private BaseRequest registerImageURLAssetsReq, signReq;
    private int numOutstandingAsyncOpertions = 0;
    private boolean notifiedUploadListenerOfOutcome = false;

    public void cancelUpload() {
        cancelled = true;
        notifiedUploadListenerOfOutcome = false;
        numOutstandingAsyncOpertions = 0;
        if (registerImageURLAssetsReq != null) {
            registerImageURLAssetsReq.cancel();
            registerImageURLAssetsReq = null;
        }

        if (signReq != null) {
            signReq.cancel();
            signReq = null;
        }
    }

    public void uploadAsset(Asset asset, Context context, AssetUploadRequestListener uploadListener) {
        ArrayList<Asset> list = new ArrayList<Asset>();
        list.add(asset);
        uploadAssets(list, context, uploadListener);
    }

    public void uploadAssets(final List<Asset> assets, Context context, final AssetUploadRequestListener uploadListener) {
        ArrayList<Asset> urlsToRegister = new ArrayList<Asset>();
        ArrayList<Asset> assetsToUpload = new ArrayList<Asset>();

        for (Asset asset : assets) {
            if (asset.getType() == Asset.AssetType.REMOTE_URL) {
                urlsToRegister.add(asset);
            } else {
                assetsToUpload.add(asset);
            }
        }

        final AssetUploadOrRegisterListener listener = new AssetUploadOrRegisterListener() {
            @Override
            public void onProgress(int totalAssetsUploaded, int totalAssetsToUpload, long bytesWritten, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite) {
                if (cancelled || notifiedUploadListenerOfOutcome) return;
                uploadListener.onProgress(AssetUploadRequest.this, totalAssetsUploaded, totalAssetsToUpload, bytesWritten, totalAssetBytesWritten, totalAssetBytesExpectedToWrite);
            }

            @Override
            public void onSuccess() {
                if (cancelled || notifiedUploadListenerOfOutcome) return;
                completedOutstandingAsyncOperation(assets, null, uploadListener);
            }

            @Override
            public void onError(Exception ex) {
                if (cancelled || notifiedUploadListenerOfOutcome) return;
                completedOutstandingAsyncOperation(assets, ex, uploadListener);
            }
        };

        if (assetsToUpload.size() > 0) {
            ++numOutstandingAsyncOpertions;
            uploadAssets(assetsToUpload, context, listener);
        }

        if (urlsToRegister.size() > 0) {
            ++numOutstandingAsyncOpertions;
            registerImageURLs(urlsToRegister, context, listener);
        }
    }

    private void completedOutstandingAsyncOperation(List<Asset> assets, Exception ex, AssetUploadRequestListener listener) {
        if (cancelled || notifiedUploadListenerOfOutcome) {
            return;
        }

        if (ex != null) {
            notifiedUploadListenerOfOutcome = true;
            listener.onError(this, ex);
            return;
        }

        if (--numOutstandingAsyncOpertions == 0) {
            notifiedUploadListenerOfOutcome = true;
            assert ex == null : "errors should be covered above";
            listener.onUploadComplete(this, assets);
        }
    }

    private void getSignedS3UploadRequestURLs(final List<Asset> assets, Context context, final SignS3UploadsRequestListener listener) {
        StringBuilder mimeTypes = new StringBuilder();
        for (Asset a : assets) {
            if (mimeTypes.length() > 0) {
                mimeTypes.append(",");
            }

            mimeTypes.append(a.getMimeType(context).getMimeTypeString());
        }

        String url = String.format("%s/v1/asset/sign/?mime_types=%s&client_asset=true", KitePrintSDK.getEnvironment().getPrintAPIEndpoint(), mimeTypes.toString());
        registerImageURLAssetsReq = new BaseRequest(BaseRequest.HttpMethod.GET, url, null, (String) null);
        registerImageURLAssetsReq.start(new BaseRequest.BaseRequestListener() {
            @Override
            public void onSuccess(int httpStatusCode, JSONObject json) {
                if (cancelled || notifiedUploadListenerOfOutcome) return;

                try {
                    if (httpStatusCode != 200) {
                        JSONObject error = json.getJSONObject("error");
                        String message = error.getString("message");
                        listener.onError(new IllegalStateException(message));
                        return;
                    }

                    JSONArray signedUploadReqURLs = json.getJSONArray("signed_requests");
                    JSONArray assetS3PreviewURLs = json.getJSONArray("urls");
                    JSONArray assetIds = json.getJSONArray("asset_ids");

                    if (assets.size() != signedUploadReqURLs.length() || signedUploadReqURLs.length() != assetS3PreviewURLs.length() || assetS3PreviewURLs.length() != assetIds.length()) {
                        listener.onError(new IllegalStateException(String.format("Only got sign %d/%d sign requests", signedUploadReqURLs.length(), assets.size())));
                        return;
                    }

                    ArrayList<SignedS3RequestUploadDetails> details = new ArrayList<SignedS3RequestUploadDetails>();
                    for (int i = 0; i < signedUploadReqURLs.length(); ++i) {
                        SignedS3RequestUploadDetails d = new SignedS3RequestUploadDetails();
                        d.signedS3UploadReqURL = new URL(signedUploadReqURLs.getString(i));
                        d.s3AssetPreviewURL = new URL(assetS3PreviewURLs.getString(i));
                        d.assetId = assetIds.getLong(i);
                        d.asset = assets.get(i);
                        details.add(d);
                    }

                    listener.onSuccess(details);
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

    private void uploadAssetToS3(Context context, final SignedS3RequestUploadDetails details, final byte[] bytes, final UploadToS3Listener listener) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("Attempting to kick off asset upload on a thread that is not the main thread");
        }

        final String mimeType = details.asset.getMimeType(context).getMimeTypeString();
        AsyncTask<Void, Void, Exception> uploadTask = new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... voids) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPut request = new HttpPut(details.signedS3UploadReqURL.toString());
                request.setHeader("Content-Type", mimeType);
                request.setHeader("x-amz-acl", "public-read");
                request.setEntity(new ByteArrayEntity(bytes));

                try {
                    HttpResponse response = httpclient.execute(request);
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode >= 200 && statusCode <= 299) {
                        return null;
                    } else {
                        return new IllegalStateException("Failed to upload asset to amazon s3 with status code: " + statusCode);
                    }
                } catch (Exception e) {
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Exception ex) {
                if (ex != null) {
                    listener.onError(ex);
                } else {
                    listener.onProgress(bytes.length, bytes.length, bytes.length); // TODO: do proper upload progress tracking rather than one off callback on completion
                    listener.onUploadComplete();
                }
            }
        };

        uploadTask.execute();
    }

    private void uploadAssetsToS3(final List<SignedS3RequestUploadDetails> remainingAssetsToUpload, final List<Asset> uploadedAssetAccumulator, final Context context, final AssetUploadOrRegisterListener listener) {
        if (cancelled || notifiedUploadListenerOfOutcome) return;

        final SignedS3RequestUploadDetails assetToUploadDetails = remainingAssetsToUpload.remove(0);
        final int totalAssetsToUpload = uploadedAssetAccumulator.size() + remainingAssetsToUpload.size() + 1;
        assetToUploadDetails.asset.getBytes(context, new AssetGetBytesListener() {
            @Override
            public void onBytes(Asset asset, byte[] bytes) {
                if (cancelled || notifiedUploadListenerOfOutcome) return;

                listener.onProgress(uploadedAssetAccumulator.size(), totalAssetsToUpload, 0, 0, bytes.length);
                uploadAssetToS3(context, assetToUploadDetails, bytes, new UploadToS3Listener() {
                    @Override
                    public void onProgress(long bytesWritten, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite) {
                        if (cancelled || notifiedUploadListenerOfOutcome) return;
                        listener.onProgress(uploadedAssetAccumulator.size(), totalAssetsToUpload, bytesWritten, totalAssetBytesWritten, totalAssetBytesExpectedToWrite);
                    }

                    @Override
                    public void onUploadComplete() {
                        if (cancelled || notifiedUploadListenerOfOutcome) return;

                        Asset asset = assetToUploadDetails.asset;
                        asset.markAsUploaded(assetToUploadDetails.assetId, assetToUploadDetails.s3AssetPreviewURL);
                        uploadedAssetAccumulator.add(asset);
                        if (remainingAssetsToUpload.size() == 0) {
                            listener.onSuccess();
                        } else {
                            uploadAssetsToS3(remainingAssetsToUpload, uploadedAssetAccumulator, context, listener);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        if (cancelled || notifiedUploadListenerOfOutcome) return;
                        listener.onError(ex);
                    }
                });
            }

            @Override
            public void onError(Asset asset, Exception ex) {
                if (cancelled || notifiedUploadListenerOfOutcome) return;
                listener.onError(ex);
            }
        });
    }

    private void uploadAssets(final List<Asset> assets, final Context context, final AssetUploadOrRegisterListener listener) {
        getSignedS3UploadRequestURLs(assets, context, new SignS3UploadsRequestListener() {

            @Override
            public void onSuccess(ArrayList<SignedS3RequestUploadDetails> details) {
                uploadAssetsToS3(details, new ArrayList<Asset>(), context, listener);
            }

            @Override
            public void onError(Exception ex) {
                listener.onError(ex);
            }
        });
    }

    private void registerImageURLs(final List<Asset> assets, Context context, final AssetUploadOrRegisterListener listener) {
        int c = 0;
        JSONObject jsonBody = new JSONObject();
        JSONArray objects = new JSONArray();
        try {
            jsonBody.put("objects", objects);
            for (Asset asset : assets) {
                if (asset.getType() == Asset.AssetType.REMOTE_URL) {
                    ++c;
                    JSONObject o = new JSONObject();
                    o.put("url", asset.getRemoteURL().toString());
                    o.put("client_asset", true);
                    o.put("mime_type", asset.getMimeType(context).getMimeTypeString());
                    objects.put(o);
                }
            }
        } catch (JSONException ex) {
            listener.onError(ex);
            return;
        }

        final int expectedRegisteredAssetCount = c;

        String url = String.format("%s/v1/asset/", KitePrintSDK.getEnvironment().getPrintAPIEndpoint());
        registerImageURLAssetsReq = new BaseRequest(BaseRequest.HttpMethod.PATCH, url, null, jsonBody.toString());
        registerImageURLAssetsReq.start(new BaseRequest.BaseRequestListener() {
            @Override
            public void onSuccess(int httpStatusCode, JSONObject json) {
                int registeredAssetCount = 0;
                try {
                    if (httpStatusCode >= 200 && httpStatusCode <= 299) {
                        JSONArray objects = json.getJSONArray("objects");
                        for (int i = 0; i < objects.length(); ++i) {
                            JSONObject o = objects.getJSONObject(i);
                            long assetId = o.getLong("asset_id");
                            URL previewURL = new URL(o.getString("url"));

                            for (Asset asset : assets) {
                                if (asset.getType() == Asset.AssetType.REMOTE_URL && asset.getRemoteURL().equals(previewURL)) {
                                    asset.markAsUploaded(assetId, previewURL);
                                    ++registeredAssetCount;
                                }
                            }
                        }

                        if (registeredAssetCount == expectedRegisteredAssetCount) {
                            listener.onSuccess();
                        } else {
                            listener.onError(new IllegalStateException(String.format("Only registered %d/%d image URLs with the asset endpoint", registeredAssetCount, expectedRegisteredAssetCount)));
                        }
                    } else {
                        JSONObject error = json.getJSONObject("error");
                        String message = error.getString("message");
                        listener.onError(new IllegalStateException(message));
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

    private static class SignedS3RequestUploadDetails {
        URL signedS3UploadReqURL;
        URL s3AssetPreviewURL;
        long assetId;
        Asset asset;
    }

    private static interface AssetUploadOrRegisterListener {
        void onProgress(int totalAssetsUploaded, int totalAssetsToUpload, long bytesWritten, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite);
        void onSuccess();
        void onError(Exception ex);
    }

    private static interface SignS3UploadsRequestListener {
        void onSuccess(ArrayList<SignedS3RequestUploadDetails> details);
        void onError(Exception ex);
    }

    private static interface UploadToS3Listener {
        void onProgress(long bytesWritten, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite);
        void onUploadComplete();
        void onError(Exception ex);
    }
}
