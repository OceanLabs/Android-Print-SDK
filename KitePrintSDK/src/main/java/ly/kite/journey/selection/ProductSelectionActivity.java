/*****************************************************
 *
 * ProductSelectionActivity.java
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.CatalogueLoaderFragment;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.catalogue.ProductOption;
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.creation.ProductCreationActivity;
import ly.kite.ordering.ImageSpec;
import ly.kite.util.Asset;
import ly.kite.catalogue.Product;
import ly.kite.catalogue.ProductGroup;
import ly.kite.widget.HeaderFooterGridView;


///// Class Declaration /////

/*****************************************************
 *
 * This activity coordinates the various fragments involved
 * in selecting a product. Once the product has been selected,
 * it hands over to the product creation activity, which
 * starts the appropriate fragments for the UI class / user
 * journey type.
 *
 *****************************************************/
public class ProductSelectionActivity extends AKiteActivity implements ICatalogueHolder,
                                                                       ICatalogueConsumer,
                                                                       ChooseProductGroupFragment.ICallback,
                                                                       ChooseProductFragment.ICallback,
                                                                       ProductOverviewFragment.ICallback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                                     = "ProductSelectionAct.";  // Can't be more than 23 characters ... who knew?!

  static private final String  INTENT_EXTRA_NAME_ASSET_LIST                = KiteSDK.INTENT_PREFIX + ".assetList";
  static private final String  INTENT_EXTRA_NAME_FILTER_PRODUCT_IDS        = KiteSDK.INTENT_PREFIX + ".filterProductIds";
  static private final String  INTENT_EXTRA_NAME_GOTO_PRODUCT_GROUP_LABEL  = KiteSDK.INTENT_PREFIX + ".gotoProductGroupLabel";
  static private final String  INTENT_EXTRA_NAME_GOTO_PRODUCT_ID           = KiteSDK.INTENT_PREFIX + ".gotoProductId";

  static private final String  BUNDLE_KEY_OPTION_MAP                       = "optionMap";
  static private final String  BUNDLE_KEY_ADD_FRAGMENT_ON_CATALOGUE        = "addFragmentOnCatalogue";

  static private final String  JSON_NAME_PAYMENT_KEYS                      = "payment_keys";
  static private final String  JSON_NAME_PAYPAL                            = "paypal";
  static private final String  JSON_NAME_STRIPE                            = "stripe";
  static private final String  JSON_NAME_PUBLIC_KEY                        = "public_key";
  static private final String  JSON_NAME_ACCOUNT_ID                        = "account_id";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<ImageSpec>          mImageSpecArrayList;
  private String[]                      mFilterProductIds;
  private String                        mGotoProductGroupLabel;
  private String                        mGotoProductId;

  private ProgressBar                   mProgressSpinner;
  private ChooseProductGroupFragment    mChooseProductGroupFragment;
  private ChooseProductFragment         mChooseProductFragment;
  private ProductOverviewFragment       mProductOverviewFragment;

  private CatalogueLoaderFragment       mCatalogueLoaderFragment;
  private Catalogue                     mCatalogue;
  private ICatalogueConsumer            mCatalogueConsumer;
  private boolean                       mAddFragmentOnCatalogue;

  private HashMap<String,String>        mProductOptionValueMap;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  private static Intent getIntent( Context context, ArrayList<Asset> assetArrayList )
    {
    Intent intent = new Intent( context, ProductSelectionActivity.class );

    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST, assetArrayList );

    return ( intent );
    }


  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  public static void start( Activity activity, ArrayList<Asset> assetArrayList, String... filterProductIds )
    {
    Intent intent = getIntent( activity, assetArrayList );

    if ( filterProductIds != null && filterProductIds.length > 0 )
      {
      intent.putExtra( INTENT_EXTRA_NAME_FILTER_PRODUCT_IDS, filterProductIds );
      }

    activity.startActivity( intent );
    }


  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  public static void startInProductGroup( Activity activity, ArrayList<Asset> assetArrayList, String productGroupLabel )
    {
    Intent intent = getIntent( activity, assetArrayList );

    if ( productGroupLabel != null )
      {
      intent.putExtra( INTENT_EXTRA_NAME_GOTO_PRODUCT_GROUP_LABEL, productGroupLabel );
      }

    activity.startActivity( intent );
    }


  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  public static void startInProduct( Activity activity, ArrayList<Asset> assetArrayList, String productId )
    {
    Intent intent = getIntent( activity, assetArrayList );

    if ( productId != null )
      {
      intent.putExtra( INTENT_EXTRA_NAME_GOTO_PRODUCT_ID, productId );
      }

    activity.startActivity( intent );
    }


  /*****************************************************
   *
   * Converts an asset array list into an asset + quantity
   * array list, with the quantities set to 1.
   *
   *****************************************************/
  private static ArrayList<ImageSpec> imageSpecArrayListFrom( ArrayList<Asset> assetArrayList )
    {
    ArrayList<ImageSpec> imageSpecArrayList = new ArrayList<>( assetArrayList.size() );

    for ( Asset asset : assetArrayList )
      {
      imageSpecArrayList.add( new ImageSpec( asset ) );
      }

    return ( imageSpecArrayList );
    }


  /*****************************************************
   *
   * Returns the value from a list corresponding to the supplied
   * value code, or the first, if the code is not found or null.
   *
   *****************************************************/
  static protected ProductOption.Value getValueForCode( List<ProductOption.Value> valueList, String soughtValueCode )
    {
    if ( soughtValueCode != null )
      {
      for ( ProductOption.Value candidateOptionValue : valueList )
        {
        if ( candidateOptionValue.getCode().equals( soughtValueCode ) ) return ( candidateOptionValue );
        }
      }

    return ( valueList.get( 0 ) );
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


    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
      {
      getWindow().setStatusBarColor( getResources().getColor( R.color.translucent_status_bar ) );
      }



    // Get the assets. Note that the asset list may be null, since some apps allow assets to be
    // chosen at a later stage, in which case we create an empty one here.

    ArrayList<Asset> assetArrayList = null;
    Intent           intent         = getIntent();

    if ( intent != null )
      {
      assetArrayList         = intent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST );
      mFilterProductIds      = intent.getStringArrayExtra( INTENT_EXTRA_NAME_FILTER_PRODUCT_IDS );
      mGotoProductGroupLabel = intent.getStringExtra( INTENT_EXTRA_NAME_GOTO_PRODUCT_GROUP_LABEL );
      mGotoProductId         = intent.getStringExtra( INTENT_EXTRA_NAME_GOTO_PRODUCT_ID );
      }

    if ( assetArrayList == null ) assetArrayList = new ArrayList<Asset>();


    // We convert the asset list into an assets and quantity list (long before we get
    // to cropping and editing) because if the user comes out of product creation, and
    // goes back into another product - we want to remember quantities.

    mImageSpecArrayList = imageSpecArrayListFrom( assetArrayList );


    // Set up the screen content

    setContentView( R.layout.screen_product_selection );

    mProgressSpinner = (ProgressBar)findViewById( R.id.progress_spinner );


    // We need to get a filtered product catalogue before we create any fragments,
    // since depending on how many products are returned - we can either start
    // the choose group, choose product, or product overview fragment.

    if ( savedInstanceState == null )
      {
      mAddFragmentOnCatalogue = true;
      mProductOptionValueMap  = new HashMap<>();
      }
    else
      {
      mAddFragmentOnCatalogue = savedInstanceState.getBoolean( BUNDLE_KEY_ADD_FRAGMENT_ON_CATALOGUE );
      mProductOptionValueMap  = (HashMap<String,String>)savedInstanceState.getSerializable( BUNDLE_KEY_OPTION_MAP );
      }


    mCatalogueLoaderFragment = CatalogueLoaderFragment.findFragment( this );

    requestCatalogue();
    }


  /*****************************************************
   *
   * Called when an activity result is received.
   *
   *****************************************************/
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    // See if we got an updated assets + quantity list

    if ( data != null )
      {
      ArrayList<ImageSpec> imageSpecArrayList = data.getParcelableArrayListExtra( INTENT_EXTRA_NAME_IMAGE_SPEC_LIST );

      if ( imageSpecArrayList != null ) mImageSpecArrayList = imageSpecArrayList;
      }


    // If we get a continue shopping result - go back to the product group screen

    if ( resultCode == ACTIVITY_RESULT_CODE_CONTINUE_SHOPPING )
      {
      mFragmentManager.popBackStackImmediate( ChooseProductGroupFragment.TAG, 0 );

      return;
      }


    // The parent method will check for the checkout result.
    super.onActivityResult( requestCode, resultCode, data );
    }


  /*****************************************************
   *
   * Called to save the instance state.
   *
   *****************************************************/
  @Override
  protected void onSaveInstanceState( Bundle outState )
    {
    if ( KiteSDK.DEBUG_SAVE_INSTANCE_STATE ) Log.d( LOG_TAG, "--> onSaveInstanceState( outState = " + outState + " )" );

    super.onSaveInstanceState( outState );

    // Save the selected product options, so we can restore them later
    if ( outState != null )
      {
      outState.putSerializable( BUNDLE_KEY_OPTION_MAP, mProductOptionValueMap );
      outState.putBoolean( BUNDLE_KEY_ADD_FRAGMENT_ON_CATALOGUE, mAddFragmentOnCatalogue );
      }

    if ( KiteSDK.DEBUG_SAVE_INSTANCE_STATE )
      {
      logFragments();

      Log.d( LOG_TAG, "<-- onSaveInstanceState( outState = " + outState + " ) size = " + parcelFromBundle( outState ).dataSize() );
      }
    }


  /*****************************************************
   *
   * Called some time after the activity is no longer visible.
   *
   *****************************************************/
  @Override
  protected void onStop()
    {
    super.onStop();

    if ( mCatalogueLoaderFragment != null )
      {
      mCatalogueLoaderFragment.cancelRequests();

      mCatalogueLoaderFragment.removeFrom( this );

      mCatalogueLoaderFragment = null;
      }
    }


  ////////// ICatalogueHolder Method(s) //////////

  /*****************************************************
   *
   * Returns a catalogue.
   *
   *****************************************************/
  @Override
  public void requestCatalogue( ICatalogueConsumer consumer )
    {
    if ( mCatalogue != null )
      {
      consumer.onCatalogueSuccess( mCatalogue );
      }
    else
      {
      // We request a catalogue immediately the activity is created, so if we don't yet
      // have a catalogue - we assume the request is still in progress. So all we need to
      // do here is save the consumer. We'll deliver the catalogue to it when we get it.

      mCatalogueConsumer = consumer;
      }
    }


  ////////// CatalogueLoader.ICatalogueConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the catalogue is loaded successfully.
   *
   *****************************************************/
  @Override
  public void onCatalogueSuccess( Catalogue catalogue )
    {
    onCatalogueRequestComplete();

    // Some apps may wish to amend the catalogue
    mCatalogue = getAdjustedCatalogue( catalogue );


    // See if there are any PayPay / Stripe keys in the catalogue custom data
    //
    //  "payment_keys":
    //    {
    //    "paypal":
    //      {
    //      "public_key": "AUxpFaaJlAcWZ92UNGsxGYTGwblzI1upclHVIOv5NZGcM-LY-dMEhH66KNrtSfrUlGSXqLhdpPrlhezl",
    //      "account_id": "P-3"
    //      },
    //    "stripe":
    //      {0
    //      "public_key": "pk_test_FxzXniUJWigFysP0bowWbuy3",
    //      "account_id": null
    //      }
    //    }

    JSONObject paymentKeysJSONObject = catalogue.getCustomObject( JSON_NAME_PAYMENT_KEYS );

    if ( KiteSDK.DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  Payment keys = " + paymentKeysJSONObject );

    if ( paymentKeysJSONObject != null )
      {
      // Check for PayPal keys

      KiteSDK kiteSDK = KiteSDK.getInstance( this );

      JSONObject payPalJSONObject = paymentKeysJSONObject.optJSONObject( JSON_NAME_PAYPAL );

      if ( KiteSDK.DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  PayPal payment key = " + payPalJSONObject );

      if ( payPalJSONObject != null )
        {
        String publicKey = ( ! payPalJSONObject.isNull( JSON_NAME_PUBLIC_KEY ) ? payPalJSONObject.optString( JSON_NAME_PUBLIC_KEY ) : null );
        String accountId = ( ! payPalJSONObject.isNull( JSON_NAME_ACCOUNT_ID ) ? payPalJSONObject.optString( JSON_NAME_ACCOUNT_ID ) : null );

        if ( KiteSDK.DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  PayPal client id = " + publicKey + ", account id = " + accountId );

        kiteSDK.setPermanentPayPalKey( publicKey, accountId );
        }
      else
        {
        if ( KiteSDK.DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  Removing permanent PayPal key" );

        kiteSDK.removePermanentPayPalKey();
        }


      // Check for Stripe keys

      JSONObject stripeJSONObject = paymentKeysJSONObject.optJSONObject( JSON_NAME_STRIPE );

      if ( KiteSDK.DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  Stripe payment key = " + stripeJSONObject );

      if ( stripeJSONObject != null )
        {
        String publicKey = ( ! stripeJSONObject.isNull( JSON_NAME_PUBLIC_KEY ) ? stripeJSONObject.optString( JSON_NAME_PUBLIC_KEY ) : null );
        String accountId = ( ! stripeJSONObject.isNull( JSON_NAME_ACCOUNT_ID ) ? stripeJSONObject.optString( JSON_NAME_ACCOUNT_ID ) : null );

        if ( KiteSDK.DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  Stripe public key = " + publicKey + ", account id = " + accountId );

        kiteSDK.setPermanentStripeKey( publicKey, accountId );
        }
      else
        {
        if ( KiteSDK.DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  Removing permanent Stripe key" );

        kiteSDK.removePermanentStripeKey();
        }
      }


    // If this is the first time we have been created - start the first fragment
    if ( mAddFragmentOnCatalogue )
      {
      mAddFragmentOnCatalogue = false;

      onDisplayFirstFragment();
      }


    // Deliver the catalogue to any waiting consumer

    if ( mCatalogueConsumer != null )
      {
      mCatalogueConsumer.onCatalogueSuccess( mCatalogue );

      mCatalogueConsumer = null;
      }
    }


  /*****************************************************
   *
   * Called when the catalogue load fails.
   *
   *****************************************************/
  @Override
  public void onCatalogueError( Exception exception )
    {
    onCatalogueRequestComplete();

    mCatalogue = null;

    if ( isVisible() )
      {
      // Display an error dialog
      displayModalDialog
              (
                      R.string.alert_dialog_title_error_retrieving_products,
                      R.string.alert_dialog_message_error_retrieving_products,
                      R.string.Retry,
                      new RequestCatalogueRunnable(),
                      R.string.Cancel,
                      new FinishRunnable()
              );
      }

    }


  ////////// ChooseProductGroupFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called before the product group grid view is populated.
   *
   * Override this to add headers / banners to the grid.
   *
   *****************************************************/
  @Override
  public void pgOnPrePopulateProductGroupGrid( Catalogue catalogue, HeaderFooterGridView headerFooterGridView )
    {
    // The default is to do nothing
    }


  /*****************************************************
   *
   * Called when a header or footer view (i.e. non product
   * group) is clicked.
   *
   *****************************************************/
  @Override
  public void pgOnHeaderOrFooterClicked( int position, int adaptorIndex )
    {
    // The default is to do nothing
    }


  /*****************************************************
   *
   * Called when a product group is chosen.
   *
   *****************************************************/
  @Override
  public void pgOnProductGroupChosen( ProductGroup productGroup )
    {
    // If the product group contains more than one product - display
    // the choose product screen. Otherwise go straight to the product
    // overview.

    List<Product> productList = productGroup.getProductList();

    if ( productList == null || productList.size() > 1 )
      {
      onDisplayChooseProduct( productGroup );

      return;
      }


    onDisplayProductOverview( productList.get( 0 ) );
    }


  ////////// ChooseProductFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when a product group is chosen.
   *
   *****************************************************/
  @Override
  public void pOnProductChosen( Product product )
    {
    onDisplayProductOverview( product );
    }


  ////////// ProductOverviewFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called to populate any product options. The default
   * is to just populate any options layout with spinners.
   *
   *****************************************************/
  @Override
  public void poOnPopulateOptions( Product product, View view )
    {
    // Check that there's a product options layout

    ViewGroup productOptionsLayout = (ViewGroup)view.findViewById( R.id.product_options_layout );

    if ( productOptionsLayout == null )
      {
      return;
      }


    // Get the product options list. If it is empty - hide the layout.

    List<ProductOption> productOptionList = product.getOptionList();

    if ( productOptionList == null || productOptionList.size() < 1 )
      {
      productOptionsLayout.setVisibility( View.GONE );

      return;
      }


    productOptionsLayout.setVisibility( View.VISIBLE );


    // Go through each of the options. Get the view for each one, and add it to the
    // option layout.

    LayoutInflater layoutInflater = LayoutInflater.from( this );

    int optionIndex = 0;

    for ( ProductOption option : productOptionList )
      {
      View optionView = getOptionView( optionIndex, option, layoutInflater, productOptionsLayout );

      if ( optionView != null ) productOptionsLayout.addView( optionView );

      optionIndex ++;
      }
    }


  /*****************************************************
   *
   * Called when the user wishes to create a product.
   *
   *****************************************************/
  @Override
  public void poOnCreateProduct( Product product )
    {
    // Once the product has been chosen and the user clicks "Start Creating",
    // we then hand over to the product creation activity to choose the journey
    // depending on the product.

    ProductCreationActivity.startForResult( this, product, mProductOptionValueMap, mImageSpecArrayList, ACTIVITY_REQUEST_CODE_CREATE );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Requests the catalogue.
   *
   *****************************************************/
  void requestCatalogue()
    {
    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.VISIBLE );

    if ( mCatalogueLoaderFragment == null )
      {
      mCatalogueLoaderFragment = CatalogueLoaderFragment.start( this, mFilterProductIds );
      }
    }


  /*****************************************************
   *
   * Called when the catalogue load completes.
   *
   *****************************************************/
  private void onCatalogueRequestComplete()
    {
    if ( isVisible() )
      {
      // Hide any progress spinner
      if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.GONE );
      }

    // Remove the loader fragment
    if ( mCatalogueLoaderFragment != null )
      {
      mCatalogueLoaderFragment.removeFrom( this );

      mCatalogueLoaderFragment = null;
      }
    }


  /*****************************************************
   *
   * Should be overridden if an app wants to adjust the
   * catalogue in some way, such as to remove shipping
   * to certain locations.
   *
   *****************************************************/
  protected Catalogue getAdjustedCatalogue( Catalogue catalogue )
    {
    // The default implementation is to do nothing
    return ( catalogue );
    }


  /*****************************************************
   *
   * Displays the first fragment.
   *
   *****************************************************/
  private void onDisplayFirstFragment()
    {
    // Determine which fragment we need to start with. This is decided in the
    // following manner:
    //   - If a goto product group was supplied, and it exists, go straight to
    //     the choose product screen for that group
    //   - If a goto product id was supplied, and it exists, go straight to
    //     the product overview screen
    //   - If there is just one product group, go straight to the choose product screen
    //     for that group
    //   - If there is just one product, go straight to its product overview screen
    //   - Otherwise show the the choose product group screen

    ArrayList<ProductGroup> productGroupList = mCatalogue.getProductGroupList();

    if ( productGroupList != null )
      {
      // Check for a goto product group

      if ( mGotoProductGroupLabel != null )
        {
        for ( ProductGroup candidateProductGroup : productGroupList )
          {
          if ( mGotoProductGroupLabel.equalsIgnoreCase( candidateProductGroup.getDisplayLabel() ) )
            {
            onDisplayChooseProduct( candidateProductGroup );

            return;
            }
          }
        }


      // Check for a goto product

      if ( mGotoProductId != null )
        {
        Product gotoProduct = mCatalogue.getProductById( mGotoProductId );

        if ( gotoProduct != null )
          {
          onDisplayProductOverview( gotoProduct );

          return;
          }
        }


      // Check for just one product group

      if ( productGroupList.size() == 1 )
        {
        ProductGroup       productGroup = productGroupList.get( 0 );
        ArrayList<Product> productList  = productGroup.getProductList();

        if ( productList != null )
          {
          // Check for just one product
          if ( productList.size() == 1 )
            {
            onDisplayProductOverview( productList.get( 0 ) );
            }
          else
            {
            onDisplayChooseProduct( productGroup );
            }

          return;
          }
        }
      }


    // Display the choose product group screen, even if its blank (because there are no
    // products).
    onDisplayChooseProductGroup();
    }


  /*****************************************************
   *
   * Displays the choose product group fragment.
   *
   *****************************************************/
  private void onDisplayChooseProductGroup()
    {
    mChooseProductGroupFragment = ChooseProductGroupFragment.newInstance();

    addFragment( mChooseProductGroupFragment, ChooseProductGroupFragment.TAG );
    }


  /*****************************************************
   *
   * Displays the choose product fragment for the supplied
   * product group.
   *
   *****************************************************/
  private void onDisplayChooseProduct( ProductGroup productGroup )
    {
    mChooseProductFragment = ChooseProductFragment.newInstance( productGroup );

    addFragment( mChooseProductFragment, ChooseProductFragment.TAG );
    }


  /*****************************************************
   *
   * Displays the product overview fragment for the supplied
   * product.
   *
   *****************************************************/
  private void onDisplayProductOverview( Product product )
    {
    mProductOverviewFragment = ProductOverviewFragment.newInstance( product );

    addFragment( mProductOverviewFragment, ProductOverviewFragment.TAG );
    }


  /*****************************************************
   *
   * Returns a view for a product option.
   *
   *****************************************************/
  protected View getOptionView( int optionIndex, ProductOption option, LayoutInflater layoutInflator, ViewGroup parent )
    {
    String optionCode = option.getCode();


    // Create a spinner for the option and add it to the layout

    View     optionView    = layoutInflator.inflate( R.layout.product_option, parent, false );

    TextView nameTextView  = (TextView)optionView.findViewById( R.id.option_name_text_view );
    Spinner  valuesSpinner = (Spinner)optionView.findViewById( R.id.option_values_spinner );

    nameTextView.setText( option.getName() );

    ArrayList<ProductOption.Value> valueList = option.getValueList();

    ArrayAdapter<ProductOption.Value> valueArrayAdaptor = new ArrayAdapter<>( this, R.layout.list_item_product_option_value, R.id.value_text_view, valueList );

    valuesSpinner.setAdapter( valueArrayAdaptor );


    // If there is already an entry in the option map - set the spinner value from it. Otherwise
    // create a default entry now.

    String valueCode = mProductOptionValueMap.get( optionCode );

    if ( valueCode != null )
      {
      valuesSpinner.setSelection( valueList.indexOf( valueCode ) );
      }
    else
      {
      mProductOptionValueMap.put( optionCode, valueArrayAdaptor.getItem( 0 ).getCode() );
      }


    // Set up any listener(s)
    onSetOptionSpinnerListeners( optionIndex, option, valuesSpinner, valueList );


    return ( optionView );
    }


  /*****************************************************
   *
   * Sets up any listeners on an options spinner.
   *
   *****************************************************/
  protected void onSetOptionSpinnerListeners( int optionIndex, ProductOption option, Spinner spinner, ArrayList<ProductOption.Value> valueList )
    {
    spinner.setOnItemSelectedListener( new SpinnerItemClickListener( option ) );
    }


  /*****************************************************
   *
   * Sets a product option.
   *
   *****************************************************/
  protected void setValueCodeForOption( String optionCode, String valueCode )
    {
    mProductOptionValueMap.put( optionCode, valueCode );
    }


  /*****************************************************
   *
   * Sets a product option.
   *
   *****************************************************/
  protected void setValueForOption( String optionCode, ProductOption.Value value )
    {
    setValueCodeForOption( optionCode, value.getCode() );
    }


  /*****************************************************
   *
   * Returns a product option code.
   *
   *****************************************************/
  protected String getValueCodeForOption( String optionCode )
    {
    return ( mProductOptionValueMap.get( optionCode ) );
    }


  /*****************************************************
   *
   * Returns the value index corresponding to the supplied
   * option code, or 0, if the code is not found or null.
   *
   *****************************************************/
  protected int getValueIndexForOption( String optionCode, List<ProductOption.Value> valueList )
    {
    String soughtValueCode = getValueCodeForOption( optionCode );

    if ( soughtValueCode != null )
      {
      int valueIndex = 0;

      for ( ProductOption.Value candidateOptionValue : valueList )
        {
        if ( candidateOptionValue.getCode().equals( soughtValueCode ) ) return ( valueIndex );

        valueIndex++;
        }
      }

    return ( 0 );
    }


  /*****************************************************
   *
   * Returns the product option value corresponding to the
   * supplied option code, or the first one, if the code is
   * not found or null.
   *
   *****************************************************/
  protected ProductOption.Value getValueForOption( String optionCode, List<ProductOption.Value> valueList )
    {
    return ( valueList.get( getValueIndexForOption( optionCode, valueList ) ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Requests the catalogue.
   *
   *****************************************************/
  private class RequestCatalogueRunnable implements Runnable
    {
    @Override
    public void run()
      {
      requestCatalogue();
      }
    }


  /*****************************************************
   *
   * Spinner listener.
   *
   *****************************************************/
  protected class SpinnerItemClickListener implements Spinner.OnItemSelectedListener
    {
    private ProductOption           mOption;


    public SpinnerItemClickListener( ProductOption option )
      {
      mOption    = option;
      }


    @Override
    public void onItemSelected( AdapterView<?> parent, View view, int position, long id )
      {
      // Update the option in the option map with the new selected value

      String optionCode = mOption.getCode();
      String valueCode  = ( (ProductOption.Value)parent.getItemAtPosition( position ) ).getCode();

      mProductOptionValueMap.put( optionCode, valueCode );
      }

    @Override
    public void onNothingSelected( AdapterView<?> parent )
      {
      // Ignore
      }
    }

  }

