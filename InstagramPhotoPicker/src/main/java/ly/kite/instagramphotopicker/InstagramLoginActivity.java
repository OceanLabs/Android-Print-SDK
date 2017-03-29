/*****************************************************
 *
 * InstagramLoginActivity.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2017 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.instagramphotopicker;


///// Import(s) /////

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


///// Class Declaration /////

/*****************************************************
 *
 * This activity displays the Instagram login screen.
 *
 *****************************************************/
public class InstagramLoginActivity extends Activity
  {

  ////////// Static Constant(s) //////////

  static private final String LOG_TAG = "InstagramLoginActivity";

  static private final boolean DEBUGGING_ENABLED = true;

  static private final String EXTRA_PREFIX       = "ly.kite.instagramimagepicker";

  static private final String EXTRA_CLIENT_ID    = EXTRA_PREFIX + ".EXTRA_CLIENT_ID";
  static private final String EXTRA_REDIRECT_URI = EXTRA_PREFIX + ".EXTRA_REDIRECT_URI";
  static private final String EXTRA_ACCESS_TOKEN = EXTRA_PREFIX + ".EXTRA_ACCESS_TOKEN";


  ////////// Member Variable(s) //////////

  private WebView  mWebView;
  private String   mClientId;
  private String   mRedirectUri;


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an intent used to start this activity.
   *
   *****************************************************/
  static private Intent getIntent( Activity activity, String clientId, String redirectUri )
    {
    Intent intent = new Intent( activity, InstagramLoginActivity.class );

    intent.putExtra( EXTRA_CLIENT_ID,    clientId );
    intent.putExtra( EXTRA_REDIRECT_URI, redirectUri );

    return ( intent );
    }


  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  public static void startLoginForResult( Activity activity, String clientId, String redirectUri, int requestCode )
    {
    Intent intent = getIntent( activity, clientId, redirectUri );

    activity.startActivityForResult( intent, requestCode );
    }


  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  public static void startLoginForResult( Fragment fragment, String clientId, String redirectUri, int requestCode )
    {
    Intent intent = getIntent( fragment.getActivity(), clientId, redirectUri );

    fragment.startActivityForResult( intent, requestCode );
    }


  /*****************************************************
   *
   * Returns the access token from result data.
   *
   *****************************************************/
  static public String getAccessToken( Intent data )
    {
    return ( data.getStringExtra( EXTRA_ACCESS_TOKEN ) );
    }


  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    mClientId    = getIntent().getStringExtra( EXTRA_CLIENT_ID );
    mRedirectUri = getIntent().getStringExtra( EXTRA_REDIRECT_URI );


    setContentView( R.layout.screen_instagram_login );

    mWebView = (WebView)findViewById( R.id.webview );

    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled( true );
    mWebView.setWebViewClient( new InstagramWebViewClient() );

    loadLoginPage();
    }


  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if ( id == android.R.id.home )
      {
      setResult( RESULT_CANCELED );

      finish();

      return true;
      }

    return super.onOptionsItemSelected( item );
    }

  private void loadLoginPage()
    {
    String instagramAuthURL = "https://api.instagram.com/oauth/authorize/?client_id=" + this.mClientId + "&redirect_uri=" + this.mRedirectUri + "&response_type=token";

    mWebView.loadUrl( instagramAuthURL );
    }

  @Override
  protected void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    outState.putString( EXTRA_CLIENT_ID,    mClientId );
    outState.putString( EXTRA_REDIRECT_URI, mRedirectUri );

    mWebView.saveState( outState );
    }

  @Override
  protected void onRestoreInstanceState( Bundle savedInstanceState )
    {
    super.onRestoreInstanceState( savedInstanceState );

    mClientId    = savedInstanceState.getString( EXTRA_CLIENT_ID );
    mRedirectUri = savedInstanceState.getString( EXTRA_REDIRECT_URI );

    mWebView.restoreState( savedInstanceState );
    }


  @Override
  public void onBackPressed()
    {
    setResult( RESULT_CANCELED );

    finish();
    }


  private void gotAccessToken( final String instagramAccessToken )
    {
    Intent resultData = new Intent();

    resultData.putExtra( EXTRA_ACCESS_TOKEN, instagramAccessToken );

    setResult( RESULT_OK, resultData );

    finish();
    }

  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    setResult( resultCode, data );

    finish();
    }

  private final String getLoginErrorMessage( Uri uri )
    {
    // Default message
    String errorMessage = getString( R.string.instagram_error );

    String errorReason = uri.getQueryParameter( "error_reason" );

    if ( errorReason != null )
      {
      if ( ! errorReason.equalsIgnoreCase( "user_denied" ) )
        {
        String errorDescription = uri.getQueryParameter( "error_description" );

        if ( errorDescription != null )
          {
          try
            {
            errorMessage = URLDecoder.decode( errorMessage, "UTF-8" );
            }
          catch ( UnsupportedEncodingException ignore )
            {
            // Ignore
            }
          }
        }
      }

    return ( errorMessage );
    }

  private void showErrorDialog( String message )
    {
    AlertDialog.Builder builder = new AlertDialog.Builder( this );
    builder.setTitle( R.string.title_instagram_alert_dialog );
    builder.setMessage( message );
    builder.setPositiveButton( R.string.button_text_retry, null );
    builder.setNegativeButton( R.string.button_text_cancel, new CancelButtonClickListener() );
    builder.show();
    }


  ////////// Inner Class(es) //////////

  private class InstagramWebViewClient extends WebViewClient
    {
    public boolean shouldOverrideUrlLoading( WebView view, String url )
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "shouldOverrideUrlLoading( view, url = " + url.toString() + " )" );

      if ( url != null && url.startsWith( mRedirectUri ) )
        {
        Uri uri = Uri.parse( url );
        String error = uri.getQueryParameter( "error" );
        if ( error != null )
          {
          String errorMessage = getLoginErrorMessage( uri );
          mWebView.stopLoading();
          loadLoginPage();
          showErrorDialog( errorMessage );
          }
        else
          {
          String fragment = uri.getFragment();
          String accessToken = fragment.substring( "access_token=".length() );
          gotAccessToken( accessToken );
          }

        return true;
        }

      return false;
      }

    public void onPageStarted( WebView view, String url, Bitmap favicon )
      {

      if ( DEBUGGING_ENABLED )
        Log.d( LOG_TAG, "onPageStarted( view, url = " + url.toString() + ", favicon )" );

      }

    public void onPageFinished( WebView view, String url )
      {

      if ( DEBUGGING_ENABLED )
        Log.d( LOG_TAG, "onPageFinished( view, url = " + url.toString() + " )" );

      }

    public void onLoadResource( WebView view, String url )
      {

      if ( DEBUGGING_ENABLED )
        Log.d( LOG_TAG, "onLoadResources( view, url = " + url.toString() + " )" );

      }
    };


  private class CancelButtonClickListener implements AlertDialog.OnClickListener
    {
    @Override
    public void onClick( DialogInterface dialog, int which )
      {
      setResult( RESULT_CANCELED );

      finish();
      }
    }

  }
