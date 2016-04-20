package ly.kite.ordering;

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
import java.util.Locale;
import java.util.Set;

import ly.kite.address.Address;
import ly.kite.api.AssetUploadRequest;
import ly.kite.api.SubmitOrderRequest;
import ly.kite.catalogue.SingleCurrencyAmount;
import ly.kite.pricing.OrderPricing;
import ly.kite.util.Asset;

/**
 * Created by deonbotha on 09/02/2014.
 */
public class Order implements Parcelable /* , Serializable */
    {

    static private final String LOG_TAG = "PrintOrder";

    private static final int NOT_PERSISTED          = -1;

    private static final int JOB_TYPE_POSTCARD      = 0;
    private static final int JOB_TYPE_GREETING_CARD = 1;
    private static final int JOB_TYPE_PHOTOBOOK     = 2;
    private static final int JOB_TYPE_PRINTS        = 3;

    static private final String JSON_NAME_LOCALE = "locale";


    private Address shippingAddress;
    private String proofOfPayment;
    private String voucherCode;
    private JSONObject userData;
    private ArrayList<Job> jobs = new ArrayList<Job>();

    private boolean userSubmittedForPrinting;
    //private long totalBytesWritten, totalBytesExpectedToWrite;
    private AssetUploadRequest assetUploadReq;
    private List<Asset> assetsToUpload;
    private boolean assetUploadComplete;
    private SubmitOrderRequest printOrderReq;
    private Date lastPrintSubmissionDate;
    private String receipt;
    private ISubmissionProgressListener submissionListener;
    private Exception lastPrintSubmissionError;
    private int storageIdentifier = NOT_PERSISTED;

    private String promoCode;

    private OrderPricing  mOrderPricing;

    //MultipleCurrencyAmount  mPromoCodeDiscount;
    //private BigDecimal promoCodeDiscount;
    private String statusNotificationEmail;
    private String statusNotificationPhone;
    //private String currencyCode = null;

    //private MultipleCurrencyAmount mTotalCost;

    public Order() {}

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public void setProofOfPayment( String proofOfPayment )
      {
      if ( proofOfPayment == null ||
           ( ! proofOfPayment.startsWith( "AP-"  ) &&
             ! proofOfPayment.startsWith( "PAY-" ) &&
             ! proofOfPayment.startsWith( "PAUTH-" ) &&
             ! proofOfPayment.startsWith( "tok_" ) ) )
        {
        throw new IllegalArgumentException( "Proof of payment must start with AP-, PAY-, PAUTH-, or tok_ : " + proofOfPayment );
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

    public JSONObject getJSONRepresentation() {
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
            for (Job job : this.jobs) {
                jobs.put(job.getJSONRepresentation());
            }


        // Make sure we always have user data, and put the default locale into it

        if ( userData == null ) userData = new JSONObject();

        userData.put( JSON_NAME_LOCALE, Locale.getDefault().toString() );

        json.put( "user_data", userData );


        // Add the customer payment information

        OrderPricing orderPricing = getOrderPricing();

        if ( orderPricing != null )
          {
          SingleCurrencyAmount orderTotalCost = orderPricing.getTotalCost().getDefaultAmountWithFallback();
          // construct customer payment object in a round about manner to guarantee 2dp amount value
          StringBuilder builder = new StringBuilder();
          builder.append( "{" );
          builder.append( "\"currency\": \"" ).append( orderTotalCost.getCurrencyCode() ).append( "\"" ).append( "," );
          builder.append( String.format( Locale.ENGLISH, "\"amount\": %.2f", orderTotalCost.getAmount().floatValue() ) ); // Local.ENGLISH to force . separator instead of comma
          builder.append( "}" );
          JSONObject customerPayment = new JSONObject( builder.toString() );
          json.put( "customer_payment", customerPayment );
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
        for (Job job : jobs) {
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

      for ( Job job : jobs )
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
        for (Job job : jobs) {
            for (Asset asset : job.getAssetsForUploading()) {
                if ( asset != null && !assets.contains(asset)) {
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

    public void setReceipt( String receipt )
      {
      this.receipt = receipt;
      }

    // Used for testing
    public void clearReceipt()
        {
        this.receipt = null;
        }

    public String getReceipt() {
        return this.receipt;
    }

    public Order addJob( Job job) {
        if (!(job instanceof AssetListJob || job instanceof PostcardJob || job instanceof GreetingCardJob )) {
            throw new IllegalArgumentException("Currently only support PrintsPrintJobs & PostcardPrintJob, if any further jobs " +
                    "classes are added support for them must be added to the Parcelable interface in particular readTypedList must work ;)");
        }

        jobs.add(job);

    return ( this );
    }

    public void removeJob( Job job) {
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

        startAssetUpload( context );
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

    public void submitForPrinting(Context context, ISubmissionProgressListener listener) {
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
        printOrderReq = new SubmitOrderRequest(this);
        printOrderReq.submitForPrinting( context, new SubmitOrderRequest.IProgressListener()
        {
        @Override
        public void onSubmissionComplete( SubmitOrderRequest req, String orderId )
            {
            setReceipt( orderId );

            printOrderReq = null;

            submissionListener.onSubmissionComplete( Order.this, orderId );
            }

        @Override
        public void onError( SubmitOrderRequest req, Exception error )
            {
            userSubmittedForPrinting = false;
            lastPrintSubmissionError = error;

            printOrderReq = null;

            submissionListener.onError( Order.this, error );
            }
        } );
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

        for ( Job job : jobs )
          {
          if ( job instanceof PostcardJob )
            {
            p.writeInt( JOB_TYPE_POSTCARD );
            job.writeToParcel( p, flags );
            }
          else if ( job instanceof GreetingCardJob )
            {
            p.writeInt( JOB_TYPE_GREETING_CARD );
            job.writeToParcel( p, flags );
            }
          else if ( job instanceof PhotobookJob )
            {
            p.writeInt( JOB_TYPE_PHOTOBOOK );
            job.writeToParcel( p, flags );
            }
          else
            {
            p.writeInt( JOB_TYPE_PRINTS );
            job.writeToParcel( p, flags );
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

    private Order( Parcel p) {
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

        for ( int i = 0; i < numJobs; ++i )
          {
          int jobType = p.readInt();

          Job job;

          switch ( jobType )
            {
            case JOB_TYPE_POSTCARD:
              job = PostcardJob.CREATOR.createFromParcel( p );
              break;

            case JOB_TYPE_GREETING_CARD:
              job = GreetingCardJob.CREATOR.createFromParcel( p );
              break;

            case JOB_TYPE_PHOTOBOOK:
              job = PhotobookJob.CREATOR.createFromParcel( p );
              break;

            default:
              job = AssetListJob.CREATOR.createFromParcel( p );
            }

          this.jobs.add( job );
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

    public static final Parcelable.Creator<Order> CREATOR
            = new Parcelable.Creator<Order>() {
        public Order createFromParcel( Parcel in) {
            return new Order(in);
        }

        public Order[] newArray( int size) {
            return new Order[size];
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


    private class MyAssetUploadRequestListener implements AssetUploadRequest.IProgressListener
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
          submissionListener.onProgress( Order.this, primaryProgressPercent, secondaryProgressPercent );
          }
        }

      @Override
      public void onUploadComplete( AssetUploadRequest req, List<Asset> uploadedAssets )
        {
        // We don't want to check that the number of upload assets matches the size
        // of the asset list, because some of them might be blank.


        // Check that all the assets scheduled to be uploaded, were

        for ( Asset uploadedAsset : uploadedAssets )
          {
          if ( ! assetsToUpload.contains( uploadedAsset ) )
            {
            throw new AssertionError( "Oops - found an asset not in the upload list" );
            }
          }


        // Make sure all job assets have asset ids & preview urls. We need to do this because
        // we optimize the asset upload to avoid uploading assets that are considered to have
        // duplicate contents.

        for ( Job job : jobs )
          {
          for ( Asset uploadedAsset : uploadedAssets )
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


        // Sanity check all assets are uploaded

        for ( Job job : jobs )
          {
          for ( Asset assetToUpload : job.getAssetsForUploading() )
            {
            if ( assetToUpload != null && ! assetToUpload.hasBeenUploaded() )
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
          submissionListener.onError( Order.this, error );
          }
        }
      }


    public interface ISubmissionProgressListener
        {
        void onProgress( Order order, int primaryProgressPercent, int secondaryProgressPercent );

        void onSubmissionComplete( Order order, String orderId );

        void onError( Order order, Exception error );
        }

    }
