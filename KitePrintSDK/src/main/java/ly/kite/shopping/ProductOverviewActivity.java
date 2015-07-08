/*****************************************************
 *
 * ProductOverviewActivity.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.shopping;


///// Import(s) /////


///// Class Declaration /////

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.address.Country;
import ly.kite.print.Asset;
import ly.kite.print.Product;
import ly.kite.print.ProductManager;
import ly.kite.print.SingleUnitSize;
import ly.kite.print.UnitOfLength;
import ly.kite.widget.BellInterpolator;
import ly.kite.widget.PagingDots;
import ly.kite.widget.SlidingOverlayFrame;

/*****************************************************
 *
 * This activity displays a product overview.
 *
 *****************************************************/
public class ProductOverviewActivity extends AKiteActivity implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                                   = "ProductOverviewActivity";

  public  static final String      INTENT_EXTRA_NAME_ASSET_LIST              = KiteSDK.INTENT_PREFIX + ".AssetList";
  public  static final String      INTENT_EXTRA_NAME_PRODUCT_ID              = KiteSDK.INTENT_PREFIX + ".ProductId";

  private static final BigDecimal  BIG_DECIMAL_ZERO                          = BigDecimal.valueOf( 0 );

  private static final long        PAGING_DOT_ANIMATION_DURATION_MILLIS      = 300L;
  private static final float       PAGING_DOT_ANIMATION_OPAQUE               = 1.0f;
  private static final float       PAGING_DOT_ANIMATION_TRANSLUCENT          = 0.5f;
  private static final float       PAGING_DOT_ANIMATION_NORMAL_SCALE         = 1.0f;

  private static final long        SLIDE_ANIMATION_DURATION_MILLIS           = 500L;
  private static final long        OPEN_CLOSE_ICON_ANIMATION_DELAY_MILLIS    = 250L;
  private static final long        OPEN_CLOSE_ICON_ANIMATION_DURATION_MILLIS = SLIDE_ANIMATION_DURATION_MILLIS - OPEN_CLOSE_ICON_ANIMATION_DELAY_MILLIS;

  private static final float       OPEN_CLOSE_ICON_ROTATION_UP               = -180f;
  private static final float       OPEN_CLOSE_ICON_ROTATION_DOWN             =    0f;

  private static final String      BUNDLE_KEY_SLIDING_DRAWER_IS_EXPANDED     = "slidingDrawerIsExpanded";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<Asset>         mAssetArrayList;
  private Product                  mProduct;

  private View                     mOverlaidComponents;
  private ViewPager                mProductImageViewPager;
  private PagingDots               mPagingDots;
  private Button                   mOverlaidStartButton;
  private SlidingOverlayFrame      mSlidingOverlayFrame;
  private ImageView                mOpenCloseDrawerIconImageView;

  private PagerAdapter             mProductImageAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  static void start( Context context, ArrayList<Asset> assetArrayList, Product product )
    {
    Intent intent = new Intent( context, ProductOverviewActivity.class );

    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST, assetArrayList );
    intent.putExtra( INTENT_EXTRA_NAME_PRODUCT_ID, product.getId() );

    context.startActivity( intent );
    }


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Get the assets and product

    Intent intent = getIntent();

    if ( intent == null )
      {
      Log.e( LOG_TAG, "No intent found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_intent,
              R.string.alert_dialog_message_no_intent,
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      return;
      }

    if ( ( mAssetArrayList = intent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST ) ) == null || mAssetArrayList.size() < 1 )
      {
      Log.e( LOG_TAG, "No asset list found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_asset_list,
              R.string.alert_dialog_message_no_asset_list,
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      return;
      }


    String productId = intent.getStringExtra( INTENT_EXTRA_NAME_PRODUCT_ID );

    if ( productId == null )
      {
      Log.e( LOG_TAG, "No product id found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_product_id,
              R.string.alert_dialog_message_no_product_id,
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      return;
      }

    mProduct = ProductManager.getInstance().getProductById( productId );

    if ( mProduct == null )
      {
      Log.e( LOG_TAG, "No product found for id " + productId );

      displayModalDialog(
              R.string.alert_dialog_title_product_not_found,
              getString( R.string.alert_dialog_message_no_product_for_id, productId ),
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      return;
      }


    // Get any saved instance state

    boolean slidingDrawerIsExpanded = false;

    if ( savedInstanceState != null )
      {
      slidingDrawerIsExpanded = savedInstanceState.getBoolean( BUNDLE_KEY_SLIDING_DRAWER_IS_EXPANDED, false );
      }


    // Set up the screen

    setTitle( mProduct.getName() );

    setContentView( R.layout.screen_product_overview );

    mProductImageViewPager        = (ViewPager)findViewById( R.id.view_pager );
    mOverlaidComponents           = findViewById( R.id.overlaid_components );
    mPagingDots                   = (PagingDots)findViewById( R.id.paging_dots );
    mOverlaidStartButton          = (Button)findViewById( R.id.overlaid_start_button );
    mSlidingOverlayFrame          = (SlidingOverlayFrame)findViewById( R.id.sliding_overlay_frame );
    mOpenCloseDrawerIconImageView = (ImageView)findViewById( R.id.open_close_drawer_icon_image_view );
    TextView priceTextView        = (TextView)findViewById( R.id.price_text_view );
    TextView sizeTextView         = (TextView)findViewById( R.id.size_text_view );
    View     quantityLayout       = findViewById( R.id.quantity_layout );
    TextView quantityTextView     = (TextView)findViewById( R.id.quantity_text_view );
    TextView shippingTextView     = (TextView)findViewById( R.id.shipping_text_view );


    mProductImageAdaptor = new ProductImageAdaptor( this, mProduct.getImageURLList(), this );
    mProductImageViewPager.setAdapter( mProductImageAdaptor );
    mProductImageViewPager.setOnClickListener( this );


    // Paging dots

    Animation pagingDotOutAlphaAnimation = new AlphaAnimation( PAGING_DOT_ANIMATION_OPAQUE, PAGING_DOT_ANIMATION_TRANSLUCENT );
    pagingDotOutAlphaAnimation.setFillAfter( true );
    pagingDotOutAlphaAnimation.setDuration( PAGING_DOT_ANIMATION_DURATION_MILLIS );

    Animation pagingDotOutScaleAnimation = new ScaleAnimation(
            0f,
            PAGING_DOT_ANIMATION_NORMAL_SCALE,
            0f,
            PAGING_DOT_ANIMATION_NORMAL_SCALE,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f );

    pagingDotOutScaleAnimation.setFillAfter( true );
    pagingDotOutScaleAnimation.setDuration( PAGING_DOT_ANIMATION_DURATION_MILLIS );
    pagingDotOutScaleAnimation.setInterpolator( new BellInterpolator( 1.0f, 0.8f, true ) );

    AnimationSet pagingDotOutAnimation = new AnimationSet( false );
    pagingDotOutAnimation.addAnimation( pagingDotOutAlphaAnimation );
    pagingDotOutAnimation.addAnimation( pagingDotOutScaleAnimation );
    pagingDotOutAnimation.setFillAfter( true );


    Animation pagingDotInAlphaAnimation = new AlphaAnimation( PAGING_DOT_ANIMATION_TRANSLUCENT, PAGING_DOT_ANIMATION_OPAQUE );
    pagingDotInAlphaAnimation.setFillAfter( true );
    pagingDotInAlphaAnimation.setDuration( PAGING_DOT_ANIMATION_DURATION_MILLIS );

    Animation pagingDotInScaleAnimation = new ScaleAnimation(
            0f,
            PAGING_DOT_ANIMATION_NORMAL_SCALE,
            0f,
            PAGING_DOT_ANIMATION_NORMAL_SCALE,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f );

    pagingDotInScaleAnimation.setFillAfter( true );
    pagingDotInScaleAnimation.setDuration( PAGING_DOT_ANIMATION_DURATION_MILLIS );
    pagingDotInScaleAnimation.setInterpolator( new BellInterpolator( 1.0f, 1.2f ) );

    AnimationSet pagingDotInAnimation = new AnimationSet( false );
    pagingDotInAnimation.addAnimation( pagingDotInAlphaAnimation );
    pagingDotInAnimation.addAnimation( pagingDotInScaleAnimation );
    pagingDotInAnimation.setFillAfter( true );


    mPagingDots.setProperties( mProductImageAdaptor.getCount(), R.drawable.paging_dot_unselected, R.drawable.paging_dot_selected );
    mPagingDots.setOutAnimation( pagingDotOutAlphaAnimation );
    mPagingDots.setInAnimation( pagingDotInAnimation );


    mProductImageViewPager.setOnPageChangeListener( mPagingDots );

    mOverlaidComponents.setAlpha( slidingDrawerIsExpanded ? 0f : 1f );  // If the drawer starts open, these components need to be invisible

    mSlidingOverlayFrame.snapToExpandedState( slidingDrawerIsExpanded );
    mSlidingOverlayFrame.setSlideAnimationDuration( SLIDE_ANIMATION_DURATION_MILLIS );
    mOpenCloseDrawerIconImageView.setRotation( slidingDrawerIsExpanded ? OPEN_CLOSE_ICON_ROTATION_DOWN : OPEN_CLOSE_ICON_ROTATION_UP );


    // Price

    Locale locale = Locale.getDefault();

    SingleCurrencyCost cost = mProduct.getCostWithFallback( locale );

    if ( cost != null ) priceTextView.setText( cost.getDisplayAmountForLocale( locale ) );


    // Size

    SingleUnitSize size = mProduct.getSizeWithFallback( UnitOfLength.CENTIMETERS );

    if ( size != null )
      {
      sizeTextView.setText( String.format( getString( R.string.product_size_format_string ), size.getWidth(), size.getHeight(), size.getUnit().shortString( this ) ) );
      }


    // Quantity

    int quantityPerSheet = mProduct.getQuantityPerSheet();

    if ( quantityPerSheet > 1 )
      {
      quantityLayout.setVisibility( View.VISIBLE );

      quantityTextView.setText( getString( R.string.product_quantity_format_string, quantityPerSheet ) );
      }
    else
      {
      quantityLayout.setVisibility( View.GONE );
      }


    // Shipping (postage)

    Country country = Country.getInstance( locale );

    List<SingleDestinationShippingCost> sortedShippingCostList = mProduct.getSortedShippingCosts( country );

    StringBuilder shippingCostsStringBuilder = new StringBuilder();

    String newlineString = "";

    for ( SingleDestinationShippingCost singleDestinationShippingCost : sortedShippingCostList )
      {
      // We want to prepend a new line for every shipping destination except the first

      shippingCostsStringBuilder.append( newlineString );

      newlineString = "\n";


      // Get the cost in the default currency for the locale, and format the amount.

      cost = singleDestinationShippingCost.getCost().getDefaultCostWithFallback();

      if ( cost != null )
        {
        String formatString = getString( R.string.product_shipping_format_string );

        String costString = ( cost.getAmount().compareTo( BIG_DECIMAL_ZERO ) != 0
                ? cost.getDisplayAmountForLocale( locale )
                : getString( R.string.product_free_shipping ) );

        shippingCostsStringBuilder
                .append( String.format( formatString, singleDestinationShippingCost.getDestinationDescription( this ), costString ) );
        }

      shippingTextView.setText( shippingCostsStringBuilder.toString() );
      }




    //ProductManager.getInstance().getAllProducts( this );
    }


  /*****************************************************
   *
   * Called when the back key is pressed.
   *
   *****************************************************/
  @Override
  public void onBackPressed()
    {
    // If the slider is open - close it
    if ( mSlidingOverlayFrame.sliderIsExpanded() )
      {
      toggleSliderState();

      return;
      }

    // Otherwise do what we would have done normally
    super.onBackPressed();
    }


  /*****************************************************
   *
   * Called to save the state of the instance when (e.g.)
   * killing the activity after changing orientation.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    // Save the state of the sliding drawer
    outState.putBoolean( BUNDLE_KEY_SLIDING_DRAWER_IS_EXPANDED, mSlidingOverlayFrame.sliderIsExpanded() );
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
    // All clicks are assumed to be equivalent to pressing the start button
    onStartClicked( view );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when one of the start creating buttons is clicked.
   *
   *****************************************************/
  public void onStartClicked( View view )
    {
    // Start the product-specific user journey

    UserJourneyCoordinator.getInstance().start( this, mAssetArrayList, mProduct );
    }


  /*****************************************************
   *
   * Called when the details control is clicked.
   *
   *****************************************************/
  public void onDetailsClicked( View view )
    {
    toggleSliderState();
    }


  /*****************************************************
   *
   * Called when the details control is clicked.
   *
   *****************************************************/
  private void toggleSliderState()
    {
    // We want to animation the following:
    //   - Overlaid start button fade in / out
    //   - Sliding drawer up / down
    //   - Open / close drawer icon rotation

    boolean sliderWillBeOpening = ! mSlidingOverlayFrame.sliderIsExpanded();

    float overlaidComponentsInitialAlpha;
    float overlaidComponentsFinalAlpha;

    float openCloseIconInitialRotation;
    float openCloseIconFinalRotation;

    if ( sliderWillBeOpening )
      {
      overlaidComponentsInitialAlpha = 1f;
      overlaidComponentsFinalAlpha   = 0f;

      openCloseIconInitialRotation   = OPEN_CLOSE_ICON_ROTATION_UP;
      openCloseIconFinalRotation     = OPEN_CLOSE_ICON_ROTATION_DOWN;
      }
    else
      {
      overlaidComponentsInitialAlpha = 0f;
      overlaidComponentsFinalAlpha   = 1f;

      openCloseIconInitialRotation   = OPEN_CLOSE_ICON_ROTATION_DOWN;
      openCloseIconFinalRotation     = OPEN_CLOSE_ICON_ROTATION_UP;
      }


    // Create the overlaid components animation
    Animation overlaidComponentsAnimation = new AlphaAnimation( overlaidComponentsInitialAlpha, overlaidComponentsFinalAlpha );
    overlaidComponentsAnimation.setDuration( SLIDE_ANIMATION_DURATION_MILLIS );
    overlaidComponentsAnimation.setFillAfter( true );


    // Create the open/close icon animation.
    // The rotation is delayed, but will finish at the same time as the slide animation.
    Animation openCloseIconAnimation = new RotateAnimation( openCloseIconInitialRotation, openCloseIconFinalRotation, mOpenCloseDrawerIconImageView.getWidth() * 0.5f, mOpenCloseDrawerIconImageView.getHeight() * 0.5f );
    openCloseIconAnimation.setStartOffset( OPEN_CLOSE_ICON_ANIMATION_DELAY_MILLIS );
    openCloseIconAnimation.setDuration( OPEN_CLOSE_ICON_ANIMATION_DURATION_MILLIS );
    openCloseIconAnimation.setFillAfter( true );


    mOverlaidComponents.setAlpha( 1f );               // Clear any alpha already applied
    mOpenCloseDrawerIconImageView.setRotation( 0f );  // Clear any rotation already applied

    mSlidingOverlayFrame.animateToExpandedState( sliderWillBeOpening );

    mOverlaidComponents.startAnimation( overlaidComponentsAnimation );
    mOpenCloseDrawerIconImageView.startAnimation( openCloseIconAnimation );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

