/*****************************************************
 *
 * BasketActivity.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.journey.basket;


///// Import(s) /////

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.address.Address;
import ly.kite.basket.BasketAgent;
import ly.kite.basket.BasketItem;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.catalogue.Product;
import ly.kite.checkout.PaymentActivity;
import ly.kite.checkout.ShippingActivity;
import ly.kite.image.ImageAgent;
import ly.kite.journey.AKiteActivity;
import ly.kite.R;
import ly.kite.journey.creation.ProductCreationActivity;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.pricing.OrderPricing;
import ly.kite.pricing.PricingAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the activity that displays the basket
 * screen.
 *
 *****************************************************/
public class BasketActivity extends AKiteActivity implements ICatalogueConsumer, View.OnClickListener, PricingAgent.IPricingConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                         = "BasketActivity";

  static private final String  KEY_MANAGED_ORDER               = "ly.kite.managedorder";
  static private final String  KEY_SHIPPING_ADDRESS            = "ly.kite.shippingaddress";
  static private final String  KEY_CONTACT_EMAIL               = "ly.kite.contactemail";
  static private final String  KEY_CONTACT_PHONE               = "ly.kite.contactphone";

  static private final String  NO_PROMO_CODE_YET               = null;

  static private final String  PARAMETER_NAME_SHIPPING_ADDRESS = "basket_shipping_address";
  static private final String  PARAMETER_NAME_CONTACT_EMAIL    = "basket_contact_email";
  static private final String  PARAMETER_NAME_CONTACT_PHONE    = "basket_contact_phone";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private boolean           mIsManagedCheckout;

  private Order             mManagedOrder;
  private List<BasketItem>  mBasketItemList;

  private Order             mBasketOrder;

  private Address           mShippingAddress;
  private String            mContactEmail;
  private String            mContactPhone;

  private ListView          mListView;
  private TextView          mBasketEmptyTextView;
  private ProgressBar       mProgressSpinner;
  private TextView          mDeliveryAddressTextView;
  private TextView          mTotalShippingPriceTextView;
  private TextView          mTotalPriceTextView;

  private Catalogue         mCatalogue;

  private BasketAdaptor     mBasketAdaptor;

  private int               mPricingRequestId;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts this activity for managed check-out, using the
   * supplied order.
   *
   *****************************************************/
  static public void start( Context context, Order order )
    {
    Intent intent = new Intent( context, BasketActivity.class );

    intent.putExtra( KEY_MANAGED_ORDER, order );

    context.startActivity( intent );
    }


  /*****************************************************
   *
   * Starts this activity for managed check-out, using the
   * supplied order.
   *
   *****************************************************/
  static public void startForResult( Activity activity, Order order, int requestCode )
    {
    Intent intent = new Intent( activity, BasketActivity.class );

    intent.putExtra( KEY_MANAGED_ORDER, order );

    activity.startActivityForResult( intent, requestCode );
    }


  /*****************************************************
   *
   * Starts this activity as part of a shopping experience,
   * where the basket items are already assumed to be populated/
   *
   *****************************************************/
  static public void startForResult( Activity activity, int requestCode )
    {
    Intent intent = new Intent( activity, BasketActivity.class );

    activity.startActivityForResult( intent, requestCode );
    }


  ////////// Constructor(s) //////////


  ////////// AKiteActivity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // If we have an order then we are the first stage in managed
    // check-out. We need to check for a saved one first, because
    // we might have changed orientation and are being re-created.

    if ( savedInstanceState != null )
      {
      mManagedOrder    = savedInstanceState.getParcelable( KEY_MANAGED_ORDER );

      mShippingAddress = savedInstanceState.getParcelable( KEY_SHIPPING_ADDRESS );
      mContactEmail    = savedInstanceState.getString( KEY_CONTACT_EMAIL );
      mContactPhone    = savedInstanceState.getString( KEY_CONTACT_PHONE );
      }

    if ( mManagedOrder == null )
      {
      Intent intent = getIntent();

      if ( intent != null )
        {
        mManagedOrder = intent.getParcelableExtra( KEY_MANAGED_ORDER );
        }
      }

    if ( mManagedOrder != null )
      {
      mIsManagedCheckout = true;

      if ( mShippingAddress == null ) mShippingAddress = mManagedOrder.getShippingAddress();
      if ( mContactEmail    == null ) mContactEmail    = mManagedOrder.getNotificationEmail();
      if ( mContactPhone    == null ) mContactPhone    = mManagedOrder.getNotificationPhoneNumber();
      }


    setContentView( R.layout.screen_basket );

    mListView                   = (ListView)findViewById( R.id.list_view );
    mBasketEmptyTextView        = (TextView)findViewById( R.id.basket_empty_text_view );
    mProgressSpinner            = (ProgressBar)findViewById( R.id.progress_spinner );
    mDeliveryAddressTextView    = (TextView)findViewById( R.id.delivery_address_text_view );
    mTotalShippingPriceTextView = (TextView)findViewById( R.id.total_shipping_price_text_view );
    mTotalPriceTextView         = (TextView)findViewById( R.id.total_price_text_view );


    setTitle( R.string.title_basket );

    setLeftButtonText( R.string.basket_left_button_text );
    setLeftButtonColourRes( R.color.basket_left_button );

    setRightButtonText( R.string.basket_right_button_text );
    setRightButtonColourRes( R.color.basket_right_button );


    mDeliveryAddressTextView.setOnClickListener( this );
    }


  /*****************************************************
   *
   * Called when the activity starts.
   *
   *****************************************************/
  @Override
  public void onStart()
    {
    super.onStart();


    // If we still don't have some shipping details, see if we saved them from the same session

    KiteSDK kiteSDK = KiteSDK.getInstance( this );

    if ( mShippingAddress == null ) mShippingAddress = kiteSDK.getAddressAppParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_SHIPPING_ADDRESS );
    if ( mContactEmail    == null ) mContactEmail    = kiteSDK.getStringAppParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_CONTACT_EMAIL, null );
    if ( mContactPhone    == null ) mContactPhone    = kiteSDK.getStringAppParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_CONTACT_PHONE, null );


    // See what mode we're in

    if ( mIsManagedCheckout )
      {
      ///// Managed check-out /////

      // Create a list of basket items for the job

      List<Job> jobList = mManagedOrder.getJobs();

      mBasketItemList = new ArrayList<>( jobList.size() );

      for ( Job job : jobList )
        {
        mBasketItemList.add( new BasketItem( 0L, job.getProduct(), job.getOrderQuantity(), null, null ) );
        }


      setLeftButtonVisible( false );

      onGotBasket();
      }
    else
      {
      ///// Full shopping experience /////

      // Load the catalogue

      setLeftButtonEnabled( false );
      setRightButtonEnabled( false );

      if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.VISIBLE );

      KiteSDK.getInstance( this ).getCatalogueLoader().requestCatalogue( this );
      }

    }


  /*****************************************************
   *
   * Called when back is pressed.
   *
   *****************************************************/
  @Override
  public void onBackPressed()
    {
    // Once we have reached the basket, we don't want back
    // to go back to the last creation screen. Instead it
    // behaves in the same way as if continue shopping were
    // clicked.

    continueShopping();
    }


  /*****************************************************
   *
   * Called when an activity returns a result
   *
   *****************************************************/
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    // Check for return from shipping activity

    if ( requestCode == ACTIVITY_REQUEST_CODE_GET_CONTACT_DETAILS && resultCode == RESULT_OK )
      {
      mShippingAddress = ShippingActivity.getShippingAddress( data );
      mContactEmail    = ShippingActivity.getEmail( data );
      mContactPhone    = ShippingActivity.getPhone( data );

      onNewShippingDetails();

      return;
      }


    // Check for return from payment activity

    else if ( requestCode == ACTIVITY_REQUEST_CODE_CHECKOUT && resultCode == RESULT_OK )
      {
      // If we checked out OK, then we'll want to clear the basket
      BasketAgent.getInstance( this ).clear();

      setResult( ACTIVITY_RESULT_CODE_CHECKED_OUT );

      finish();

      return;
      }


    super.onActivityResult( requestCode, resultCode, data );
    }


  /*****************************************************
   *
   * Called to save the current instance state. Save the
   * order in case it has been updated.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    if ( mManagedOrder    != null ) outState.putParcelable( KEY_MANAGED_ORDER,    mManagedOrder );
    if ( mShippingAddress != null ) outState.putParcelable( KEY_SHIPPING_ADDRESS, mShippingAddress );
    if ( mContactEmail    != null ) outState.putString( KEY_CONTACT_EMAIL, mContactEmail );
    if ( mContactPhone    != null ) outState.putString( KEY_CONTACT_PHONE, mContactPhone );
    }


  /*****************************************************
   *
   * Called when the left CTA button is clicked.
   *
   *****************************************************/
  @Override
  protected void onLeftButtonClicked()
    {
    continueShopping();
    }


  /*****************************************************
   *
   * Called when the right CTA button is clicked.
   *
   *****************************************************/
  @Override
  protected void onRightButtonClicked()
    {
    // Set up the order shipping details

    Order order = getOrder();

    order.setShippingAddress( mShippingAddress );
    order.setNotificationEmail( mContactEmail );
    order.setNotificationPhoneNumber( mContactPhone );


    // Populate the order with the shipping / contact details

    JSONObject userData = order.getUserData();

    if ( userData == null )
      {
      userData = new JSONObject();
      }

    try
      {
      userData.put( "email", mContactEmail );
      userData.put( "phone", mContactPhone );
      }
    catch ( JSONException je )
      {
      // Ignore
      }

    order.setUserData( userData );


    // Go to payment screen
    PaymentActivity.startForResult( this, order, ACTIVITY_REQUEST_CODE_CHECKOUT );
    }


  ////////// ICatalogueConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the catalogue is loaded.
   *
   *****************************************************/
  @Override
  public void onCatalogueSuccess( Catalogue catalogue )
    {
    mCatalogue = catalogue;


    // Clear the progress spinner and enable the buttons

    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.INVISIBLE );

    setLeftButtonEnabled( true );


    loadAndDisplayBasket();
    }


  /*****************************************************
   *
   * Called when there is an error loading the catalogue.
   *
   *****************************************************/
  @Override
  public void onCatalogueError( Exception exception )
    {
    Log.e( LOG_TAG, "Unable to load catalogue", exception );

    // TODO: Display an error dialog with retry / cancel options
    }


  ////////// View.OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when a view is clicked.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    if ( view == mDeliveryAddressTextView )
      {
      ShippingActivity.startForResult( this, getOrder(), ACTIVITY_REQUEST_CODE_GET_CONTACT_DETAILS );

      return;
      }

    super.onClick( view );
    }


  ////////// IPricingConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the pricing agent returns the order pricing.
   *
   *****************************************************/
  @Override
  public void paOnSuccess( int requestId, OrderPricing pricing )
    {
    // We only use the pricing if it matches the last request we made
    if ( requestId == mPricingRequestId )
      {
      setOrderPricing( pricing );
      }
    }


  /*****************************************************
   *
   * Called when there is an error returning the order pricing.
   *
   *****************************************************/
  @Override
  public void paOnError( int requestId, Exception exception )
    {
    Log.e( LOG_TAG, "Unable to get pricing", exception );

    // TODO
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Displays the basket.
   *
   *****************************************************/
  private void loadAndDisplayBasket()
    {
    mBasketOrder = null;

    mBasketItemList = BasketAgent.getInstance( this ).getAllItems( mCatalogue );

    onGotBasket();
    }


  /*****************************************************
   *
   * Displays the basket.
   *
   *****************************************************/
  private void onGotBasket()
    {
    // Set up the adaptor

    mBasketAdaptor = new BasketAdaptor();

    mListView.setAdapter( mBasketAdaptor );


    // If there are no items in the basket - show the empty text

    if ( mBasketItemList != null && mBasketItemList.size() > 0 )
      {
      mBasketEmptyTextView.setVisibility( View.GONE );
      }
    else
      {
      mBasketEmptyTextView.setVisibility( View.VISIBLE );
      }


    // If we already have an address - get the prices

    if ( mShippingAddress != null )
      {
      onShippingAddress();
      }
    }


  /*****************************************************
   *
   * Returns the order. If we are in managed check-out, this
   * will be the one we were supplied, otherwise we create a
   * new one from the basket items.
   *
   *****************************************************/
  private Order getOrder()
    {
    if ( mIsManagedCheckout ) return ( mManagedOrder );


    if ( mBasketOrder == null )
      {
      // Create an order from the basket items

      mBasketOrder = new Order();

      for ( BasketItem item : mBasketItemList )
        {
        Product product       = item.getProduct();
        int     orderQuantity = item.getOrderQuantity();

        product.getUserJourneyType().addJobsToOrder( product, orderQuantity, item.getOptionsMap(), item.getImageSpecList(), mBasketOrder );
        }
      }


    return ( mBasketOrder );
    }


  /*****************************************************
   *
   * Called when we have a new shipping address.
   *
   *****************************************************/
  private void onNewShippingDetails()
    {
    // Save the shipping details for this session

    KiteSDK kiteSDK = KiteSDK.getInstance( this );

    kiteSDK.setAppParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_SHIPPING_ADDRESS, mShippingAddress );
    kiteSDK.setAppParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_CONTACT_EMAIL,    mContactEmail );
    kiteSDK.setAppParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_CONTACT_PHONE,    mContactPhone );

    onShippingAddress();
    }


  /*****************************************************
   *
   * Called when we have a shipping address.
   *
   *****************************************************/
  private void onShippingAddress()
    {
    mDeliveryAddressTextView.setText( mShippingAddress.toSingleLineText() );

    // The Checkout button is only enabled when we have an address
    setRightButtonEnabled( true );

    requestPrices();
    }


  /*****************************************************
   *
   * Requests the prices.
   *
   *****************************************************/
  private void requestPrices()
    {
    // CLear the current prices
    mTotalShippingPriceTextView.setText( null );
    mTotalPriceTextView.setText( null );


    // Re-request the pricing if the shipping address changes, as the shipping price may
    // have changed.

    OrderPricing pricing = PricingAgent.getInstance().requestPricing( this, getOrder(), NO_PROMO_CODE_YET, this, ++ mPricingRequestId );

    if ( pricing != null )
      {
      setOrderPricing( pricing );
      }
    }


  /*****************************************************
   *
   * Sets the order pricing.
   *
   *****************************************************/
  private void setOrderPricing( OrderPricing pricing )
    {
    // Display the shipping & total prices

    mTotalShippingPriceTextView.setText( pricing.getTotalShippingCost().getDefaultDisplayAmountWithFallback() );

    mTotalPriceTextView.setText( getString( R.string.Total ) + " " + pricing.getTotalCost().getDefaultDisplayAmountWithFallback() );
    }


  /*****************************************************
   *
   * Called to continue shopping.
   *
   *****************************************************/
  private void continueShopping()
    {
    setResult( ACTIVITY_RESULT_CODE_CONTINUE_SHOPPING );

    finish();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Adaptor for basket items List view.
   *
   *****************************************************/
  private class BasketAdaptor extends BaseAdapter
    {
    @Override
    public int getCount()
      {
      return ( mBasketItemList.size() );
      }


    @Override
    public Object getItem( int position )
      {
      return ( mBasketItemList.get( position ) );
      }


    @Override
    public long getItemId( int position )
      {
      return ( 0 );
      }


    @Override
    public View getView( int position, View convertView, ViewGroup parent )
      {
      // Get the view and view holder

      View       view;
      Object     tag;
      ViewHolder viewHolder;

      if ( convertView != null &&
           ( tag = convertView.getTag() ) != null &&
           tag instanceof ViewHolder )
        {
        view       = convertView;
        viewHolder = (ViewHolder)tag;
        }
      else
        {
        view       = getLayoutInflater().inflate( R.layout.list_item_basket, parent, false );
        viewHolder = new ViewHolder( view );
        }


      // Set up the view / holder
      viewHolder.bind( position );


      return ( view );
      }


    /*****************************************************
     *
     * View holder for basket items.
     *
     *****************************************************/
    private class ViewHolder implements View.OnClickListener
      {
      private int         mPosition;
      private BasketItem  mBasketItem;
      private Product     mProduct;

      private ImageView   mProductImageView;
      private TextView    mQuantityTextView;

      private Button      mDecrementButton;
      private Button      mIncrementButton;

      private View        mEditTouchFrame;

      private TextView    mProductNameTextView;
      private TextView    mPriceTextView;


      ViewHolder( View view )
        {
        mProductImageView    = (ImageView)view.findViewById( R.id.product_image_view );
        mQuantityTextView    = (TextView)view.findViewById( R.id.quantity_text_view );

        mDecrementButton     = (Button)view.findViewById( R.id.decrement_button );
        mIncrementButton     = (Button)view.findViewById( R.id.increment_button );

        mEditTouchFrame      = view.findViewById( R.id.edit_touch_frame );

        mProductNameTextView = (TextView)view.findViewById( R.id.product_name_text_view );

        mPriceTextView       = (TextView)view.findViewById( R.id.price_text_view );

        mDecrementButton.setOnClickListener( this );
        mIncrementButton.setOnClickListener( this );
        mEditTouchFrame.setOnClickListener( this );
        }


      @Override
      public void onClick( View view )
        {
        BasketAgent basketAgent = BasketAgent.getInstance( BasketActivity.this );


        // Check for edit

        if ( view == mEditTouchFrame )
          {
          ProductCreationActivity.startForResult( BasketActivity.this, mBasketItem.getId(), mBasketItem.getProduct(), mBasketItem.getOptionsMap(), mBasketItem.getImageSpecList(), mBasketItem.getOrderQuantity(), ACTIVITY_REQUEST_CODE_EDIT_BASKET_ITEM );

          return;
          }


        // Check for quantity change

        int orderQuantity = mBasketItem.getOrderQuantity();

        if ( view == mDecrementButton )
          {
          // Try to decrement the order quantity for this job

          orderQuantity = basketAgent.decrementOrderQuantity( mBasketItem.getId() );
          }
        else if ( view == mIncrementButton )
          {
          // Try to increment the order quantity for this job

          orderQuantity = basketAgent.incrementOrderQuantity( mBasketItem.getId() );
          }


        // The order will have changed, so clear any cached one.
        mBasketOrder = null;


        // If order quantity goes down to 0, remove the job and refresh the whole basket list.
        // Otherwise update the order quantity for the job, and display the new quantity on
        // screen.

        if ( orderQuantity > 0 )
          {
          mBasketItem.setOrderQuantity( orderQuantity );

          setQuantityDependentText();

          // If we have a shipping address - re-request the prices
          if ( mShippingAddress != null ) requestPrices();
          }
        else
          {
          loadAndDisplayBasket();
          }
        }


      void bind( int position )
        {
        // Save the position in the view holder
        mPosition = position;


        // Get the appropriate basket item, and populate the view

        mBasketItem = (BasketItem)getItem( position );
        mProduct    = mBasketItem.getProduct();

        ImageAgent.with( BasketActivity.this )
                .load( mProduct.getDisplayImageURL(), KiteSDK.IMAGE_CATEGORY_PRODUCT_ITEM )
                .reduceColourSpace()
                .resizeForDimen( mProductImageView, R.dimen.basket_item_image_width, R.dimen.basket_item_height )
                .onlyScaleDown()
                .into( mProductImageView );

        setQuantityDependentText();


        mProductNameTextView.setText( mProduct.getName() );


        if ( mIsManagedCheckout )
          {
          mDecrementButton.setVisibility( View.INVISIBLE );
          mIncrementButton.setVisibility( View.INVISIBLE );
          }
        else
          {
          mDecrementButton.setVisibility( View.VISIBLE );
          mIncrementButton.setVisibility( View.VISIBLE );
          }
        }


      private void setQuantityDependentText()
        {
        mQuantityTextView.setText( String.valueOf( mBasketItem.getOrderQuantity() ) );
        mPriceTextView.setText( mProduct.getDisplayPriceMultipliedBy( mBasketItem.getOrderQuantity() ) );
        }
      }

    }

  }

