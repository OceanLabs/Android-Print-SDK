package co.oceanlabs.pssdk;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import co.oceanlabs.pssdk.address.Address;
import co.oceanlabs.pssdk.payment.CheckPromoCodeRequestListener;
import co.oceanlabs.pssdk.payment.CheckPromoRequest;

/**
 * Created by deonbotha on 09/02/2014.
 */
public class PrintOrder implements Parcelable, Serializable {

    private static final String PERSISTED_PRINT_ORDERS_FILENAME = "print_orders";
    private static final int NOT_PERSITED = -1;

    private static final long serialVersionUID = 0L;

    private Address shippingAddress;
    private String proofOfPayment;
    private String voucherCode;
    private JSONObject userData;
    private ArrayList<PrintJob> jobs = new ArrayList<PrintJob>();

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
    private int storageIdentifier = NOT_PERSITED;

    private String promoCode;
    private BigDecimal promoCodeDiscount;

    public PrintOrder() {}

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public ArrayList<PrintJob> getJobs() {
        return jobs;
    }

    public void setProofOfPayment(String proofOfPayment) {
        if (!proofOfPayment.startsWith("AP-") && !proofOfPayment.startsWith("PAY-")) {
            throw new IllegalArgumentException("Proof of payment must be a PayPal REST payment confirmation id or a PayPal Adaptive PayPalCard pay key i.e. PAY-... or AP-...");
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
            cost = cost.add(job.getCost());
        }

        if (this.promoCodeDiscount != null) {
            cost = cost.subtract(this.promoCodeDiscount);
            if (cost.compareTo(BigDecimal.ZERO) < 0) {
                cost = BigDecimal.ZERO;
            }
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
        if (!(job instanceof PrintsPrintJob)) {
            throw new IllegalArgumentException("Currently only support PrintsPrintJobs, if any further jobs classes are added support for them must be added to the Parcelable interface in particular readTypedList must work ;)");
        }
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
        if (userSubmittedForPrinting) throw new AssertionError("A PrintOrder can only be submitted once unless you cancel the previous submission");
        if (proofOfPayment == null) throw new AssertionError("You must provide a proofOfPayment before you can submit a print order");
        if (printOrderReq != null) throw new AssertionError("A PrintOrder request should not already be in progress");

        lastPrintSubmissionDate = new Date();
        userSubmittedForPrinting = true;

        this.submissionListener = listener;
        if (assetUploadComplete) {
            submitForPrinting();
        } else if (!isAssetUploadInProgress()) {
            startAssetUpload(context);
        }
    }

    private void submitForPrinting() {
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
                submissionListener.onProgress(PrintOrder.this, totalAssetsUploaded, totalAssetsToUpload, totalAssetBytesWritten, totalAssetBytesExpectedToWrite, totalBytesWritten, totalBytesExpectedToWrite);
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
            assetsToUpload = null;
            if (userSubmittedForPrinting) {
                lastPrintSubmissionError = error;
                userSubmittedForPrinting = false; // allow the user to resubmit for printing
                submissionListener.onError(PrintOrder.this, error);
            }
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeValue(shippingAddress);
        p.writeString(proofOfPayment);
        p.writeString(voucherCode);
        String userDataString = userData == null ? null : userData.toString();
        p.writeString(userDataString);
        p.writeTypedList(jobs);
        p.writeValue(userSubmittedForPrinting);
        p.writeValue(assetUploadComplete);
        p.writeValue(lastPrintSubmissionDate);
        p.writeString(receipt);
        p.writeValue(lastPrintSubmissionError);
        p.writeInt(storageIdentifier);
        p.writeString(promoCode);
        p.writeValue(promoCodeDiscount);
    }

    private PrintOrder(Parcel p) {
        this.shippingAddress = (Address) p.readValue(Address.class.getClassLoader());
        this.proofOfPayment = p.readString();
        this.voucherCode = p.readString();
        String userDataString = p.readString();
        if (userDataString != null) {
            try {
                this.userData = new JSONObject(userDataString);
            } catch (JSONException ex) {
                throw new RuntimeException(ex); // will never happen ;)
            }
        }

        ArrayList<PrintsPrintJob> jobs = new ArrayList<PrintsPrintJob>();
        p.readTypedList(jobs, PrintsPrintJob.CREATOR);
        this.jobs.addAll(jobs);
        this.userSubmittedForPrinting = (Boolean) p.readValue(Boolean.class.getClassLoader());
        this.assetUploadComplete = (Boolean) p.readValue(Boolean.class.getClassLoader());
        this.lastPrintSubmissionDate = (Date) p.readValue(Date.class.getClassLoader());
        this.receipt = p.readString();
        this.lastPrintSubmissionError = (Exception) p.readValue(Exception.class.getClassLoader());
        this.storageIdentifier = p.readInt();
        this.promoCode = p.readString();
        this.promoCodeDiscount = (BigDecimal) p.readValue(BigDecimal.class.getClassLoader());
    }

    public static final Parcelable.Creator<PrintOrder> CREATOR
            = new Parcelable.Creator<PrintOrder>() {
        public PrintOrder createFromParcel(Parcel in) {
            return new PrintOrder(in);
        }

        public PrintOrder[] newArray(int size) {
            return new PrintOrder[size];
        }
    };

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(shippingAddress);
        out.writeObject(proofOfPayment);
        out.writeObject(voucherCode);
        String userDataString = userData == null ? null : userData.toString();
        out.writeObject(userDataString);

        out.writeInt(jobs.size());
        for (int i = 0; i < jobs.size(); ++i) {
            out.writeObject(jobs.get(i));
        }

        out.writeBoolean(userSubmittedForPrinting);
        out.writeBoolean(assetUploadComplete);
        out.writeObject(lastPrintSubmissionDate);
        out.writeObject(receipt);
        out.writeObject(lastPrintSubmissionError);
        out.writeInt(storageIdentifier);
        out.writeObject(promoCode);
        out.writeObject(promoCodeDiscount);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        shippingAddress = (Address) in.readObject();
        proofOfPayment = (String) in.readObject();
        voucherCode = (String) in.readObject();
        String userDataString = (String) in.readObject();
        if (userDataString != null) {
            try {
                this.userData = new JSONObject(userDataString);
            } catch (JSONException ex) {
                throw new RuntimeException(ex); // will never happen ;)
            }
        }

        int numJobs = in.readInt();
        jobs = new ArrayList<PrintJob>();
        for (int i = 0; i < numJobs; ++i) {
            jobs.add((PrintJob) in.readObject());
        }

        userSubmittedForPrinting = in.readBoolean();
        assetUploadComplete = in.readBoolean();
        lastPrintSubmissionDate = (Date) in.readObject();
        receipt = (String) in.readObject();
        lastPrintSubmissionError = (Exception) in.readObject();
        storageIdentifier = in.readInt();
        promoCode = (String) in.readObject();
        promoCodeDiscount = (BigDecimal) in.readObject();
    }

    /*
     * Promo code stuff
     */

    public BigDecimal getPromoCodeDiscount() {
        return promoCodeDiscount;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public CheckPromoRequest applyPromoCode(final String promoCode, final ApplyPromoCodeListener listener) {
        CheckPromoRequest req = new CheckPromoRequest();
        req.checkPromoCode(promoCode, this, new CheckPromoCodeRequestListener() {
            @Override
            public void onDiscount(BigDecimal discount) {
                if (promoCode != null && discount.compareTo(BigDecimal.ZERO) > 0) {
                    PrintOrder.this.promoCode = promoCode;
                    PrintOrder.this.promoCodeDiscount = discount;
                }

                listener.onPromoCodeApplied(PrintOrder.this, discount);
            }

            @Override
            public void onError(Exception ex) {
                listener.onError(PrintOrder.this, ex);
            }
        });

        return req;
    }

    public void clearPromoCode() {
        this.promoCode = null;
        this.promoCodeDiscount = null;
    }

    /*
     * PrintOrder history persisting methods
     */

    public void saveToHistory(Context c) {
        List<PrintOrder> currentOrders = getPrintOrderHistory(c);
        if (!isSavedInHistory()) {
            storageIdentifier = getNextStorageIdentifier(currentOrders);
        }

        ArrayList<PrintOrder> updatedOrders = new ArrayList<PrintOrder>();
        updatedOrders.add(this);
        for (PrintOrder order : currentOrders) {
            if (order.storageIdentifier != storageIdentifier) {
                updatedOrders.add(order);
            }
        }

        persistOrdersToDisk(c, updatedOrders);
    }

    public void deleteFromHistory(Context c) {
        if (!isSavedInHistory()) {
            return;
        }

        List<PrintOrder> orders = getPrintOrderHistory(c);
        Iterator<PrintOrder> iter = orders.iterator();
        while (iter.hasNext()) {
            PrintOrder o = iter.next();
            if (o.storageIdentifier == storageIdentifier) {
                iter.remove();
                break;
            }
        }

        persistOrdersToDisk(c, orders);
    }

    private void persistOrdersToDisk(Context c, List<PrintOrder> orders) {
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new BufferedOutputStream(c.openFileOutput(PERSISTED_PRINT_ORDERS_FILENAME, Context.MODE_PRIVATE)));
            os.writeObject(orders);
        } catch (Exception ex) {
            // ignore, we'll just lose this order from the history now
        } finally {
            try {
                os.close();
            } catch (Exception ex) {/* ignore */}
        }
    }

    public boolean isSavedInHistory() {
        return storageIdentifier != NOT_PERSITED;
    }

    public static int getNextStorageIdentifier(List<PrintOrder> orders) {
        int nextIdentifier = 0;
        for (int i = 0; i < orders.size(); ++i) {
            PrintOrder o = orders.get(i);
            if (nextIdentifier <= o.storageIdentifier) {
                nextIdentifier = o.storageIdentifier + 1;
            }
        }
        return nextIdentifier;
    }

    public static List<PrintOrder> getPrintOrderHistory(Context c) {
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new BufferedInputStream(c.openFileInput(PERSISTED_PRINT_ORDERS_FILENAME)));
            ArrayList<PrintOrder> orders = (ArrayList<PrintOrder>) is.readObject();
            return orders;
        } catch (FileNotFoundException ex) {
            return new ArrayList<PrintOrder>();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                is.close();
            } catch (Exception ex) { /* ignore */ }
        }
    }
}
