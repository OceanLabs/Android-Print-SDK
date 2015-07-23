package ly.kite.sample;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
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

import ly.kite.print.Asset;
import ly.kite.KiteSDK;

public class MainActivity extends Activity
  {
  private static final String NON_REPLACED_API_KEY = "REPLACE_ME";

  /**********************************************************************
   * Insert your Kite API keys here. These are found under your profile
   * by logging in to the developer portal at https://www.kite.ly
   **********************************************************************/

  private static final String API_KEY_TEST = NON_REPLACED_API_KEY;

  private static final String API_KEY_LIVE = NON_REPLACED_API_KEY;

  private static final int REQUEST_CODE_SELECT_PICTURE = 1;
  private static final int REQUEST_CODE_CHECKOUT = 2;

  private Switch environmentSwitch;
  //private Spinner productSpinner;

  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );
    Fabric.with(this, new Crashlytics());
    setContentView( R.layout.activity_main );
    environmentSwitch = (Switch) findViewById( R.id.environment );
    }

  public void onGalleryButtonClicked( View view )
    {
    Intent intent = new Intent();
    intent.setType( "image/*" );
    intent.setAction( Intent.ACTION_GET_CONTENT );
    startActivityForResult( Intent.createChooser( intent, "Select Picture" ), REQUEST_CODE_SELECT_PICTURE );
    }

  public void onRemoteButtonClicked( View view )
    {
    ArrayList<Asset> assets = new ArrayList<Asset>();
    assets.add( new Asset( R.drawable.instagram1 ) );

    try
      {
      assets.add( new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/1.jpg" ) ) );
      assets.add( new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/2.jpg" ) ) );
      assets.add( new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/3.jpg" ) ) );
      assets.add( new Asset( new URL( "http://psps.s3.amazonaws.com/sdk_static/4.jpg" ) ) );
      }
    catch ( Exception ex )
      {
      }

    checkoutWithAssets( assets );
    }

  private void checkoutWithAssets( ArrayList<Asset> assets )
    {
    String               apiKey;
    KiteSDK.Environment  environment;

    if ( environmentSwitch.isChecked() )
      {
      apiKey      = API_KEY_LIVE;
      environment = KiteSDK.Environment.LIVE;
      }
    else
      {
      apiKey      = API_KEY_TEST;
      environment = KiteSDK.Environment.TEST;
      }


    if ( apiKey.equals( NON_REPLACED_API_KEY ) )
      {
      showError( "Set API Keys", "Please set your Kite API keys at the top of the SampleApp's MainActivity.java. You can find these by logging into https://www.kite.ly." );
      return;
      }

    KiteSDK.startShopping( this, apiKey, environment, assets );
    }

  private void showError( String title, String message )
    {
    AlertDialog.Builder builder = new AlertDialog.Builder( this );
    builder.setTitle( title );
    builder.setMessage( message );
    builder.setPositiveButton( "OK", null );
    builder.show();
    }

  public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    if ( requestCode == REQUEST_CODE_CHECKOUT )
      {
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
      if ( resultCode == RESULT_OK )
        {
        Uri selectedImageUri = data.getData();
        ArrayList<Asset> assets = new ArrayList<Asset>();
        assets.add( new Asset( selectedImageUri ) );
        checkoutWithAssets( assets );
        }
      }
    }

  }
