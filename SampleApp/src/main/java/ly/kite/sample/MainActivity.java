/*****************************************************
 *
 * MainActivity.java
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

package ly.kite.sample;


///// Import(s) /////

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

// Uncomment to include Crashlytics (Fabric) crash reporting
//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

import ly.kite.address.Address;
import ly.kite.app.ADeepLinkableActivity;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.catalogue.Product;
import ly.kite.devicephotopicker.DevicePhotoPicker;
import ly.kite.journey.AImageSource;
import ly.kite.journey.DeviceImageSource;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.ordering.OrderingDatabaseAgent;
import ly.kite.util.Asset;
import ly.kite.KiteSDK;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the activity for thw Kite SDK sample app.
 * It demonstrates how to create some image assets for
 * personalisation, and then how to start the SDK product
 * selection / shopping journey.
 *
 *****************************************************/
public class MainActivity extends ADeepLinkableActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                    = "MainActivity";

  private static final String  REPLACE_DETAILS_HERE       = "REPLACE_ME";

  private static final int    REQUEST_CODE_SELECT_PICTURE = 1;
  private static final int    REQUEST_CODE_CHECKOUT       = 2;


  static private final String DEEP_LINK_URI_SCHEMA        = "kite-sample-app";

  static private final String PRODUCT_ID_POSTCARD         = "postcard";


  /**********************************************************************
   * Insert your Kite API keys here. These are found under your profile
   * by logging in to the developer portal at https://www.kite.ly
   **********************************************************************/
  static private       String API_KEY_TEST                = REPLACE_DETAILS_HERE;
  static private       String API_KEY_STAGING             = REPLACE_DETAILS_HERE;
  static private       String API_KEY_LIVE                = REPLACE_DETAILS_HERE;


  /**********************************************************************
   * Insert your Instagram details here.
   **********************************************************************/
  private static      String INSTAGRAM_API_KEY           = REPLACE_DETAILS_HERE;
  private static      String INSTAGRAM_REDIRECT_URI      = REPLACE_DETAILS_HERE;



  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Switch   mEnvironmentSwitch;

  private KiteSDK  mKiteSDK;


  ////////// Static Initialiser(s) //////////

  static
    {
    }


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    // Uncomment to include Crashlytics (Fabric) crash reporting
    //Fabric.with( this, new Crashlytics() );


    // Set up the screen

    setContentView( R.layout.screen_main );

    mEnvironmentSwitch = (Switch) findViewById( R.id.environment_switch );


    // Check for any deep linking, i.e. if the start intent contains
    // a product group label or product id that we should go to directly.

    findDeepLink( DEEP_LINK_URI_SCHEMA );
    }


  /*****************************************************
   *
   * Called with the result of a called activity.
   *
   *****************************************************/
  public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    if ( requestCode == REQUEST_CODE_CHECKOUT )
      {
      ///// Check out /////

      if ( resultCode == Activity.RESULT_OK )
        {
        Toast.makeText( this, "User successfully checked out!", Toast.LENGTH_LONG ).show();
        }
      else if ( resultCode == Activity.RESULT_CANCELED )
        {
        Toast.makeText( this, "User cancelled checkout :(", Toast.LENGTH_LONG ).show();
        }
      }
    else if ( requestCode == REQUEST_CODE_SELECT_PICTURE )
      {
      ///// Select gallery picture /////

      if ( resultCode == RESULT_OK )
        {
        ArrayList<Asset> assetArrayList = DeviceImageSource.getAssets( data );

        fullJourney( assetArrayList );
        }
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the print local photos button is clicked.
   *
   *****************************************************/
  public void onPrintLocalButtonClicked( View view )
    {
    callRunnableWithPermissions( new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, new Runnable()
      {
      public void run()
        {
        // Launch the picture selector
        DevicePhotoPicker.startPhotoPickerForResult( MainActivity.this, AImageSource.UNLIMITED_IMAGES, REQUEST_CODE_SELECT_PICTURE );
        }
      });
    }


  /*****************************************************
   *
   * Called when the print photos at remove URLs button
   * is clicked.
   *
   *****************************************************/
  public void onPrintRemoteButtonClicked( View view )
    {
    // Create some assets from a combination of a local resource and remote URLs

    ArrayList<Asset> assetArrayList = new ArrayList<>();

    assetArrayList.add( new Asset( R.drawable.instagram1 ) );

    try
      {
      assetArrayList.add( Asset.create( "http://psps.s3.amazonaws.com/sdk_static/1.jpg" ) );
      assetArrayList.add( Asset.create( "http://psps.s3.amazonaws.com/sdk_static/2.jpg" ) );
      assetArrayList.add( Asset.create( "http://psps.s3.amazonaws.com/sdk_static/3.jpg" ) );
      assetArrayList.add( Asset.create( "http://psps.s3.amazonaws.com/sdk_static/4.jpg" ) );

      // If authentication is required the following methods can be used:
      //    Asset.create( stringURL, headerMap) ); //for URL that contains the image format at the end
      // or
      //    Asset.create( stringURL, headerMap , Asset.MIMEType.[JPEG/PNG]) ); //for URL that does not contain the image format at the end
      //
      // example:
      //   Map<String, String> headerMap = new HashMap<>();
      //   headerMap.put( "Authorization", "auth_key_1234abc" );
      //   assetArrayList.add( Asset.create( "http://kyte.ly/url?id=abc123", headerMap , Asset.MIMEType.JPEG) );
      }
    catch ( Exception ex )
      {
        Log.e(LOG_TAG, "Encountered error while adding assets: " + ex.getMessage());
      }

    fullJourney( assetArrayList );
    }


  /*****************************************************
   *
   * Called when the order history button is clicked.
   *
   *****************************************************/
  public void onOrderHistoryButtonClicked( View view )
    {
    KiteSDK kiteSDK = configureSDK();

    if ( kiteSDK == null ) return;

    kiteSDK.startOrderHistory( this );
    }


  /*****************************************************
   *
   * Called when the print local photos button is clicked.
   *
   *****************************************************/
  public void onPrintPostcardButtonClicked( View view )
    {
    postcardJourney();
    }


  /*****************************************************
   *
   * Sets up the SDK.
   *
   *****************************************************/
  private KiteSDK configureSDK()
    {
    String apiKey;
    KiteSDK.IEnvironment environment;


    // Determine the API and environment based on the position
    // of the on-screen switch.

    if ( mEnvironmentSwitch != null && mEnvironmentSwitch.isChecked() )
      {
      apiKey      = API_KEY_LIVE;
      environment = KiteSDK.DefaultEnvironment.LIVE;
      }
    else
      {
      apiKey      = API_KEY_TEST;
      environment = KiteSDK.DefaultEnvironment.TEST;
      }

//    apiKey      = API_KEY_STAGING;
//    environment = KiteSDK.DefaultEnvironment.STAGING_DO_NOT_USE;


    // Check that the API has been set in code
    if ( apiKey.equals( REPLACE_DETAILS_HERE ) )
      {
      showError( "Set API Keys", "Please set your Kite API keys at the top of the SampleApp's MainActivity.java. You can find these by logging into https://www.kite.ly." );

      return ( null );
      }


    // Create an instance of the SDK, and set a customiser

    KiteSDK kiteSDK = KiteSDK.getInstance( this, apiKey, environment );

    kiteSDK.setCustomiser( SampleSDKCustomiser.class );


    // Set Kite analytics, by default this is off

    kiteSDK.setKiteAnalyticsEnabled(false);


    // Add this in if you want to use Google Pay, DO NOT deploy the option
    // in PRODUCTION until your app has been enabled/whitelisted by the
    // Google API team via review here otherwise is WILL NOT WORK:
    // https://services.google.com/fb/forms/googlepayAPIenable/

    // For more details on the Google Pay process:
    // https://developers.google.com/pay/api/android/overview

    //  kiteSDK.setGooglePayPaymentsEnabled(true);


    return ( kiteSDK );
    }


  /*****************************************************
   *
   * Starts the SDK shopping journey with a set of image
   * assets.
   *
   *****************************************************/
  private void fullJourney( ArrayList<Asset> assetList )
    {
    KiteSDK kiteSDK = configureSDK();

    if ( kiteSDK != null )
      {
      kiteSDK

        // Uncomment this if you have defined Instagram credentials
        .setInstagramCredentials( INSTAGRAM_API_KEY, INSTAGRAM_REDIRECT_URI )

        .startShopping( this, assetList );  // Use this to shop all products in catalogue
        //.startShoppingByProductId( this, assets, "pbx_squares_5x5", "pbx_squares_8x8" );  // Use this to shop specific products by id
      }
    }


  /*****************************************************
   *
   * Called when the intent URI contains a product group
   * label.
   *
   *****************************************************/
  @Override
  protected void onDeepLinkProductGroup( String productGroupLabel )
    {
    KiteSDK kiteSDK = configureSDK();

    if ( kiteSDK != null ) kiteSDK.startShoppingForProductGroup( this, null, productGroupLabel );
    }


  /*****************************************************
   *
   * Called when the intent URI contains a product id.
   *
   *****************************************************/
  @Override
  protected void onDeepLinkProductId( String productId )
    {
    KiteSDK kiteSDK = configureSDK();

    if ( kiteSDK != null ) kiteSDK.startShoppingForProduct( this, null, productId );
    }


  /*****************************************************
   *
   * Prints a postcode and goes straight to the checkout
   * activity.
   *
   *****************************************************/
  private void postcardJourney()
    {
    final KiteSDK kiteSDK = configureSDK();

    if ( kiteSDK == null ) return;

    kiteSDK.getCatalogueLoader().requestCatalogue( new ICatalogueConsumer()
      {
      @Override
      public void onCatalogueSuccess( Catalogue catalogue )
        {
        Product product = catalogue.findProductById( PRODUCT_ID_POSTCARD );

        if ( product == null )
          {
          showError( "Invalid Product Id", "No product could be found for product id: " + PRODUCT_ID_POSTCARD );

          return;
          }

        try
          {
          // Create Postcard Job & Add to Order

          Asset frontImage = new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/1.jpg" ) );
          Asset backImage  = new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/2.jpg" ) );

          Job postcard = Job.createPostcardJob(
                  product,
                  frontImage,
                  backImage,
                  "Message to go on the back of the postcard",
                  Address.getKiteTeamAddress() );

          Order order = new Order();

          order.addJob( postcard );


          // Start managed check-out
          kiteSDK.startCheckoutForResult( MainActivity.this, order, REQUEST_CODE_CHECKOUT );
          }
        catch ( MalformedURLException ex )
          {
          // Ignore
          }
        }

      @Override
      public void onCatalogueCancelled()
        {
        // Ignore
        }

      @Override
      public void onCatalogueError( Exception exception )
        {
        // Handle gracefully
        }
      } );
    }


  /*****************************************************
   *
   * Displays an error dialog.
   *
   *****************************************************/
  private void showError( String title, String message )
    {
    new AlertDialog.Builder( this )
            .setTitle( title )
            .setMessage( message )
            .setPositiveButton( "OK", null )
            .show();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

}
