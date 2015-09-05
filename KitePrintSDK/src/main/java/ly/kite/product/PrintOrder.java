package ly.kite.product;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import ly.kite.address.Address;
import ly.kite.pricing.OrderPricing;

/**
 * Created by deonbotha on 09/02/2014.
 */
public class PrintOrder implements Parcelable /* , Serializable */
    {

    private static final String PERSISTED_PRINT_ORDERS_FILENAME = "print_orders";
    private static final int NOT_PERSITED = -1;

    private static final long serialVersionUID = 0L;

    private Address shippingAddress;
    private String proofOfPayment;
    private String voucherCode;
    private JSONObject userData;
    private ArrayList<PrintJob> jobs = new ArrayList<PrintJob>();

    private boolean userSubmittedForPrinting;
    //private long totalBytesWritten, totalBytesExpectedToWrite;
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

    private OrderPricing  mOrderPricing;

    //MultipleCurrencyAmount  mPromoCodeDiscount;
    //private BigDecimal promoCodeDiscount;
    private String statusNotificationEmail;
    private String statusNotificationPhone;
    //private String currencyCode = null;

    //private MultipleCurrencyAmount mTotalCost;

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

    public void setNotificationEmail(String receiptEmail) {
        this.statusNotificationEmail = receiptEmail;
    }

    public String getNotificationEmail() {
        return statusNotificationEmail;
    }

    public void setNotificationPhoneNumber(String statusNotificationPhone) {
        this.statusNotificationPhone = statusNotificationPhone;
    }

    public String getNotificationPhoneNumber() {
        return statusNotificationPhone;
    }

    public JSONObject getUserData() {
        return userData;
    }

    JSONObject getJSONRepresentation() {
        try {
            JSONObject json = new JSONObject();
            if (proofOfPayment != null)
            {
                json.put("proof_of_payment", proofOfPayment);
            }
            else{
                json.put("proof_of_payment","");
            }

            json.put("receipt_email", statusNotificationEmail);
            if (promoCode != null) {
                json.put("promo_code", promoCode);
            }

            JSONArray jobs = new JSONArray();
            json.put("jobs", jobs);
            for (PrintJob job : this.jobs) {
                jobs.put(job.getJSONRepresentation());
            }

            if (userData != null) {
                json.put("user_data", userData);
            }

            if (getOrderPricing() != null) {
                SingleCurrencyAmount orderCost = getOrderPricing().getTotalCost().getDefaultAmountWithFallback();
                // construct customer payment object in a round about manner to guarantee 2dp amount value
                StringBuilder builder = new StringBuilder();
                builder.append("{");
                builder.append("\"currency\": \"").append(orderCost.getCurrencyCode()).append("\"").append(",");
                builder.append(String.format("\"amount\": %.2f",  orderCost.getAmount().floatValue()));
                builder.append("}");
                JSONObject customerPayment = new JSONObject(builder.toString());
                json.put("customer_payment", customerPayment);
            }

            if (shippingAddress != null) {
                JSONObject sajson = new JSONObject();
                sajson.put("recipient_name", shippingAddress.getRecipientName());
                sajson.put("address_line_1", shippingAddress.getLine1());
                sajson.put("address_line_2", shippingAddress.getLine2());
                sajson.put("city", shippingAddress.getCity());
                sajson.put("county_state", shippingAddress.getStateOrCounty());
                sajson.put("postcode", shippingAddress.getZipOrPostalCode());
                sajson.put("country_code", shippingAddress.getCountry().iso3Code());
                json.put("shipping_address", sajson);
            }

            return json;
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Set<String> getCurrenciesSupported() {
        Set<String> supported = null;
        for (PrintJob job : jobs) {
            Set<String> supported2 = job.getCurrenciesSupported();
            if (supported == null) {
                supported = supported2;
            } else {
                supported.retainAll(supported2);
            }
        }

        return supported == null ? Collections.EMPTY_SET : supported;
    }

    /*****************************************************
     *
     * Returns a string representation of this order as a
     * basket, in the form:
     *
     *   <template-id>:<quantity>[,<template-id>:<quantity> ...]
     *
     *****************************************************/
    public void toBasketString( StringBuilder stringBuilder )
      {
      String separatorString = "";

      for ( PrintJob job : jobs )
        {
        stringBuilder
                .append( separatorString )
                .append( job.getProductId() )
                .append( ":")
                .append( String.valueOf( job.getQuantity() ) );

        separatorString = ",";
        }
      }


    /*****************************************************
     *
     * Returns a string representation of this order as a
     * basket, in the form:
     *
     *   <template-id>:<quantity>[,<template-id>:<quantity> ...]
     *
     *****************************************************/
    public String toBasketString()
      {
      StringBuilder basketStringBuilder = new StringBuilder();

      toBasketString( basketStringBuilder );

      return ( basketStringBuilder.toString() );
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
        if (!(job instanceof PrintsPrintJob || job instanceof PostcardPrintJob )) {
            throw new IllegalArgumentException("Currently only support PrintsPrintJobs & PostcardPrintJob, if any further jobs " +
                    "classes are added support for them must be added to the Parcelable interface in particular readTypedList must work ;)");
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

        assetUploadReq = new AssetUploadRequest( context );
        assetUploadReq.uploadAssets( context, assetsToUpload, new MyAssetUploadRequestListener( context ) );
    }

    public void submitForPrinting(Context context, PrintOrderSubmissionListener listener) {
        if (userSubmittedForPrinting) throw new AssertionError("A PrintOrder can only be submitted once unless you cancel the previous submission");
        //if (proofOfPayment == null) throw new AssertionError("You must provide a proofOfPayment before you can submit a print order");
        if (printOrderReq != null) throw new AssertionError("A PrintOrder request should not already be in progress");

        lastPrintSubmissionDate = new Date();
        userSubmittedForPrinting = true;

        this.submissionListener = listener;
        if (assetUploadComplete) {
            submitForPrinting( context );
        } else if (!isAssetUploadInProgress()) {
            startAssetUpload(context);
        }
    }

    private void submitForPrinting( Context context ) {
        if (!userSubmittedForPrinting) throw new IllegalStateException("oops");
        if (!assetUploadComplete || isAssetUploadInProgress()) throw new IllegalStateException("Oops asset upload should be complete by now");

        // Step 2: Submit print order to the server. Print Job JSON can now reference real asset ids.
        printOrderReq = new SubmitPrintOrderRequest(this);
        printOrderReq.submitForPrinting( context, new SubmitPrintOrderRequestListener() {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeValue( shippingAddress );
        p.writeString(proofOfPayment);
        p.writeString( voucherCode );
        String userDataString = userData == null ? null : userData.toString();
        p.writeString( userDataString );

        p.writeInt( jobs.size() );
        for (PrintJob job : jobs) {
            if (job instanceof PostcardPrintJob) {
                p.writeInt(1);
                job.writeToParcel(p, flags);
            } else {
                p.writeInt(0);
                job.writeToParcel(p, flags);
            }
        }

        p.writeValue(userSubmittedForPrinting);
        p.writeValue(assetUploadComplete);
        p.writeValue( lastPrintSubmissionDate );
        p.writeString( receipt );
        p.writeSerializable( lastPrintSubmissionError );
        p.writeInt( storageIdentifier );
        p.writeString( promoCode );
        p.writeParcelable( mOrderPricing, flags );
        p.writeString( statusNotificationEmail );
        p.writeString( statusNotificationPhone );
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

        int numJobs = p.readInt();
        for (int i = 0; i < numJobs; ++i) {
            boolean postcardJob = p.readInt() == 1;
            if (postcardJob) {
                PostcardPrintJob job = PostcardPrintJob.CREATOR.createFromParcel(p);
                this.jobs.add(job);
            } else {
                PrintsPrintJob job = PrintsPrintJob.CREATOR.createFromParcel(p);
                this.jobs.add(job);
            }
        }

        this.userSubmittedForPrinting = (Boolean) p.readValue(Boolean.class.getClassLoader());
        this.assetUploadComplete = (Boolean) p.readValue(Boolean.class.getClassLoader());
        this.lastPrintSubmissionDate = (Date) p.readValue( Date.class.getClassLoader() );
        this.receipt = p.readString();
        this.lastPrintSubmissionError = (Exception) p.readSerializable();
        this.storageIdentifier = p.readInt();
        this.promoCode = p.readString();
        mOrderPricing = (OrderPricing)p.readParcelable( OrderPricing.class.getClassLoader() );
        this.statusNotificationEmail = p.readString();
        this.statusNotificationPhone = p.readString();
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


    /*
     * Promo code stuff
     */

    public void setPromoCode( String promoCode )
        {
        this.promoCode = promoCode;
        }

    public String getPromoCode() {
        return promoCode;
    }

    public void clearPromoCode() {
        this.promoCode = null;
        mOrderPricing = null;
    }

    public void setOrderPricing( OrderPricing orderPricing )
      {
      mOrderPricing = orderPricing;
      }

    public OrderPricing getOrderPricing()
      {
      return ( mOrderPricing );
      }


    private class MyAssetUploadRequestListener implements AssetUploadRequestListener
      {
      private Context  mContext;


      MyAssetUploadRequestListener( Context context )
        {
        mContext = context;
        }


      @Override
      public void onProgress( AssetUploadRequest req, int totalAssetsUploaded, int totalAssetsToUpload, long bytesWritten, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite )
        {


        // Calculate the primary / secondary progress
        int primaryProgressPercent = Math.round( (float) totalAssetsUploaded * 100f / (float) totalAssetsToUpload );
        int secondaryProgressPercent = Math.round( (float) totalAssetBytesWritten * 100f / (float) totalAssetBytesExpectedToWrite );

        if ( userSubmittedForPrinting )
          {
          submissionListener.onProgress( PrintOrder.this, primaryProgressPercent, secondaryProgressPercent );
          }
        }

      @Override
      public void onUploadComplete( AssetUploadRequest req, List<Asset> assets )
        {
        if ( assets.size() != assetsToUpload.size() )
          {
          throw new IllegalStateException( String.format( "Oops there should be a 1:1 relationship between uploaded assets and submitted, currently its: %d:%d", assetsToUpload.size(), assets.size() ) );
          }

        for ( Asset asset : assets )
          {
          if ( !assetsToUpload.contains( asset ) )
            {
            throw new AssertionError( "oops" );
            }
          }

        // make sure all job assets have asset ids & preview urls. We need to do this because we optimize the asset upload to avoid uploading
        // assets that are considered to have duplicate contents
        for ( PrintJob job : jobs )
          {
          for ( Asset uploadedAsset : assets )
            {
            for ( Asset jobAsset : job.getAssetsForUploading() )
              {
              if ( uploadedAsset != jobAsset && uploadedAsset.equals( jobAsset ) )
                {
                jobAsset.markAsUploaded( uploadedAsset.getId(), uploadedAsset.getPreviewURL() );
                }
              }
            }
          }

        // sanity check all assets are uploaded
        for ( PrintJob job : jobs )
          {
          for ( Asset a : job.getAssetsForUploading() )
            {
            if ( !a.hasBeenUploaded() )
              {
              throw new AssertionError( "oops all assets should have been uploaded" );
              }
            }
          }

        assetUploadComplete = true;
        assetsToUpload = null;
        assetUploadReq = null;
        if ( userSubmittedForPrinting )
          {
          submitForPrinting( mContext );
          }
        }

      @Override
      public void onError( AssetUploadRequest req, Exception error )
        {
        assetUploadReq = null;
        assetsToUpload = null;
        if ( userSubmittedForPrinting )
          {
          lastPrintSubmissionError = error;
          userSubmittedForPrinting = false; // allow the user to resubmit for printing
          submissionListener.onError( PrintOrder.this, error );
          }
        }
      }

    }
