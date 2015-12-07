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

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ly.kite.R;
import ly.kite.address.Country;
import ly.kite.analytics.Analytics;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.ProductOption;
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.AKiteFragment;
import ly.kite.catalogue.MultipleCurrencyAmount;
import ly.kite.catalogue.MultipleDestinationShippingCosts;
import ly.kite.catalogue.Product;
import ly.kite.catalogue.SingleUnitSize;
import ly.kite.catalogue.UnitOfLength;
import ly.kite.catalogue.SingleCurrencyAmount;
import ly.kite.catalogue.SingleDestinationShippingCost;
import ly.kite.widget.BellInterpolator;
import ly.kite.widget.PagingDots;
import ly.kite.widget.SlidingOverlayFrame;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment displays a product overview.
 *
 *****************************************************/
public class ProductOverviewFragment extends AKiteFragment implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  public static final String      TAG                                       = "ProductOverviewFragment";

  public static final String      BUNDLE_KEY_PRODUCT                        = "product";

  private static final long       PAGING_DOT_ANIMATION_DURATION_MILLIS      = 300L;
  private static final float      PAGING_DOT_ANIMATION_OPAQUE               = 1.0f;
  private static final float      PAGING_DOT_ANIMATION_TRANSLUCENT          = 0.5f;
  private static final float      PAGING_DOT_ANIMATION_NORMAL_SCALE         = 1.0f;

  private static final long       SLIDE_ANIMATION_DURATION_MILLIS           = 500L;
  private static final long       OPEN_CLOSE_ICON_ANIMATION_DELAY_MILLIS    = 250L;
  private static final long       OPEN_CLOSE_ICON_ANIMATION_DURATION_MILLIS = SLIDE_ANIMATION_DURATION_MILLIS - OPEN_CLOSE_ICON_ANIMATION_DELAY_MILLIS;

  private static final float      OPEN_CLOSE_ICON_ROTATION_UP               = -180f;
  private static final float      OPEN_CLOSE_ICON_ROTATION_DOWN             = 0f;

  private static final String     BUNDLE_KEY_SLIDING_DRAWER_IS_EXPANDED     = "slidingDrawerIsExpanded";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Product                  mProduct;

  private View                     mOverlaidComponents;
  private ViewPager                mProductImageViewPager;
  private PagingDots               mPagingDots;
  private Button                   mOverlaidStartButton;
  private SlidingOverlayFrame      mSlidingOverlayFrame;
  private View                     mDrawerControlLayout;
  private ImageView                mOpenCloseDrawerIconImageView;
  private Button                   mProceedOverlayButton;

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

    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

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


    // Get the product

    Bundle arguments = getArguments();

    if ( arguments == null )
      {
      Log.e( TAG, "No arguments found" );

      mKiteActivity.displayModalDialog(
        R.string.alert_dialog_title_no_arguments,
        R.string.alert_dialog_message_no_arguments,
        AKiteActivity.NO_BUTTON,
        null,
        R.string.Cancel,
        mKiteActivity.new FinishRunnable()
        );

      return;
      }


    mProduct = (Product)arguments.getParcelable( BUNDLE_KEY_PRODUCT );

    if ( mProduct == null )
      {
      Log.e( TAG, "No product found" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_product_not_found,
              R.string.alert_dialog_message_product_not_found,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }

    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    boolean slidingDrawerIsExpanded = false;

    // Get any saved instance state

    if ( savedInstanceState != null )
      {
      slidingDrawerIsExpanded = savedInstanceState.getBoolean( BUNDLE_KEY_SLIDING_DRAWER_IS_EXPANDED, false );
      }
    else
      {
      Analytics.getInstance( mKiteActivity ).trackProductOverviewScreenViewed( mProduct );
      }


    // Set up the screen. Note that the SDK allows for different layouts to be used in place of the standard
    // one, so some of these views are optional and may not actually exist in the current layout.

    View view = layoutInflator.inflate( R.layout.screen_product_overview, container, false );

    mProductImageViewPager              = (ViewPager) view.findViewById( R.id.view_pager );
    mOverlaidComponents                 = view.findViewById( R.id.overlaid_components );
    mPagingDots                         = (PagingDots) view.findViewById( R.id.paging_dots );
    mOverlaidStartButton                = (Button)view.findViewById( R.id.overlaid_start_button );
    mSlidingOverlayFrame                = (SlidingOverlayFrame) view.findViewById( R.id.sliding_overlay_frame );
    mDrawerControlLayout                = view.findViewById( R.id.drawer_control_layout );
    //mProductOptionsLayout               = (ViewGroup)view.findViewById( R.id.product_options_layout );
    mOpenCloseDrawerIconImageView       = (ImageView) view.findViewById( R.id.open_close_drawer_icon_image_view );
    mProceedOverlayButton               = (Button)view.findViewById( R.id.proceed_overlay_button );
    TextView priceTextView              = (TextView) view.findViewById( R.id.price_text_view );
    TextView summaryDescriptionTextView = (TextView)view.findViewById( R.id.summary_description_text_view );
    TextView summaryShippingTextView    = (TextView)view.findViewById( R.id.summary_shipping_text_view );
    View     descriptionLayout          = view.findViewById( R.id.description_layout );
    TextView descriptionTextView        = (TextView)view.findViewById( R.id.description_text_view );
    View     sizeLayout                 = view.findViewById( R.id.size_layout );
    TextView sizeTextView               = (TextView) view.findViewById( R.id.size_text_view );
    View     quantityLayout             = view.findViewById( R.id.quantity_layout );
    TextView quantityTextView           = (TextView) view.findViewById( R.id.quantity_text_view );
    TextView shippingTextView           = (TextView) view.findViewById( R.id.shipping_text_view );


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


    mProductImageViewPager.setOnPageChangeListener( mPagingDots );

    if ( mOverlaidComponents != null ) mOverlaidComponents.setAlpha( slidingDrawerIsExpanded ? 0f : 1f );  // If the drawer starts open, these components need to be invisible


    if ( mSlidingOverlayFrame != null )
      {
      mSlidingOverlayFrame.snapToExpandedState( slidingDrawerIsExpanded );
      mSlidingOverlayFrame.setSlideAnimationDuration( SLIDE_ANIMATION_DURATION_MILLIS );
      mOpenCloseDrawerIconImageView.setRotation( slidingDrawerIsExpanded ? OPEN_CLOSE_ICON_ROTATION_DOWN : OPEN_CLOSE_ICON_ROTATION_UP );
      }


    // Populate any product options. The activity is responsible for this because custom apps
    // will want to do their own thing.
    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).poOnPopulateOptions( mProduct, view );
      }


    SingleUnitSize                   size             = mProduct.getSizeWithFallback( UnitOfLength.CENTIMETERS );
    boolean                          formatAsInt      = size.getWidth() == (int) size.getWidth() && size.getHeight() == (int) size.getHeight();
    String                           sizeFormatString = getString( formatAsInt ? R.string.product_size_format_string_int : R.string.product_size_format_string_float );
    String                           sizeString       = String.format( sizeFormatString, size.getWidth(), size.getHeight(), size.getUnit().shortString( mKiteActivity ) );

    int                              quantityPerSheet = mProduct.getQuantityPerSheet();
    MultipleDestinationShippingCosts shippingCosts    = mProduct.getShippingCosts();

    Locale                           locale           = Locale.getDefault();
    Country                          country          = Country.getInstance( locale );


    SingleCurrencyAmount singleCurrencyCost;


    // Price

    if ( isVisible( priceTextView ) )
      {
      singleCurrencyCost = mProduct.getCostWithFallback( locale );

      if ( singleCurrencyCost != null ) priceTextView.setText( singleCurrencyCost.getDisplayAmountForLocale( locale ) );
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

        quantityTextView.setText( getString( R.string.product_quantity_format_string, quantityPerSheet ) );
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
        MultipleCurrencyAmount multipleCurrencyShippingCost = singleDestinationShippingCosts.getCost();

        for ( SingleCurrencyAmount singleCurrencyShippingCost : multipleCurrencyShippingCost.asCollection() )
          {
          if ( singleCurrencyShippingCost.isNonZero() )
            {
            freeShippingEverywhere = false;
            }
          }
        }

      if ( freeShippingEverywhere )
        {
        summaryShippingTextView.setText( R.string.product_free_worldwide_shipping );
        }
      else
        {
        summaryShippingTextView.setText( getString( R.string.product_shipping_summary_format_string, shippingCosts.getDisplayCost( locale ) ) );
        }
      }


    // Shipping (postage)

    if ( isVisible( shippingTextView ) )
      {
      List<SingleDestinationShippingCost> sortedShippingCostList = mProduct.getSortedShippingCosts( country );

      StringBuilder shippingCostsStringBuilder = new StringBuilder();

      String newlineString = "";

      for ( SingleDestinationShippingCost singleDestinationShippingCost : sortedShippingCostList )
        {
        // We want to prepend a new line for every shipping destination except the first

        shippingCostsStringBuilder.append( newlineString );

        newlineString = "\n";


        // Get the cost in the default currency for the locale, and format the amount.

        singleCurrencyCost = singleDestinationShippingCost.getCost().getDefaultAmountWithFallback();

        if ( singleCurrencyCost != null )
          {
          String formatString = getString( R.string.product_shipping_format_string );

          String costString = ( singleCurrencyCost.isNonZero()
                  ? singleCurrencyCost.getDisplayAmountForLocale( locale )
                  : getString( R.string.product_free_shipping ) );

          shippingCostsStringBuilder
                  .append( String.format( formatString, singleDestinationShippingCost.getDestinationDescription( mKiteActivity ), costString ) );
          }

        shippingTextView.setText( shippingCostsStringBuilder.toString() );
        }
      }


    if ( mProceedOverlayButton != null )
      {
      mProceedOverlayButton.setText( R.string.product_overview_start_button_text );

      mProceedOverlayButton.setOnClickListener( this );
      }


    mProductImageViewPager.setOnClickListener( this );

    if ( mDrawerControlLayout != null ) mDrawerControlLayout.setOnClickListener( this );

    if ( mOverlaidStartButton != null ) mOverlaidStartButton.setOnClickListener( this );


    return ( view );
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
    super.onSaveInstanceState( outState );

    // Save the state of the sliding drawer
    if ( mSlidingOverlayFrame != null )
      {
      outState.putBoolean( BUNDLE_KEY_SLIDING_DRAWER_IS_EXPANDED, mSlidingOverlayFrame.sliderIsExpanded() );
      }
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

    if ( mProduct != null ) mKiteActivity.setTitle( mProduct.getName() );

    // Set up the product image gallery
    mProductImageAdaptor = new ProductImagePagerAdaptor( mKiteActivity, mProduct.getImageURLList(), this );
    mProductImageViewPager.setAdapter( mProductImageAdaptor );
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

    if ( view == mDrawerControlLayout )
      {
      toggleSliderState();
      }
    else
      {
      onCreateProduct( mProduct );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Populates the product options layout.
   *
   *****************************************************/
//  protected void populateProductOptions( ViewGroup productOptionsLayout, Product product )
//    {
//    LayoutInflater layoutInflater = LayoutInflater.from( mKiteActivity );
//
//
//    // Go through each of the options. Create a view for each one, and a spinner adaptor
//    // for the option values. We also need to save a reference to the spinner so we can
//    // get the chosen options when we move on.
//
//    for ( ProductOption option : product.getOptionList() )
//      {
//      View     optionView    = layoutInflater.inflate( R.layout.product_option, null );
//
//      TextView nameTextView  = (TextView)optionView.findViewById( R.id.option_name_text_view );
//      Spinner  valuesSpinner = (Spinner)optionView.findViewById( R.id.option_values_spinner );
//
//      nameTextView.setText( option.getName() );
//
//      ArrayAdapter<ProductOption.Value> valueArrayAdaptor = new ArrayAdapter<>( mKiteActivity, R.layout.list_item_product_option_value, R.id.value_text_view, option.getValueList() );
//
//      valuesSpinner.setAdapter( valueArrayAdaptor );
//
//      productOptionsLayout.addView( optionView );
//
//
//      // Save the spinner for the option code
//      mOptionCodeSpinnerMap.put( option.getCode(), valuesSpinner );
//      }
//    }


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


    if ( mOverlaidComponents != null )
      {
      mOverlaidComponents.setAlpha( 1f );               // Clear any alpha already applied
      mOverlaidComponents.startAnimation( overlaidComponentsAnimation );
      }

    if ( mOpenCloseDrawerIconImageView != null )
      {
      mOpenCloseDrawerIconImageView.setRotation( 0f );  // Clear any rotation already applied
      mOpenCloseDrawerIconImageView.startAnimation( openCloseIconAnimation );
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

