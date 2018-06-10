/*****************************************************
 *
 * ProductOverviewFragment.java
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

package ly.kite.journey.selection;


///// Import(s) /////

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.address.Country;
import ly.kite.analytics.Analytics;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.ProductOption;
import ly.kite.journey.AKiteActivity;
import ly.kite.catalogue.MultipleCurrencyAmounts;
import ly.kite.catalogue.MultipleDestinationShippingCosts;
import ly.kite.catalogue.Product;
import ly.kite.catalogue.SingleUnitSize;
import ly.kite.catalogue.UnitOfLength;
import ly.kite.catalogue.SingleCurrencyAmounts;
import ly.kite.catalogue.SingleDestinationShippingCost;
import ly.kite.animation.BellInterpolator;
import ly.kite.widget.PagingDots;
import ly.kite.widget.SlidingOverlayFrame;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment displays a product overview.
 *
 *****************************************************/
public class ProductOverviewFragment extends AProductSelectionFragment implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  static public final String      TAG                                       = "ProductOverviewFragment";

  static public final String      BUNDLE_KEY_PRODUCT_ID                     = "productId";

  static private final long       PAGING_DOT_ANIMATION_DURATION_MILLIS      = 300L;
  static private final float      PAGING_DOT_ANIMATION_OPAQUE               = 1.0f;
  static private final float      PAGING_DOT_ANIMATION_TRANSLUCENT          = 0.5f;
  static private final float      PAGING_DOT_ANIMATION_NORMAL_SCALE         = 1.0f;

  static private final long       SLIDE_ANIMATION_DURATION_MILLIS           = 500L;
  static private final long       OPEN_CLOSE_ICON_ANIMATION_DELAY_MILLIS    = 250L;
  static private final long       OPEN_CLOSE_ICON_ANIMATION_DURATION_MILLIS = SLIDE_ANIMATION_DURATION_MILLIS - OPEN_CLOSE_ICON_ANIMATION_DELAY_MILLIS;

  static private final float      CLOSE_ICON_ROTATION_UP                    = -180f;
  static private final float      CLOSE_ICON_ROTATION_DOWN                  = 0f;

  static private final float      OPEN_ICON_ROTATION_UP                     = 0f;
  static private final float      OPEN_ICON_ROTATION_DOWN                   = 180f;

  static private final String     BUNDLE_KEY_EXPAND_SLIDING_DRAWER          = "expandSlidingDrawer";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String                   mProductId;
  private boolean                  mExpandSlidingDrawerAtStart;
  private boolean                  mTrackAnalytics;

  private Product                  mProduct;

  private View                     mContentView;
  private View                     mOverlaidComponents;
  private ViewPager                mProductImageViewPager;
  private PagingDots               mPagingDots;
  private View                     mThemableMainStartView;
  private View                     mNonThemableMainStartView;
  private View                     mMainStartView;  // References either themable or non-themable view
  private SlidingOverlayFrame      mSlidingOverlayFrame;
  private View                     mToggleDrawerView;
  private View                     mOpenDrawerView;
  private View                     mCloseDrawerView;

  // We are sometimes supplied a close drawer icon and sometimes an open drawer icon. The layout file can
  // use either the id open_drawer_icon_image_view or close_drawer_icon_image_view, and the code will do
  // the correct thing.
  private ImageView                mCloseDrawerIconImageView;
  private ImageView                mOpenDrawerIconImageView;

  private TextView                 mProceedOverlayTextView;
  private TextView                 mCTABarRightTextView;
  private View                     mThemableDrawerStartView;
  private View                     mNonThemableDrawerStartView;
  private View                     mDrawerStartView;

  private PagerAdapter             mProductImageAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static ProductOverviewFragment newInstance( Product product )
    {
    ProductOverviewFragment fragment = new ProductOverviewFragment();

    Bundle arguments = new Bundle();

    arguments.putString( BUNDLE_KEY_PRODUCT_ID, product.getId() );

    fragment.setArguments( arguments );

    return (fragment);
    }


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Get the product from the arguments

    Bundle arguments = getArguments();

    if ( arguments == null )
      {
      Log.e( TAG, "No arguments found" );

      mKiteActivity.displayModalDialog(
        R.string.kitesdk_alert_dialog_title_no_arguments,
        R.string.kitesdk_alert_dialog_message_no_arguments,
        AKiteActivity.NO_BUTTON,
        null,
        R.string.kitesdk_Cancel,
        mKiteActivity.new FinishRunnable()
        );

      return;
      }


    mProductId = arguments.getString( BUNDLE_KEY_PRODUCT_ID, null );

    if ( mProductId == null )
      {
      Log.e( TAG, "No product id found" );

      mKiteActivity.displayModalDialog(
              R.string.kitesdk_alert_dialog_title_product_not_found,
              R.string.kitesdk_alert_dialog_message_product_not_found,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.kitesdk_Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }


    // Get the sliding drawer state from any saved instance state

    if ( savedInstanceState != null )
      {
      mExpandSlidingDrawerAtStart = savedInstanceState.getBoolean( BUNDLE_KEY_EXPAND_SLIDING_DRAWER, false );
      }
    else
      {
      mTrackAnalytics = true;
      }


    setHasOptionsMenu( true );


    // Get the catalogue from the activity
    requestCatalogue();
    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    // Set up the screen. Note that the SDK allows for different layouts to be used in place of the standard
    // one, so some of these views are optional and may not actually exist in the current layout.

    mContentView = layoutInflator.inflate( R.layout.screen_product_overview, container, false );

    mProductImageViewPager              = (ViewPager)mContentView.findViewById( R.id.view_pager );
    mOverlaidComponents                 = mContentView.findViewById( R.id.overlaid_components );
    mPagingDots                         = (PagingDots)mContentView.findViewById( R.id.paging_dots );
    mThemableMainStartView              = mContentView.findViewById( R.id.themable_main_start_view );
    mNonThemableMainStartView           = mContentView.findViewById( R.id.non_themable_main_start_view );
    mSlidingOverlayFrame                = (SlidingOverlayFrame)mContentView.findViewById( R.id.sliding_overlay_frame );
    mToggleDrawerView                   = mContentView.findViewById( R.id.toggle_drawer_view );
    mOpenDrawerView                     = mContentView.findViewById( R.id.open_drawer_view );
    mCloseDrawerView                    = mContentView.findViewById( R.id.close_drawer_view );
    mCloseDrawerIconImageView           = (ImageView)mContentView.findViewById( R.id.close_drawer_icon_image_view );
    mOpenDrawerIconImageView            = (ImageView)mContentView.findViewById( R.id.open_drawer_icon_image_view );
    mProceedOverlayTextView             = (TextView)mContentView.findViewById( R.id.proceed_overlay_text_view );
    mThemableDrawerStartView            = mContentView.findViewById( R.id.themable_drawer_start_view );
    mNonThemableDrawerStartView         = mContentView.findViewById( R.id.non_themable_drawer_start_view );
    mCTABarRightTextView                = (TextView)mContentView.findViewById( R.id.cta_bar_right_text_view );

    // Determine the main start view
    if ( mThemableMainStartView != null ) mMainStartView = mThemableMainStartView;
    else                                  mMainStartView = mNonThemableMainStartView;

    // Determine the drawer start view
    if      ( mProceedOverlayTextView  != null ) mDrawerStartView = mProceedOverlayTextView;
    else if ( mThemableDrawerStartView != null ) mDrawerStartView = mThemableDrawerStartView;
    else                                         mDrawerStartView = mNonThemableDrawerStartView;


    // Check if we can set up the the screen yet
    onCheckSetUpScreen();


    return ( mContentView );
    }


  /*****************************************************
   *
   * Called when the back key is pressed. The fragment
   * can either intercept it, or ignore it - in which case
   * the default behaviour is performed.
   *
   *****************************************************/
  @Override
  public boolean onBackPressIntercepted()
    {
    // If the slider is open - close it
    if ( mSlidingOverlayFrame != null && mSlidingOverlayFrame.sliderIsExpanded() )
      {
      toggleSliderState();

      return ( true );
      }

    return ( false );
    }


  /*****************************************************
   *
   * Called to save the state of the instance when (e.g.)
   * changing orientation.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    if ( KiteSDK.DEBUG_SAVE_INSTANCE_STATE ) Log.d( TAG, "--> onSaveInstanceState( outState = " + outState + " )" );

    super.onSaveInstanceState( outState );

    // Save the state of the sliding drawer
    if ( mSlidingOverlayFrame != null )
      {
      outState.putBoolean( BUNDLE_KEY_EXPAND_SLIDING_DRAWER, mSlidingOverlayFrame.sliderIsExpanded() );
      }

    if ( KiteSDK.DEBUG_SAVE_INSTANCE_STATE ) Log.d( TAG, "<-- onSaveInstanceState( outState = " + outState + " )" );
    }


  /*****************************************************
   *
   * Called when the fragment is top-most.
   *
   *****************************************************/
  @Override
  public void onTop()
    {
    super.onTop();

    if ( mProduct != null )
      {
      mKiteActivity.setTitle( mProduct.getName() );

      setUpProductImageGallery();
      }
    }


  /*****************************************************
   *
   * Called when the fragment is not on top.
   *
   *****************************************************/
  @Override
  public void onNotTop()
    {
    super.onNotTop();


    // Clear out the stored images to reduce memory usage
    // when not on this screen.

    if ( mProductImageViewPager != null ) mProductImageViewPager.setAdapter( null );

    mProductImageAdaptor = null;
    }


  /*****************************************************
   *
   * Called to create the menu.
   *
   *****************************************************/
  @Override
  public void onCreateOptionsMenu( Menu menu, MenuInflater menuInflator )
    {
    menuInflator.inflate( R.menu.product_overview, menu );
    }


  ////////// ICatalogueConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onCatalogueSuccess( Catalogue catalogue )
    {
    super.onCatalogueSuccess( catalogue );


    // Now we have the catalogue, we can get try and get the
    // product from its id.

    mProduct = catalogue.findProductById( mProductId );

    if ( mProduct != null )
      {
      onCheckSetUpScreen();

      if ( mTrackAnalytics )
        {
        mTrackAnalytics = false;

        Analytics.getInstance( mKiteActivity ).trackProductDetailsScreenViewed( mProduct );
        }
      }
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
    // Anything that's not the drawer control is assumed to be
    // one of the start buttons.

    if ( view == mToggleDrawerView )
      {
      toggleSliderState();

      return;
      }
    else if ( mOpenDrawerView != null && view == mOpenDrawerView )
      {
      if ( mSlidingOverlayFrame != null && ! mSlidingOverlayFrame.sliderIsExpanded() ) toggleSliderState();

      return;
      }
    else if ( mCloseDrawerView != null && view == mCloseDrawerView )
      {
      if ( mSlidingOverlayFrame != null && mSlidingOverlayFrame.sliderIsExpanded() ) toggleSliderState();

      return;
      }
    else if ( view == mProductImageViewPager )
      {
      // If the sliding drawer is open then clicking on the product view will close it. Otherwise
      // we go to the next screen.
      if ( mSlidingOverlayFrame != null && mSlidingOverlayFrame.sliderIsExpanded() )
        {
        toggleSliderState();

        return;
        }
      }


    // Anything not already captured will take us to product creation
    onCreateProduct( mProduct );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Checks that we have both the view and the product,
   * and populates the screen.
   *
   *****************************************************/
  private void onCheckSetUpScreen()
    {
    if ( mProduct == null || mContentView == null ) return;

    TextView originalPriceTextView      = (TextView)mContentView.findViewById( R.id.original_price_text_view );
    TextView priceTextView              = (TextView)mContentView.findViewById( R.id.price_text_view );
    TextView summaryDescriptionTextView = (TextView)mContentView.findViewById( R.id.summary_description_text_view );
    TextView summaryShippingTextView    = (TextView)mContentView.findViewById( R.id.summary_shipping_text_view );
    View     descriptionLayout          = mContentView.findViewById( R.id.description_layout );
    TextView descriptionTextView        = (TextView)mContentView.findViewById( R.id.description_text_view );
    View     sizeLayout                 = mContentView.findViewById( R.id.size_layout );
    TextView sizeTextView               = (TextView)mContentView.findViewById( R.id.size_text_view );
    View     quantityLayout             = mContentView.findViewById( R.id.quantity_layout );
    TextView quantityTextView           = (TextView)mContentView.findViewById( R.id.quantity_text_view );
    TextView shippingTextView           = (TextView)mContentView.findViewById( R.id.shipping_text_view );


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

    mPagingDots.setProperties( mProduct.getImageURLList().size(), R.drawable.paging_dot_unselected, R.drawable.paging_dot_selected );
    mPagingDots.setOutAnimation( pagingDotOutAlphaAnimation );
    mPagingDots.setInAnimation( pagingDotInAnimation );


    // Product images

    setUpProductImageGallery();

    mProductImageViewPager.setOnPageChangeListener( mPagingDots );


    if ( mOverlaidComponents != null ) mOverlaidComponents.setAlpha( mExpandSlidingDrawerAtStart ? 0f : 1f );  // If the drawer starts open, these components need to be invisible


    if ( mSlidingOverlayFrame != null )
      {
      mSlidingOverlayFrame.snapToExpandedState( mExpandSlidingDrawerAtStart );
      mSlidingOverlayFrame.setSlideAnimationDuration( SLIDE_ANIMATION_DURATION_MILLIS );

      if ( mCloseDrawerIconImageView != null ) mCloseDrawerIconImageView.setRotation( mExpandSlidingDrawerAtStart ? CLOSE_ICON_ROTATION_DOWN : CLOSE_ICON_ROTATION_UP );
      if ( mOpenDrawerIconImageView  != null ) mOpenDrawerIconImageView.setRotation(  mExpandSlidingDrawerAtStart ? OPEN_ICON_ROTATION_DOWN  : OPEN_ICON_ROTATION_UP );
      }


    // Populate any product options. The activity is responsible for this because custom apps
    // will want to do their own thing.
    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).poOnPopulateOptions( mProduct, mContentView );
      }


    SingleUnitSize                   size             = mProduct.getSizeWithFallback( UnitOfLength.CENTIMETERS );
    boolean                          formatAsInt      = size.getWidth() == (int) size.getWidth() && size.getHeight() == (int) size.getHeight();
    String                           sizeFormatString = getString( formatAsInt ? R.string.kitesdk_product_size_format_string_int : R.string.kitesdk_product_size_format_string_float);
    String                           sizeString       = String.format( sizeFormatString, size.getWidth(), size.getHeight(), size.getUnit().shortString( mKiteActivity ) );

    int                              quantityPerSheet = mProduct.getQuantityPerSheet();
    MultipleDestinationShippingCosts shippingCosts    = mProduct.getShippingCosts();

    Locale                           locale           = Locale.getDefault();
    Country                          country          = Country.getInstance( locale );


    SingleCurrencyAmounts singleCurrencyCost = mProduct.getCostWithFallback( locale );

    if ( singleCurrencyCost != null )
      {
      // Original price

      if ( singleCurrencyCost.hasOriginalAmount() && isVisible( originalPriceTextView ) )
        {
        originalPriceTextView.setText( singleCurrencyCost.getDisplayOriginalAmountForLocale( locale ) );
        originalPriceTextView.setPaintFlags( originalPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }


      // Price

      if ( isVisible( priceTextView ) )
        {
        priceTextView.setText( singleCurrencyCost.getDisplayAmountForLocale( locale ) );
        }
      }
    else
      {
      Log.e( TAG, "No cost found" );
      }


    // Summary description. This is a short description - not to be confused with the (full) description.

    if ( isVisible( summaryDescriptionTextView ) )
      {
      String summaryDescription =
              String.valueOf( quantityPerSheet )
                      + " "
                      + mProduct.getName()
                      + ( Product.isSensibleSize( size ) ? " (" + sizeString + ")" : "" );

      summaryDescriptionTextView.setText( summaryDescription );
      }


    // (Full) description

    String  description     = mProduct.getDescription();

    boolean haveDescription = ( description != null && ( ! description.trim().equals( "" ) ) );

    if ( haveDescription             &&
            descriptionLayout   != null &&
            descriptionTextView != null )
      {
      descriptionLayout.setVisibility( View.VISIBLE );
      descriptionTextView.setVisibility( View.VISIBLE );

      descriptionTextView.setText( description );
      }
    else
      {
      if ( descriptionLayout   != null ) descriptionLayout.setVisibility( View.GONE );
      if ( descriptionTextView != null ) descriptionTextView.setVisibility( View.GONE );
      }


    // Size

    if ( isVisible( sizeTextView ) )
      {
      if ( Product.isSensibleSize( size ) )
        {
        sizeTextView.setText( String.format( sizeFormatString, size.getWidth(), size.getHeight(), size.getUnit().shortString( mKiteActivity ) ) );
        }
      else
        {
        sizeLayout.setVisibility( View.GONE );
        }
      }


    // Quantity

    if ( isVisible( quantityTextView ) )
      {
      if ( quantityPerSheet > 1 )
        {
        quantityLayout.setVisibility( View.VISIBLE );

        quantityTextView.setText( getString( R.string.kitesdk_product_quantity_format_string, quantityPerSheet ) );
        }
      else
        {
        quantityLayout.setVisibility( View.GONE );
        }
      }


    // Shipping description

    if ( isVisible( summaryShippingTextView ) )
      {
      // Currently we just check that shipping is free everywhere. If it isn't - we don't display
      // anything.

      boolean freeShippingEverywhere = true;

      MultipleDestinationShippingCosts multipleDestinationShippingCosts = shippingCosts;

      for ( SingleDestinationShippingCost singleDestinationShippingCosts : multipleDestinationShippingCosts.asList() )
        {
        MultipleCurrencyAmounts multipleCurrencyShippingCost = singleDestinationShippingCosts.getCost();

        for ( SingleCurrencyAmounts singleCurrencyShippingCost : multipleCurrencyShippingCost.asCollection() )
          {
          if ( singleCurrencyShippingCost.isNonZero() )
            {
            freeShippingEverywhere = false;
            }
          }
        }

      if ( freeShippingEverywhere )
        {
        summaryShippingTextView.setText( R.string.kitesdk_product_free_worldwide_shipping);
        }
      else
        {
        summaryShippingTextView.setText( getString( R.string.kitesdk_product_shipping_summary_format_string, shippingCosts.getDisplayCost( locale ) ) );
        }
      }


    // Shipping (postage)

    if ( isVisible( shippingTextView ) )
      {
      List<SingleDestinationShippingCost> sortedShippingCostList = mProduct.getSortedShippingCosts( country );

      SpannableStringBuilder shippingCostsStringBuilder = new SpannableStringBuilder();

      String newlineString = "";

      for ( SingleDestinationShippingCost singleDestinationShippingCost : sortedShippingCostList )
        {
        // We want to prepend a new line for every shipping destination except the first

        shippingCostsStringBuilder.append( newlineString );

        newlineString = "\n";


        // Get the cost in the default currency for the locale, and format the amount.

        singleCurrencyCost = singleDestinationShippingCost.getCost().getAmountsWithFallback( KiteSDK.getInstance( getActivity() ).getLockedCurrencyCode() );

        if ( singleCurrencyCost != null )
          {
          // Add the shipping destination

          String formatString = getString( R.string.kitesdk_product_shipping_destination_format_string);

          shippingCostsStringBuilder
                  .append( String.format( formatString, singleDestinationShippingCost.getDestinationDescription( getActivity() ) ) )
                  .append( "  " );


          // Add any original price

          if ( singleCurrencyCost.hasOriginalAmount() )
            {
            SpannableString originalCostString = new SpannableString( singleCurrencyCost.getDisplayOriginalAmountForLocale( locale ) );

            originalCostString.setSpan( new StrikethroughSpan(), 0, originalCostString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

            shippingCostsStringBuilder
                    .append( originalCostString )
                    .append( "  " );
            }


          String costString = ( singleCurrencyCost.isNonZero()
                  ? singleCurrencyCost.getDisplayAmountForLocale( locale )
                  : getString( R.string.kitesdk_product_free_shipping) );

          shippingCostsStringBuilder.append( costString );
          }

        shippingTextView.setText( shippingCostsStringBuilder, TextView.BufferType.SPANNABLE );
        }
      }


    ///// Buttons /////

    if ( mProceedOverlayTextView != null )
      {
      mProceedOverlayTextView.setText( R.string.kitesdk_product_overview_start_button_text);

      setThemeColour( mCatalogue.getPrimaryThemeColour(), mProceedOverlayTextView );
      }

    if ( mThemableDrawerStartView != null )
      {
      if ( mThemableDrawerStartView instanceof TextView )
        {
        ( (TextView)mThemableDrawerStartView ).setText( R.string.kitesdk_product_overview_start_button_text);
        }

      setThemeColour( mCatalogue.getPrimaryThemeColour(), mThemableDrawerStartView );
      }

    if ( mCTABarRightTextView != null )
      {
      mCTABarRightTextView.setText( R.string.kitesdk_product_overview_start_button_text);

      mCTABarRightTextView.setOnClickListener( this );
      }

    if ( mDrawerStartView != null ) mDrawerStartView.setOnClickListener( this );


    mProductImageViewPager.setOnClickListener( this );


    // Set any theme colour
    setThemeColour( mCatalogue.getSecondaryThemeColour(), mContentView, R.id.drawer_control_themable_view );


    if ( mToggleDrawerView != null ) mToggleDrawerView.setOnClickListener( this );

    if ( mOpenDrawerView   != null ) mOpenDrawerView.setOnClickListener( this );

    if ( mCloseDrawerView  != null ) mCloseDrawerView.setOnClickListener( this );


    if ( mThemableMainStartView != null )
      {
      // Set any theme colour
      setThemeColour( mCatalogue.getPrimaryThemeColour(), mThemableMainStartView );
      }


    if ( mMainStartView != null )
      {
      mMainStartView.setOnClickListener( this );
      }
    }


  /*****************************************************
   *
   * Sets up the product image gallery.
   *
   *****************************************************/
  private void setUpProductImageGallery()
    {
    mProductImageAdaptor = new ProductImagePagerAdaptor( mKiteActivity, mProduct.getImageURLList(), this );
    mProductImageViewPager.setAdapter( mProductImageAdaptor );
    }


  /*****************************************************
   *
   * Returns true if a view is visible, false otherwise.
   *
   *****************************************************/
  private boolean isVisible( View view )
    {
    return ( view != null && view.getVisibility() == View.VISIBLE );
    }


  /*****************************************************
   *
   * Returns a map of product option codes to chosen value
   * codes.
   *
   *****************************************************/
  protected HashMap<String,String> getOptionMap( HashMap<String,Spinner> optionCodeSpinnerMap )
    {
    HashMap<String,String> optionMap = new HashMap<>( optionCodeSpinnerMap.size() );


    // Go through the product options

    for ( String optionCode : optionCodeSpinnerMap.keySet() )
      {
      Spinner             optionSpinner = optionCodeSpinnerMap.get( optionCode );
      ProductOption.Value optionValue   = (ProductOption.Value)optionSpinner.getSelectedItem();

      optionMap.put( optionCode, optionValue.getCode() );
      }


    return ( optionMap );
    }


  /*****************************************************
   *
   * Called when one of the start creating buttons is clicked.
   *
   *****************************************************/
  public void onCreateProduct( Product product )
    {
    // Call back to the activity
    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback) mKiteActivity ).poOnCreateProduct( mProduct );
      }
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

    float closeIconInitialRotation;
    float closeIconFinalRotation;

    float openIconInitialRotation;
    float openIconFinalRotation;

    if ( sliderWillBeOpening )
      {
      overlaidComponentsInitialAlpha = 1f;
      overlaidComponentsFinalAlpha   = 0f;

      closeIconInitialRotation       = CLOSE_ICON_ROTATION_UP;
      closeIconFinalRotation         = CLOSE_ICON_ROTATION_DOWN;

      openIconInitialRotation        = OPEN_ICON_ROTATION_UP;
      openIconFinalRotation          = OPEN_ICON_ROTATION_DOWN;
      }
    else
      {
      overlaidComponentsInitialAlpha = 0f;
      overlaidComponentsFinalAlpha   = 1f;

      closeIconInitialRotation       = CLOSE_ICON_ROTATION_DOWN;
      closeIconFinalRotation         = CLOSE_ICON_ROTATION_UP;

      openIconInitialRotation        = OPEN_ICON_ROTATION_DOWN;
      openIconFinalRotation          = OPEN_ICON_ROTATION_UP;
      }

    if ( mOverlaidComponents != null )
      {
      // Create the overlaid components animation
      Animation overlaidComponentsAnimation = new AlphaAnimation( overlaidComponentsInitialAlpha, overlaidComponentsFinalAlpha );
      overlaidComponentsAnimation.setDuration( SLIDE_ANIMATION_DURATION_MILLIS );
      overlaidComponentsAnimation.setFillAfter( true );

      mOverlaidComponents.setAlpha( 1f );               // Clear any alpha already applied
      mOverlaidComponents.startAnimation( overlaidComponentsAnimation );
      }


    // Create the open/close icon animation.
    // The rotation is delayed, but will finish at the same time as the slide animation.

    if ( mCloseDrawerIconImageView != null )
      {
      Animation openCloseIconAnimation = new RotateAnimation( closeIconInitialRotation, closeIconFinalRotation, mCloseDrawerIconImageView.getWidth() * 0.5f, mCloseDrawerIconImageView.getHeight() * 0.5f );
      openCloseIconAnimation.setStartOffset( OPEN_CLOSE_ICON_ANIMATION_DELAY_MILLIS );
      openCloseIconAnimation.setDuration( OPEN_CLOSE_ICON_ANIMATION_DURATION_MILLIS );
      openCloseIconAnimation.setFillAfter( true );

      mCloseDrawerIconImageView.setRotation( 0f );  // Clear any rotation already applied
      mCloseDrawerIconImageView.startAnimation( openCloseIconAnimation );
      }

    if ( mOpenDrawerIconImageView != null )
      {
      Animation openCloseIconAnimation = new RotateAnimation( openIconInitialRotation, openIconFinalRotation, mOpenDrawerIconImageView.getWidth() * 0.5f, mOpenDrawerIconImageView.getHeight() * 0.5f );
      openCloseIconAnimation.setStartOffset( OPEN_CLOSE_ICON_ANIMATION_DELAY_MILLIS );
      openCloseIconAnimation.setDuration( OPEN_CLOSE_ICON_ANIMATION_DURATION_MILLIS );
      openCloseIconAnimation.setFillAfter( true );

      mOpenDrawerIconImageView.setRotation( 0f );  // Clear any rotation already applied
      mOpenDrawerIconImageView.startAnimation( openCloseIconAnimation );
      }


    mSlidingOverlayFrame.animateToExpandedState( sliderWillBeOpening );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void poOnPopulateOptions( Product product, View rootView );
    public void poOnCreateProduct( Product product );
    }

  }

