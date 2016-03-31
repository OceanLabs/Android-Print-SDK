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

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.CatalogueLoader;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.catalogue.ProductOption;
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.journey.creation.ProductCreationActivity;
import ly.kite.journey.creation.reviewandedit.ReviewAndEditFragment;
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
  private static final String  LOG_TAG                         = "ProductSelectionAct.";  // Can't be more than 23 characters ... who knew?!

  private static final String  INTENT_EXTRA_NAME_ASSET_LIST    = KiteSDK.INTENT_PREFIX + ".assetList";
  private static final String  INTENT_EXTRA_NAME_PRODUCT_IDS   = KiteSDK.INTENT_PREFIX + ".productIds";

  private static final String  BUNDLE_KEY_OPTION_MAP           = "optionMap";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<AssetsAndQuantity>  mAssetsAndQuantityArrayList;
  private String[]                      mProductIds;

  private ProgressBar                   mProgressSpinner;
  private ChooseProductGroupFragment    mProductGroupFragment;
  private ChooseProductFragment         mProductFragment;
  private ProductOverviewFragment       mProductOverviewFragment;
  private ReviewAndEditFragment         mReviewAndCropFragment;

  private CatalogueLoader               mCatalogueLoader;
  private Catalogue                     mCatalogue;
  private ICatalogueConsumer            mCatalogueConsumer;
  private boolean                       mAddFragmentOnCatalogue;

  private HashMap<String,String> mProductOptionValueMap;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  public static void start( Context context, ArrayList<Asset> assetArrayList, String... productIds )
    {
    Intent intent = new Intent( context, ProductSelectionActivity.class );

    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST, assetArrayList );

    if ( productIds != null && productIds.length > 0 )
      {
      intent.putExtra( INTENT_EXTRA_NAME_PRODUCT_IDS, productIds );
      }

    context.startActivity( intent );
    }


  /*****************************************************
   *
   * Converts an asset array list into an asset + quantity
   * array list, with the quantities set to 1.
   *
   *****************************************************/
  private static ArrayList<AssetsAndQuantity> assetsAndQuantityArrayListFrom( ArrayList<Asset> assetArrayList )
    {
    ArrayList<AssetsAndQuantity> assetsAndQuantityArrayList = new ArrayList<>( assetArrayList.size() );

    for ( Asset asset : assetArrayList )
      {
      assetsAndQuantityArrayList.add( new AssetsAndQuantity( asset ) );
      }

    return ( assetsAndQuantityArrayList );
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
      assetArrayList = intent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST );
      mProductIds    = intent.getStringArrayExtra( INTENT_EXTRA_NAME_PRODUCT_IDS );
      }

    if ( assetArrayList == null ) assetArrayList = new ArrayList<Asset>();


    // We convert the asset list into an assets and quantity list (long before we get
    // to cropping and editing) because if the user comes out of product creation, and
    // goes back into another product - we want to remember quantities.

    mAssetsAndQuantityArrayList = assetsAndQuantityArrayListFrom( assetArrayList );


    // Set up the screen content

    setContentView( R.layout.screen_product_selection );

    mProgressSpinner = (ProgressBar)findViewById( R.id.progress_spinner );


    // We need to get a filtered product catalogue before we create any fragments,
    // since depending on how many products are returned - we can either start
    // the choose group, choose product, or product overview fragment.

    if ( savedInstanceState == null )
      {
      mAddFragmentOnCatalogue = true;

      mProductOptionValueMap = new HashMap<>();
      }
    else
      {
      mProductOptionValueMap = (HashMap<String,String>)savedInstanceState.getSerializable( BUNDLE_KEY_OPTION_MAP );
      }


    mCatalogueLoader = CatalogueLoader.getInstance( this );

    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.VISIBLE );

    mCatalogueLoader.requestCatalogue( KiteSDK.MAX_ACCEPTED_PRODUCT_AGE_MILLIS, mProductIds, this );
    }


  /*****************************************************
   *
   * Called when an activity result is received.
   *
   *****************************************************/
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    super.onActivityResult( requestCode, resultCode, data );


    // The parent method will check for the checkout result.


    // See if we got an updated assets + quantity list

    if ( data != null )
      {
      ArrayList<AssetsAndQuantity> assetsAndQuantityArrayList = data.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSETS_AND_QUANTITY__LIST );

      if ( assetsAndQuantityArrayList != null ) mAssetsAndQuantityArrayList = assetsAndQuantityArrayList;
      }
    }


  /*****************************************************
   *
   * Called to save the instance state.
   *
   *****************************************************/
  @Override
  protected void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    // Save the selected product options, so we can restore them later
    if ( outState != null ) outState.putSerializable( BUNDLE_KEY_OPTION_MAP, mProductOptionValueMap );
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

    if ( mCatalogueLoader != null ) mCatalogueLoader.cancelRequests();
    }


  ////////// ICatalogueHolder Method(s) //////////

  /*****************************************************
   *
   * Returns a catalogue.
   *
   *****************************************************/
  @Override
  public void getCatalogue( ICatalogueConsumer consumer )
    {
    if ( mCatalogue != null )
      {
      consumer.onCatalogueSuccess( mCatalogue );
      }
    else
      {
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
    // Some apps may wish to amend the catalogue
    mCatalogue = getAdjustedCatalogue( catalogue );

    // Hide the progress spinner
    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.GONE );


    // If this is the first time we have been created - start the first fragment
    if ( mAddFragmentOnCatalogue )
      {
      // Determine which fragment we need to start with

      ArrayList<ProductGroup> productGroupList = mCatalogue.getProductGroupList();

      if ( productGroupList != null && productGroupList.size() > 0 )
        {
        // If there is more than one group - start the choose group fragment
        if ( productGroupList.size() > 1 )
          {
          addFragment( mProductGroupFragment = ChooseProductGroupFragment.newInstance(), ChooseProductGroupFragment.TAG );
          }
        else
          {
          ProductGroup       productGroup = productGroupList.get( 0 );
          ArrayList<Product> productList  = productGroup.getProductList();

          if ( productList != null && productList.size() > 0 )
            {
            // If there is more than one product - start the choose product fragment
            if ( productList.size() > 1 )
              {
              addFragment( mProductFragment = ChooseProductFragment.newInstance( productGroup ), ChooseProductFragment.TAG );
              }
            else
              {
              // There is just one product - go straight to the product overview screen
              onDisplayProductOverview( productList.get( 0 ) );
              }
            }
          }
        }

      }


    // Pass the result on to any consumer
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
    mCatalogue = null;

    if ( isVisible() )
      {
      // Hide the progress spinner
      if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.GONE );

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
   *****************************************************/
  @Override
  public void pgOnPrePopulateProductGroupGrid( Catalogue catalogue, HeaderFooterGridView headerFooterGridView )
    {
    // Do nothing
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
      mProductFragment = ChooseProductFragment.newInstance( productGroup );

      addFragment( mProductFragment, ChooseProductFragment.TAG );

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

    ProductCreationActivity.startForResult( this, mAssetsAndQuantityArrayList, product, mProductOptionValueMap, ACTIVITY_REQUEST_CODE_CHECKOUT );
    }


  ////////// Method(s) //////////

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
      if ( mCatalogueLoader != null )
        {
        if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.VISIBLE );

        mCatalogueLoader.requestCatalogue( KiteSDK.MAX_ACCEPTED_PRODUCT_AGE_MILLIS, mProductIds, ProductSelectionActivity.this );
        }
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

