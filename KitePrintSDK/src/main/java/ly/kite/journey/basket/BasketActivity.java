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

import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.address.Address;
import ly.kite.address.AddressBookActivity;
import ly.kite.basket.BasketAgent;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.catalogue.Product;
import ly.kite.checkout.PaymentActivity;
import ly.kite.image.ImageAgent;
import ly.kite.journey.AKiteActivity;
import ly.kite.R;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.pricing.IPricingConsumer;
import ly.kite.pricing.OrderPricing;
import ly.kite.pricing.PricingAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the activity that displays the basket
 * screen.
 *
 *****************************************************/
public class BasketActivity extends AKiteActivity implements ICatalogueConsumer, View.OnClickListener, IPricingConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                    = "BasketActivity";

  static private final String  NO_PROMO_CODE_YET          = null;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ListView       mListView;
  private ProgressBar    mProgressSpinner;
  private TextView       mDeliveryAddressTextView;
  private TextView       mTotalShippingPriceTextView;
  private TextView       mTotalPriceTextView;

  private Catalogue      mCatalogue;

  private Order          mBasket;

  private BasketAdaptor  mBasketAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  static public void start( Context context )
    {
    Intent intent = new Intent( context, BasketActivity.class );

    context.startActivity( intent );
    }


  /*****************************************************
   *
   * Starts this activity for a result.
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


    setContentView( R.layout.screen_basket );

    mListView                   = (ListView)findViewById( R.id.list_view );
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


    // Load the catalogue

    setLeftButtonEnabled( false );
    setRightButtonEnabled( false );

    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.VISIBLE );

    KiteSDK.getInstance( this ).getCatalogueLoader().requestCatalogue( this );
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
    if ( requestCode == ACTIVITY_REQUEST_CODE_GET_ADDRESS && resultCode == RESULT_OK )
      {
      Address shippingAddress = data.getParcelableExtra( AddressBookActivity.EXTRA_ADDRESS );

      mBasket.setShippingAddress( shippingAddress );

      mDeliveryAddressTextView.setText( shippingAddress.toSingleLineText() );

      // The Checkout button is only enabled when we have an address
      setRightButtonEnabled( true );


      // Re-request the pricing if the shipping address changes, as the shipping price may
      // have changed.

      OrderPricing pricing = PricingAgent.getInstance().requestPricing( this, mBasket, NO_PROMO_CODE_YET, this );

      if ( pricing != null )
        {
        setOrderPricing( pricing );
        }


      return;
      }


    super.onActivityResult( requestCode, resultCode, data );
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
    // Go to payment screen

    PaymentActivity.startForResult( this, mBasket, ACTIVITY_REQUEST_CODE_CHECKOUT );
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
   * Displays the basket.
   *
   *****************************************************/
  private void loadAndDisplayBasket()
    {
    mBasket = BasketAgent.getInstance( this ).getBasket( mCatalogue );


    mBasketAdaptor = new BasketAdaptor( mBasket );

    mListView.setAdapter( mBasketAdaptor );
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
      AddressBookActivity.startForResult( this, ACTIVITY_REQUEST_CODE_GET_ADDRESS );

      return;
      }

    super.onClick( view );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the pricing agent returns the order pricing.
   *
   *****************************************************/
  @Override
  public void paOnSuccess( OrderPricing pricing )
    {
    setOrderPricing( pricing );
    }


  /*****************************************************
   *
   * Called when there is an error returning the order pricing.
   *
   *****************************************************/
  @Override
  public void paOnError( Exception exception )
    {
    Log.e( LOG_TAG, "Unable to get pricing", exception );

    // TODO
    }


  ////////// Method(s) //////////

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
    private Order      mBasket;
    private List<Job>  mJobList;


    BasketAdaptor( Order basket )
      {
      mBasket  = basket;
      mJobList = basket.getJobs();
      }


    @Override
    public int getCount()
      {
      return ( mJobList.size() );
      }


    @Override
    public Object getItem( int position )
      {
      return ( mJobList.get( position ) );
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
      private int        mPosition;
      private Job        mJob;

      private ImageView  mProductImageView;
      private TextView   mQuantityTextView;

      private Button     mDecrementButton;
      private Button     mIncrementButton;

      private TextView   mProductNameTextView;
      private TextView   mPriceTextView;


      ViewHolder( View view )
        {
        mProductImageView    = (ImageView)view.findViewById( R.id.product_image_view );
        mQuantityTextView    = (TextView)view.findViewById( R.id.quantity_text_view );

        mDecrementButton     = (Button)view.findViewById( R.id.decrement_button );
        mIncrementButton     = (Button)view.findViewById( R.id.increment_button );

        mProductNameTextView = (TextView)view.findViewById( R.id.product_name_text_view );

        mPriceTextView       = (TextView)view.findViewById( R.id.price_text_view );

        mDecrementButton.setOnClickListener( this );
        mIncrementButton.setOnClickListener( this );
        }


      @Override
      public void onClick( View view )
        {
        BasketAgent basketAgent = BasketAgent.getInstance( BasketActivity.this );


        int orderQuantity = mJob.getOrderQuantity();

        if ( view == mDecrementButton )
          {
          // Try to decrement the order quantity for this job

          orderQuantity = basketAgent.decrementOrderQuantity( mJob.getId() );
          }
        else if ( view == mIncrementButton )
          {
          // Try to increment the order quantity for this job

          orderQuantity = basketAgent.incrementOrderQuantity( mJob.getId() );
          }


        // If order quantity goes down to 0, remove the job and refresh the whole basket list.
        // Otherwise update the order quantity for the job, and display the new quantity on
        // screen.

        if ( orderQuantity > 0 )
          {
          mJob.setOrderQuantity( orderQuantity );

          setQuantityText();
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


        // Get the appropriate job, and populate the view

        mJob = (Job)getItem( position );

        ImageAgent.with( BasketActivity.this )
                .load( mJob.getProduct().getDisplayImageURL(), KiteSDK.IMAGE_CATEGORY_PRODUCT_ITEM )
                .reduceColourSpace()
                .resizeForDimen( mProductImageView, R.dimen.basket_item_image_width, R.dimen.basket_item_height )
                .onlyScaleDown()
                .into( mProductImageView );

        setQuantityText();


        Product product = mJob.getProduct();

        mProductNameTextView.setText( product.getName() );

        mPriceTextView.setText( product.getDisplayPrice() );
        }


      private void setQuantityText()
        {
        mQuantityTextView.setText( String.valueOf( mJob.getOrderQuantity() ) );
        }
      }

    }

  }

