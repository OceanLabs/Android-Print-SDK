package ly.kite.ordering;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ly.kite.KiteSDK;
import ly.kite.address.Address;
import ly.kite.api.AssetUploadRequest;
import ly.kite.api.SubmitOrderRequest;
import ly.kite.catalogue.Product;
import ly.kite.catalogue.SingleCurrencyAmounts;
import ly.kite.image.ImageAgent;
import ly.kite.image.ImageProcessingRequest;
import ly.kite.pricing.OrderPricing;
import ly.kite.util.Asset;
import ly.kite.util.AssetFragment;
import ly.kite.util.UploadableImage;

/**
 * Created by deonbotha on 09/02/2014.
 */
public class Order implements Parcelable /* , Serializable */
    {
    static private final String LOG_TAG                = "Order";

    static private final int    NOT_PERSISTED          = -1;

    static private final int    JOB_TYPE_POSTCARD      = 0;
    static private final int    JOB_TYPE_GREETING_CARD = 1;
    static private final int    JOB_TYPE_PHOTOBOOK     = 2;
    static private final int    JOB_TYPE_PRINTS        = 3;

    static private final String JSON_NAME_LOCALE       = "locale";
    static private final String JSON_NAME_JOB_ID       = "job_id";
    static private final String JSON_NAME_QUANTITY     = "quantity";
    static private final String JSON_NAME_TEMPLATE_ID  = "template_id";
    static private final String JSON_NAME_COUNTRY_CODE = "country_code";


    // These values are used to build the order
    private ArrayList<Job>               jobs = new ArrayList<Job>();
    private Address                      shippingAddress;
    private String                       statusNotificationEmail;
    private String                       statusNotificationPhone;
    private JSONObject                   userData;
    private HashMap<String,String>       mAdditionalParametersMap;
    private String                       promoCode;
    //private String                       voucherCode;

    // These values are generated throughout the order processing, but
    // need to be persisted in some form for the order history
    private OrderPricing                 mOrderPricing;
    private String                       proofOfPayment;
    private String                       receipt;

    // Transient values solely used during order submission
    private boolean                      userSubmittedForPrinting;
    private AssetUploadRequest           assetUploadReq;
    private int                          mImagesToCropCount;
    private List<UploadableImage>        mImagesToUpload;
    private boolean                      assetUploadComplete;
    private SubmitOrderRequest           printOrderReq;
    private Date                         lastPrintSubmissionDate;
    private ISubmissionProgressListener  submissionListener;
    private Exception                    lastPrintSubmissionError;
    private int                          storageIdentifier = NOT_PERSISTED;


    public Order() {}


    /*****************************************************
     *
     * Constructor used by basket activity.
     *
     *****************************************************/
    public Order( Context                context,
                  List<BasketItem>       basketItemList,
                  Address                shippingAddress,
                  String                 contactEmail,
                  String                 contactPhone,
                  HashMap<String,String> additionalParametersMap )
      {
      // Convert the basket items into jobs

      if ( basketItemList != null )
        {
        for ( BasketItem basketItem : basketItemList )
          {
          Product product       = basketItem.getProduct();
          int     orderQuantity = basketItem.getOrderQuantity();

          product.getUserJourneyType().addJobsToOrder( context, product, orderQuantity, basketItem.getOptionsMap(), basketItem.getImageSpecList(), this );
          }
        }


      setShippingAddress( shippingAddress );
      setEmail( contactEmail );
      setPhone( contactPhone );
      setAdditionalParameters( additionalParametersMap );
      }


    /*****************************************************
     *
     * Constructor used by order history fragment.
     *
     *****************************************************/
    public Order( Context                context,
                  List<BasketItem>       basketItemList,
                  Address                shippingAddress,
                  String                 contactEmail,
                  String                 contactPhone,
                  JSONObject             userDataJSONObject,
                  HashMap<String,String> additionalParametersMap,
                  String                 promoCode,
                  OrderPricing           orderPricing,
                  String                 proofOfPayment,
                  String                 receipt )
      {
      this( context, basketItemList, shippingAddress, contactEmail, contactPhone, additionalParametersMap );

      setUserData( userDataJSONObject );
      setPromoCode( promoCode );
      setOrderPricing( orderPricing );
      if ( proofOfPayment != null ) setProofOfPayment( proofOfPayment );
      setReceipt( receipt );
      }


    public Order setShippingAddress( Address shippingAddress )
      {
      this.shippingAddress = shippingAddress;

      return ( this );
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

//    public void setVoucherCode(String voucherCode) {
//        this.voucherCode = voucherCode;
//    }
//
//    public String getVoucherCode() {
//        return voucherCode;
//    }

    public void setUserData(JSONObject userData) {
        this.userData = userData;
    }


    public void setAdditionalParameters( HashMap<String, String> additionalParametersMap )
      {
      mAdditionalParametersMap = additionalParametersMap;
      }

    public HashMap<String,String> getAdditionalParameters()
      {
      return ( mAdditionalParametersMap );
      }


    public Order setAdditionalParameter( String parameterName, String parameterValue )
      {
      if ( mAdditionalParametersMap == null ) mAdditionalParametersMap = new HashMap<>();

      mAdditionalParametersMap.put( parameterName, parameterValue );

      return ( this );
      }


    public String getAdditionalParameter( String parameterName )
      {
      if ( mAdditionalParametersMap != null ) return ( mAdditionalParametersMap.get( parameterName ) );

      return ( null );
      }


    /*****************************************************
     *
     * Sets a user data parameter, creating a user data object
     * if necessary.
     *
     *****************************************************/
    public Order setUserDataParameter( String parameterName, String parameterValue )
      {
      if ( userData == null ) userData = new JSONObject();

      try
        {
        userData.put( parameterName, parameterValue );
        }
      catch ( JSONException je )
        {
        Log.e( LOG_TAG, "Unable to set " + parameterName + " = " + parameterValue, je );
        }

      return ( this );
      }


    /*****************************************************
     *
     * Clears a user data parameter.
     *
     *****************************************************/
    public Order removeUserDataParameter( String parameterName )
      {
      // We don't need to clear anything if there is no user data
      if ( userData != null )
        {
        userData.remove( parameterName );
        }

      return ( this );
      }


    /*****************************************************
     *
     * Sets both the notification and user data email.
     *
     *****************************************************/
    public Order setEmail( String email )
      {
      setNotificationEmail( email );

      setUserDataParameter( "email", email );

      return ( this );
      }


    /*****************************************************
     *
     * Sets both the notification and user data phone number.
     *
     *****************************************************/
    public Order setPhone( String phone )
      {
      setNotificationPhoneNumber( phone );

      setUserDataParameter( "phone", phone );

      return ( this );
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


    /*****************************************************
     *
     * Returns the JSON representation of this order for
     * submission to the print request endpoint.
     *
     *****************************************************/
    public JSONObject getJSONRepresentation( Context context )
      {
      try
        {
        JSONObject json = new JSONObject();
        if ( proofOfPayment != null )
          {
          json.put( "proof_of_payment", proofOfPayment );
          }
        else
          {
          json.put( "proof_of_payment", "" );
          }

        json.put( "receipt_email", statusNotificationEmail );
        if ( promoCode != null )
          {
          json.put( "promo_code", promoCode );
          }


        // Add the jobs

        JSONArray jobs = new JSONArray();

        json.put( "jobs", jobs );

        for ( Job job : this.jobs )
          {
          // Duplicate jobs orderQuantity times

          int orderQuantity = job.getOrderQuantity();

          for ( int index = 0; index < orderQuantity; index ++ )
            {
            jobs.put( job.getJSONRepresentation() );
            }
          }


        // Make sure we always have user data, and put the default locale into it

        if ( userData == null ) userData = new JSONObject();

        userData.put( JSON_NAME_LOCALE, Locale.getDefault().toString() );

        json.put( "user_data", userData );


        // Add any additional parameters

        if ( mAdditionalParametersMap != null )
          {
          for ( String parameterName : mAdditionalParametersMap.keySet() )
            {
            String parameterValue = mAdditionalParametersMap.get( parameterName );

            json.put( parameterName, parameterValue );
            }
          }


        // Add the customer payment information

        OrderPricing orderPricing = getOrderPricing();

        if ( orderPricing != null )
          {
          String preferredCurrencyCode = KiteSDK.getInstance( context ).getLockedCurrencyCode();

          SingleCurrencyAmounts orderTotalCost = orderPricing.getTotalCost().getAmountsWithFallback( preferredCurrencyCode );

          // construct customer payment object in a round about manner to guarantee 2dp amount value
          StringBuilder builder = new StringBuilder();
          builder.append( "{" );
          builder.append( "\"currency\": \"" ).append( orderTotalCost.getCurrencyCode() ).append( "\"" ).append( "," );
          builder.append( String.format( Locale.ENGLISH, "\"amount\": %.2f", orderTotalCost.getAmount().floatValue() ) ); // Local.ENGLISH to force . separator instead of comma
          builder.append( "}" );
          JSONObject customerPayment = new JSONObject( builder.toString() );
          json.put( "customer_payment", customerPayment );
          }


        if ( shippingAddress != null )
          {
          JSONObject sajson = new JSONObject();
          sajson.put( "recipient_name", shippingAddress.getRecipientName() );
          sajson.put( "address_line_1", shippingAddress.getLine1() );
          sajson.put( "address_line_2", shippingAddress.getLine2() );
          sajson.put( "city", shippingAddress.getCity() );
          sajson.put( "county_state", shippingAddress.getStateOrCounty() );
          sajson.put( "postcode", shippingAddress.getZipOrPostalCode() );
          sajson.put( "country_code", shippingAddress.getCountry().iso3Code() );
          json.put( "shipping_address", sajson );
          }


        if ( KiteSDK.DEBUG_PAYMENT_KEYS )
          {
          Log.d( LOG_TAG, "Create order JSON:\n" + json.toString() );
          }

        return json;
        }
      catch ( JSONException ex )
        {
        throw new RuntimeException( ex );
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
     * Returns a string description of the items. This is
     * primarily used as the item description on the order
     * history screen.
     *
     *****************************************************/
    public String getItemsDescription()
      {
      StringBuilder descriptionBuilder = new StringBuilder();

      String separatorString = "";


      // Go through each of the jobs

      for ( Job job : jobs )
        {
        Product product = job.getProduct();

        String itemString = product.getDisplayLabel();

        descriptionBuilder
                .append( separatorString )
                .append( itemString );

        separatorString = ", ";
        }


      return ( descriptionBuilder.toString() );
      }


    /*****************************************************
     *
     * Returns this order as a JSON basket.
     *
     *    [
     *     {
     *     "country_code": "GBR",
     *     "job_id": "48CD1DFA-254B-4FF9-A81C-1FB7A854C509",
     *     "quantity": 1,
     *     "template_id":"i6splus_case"
     *     }
     *   ]
     *
     *****************************************************/
    public JSONArray asBasketJSONArray( String countryCode )
      {
      JSONArray jsonArray = new JSONArray();

      String separatorString = "";

      for ( Job job : jobs )
        {
        // Each job is repeated orderQuantity times

        int orderQuantity = job.getOrderQuantity();

        for ( int index = 0; index < orderQuantity; index ++ )
          {
          JSONObject itemJSONObject = new JSONObject();

          try
            {
            itemJSONObject.put( JSON_NAME_JOB_ID,       job.getId() );
            itemJSONObject.put( JSON_NAME_QUANTITY,     job.getQuantity() );
            itemJSONObject.put( JSON_NAME_TEMPLATE_ID,  job.getProductId() );
            itemJSONObject.put( JSON_NAME_COUNTRY_CODE, countryCode );
            }
          catch ( JSONException je )
            {
            Log.e( LOG_TAG, "Unable to create basket item JSON", je );
            }

          jsonArray.put( itemJSONObject );
          }
        }

      return ( jsonArray );
      }


    List<UploadableImage> getImagesToUpload()
      {
      List<UploadableImage> uploadableImageList = new ArrayList<>();
      for ( Job job : jobs )
        {
        for ( UploadableImage uploadableImage : job.getImagesForUploading() )
          {
          if ( uploadableImage != null && !uploadableImageList.contains( uploadableImage ) )
            {
            uploadableImageList.add( uploadableImage );
            }
          }
        }
      return uploadableImageList;
      }

    public int getTotalAssetsToUpload() {
        return getImagesToUpload().size();
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
        if (!(job instanceof ImagesJob || job instanceof PostcardJob || job instanceof GreetingCardJob )) {
            throw new IllegalArgumentException("Currently only support PrintsPrintJobs & PostcardPrintJob, if any further jobs " +
                    "classes are added support for them must be added to the Parcelable interface in particular readTypedList must work ;)");
        }

        jobs.add(job);

    return ( this );
    }

    public void removeJob( Job job) {
        jobs.remove(job);
    }


    /*****************************************************
     *
     * Returns a sanitised version of this order.
     *
     *****************************************************/
    public Order createSanitisedCopy()
      {
      // Create a new order, and copy over relevant details

      Order sanitisedOrder = new Order();

      sanitisedOrder.setShippingAddress( getShippingAddress() );
      sanitisedOrder.setNotificationEmail( getNotificationEmail() );
      sanitisedOrder.setNotificationPhoneNumber( getNotificationPhoneNumber() );
      sanitisedOrder.setUserData( getUserData() );
      sanitisedOrder.setAdditionalParameters( getAdditionalParameters() );
      sanitisedOrder.setPromoCode( getPromoCode() );

      sanitisedOrder.setOrderPricing( getOrderPricing() );

      // Do not set proof of payment for free checkout(full discount) as it will crash the app
      if(!getOrderPricing().isCheckoutFree())
        {
        sanitisedOrder.setProofOfPayment(getProofOfPayment());
        }

      sanitisedOrder.setReceipt( getReceipt() );


      return ( sanitisedOrder );
      }


    private boolean isAssetUploadInProgress() {
        // There may be a brief window where assetUploadReq == null whilst we asynchronously collect info about the assets
        // to upload. assetsToUpload will be non nil whilst this is happening.
        return mImagesToUpload != null || assetUploadReq != null;
    }

    public void preemptAssetUpload(Context context) {
        if (isAssetUploadInProgress() || assetUploadComplete) {
            return;
        }

        startAssetUpload( context );
    }


    private void startAssetUpload( final Context context )
      {
      if ( isAssetUploadInProgress() || assetUploadComplete )
        {
        throw new IllegalStateException( "Asset upload should not have previously been started" );
        }


      // Call back with progress (even though we haven't made any yet), so the user gets
      // a dialog box. Otherwise there's a delay whilst any images are cropped, and it's
      // not obvious that anything's happening.

      if ( submissionListener != null ) submissionListener.onProgress( this, 0, 0 );



      // Get a list of all the images that need uploading. This list will exclude any
      // blank images.
      mImagesToUpload = getImagesToUpload();


      // Crop any images where the the asset fragment is a sub-section of the original
      // asset.

      mImagesToCropCount = 0;

      for ( UploadableImage uploadableImage : mImagesToUpload )
        {
        AssetFragment assetFragment = uploadableImage.getAssetFragment();
        Asset          asset        = uploadableImage.getAsset();


        // If this asset fragment is not full size then it needs to be cropped before
        // it can be uploaded.

        if ( !assetFragment.isFullSize() )
          {
          mImagesToCropCount++;

          ImageAgent.with( context )
                  .transform( asset )
                  .byCroppingTo( assetFragment.getProportionalRectangle() )
                  .intoNewAsset()
                  .thenNotify( new ImageCroppedCallback( context, uploadableImage ) );
          }
        }


      // If there are no images to crop - start the upload immediately
      if ( mImagesToCropCount < 1 ) startAssetUploadRequest( context );
      }


    private void startAssetUploadRequest( Context context )
      {
      final boolean[] previousError = { false };
      final int[] outstandingLengthCallbacks = { mImagesToUpload.size() };

      assetUploadReq = new AssetUploadRequest( context );
      assetUploadReq.uploadAssets( context, mImagesToUpload, new MyAssetUploadRequestListener( context ) );
      }


    public void submitForPrinting( Context context, ISubmissionProgressListener listener )
      {
      this.submissionListener = listener;

      if ( userSubmittedForPrinting )
        {
        notifyIllegalStateError( "An order has already been submitted for printing. An order submission must be cancelled before it can be submitted again." );

        return;
        }


      if ( printOrderReq != null )
        {
        notifyIllegalStateError( "A print order request is already in progress." );

        return;
        }

      lastPrintSubmissionDate = new Date();
      userSubmittedForPrinting = true;

      if ( assetUploadComplete )
        {
        submitForPrinting( context );
        }
      else if ( !isAssetUploadInProgress() )
        {
        startAssetUpload( context );
        }
      }


    private void submitForPrinting( Context context )
      {
      if ( ! userSubmittedForPrinting )
        {
        notifyIllegalStateError( "The order cannot be submitted for printing if it has not been marked as submitted" );

        return;
        }

      if ( ! assetUploadComplete || isAssetUploadInProgress() )
        {
        notifyIllegalStateError( "The order should not be submitted for priting until the asset upload has completed." );

        return;
        }

      // Step 2: Submit print order to the server. Print Job JSON can now reference real asset ids.
      printOrderReq = new SubmitOrderRequest( this );
      printOrderReq.submitForPrinting( context, new SubmitOrderRequest.IProgressListener()
        {
        @Override
        public void onSubmissionComplete( SubmitOrderRequest req, String orderId )
          {
          // The initial submission was successful, but note that this doesn't mean the order will
          // pass validation. It may fail subsequently when we are polling the order status. So remember
          // to clear the receipt / set the error if we find a problem later.

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

          notifyError( error );
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
    public void writeToParcel( Parcel p, int flags )
      {
      p.writeValue( shippingAddress );
      p.writeString( proofOfPayment );
      //p.writeString( voucherCode );

      String userDataString = userData == null ? null : userData.toString();
      p.writeString( userDataString );

      p.writeSerializable( mAdditionalParametersMap );

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

      p.writeValue( userSubmittedForPrinting );
      p.writeValue( assetUploadComplete );
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
        //this.voucherCode = p.readString();
        String userDataString = p.readString();
        if (userDataString != null) {
            try {
                this.userData = new JSONObject(userDataString);
            } catch (JSONException ex) {
                throw new RuntimeException(ex); // will never happen ;)
            }
        }

        mAdditionalParametersMap = (HashMap<String,String>)p.readSerializable();

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
              job = ImagesJob.CREATOR.createFromParcel( p );
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



    /*****************************************************
     *
     * Notifies any listener of an error.
     *
     *****************************************************/
    private void notifyError( Exception exception )
      {
      if ( submissionListener != null )
        {
        submissionListener.onError( this, exception );
        }
      }


    /*****************************************************
     *
     * Notifies any listener of an illegal state error.
     *
     *****************************************************/
    private void notifyIllegalStateError( String message )
      {
      notifyError( new IllegalStateException( message ) );
      }


    /*****************************************************
     *
     * Resets the order state with an error. This is likely
     * to have been returned from polling the order status.
     *
     *****************************************************/
    public void setError( Exception exception )
      {
      clearReceipt();

      lastPrintSubmissionError = exception;
      userSubmittedForPrinting = false;
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
      public void onUploadComplete( AssetUploadRequest req, List<UploadableImage> uploadedImages )
        {
        // We don't want to check that the number of upload assets matches the size
        // of the asset list, because some of them might be blank.


        // Check that all the assets scheduled to be uploaded, were

        for ( UploadableImage uploadedImage : uploadedImages )
          {
          if ( ! mImagesToUpload.contains( uploadedImage ) )
            {
            Log.e( LOG_TAG, "Found image not in upload list: " + uploadedImage );

            notifyIllegalStateError( "An image has been uploaded that shouldn't have been" );

            return;
            }
          }


        // Make sure all job assets have asset ids & preview urls. We need to do this because
        // we optimize the asset upload to avoid uploading assets that are considered to have
        // duplicate contents.

        for ( Job job : jobs )
          {
          for ( UploadableImage uploadedImage : uploadedImages )
            {
            for ( UploadableImage jobUploadableImage : job.getImagesForUploading() )
              {
              if ( uploadedImage != jobUploadableImage && uploadedImage.equals( jobUploadableImage ) )
                {
                jobUploadableImage.markAsUploaded( uploadedImage.getUploadedAssetId(), uploadedImage.getPreviewURL() );
                }
              }
            }
          }


        // Sanity check all assets are uploaded

        for ( Job job : jobs )
          {
          for ( UploadableImage uploadableImage : job.getImagesForUploading() )
            {
            if ( uploadableImage != null && ! uploadableImage.hasBeenUploaded() )
              {
              Log.e( LOG_TAG, "An image that should have been uploaded, hasn't been." );

              notifyIllegalStateError( "An image that should have been uploaded, hasn't been." );

              return;
              }
            }
          }


        assetUploadComplete = true;
        mImagesToUpload = null;
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
        mImagesToUpload = null;
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


    private class ImageCroppedCallback implements ImageProcessingRequest.ICallback
      {
      private Context          mContext;
      private UploadableImage  mUploadableImage;


      ImageCroppedCallback( Context context, UploadableImage uploadableImage )
        {
        mContext         = context;
        mUploadableImage = uploadableImage;
        }


      @Override
      public void ipcOnImageAvailable( Asset targetAsset )
        {
        // Replace the previous asset fragment with the entire area of the cropped asset
        mUploadableImage.setImage( targetAsset );

        mImagesToCropCount --;

        // If all the images have been cropped - start the upload
        if ( mImagesToCropCount < 1 )
          {
          startAssetUploadRequest( mContext );
          }
        }


      @Override
      public void ipcOnImageUnavailable()
        {
        assetUploadReq  = null;
        mImagesToUpload = null;

        if ( userSubmittedForPrinting )
          {
          lastPrintSubmissionError = null;
          userSubmittedForPrinting = false; // allow the user to resubmit for printing
          submissionListener.onError( Order.this, null );
          }
        }
      }
    }
