/*****************************************************
 *
 * InstagramPhotoPickerActivity.java
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

package ly.kite.instagramphotopicker;


///// Import(s) /////

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import ly.kite.imagepicker.AImagePickerActivity;


///// Class Declaration /////

/*****************************************************
 *
 * This activity is the Facebook photo picker.
 *
 *****************************************************/
public class InstagramPhotoPickerActivity extends AImagePickerActivity implements InstagramAgent.ICallback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                           = "InstagramPhotoPicker...";

  static private final String  INTENT_EXTRA_PREFIX               = "ly.kite.instagramimagepicker";
  static private final String  INTENT_EXTRA_NAME_CLIENT_ID       = INTENT_EXTRA_PREFIX + ".clientId";
  static private final String  INTENT_EXTRA_NAME_REDIRECT_URI    = INTENT_EXTRA_PREFIX + ".redirectUri";
  static private final String  INTENT_EXTRA_NAME_MAX_IMAGE_COUNT = INTENT_EXTRA_PREFIX + ".maxImageCount";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private InstagramAgent                        mInstagramAgent;

  private Menu                                  mOptionsMenu;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an intent to start this activity, with the max
   * image coung added as an extra.
   *
   *****************************************************/
  static private Intent getIntent( Context context, String clientId, String redirectUri, int maxImageCount )
    {
    Intent intent = new Intent( context, InstagramPhotoPickerActivity.class );

    intent.putExtra( INTENT_EXTRA_NAME_CLIENT_ID, clientId );
    intent.putExtra( INTENT_EXTRA_NAME_REDIRECT_URI, redirectUri );

    addExtras( intent, maxImageCount );

    return ( intent );
    }


  /*****************************************************
   *
   * Starts this activity, returning the result to a calling
   * activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, String clientId, String redirectUri, int maxImageCount, int activityRequestCode )
    {
    Intent intent = getIntent( activity, clientId, redirectUri, maxImageCount );

    activity.startActivityForResult( intent, activityRequestCode );
    }


  /*****************************************************
   *
   * Starts this activity, returning the result to a calling
   * fragment.
   *
   *****************************************************/
  static public void startForResult( Fragment fragment, String clientId, String redirectUri, int maxImageCount, int activityRequestCode )
    {
    Intent intent = getIntent( fragment.getActivity(), clientId, redirectUri, maxImageCount );

    fragment.startActivityForResult( intent, activityRequestCode );
    }


  ////////// Constructor(s) //////////

  public InstagramPhotoPickerActivity()
    {
    super();
    }


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    Intent intent = getIntent();

    if ( intent == null )
      {
      Log.e( LOG_TAG, "No intent supplied" );

      finish();

      return;
      }

    String clientId    = intent.getStringExtra( INTENT_EXTRA_NAME_CLIENT_ID );
    String redirectUri = intent.getStringExtra( INTENT_EXTRA_NAME_REDIRECT_URI );

    mInstagramAgent = InstagramAgent.getInstance( this, clientId, redirectUri );


    super.onCreate( savedInstanceState );
    }


  /*****************************************************
   *
   * Called when the options menu is created.
   *
   *****************************************************/
  public boolean onCreateOptionsMenu ( Menu menu )
    {
    super.onCreateOptionsMenu( menu );

    getMenuInflater().inflate( R.menu.action_bar_menu, menu );

    mOptionsMenu = menu;

    checkLoggedInState();

    return ( true );
    }


  /*****************************************************
   *
   * Called when an options menu item (action) is selected.
   *
   *****************************************************/
  public boolean onOptionsItemSelected ( MenuItem item )
    {
    if ( item.getItemId() == R.id.item_logout )
      {
      ///// Log out /////

      mInstagramAgent.clearAccessToken( this );

      checkLoggedInState();

      mImagePickerGridView.reload();

      return ( true );
      }

    return ( super.onOptionsItemSelected( item ) );
    }


  /*****************************************************
   *
   * Called when the activity gains focus.
   *
   *****************************************************/
  @Override
  protected void onResume()
    {
    super.onResume();

    checkLoggedInState();
    }


  /*****************************************************
   *
   * Called when an activity returns a result.
   *
   *****************************************************/
  @Override
  protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
    {
    super.onActivityResult( requestCode, resultCode, data );

    mInstagramAgent.onActivityResult( requestCode, resultCode, data );
    }


  ////////// FacebookAgent.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when photos were successfully retrieved.
   *
   *****************************************************/
  @Override
  public void iaOnPhotosSuccess( List<InstagramAgent.InstagramPhoto> photoList, boolean morePhotos )
    {
    mImagePickerGridView.onFinishedLoading( photoList, morePhotos );
    }


  /*****************************************************
   *
   * Called when there was an error retrieving photos.
   *
   *****************************************************/
  @Override
  public void iaOnError( Exception exception )
    {
    Log.e( LOG_TAG, "Instagram error", exception );

    RetryListener  retryListener  = new RetryListener();
    CancelListener cancelListener = new CancelListener();

    new AlertDialog.Builder( this )
        .setTitle( R.string.title_instagram_alert_dialog )
        .setMessage( getString( R.string.message_instagram_alert_dialog, exception.toString() ) )
        .setPositiveButton( R.string.button_text_retry, retryListener )
        .setNegativeButton( R.string.button_text_cancel, cancelListener )
        .setOnCancelListener( cancelListener )
        .create()
      .show();
    }


  /*****************************************************
   *
   * Called when photo retrieval was cancelled.
   *
   *****************************************************/
  @Override
  public void iaOnCancel()
    {
    finish();
    }


  ////////// AImagePickerActivity Method(s) //////////

  @Override
  public void onSetDepth( int depth, String parentKey )
    {
    setTitle( R.string.title_instagram_photo_picker );

    mInstagramAgent.resetPhotos();

    mInstagramAgent.getPhotos( this );
    }


  @Override
  public void onLoadMoreItems( int depth, String parentKey )
    {
    mInstagramAgent.getPhotos( this );
    }



  ////////// Method(s) //////////

  /*****************************************************
   *
   * Checks the logged in state and sets the log out
   * icon accordingly.
   *
   *****************************************************/
  private void checkLoggedInState()
    {
    if ( mOptionsMenu != null )
      {
      MenuItem logOutMenuItem = mOptionsMenu.findItem( R.id.item_logout );

      if ( logOutMenuItem != null )
        {
        logOutMenuItem.setEnabled( mInstagramAgent.haveAccessToken( this ) );
        }
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The alert dialog retry button listener.
   *
   *****************************************************/
  private class RetryListener implements Dialog.OnClickListener
    {
    @Override
    public void onClick( DialogInterface dialog, int which )
      {
      mImagePickerGridView.reload();
      }
    }


  /*****************************************************
   *
   * The alert dialog cancel (button) listener.
   *
   *****************************************************/
  private class CancelListener implements Dialog.OnClickListener, Dialog.OnCancelListener
    {
    @Override
    public void onClick( DialogInterface dialog, int which )
      {
      finish();
      }

    @Override
    public void onCancel( DialogInterface dialog )
      {
      finish();
      }
    }

  }
