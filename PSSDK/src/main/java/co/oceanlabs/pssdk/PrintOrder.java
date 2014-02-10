package co.oceanlabs.pssdk;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.oceanlabs.pssdk.address.Address;

/**
 * Created by deonbotha on 09/02/2014.
 */
public class PrintOrder {

    private Address shippingAddress;
    private String proofOfPayment;
    private String voucherCode;
    private JSONObject userData;
    private final ArrayList<PrintJob> jobs = new ArrayList<PrintJob>();

    private boolean userSubmittedForPrinting;
    private long totalBytesWritten, totalBytesExpectedToWrite;
    private AssetUploadRequest assetUploadReq;
    private List<Asset> assetsToUpload;
    private boolean assetUploadComplete;
    private SubmitPrintOrderRequest printOrderReq;
    private Date lastPrintSubmissionDate;
    private String receipt;
    private PrintOrderSubmissionListener submissionListener;
    private Exception lastPrintSubmissionError;

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setProofOfPayment(String proofOfPayment) {
        if (!proofOfPayment.startsWith("AP-") && !proofOfPayment.startsWith("PAY-")) {
            throw new IllegalArgumentException("Proof of payment must be a PayPal REST payment confirmation id or a PayPal Adaptive Payment pay key i.e. PAY-... or AP-...");
        }

        this.proofOfPayment = proofOfPayment;
    }

    public String getProofOfPayment() {
        return proofOfPayment;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setUserData(JSONObject userData) {
        this.userData = userData;
    }

    public JSONObject getUserData() {
        return userData;
    }

    JSONObject getJSONRepresentation() {
        try {
            JSONObject json = new JSONObject();
            json.put("proof_of_payment", proofOfPayment);
            if (voucherCode != null) {
                json.put("voucher", voucherCode);
            }

            JSONArray jobs = new JSONArray();
            json.put("jobs", jobs);
            for (PrintJob job : this.jobs) {
                jobs.put(job.getJSONRepresentation());
            }

            if (userData != null) {
                json.put("user_data", userData);
            }

            if (shippingAddress != null) {
                JSONObject sajson = new JSONObject();
                sajson.put("recipient_name", shippingAddress.getRecipientName());
                sajson.put("address_line_1", shippingAddress.getLine1());
                sajson.put("address_line_2", shippingAddress.getLine2());
                sajson.put("city", shippingAddress.getCity());
                sajson.put("county_state", shippingAddress.getStateOrCounty());
                sajson.put("postcode", shippingAddress.getZipOrPostalCode());
                sajson.put("country_code", shippingAddress.getCountry().getCodeAlpha3());
                json.put("shipping_address", sajson);
            }

            return json;
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public BigDecimal getCost() {
        BigDecimal cost = new BigDecimal(0);
        for (PrintJob job : jobs) {
            cost.add(job.getCost());
        }

        return cost;
    }

    List<Asset> getAssetsToUpload() {
        ArrayList<Asset> assets = new ArrayList<Asset>();
        for (PrintJob job : jobs) {
            for (Asset asset : job.getAssetsForUploading()) {
                if (!assets.contains(asset)) {
                    assets.add(asset);
                }
            }
        }
        return assets;
    }

    public int getTotalAssetsToUpload() {
        return getAssetsToUpload().size();
    }

    public boolean isPrinted() {
        return receipt != null;
    }

    public Date getLastPrintSubmissionDate() {
        return lastPrintSubmissionDate;
    }

    public Exception getLastPrintSubmissionError() {
        return lastPrintSubmissionError;
    }

    public String getReceipt() {
        return receipt;
    }

    public void addPrintJob(PrintJob job) {
        jobs.add(job);
    }

    public void removePrintJob(PrintJob job) {
        jobs.remove(job);
    }

    private boolean isAssetUploadInProgress() {
        // There may be a brief window where assetUploadReq == null whilst we asynchronously collect info about the assets
        // to upload. assetsToUpload will be non nil whilst this is happening.
        return assetsToUpload != null || assetUploadReq != null;
    }

    public void preemptAssetUpload(Context context) {
        if (isAssetUploadInProgress() || assetUploadComplete) {
            return;
        }

        startAssetUpload(context);
    }

    private void startAssetUpload(final Context context) {
        if (isAssetUploadInProgress() || assetUploadComplete) {
            throw new IllegalStateException("Asset upload should not have previously been started");
        }

        assetsToUpload = getAssetsToUpload();

        final boolean[] previousError = {false};
        final int[] outstandingLengthCallbacks = {assetsToUpload.size()};
        totalBytesWritten = 0;
        totalBytesExpectedToWrite = 0;
        for (Asset asset : assetsToUpload) {
            asset.getBytesLength(context, new AssetGetBytesLengthListener() {
                @Override
                public void onBytesLength(Asset asset, long byteLength) {
                    totalBytesExpectedToWrite += byteLength;
                    if (--outstandingLengthCallbacks[0] == 0) {
                        assetUploadReq = new AssetUploadRequest();
                        assetUploadReq.uploadAssets(assetsToUpload, context, assetUploadRequestListener);
                    }
                }

                @Override
                public void onError(Asset asset, Exception ex) {
                    if (previousError[0]) {
                        return;
                    }

                    previousError[0] = true;
                    assetUploadRequestListener.onError(null, ex);
                }
            });
        }
    }

    public void submitForPrinting(Context context, PrintOrderSubmissionListener listener) {
        if (userSubmittedForPrinting) throw new IllegalStateException("A PrintOrder can only be submitted once unless you cancel the previous submission");
        if (proofOfPayment == null) throw new IllegalArgumentException("You must provide a proofOfPayment before you can submit a print order");
        if (printOrderReq != null) throw new IllegalStateException("A PrintOrder request should not already be in progress");

        lastPrintSubmissionDate = new Date();
        userSubmittedForPrinting = true;

        this.submissionListener = listener;
        if (assetUploadComplete) {
            submitForPrinting();
        } else if (!isAssetUploadInProgress()) {
            startAssetUpload(context);
        }
    }

    public void submitForPrinting() {
        if (!userSubmittedForPrinting) throw new IllegalStateException("oops");
        if (!assetUploadComplete || isAssetUploadInProgress()) throw new IllegalStateException("Oops asset upload should be complete by now");

        // Step 2: Submit print order to the server. Print Job JSON can now reference real asset ids.
        printOrderReq = new SubmitPrintOrderRequest(this);
        printOrderReq.submitForPrinting(new SubmitPrintOrderRequestListener() {
            @Override
            public void onSubmissionComplete(SubmitPrintOrderRequest req, String orderIdReceipt) {
                receipt = orderIdReceipt;
                submissionListener.onSubmissionComplete(PrintOrder.this, orderIdReceipt);
                printOrderReq = null;
            }

            @Override
            public void onError(SubmitPrintOrderRequest req, Exception error) {
                userSubmittedForPrinting = false;
                lastPrintSubmissionError = error;
                submissionListener.onError(PrintOrder.this, error);
                printOrderReq = null;
            }
        });
    }

    public void cancelSubmissionOrPreemptedAssetUpload() {
        if (assetUploadReq != null) {
            assetUploadReq.cancelUpload();
            assetUploadReq = null;
        }

        if (printOrderReq != null) {
            printOrderReq.cancelSubmissionForPrinting();
            printOrderReq = null;
        }

        userSubmittedForPrinting = false;
    }


    private AssetUploadRequestListener assetUploadRequestListener = new AssetUploadRequestListener() {
        @Override
        public void onProgress(AssetUploadRequest req, int totalAssetsUploaded, int totalAssetsToUpload, long bytesWritten, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite) {
            totalBytesWritten += bytesWritten;
            if (userSubmittedForPrinting) {
                submissionListener.onProgress(PrintOrder.this, totalAssetsToUpload, totalAssetBytesWritten, totalAssetBytesExpectedToWrite, totalBytesWritten, totalBytesExpectedToWrite);
            }
        }

        @Override
        public void onUploadComplete(AssetUploadRequest req, List<Asset> assets) {
            if (assets.size() != assetsToUpload.size()) {
                throw new IllegalStateException(String.format("Oops there should be a 1:1 relationship between uploaded assets and submitted, currently its: %d:%d", assetsToUpload.size(), assets.size()));
            }

            for (Asset asset : assets) {
                if (!assetsToUpload.contains(asset)) {
                    throw new AssertionError("oops");
                }
            }

            // make sure all job assets have asset ids & preview urls. We need to do this because we optimize the asset upload to avoid uploading
            // assets that are considered to have duplicate contents
            for (PrintJob job : jobs) {
                for (Asset uploadedAsset : assets) {
                    for (Asset jobAsset : job.getAssetsForUploading()) {
                        if (uploadedAsset != jobAsset && uploadedAsset.equals(jobAsset)) {
                            jobAsset.markAsUploaded(uploadedAsset.getId(), uploadedAsset.getPreviewURL());
                        }
                    }
                }
            }

            // sanity check all assets are uploaded
            for (PrintJob job : jobs) {
                for (Asset a : job.getAssetsForUploading()) {
                    if (!a.isUploaded()) {
                        throw new AssertionError("oops all assets should have been uploaded");
                    }
                }
            }

            assetUploadComplete = true;
            assetsToUpload = null;
            assetUploadReq = null;
            if (userSubmittedForPrinting) {
                submitForPrinting();
            }
        }

        @Override
        public void onError(AssetUploadRequest req, Exception error) {
            assetUploadReq = null;
            if (userSubmittedForPrinting) {
                userSubmittedForPrinting = false; // allow the user to resubmit for printing
                submissionListener.onError(PrintOrder.this, error);
            }
        }
    };
}
