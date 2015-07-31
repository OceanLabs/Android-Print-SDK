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

import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import ly.kite.product.Asset;
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
  private static final String  LOG_TAG                      = "MainActivity";

  private static final String NON_REPLACED_API_KEY          = "REPLACE_ME";

  /**********************************************************************
   * Insert your Kite API keys here. These are found under your profile
   * by logging in to the developer portal at https://www.kite.ly
   **********************************************************************/
  //private static final String API_KEY_TEST                = NON_REPLACED_API_KEY;
  //private static final String API_KEY_TEST                = "ba171b0d91b1418fbd04f7b12af1e37e42d2cb1e"; // Test
  private static final String API_KEY_TEST                = "2d019f03d66bbb31328563dfef1569ece92bc842"; // JL Test
  //private static final String API_KEY_TEST                = "0453d74be957c1eb510fc2d580007294cdc31a79"; // Photobox

  private static final String API_KEY_LIVE                = NON_REPLACED_API_KEY;


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


    // Initialise crash reporting
    Fabric.with( this, new Crashlytics() );


    // Set up the screen

    setContentView( R.layout.screen_main );

    mEnvironmentSwitch = (Switch)findViewById( R.id.environment_switch );
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
        Uri selectedImageUri = data.getData();

        ArrayList<Asset> assetArrayList = new ArrayList<Asset>();

        assetArrayList.add( new Asset( selectedImageUri ) );

        checkoutWithAssets( assetArrayList );
        }
      }
    }

  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the print local photos button is clicked.
   *
   *****************************************************/
  public void onGalleryButtonClicked( View view )
    {
    // Launch the picture selector

    Intent intent = new Intent();

    intent.setType( "image/*" );
    intent.setAction( Intent.ACTION_GET_CONTENT );

    startActivityForResult( Intent.createChooser( intent, "Select Picture" ), REQUEST_CODE_SELECT_PICTURE );
    }


  /*****************************************************
   *
   * Called when the print photos at remove URLs button
   * is clicked.
   *
   *****************************************************/
  public void onRemoteButtonClicked( View view )
    {
    // Create some assets from remote URLs

    ArrayList<Asset> assetArrayList = new ArrayList<Asset>();

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

    checkoutWithAssets( assetArrayList );
    }


  /*****************************************************
   *
   * Starts the SDK shopping journey with a set of image
   * assets.
   *
   *****************************************************/
  private void checkoutWithAssets( ArrayList<Asset> assets )
    {
    String               apiKey;
    KiteSDK.Environment  environment;


    // Determine the API and environment based on the position
    // of the on-screen switch.

    if ( mEnvironmentSwitch.isChecked() )
      {
      apiKey      = API_KEY_LIVE;
      environment = KiteSDK.Environment.LIVE;
      }
    else
      {
      apiKey      = API_KEY_TEST;
      environment = KiteSDK.Environment.TEST;
      }


    // Check that the API has been set in code
    if ( apiKey.equals( NON_REPLACED_API_KEY ) )
      {
      showError( "Set API Keys", "Please set your Kite API keys at the top of the SampleApp's MainActivity.java. You can find these by logging into https://www.kite.ly." );
      return;
      }


    // Launch the SDK shopping journey
    KiteSDK.startShopping( this, apiKey, environment, assets );
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
