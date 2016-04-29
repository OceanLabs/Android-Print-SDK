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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

// Uncomment to include Crashlytics (Fabric) crash reporting
//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

import ly.kite.address.Address;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.checkout.ShippingActivity;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.photopicker.Photo;
import ly.kite.photopicker.PhotoPicker;
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
public class MainActivity extends Activity
{
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                    = "MainActivity";

  private static final String  REPLACE_DETAILS_HERE       = "REPLACE_ME";

  /**********************************************************************
   * Insert your Kite API keys here. These are found under your profile
   * by logging in to the developer portal at https://www.kite.ly
   **********************************************************************/
  private static final String API_KEY_TEST                = REPLACE_DETAILS_HERE;
  private static final String API_KEY_LIVE                = REPLACE_DETAILS_HERE;


  /**********************************************************************
   * Insert your Instagram details here.
   **********************************************************************/
  private static final String INSTAGRAM_API_KEY           = REPLACE_DETAILS_HERE;
  private static final String INSTAGRAM_REDIRECT_URI      = REPLACE_DETAILS_HERE;


  private static final int    REQUEST_CODE_SELECT_PICTURE = 1;
  private static final int    REQUEST_CODE_CHECKOUT       = 2;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Switch  mEnvironmentSwitch;


  ////////// Static Initialiser(s) //////////


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
        Photo[] photos = PhotoPicker.getResultPhotos( data );

        ArrayList<Asset> assetArrayList = new ArrayList<Asset>();

        for ( Photo photo : photos )
          {
          assetArrayList.add( new Asset( photo.getUri() ) );
          }

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
    // Launch the picture selector
    PhotoPicker.startPhotoPickerForResult( this, REQUEST_CODE_SELECT_PICTURE );
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
      assetArrayList.add( new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/1.jpg" ) ) );
      assetArrayList.add( new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/2.jpg" ) ) );
      assetArrayList.add( new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/3.jpg" ) ) );
      assetArrayList.add( new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/4.jpg" ) ) );
      }
    catch ( Exception ex )
      {
      }

    fullJourney( assetArrayList );
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

    if ( mEnvironmentSwitch.isChecked() )
      {
      apiKey      = API_KEY_LIVE;
      environment = KiteSDK.DefaultEnvironment.LIVE;
      }
    else
      {
      apiKey      = API_KEY_TEST;
      environment = KiteSDK.DefaultEnvironment.TEST;
      }


    // Check that the API has been set in code
    if ( apiKey.equals( REPLACE_DETAILS_HERE ) )
      {
      showError( "Set API Keys", "Please set your Kite API keys at the top of the SampleApp's MainActivity.java. You can find these by logging into https://www.kite.ly." );

      return ( null );
      }


    return ( KiteSDK.getInstance( this, apiKey, environment )
               .setRequestPhoneNumber( false ) );
    }


  /*****************************************************
   *
   * Starts the SDK shopping journey with a set of image
   * assets.
   *
   *****************************************************/
  private void fullJourney( ArrayList<Asset> assets )
    {
    KiteSDK kiteSDK = configureSDK();

    if ( kiteSDK == null ) return;


    // Launch the SDK shopping journey

    kiteSDK

      // Uncomment this if you want all image sources, and you have defined all the credentials. The
      // default is for the device image source, and the Instagram image source (assuming you have
      // also defined the instagram credentials).
      //.setImageSources( new DeviceImageSource(), new InstagramImageSource(), new FacebookImageSource() )

      // Uncomment this if you have defined Instagram credentials
      //.setInstagramCredentials( INSTAGRAM_API_KEY, INSTAGRAM_REDIRECT_URI )

      .startShopping( this, assets );  // Use this to shop all products in catalogue
      //.startShoppingByProductId( this, assets, "pbx_squares_5x5", "pbx_squares_8x8" );  // Use this to shop specific products by id
    }


  /*****************************************************
   *
   * Prints a postcode and goes straight to the checkout
   * activity.
   *
   *****************************************************/
  private void postcardJourney()
    {
    KiteSDK kiteSDK = configureSDK();

    if ( kiteSDK == null ) return;

    kiteSDK.getCatalogueLoader().requestCatalogue( new ICatalogueConsumer()
      {
      @Override
      public void onCatalogueSuccess( Catalogue catalogue )
        {
        try
          {
          // Create Postcard Job & Add to Order

          Asset frontImage = new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/1.jpg" ) );
          Asset backImage  = new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/2.jpg" ) );

          Job postcard = Job.createPostcardJob(
                  catalogue.getProductById( "postcard" ),
                  frontImage,
                  backImage,
                  "Message to go on the back of the postcard",
                  Address.getKiteTeamAddress() );

          Order order = new Order();

          order.addJob( postcard );


          // Start managed check-out
          KiteSDK.getInstance( MainActivity.this ).startCheckout( MainActivity.this, order, REQUEST_CODE_CHECKOUT );
          }
        catch ( MalformedURLException ex )
          {
          // Ignore
          }
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
