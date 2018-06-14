/*****************************************************
 *
 * FacebookPhotoPickerActivity.java
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

package ly.kite.facebookphotopicker;


///// Import(s) /////

import java.util.HashMap;
import java.util.List;

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

import ly.kite.imagepicker.AImagePickerActivity;


///// Class Declaration /////

/*****************************************************
 *
 * This activity is the Facebook photo picker.
 *
 *****************************************************/
public class FacebookPhotoPickerActivity extends AImagePickerActivity implements FacebookAgent.ICallback, AImagePickerActivity.ICallback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                = "FacebookPhotoPickerA...";

  static private final String  INTENT_EXTRA_NAME_MAX_IMAGE_COUNT = "ly.kite.addedAssetCount";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private FacebookAgent                        mFacebookAgent;

  private Menu                                 mOptionsMenu;

  private HashMap<String,FacebookAgent.Album>  mAlbumTable;

  private FacebookAgent.Album                  mCurrentAlbum;

  private static int                           mAddedAssetCount;
  private static int                           mPackSize;
  private static int                           mSelectedImageCount;
  private static int                           mMaxImageCount;
  private static boolean                       mSupportsMultiplePacks;
  private static String                        mSelectedBucketName;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an intent to start this activity, with the max
   * image coung added as an extra.
   *
   *****************************************************/
  static private Intent getIntent( Context context,  int addedAssetCount, boolean supportsMultiplePacks, int packSize, int maxImageCount )
    {
    Intent intent = new Intent( context, FacebookPhotoPickerActivity.class );

    mMaxImageCount         = maxImageCount;
    mPackSize              = packSize;
    mAddedAssetCount       = addedAssetCount;
    mSupportsMultiplePacks = supportsMultiplePacks;
    mSelectedImageCount    = 0;

    if( supportsMultiplePacks )
      {
      mMaxImageCount = 0;
      }
    addExtras( intent, mMaxImageCount );

    return ( intent );
    }


  /*****************************************************
   *
   * Starts this activity, returning the result to a calling
   * activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, int addedAssetCount, boolean supportsMultiplePacks,
                                     int packSize, int maxImageCount, int activityRequestCode )
    {
    Intent intent = getIntent( activity, addedAssetCount, supportsMultiplePacks, packSize, maxImageCount );

    activity.startActivityForResult( intent, activityRequestCode );
    }


  /*****************************************************
   *
   * Starts this activity, returning the result to a calling
   * fragment.
   *
   *****************************************************/
  static public void startForResult( Fragment fragment,  int addedAssetCount, boolean supportsMultiplePacks,
                                     int packSize, int maxImageCount, int activityRequestCode )
    {
    Intent intent = getIntent( fragment.getActivity(), addedAssetCount, supportsMultiplePacks, packSize, maxImageCount );

    fragment.startActivityForResult( intent, activityRequestCode );
    }


  ////////// Constructor(s) //////////

  public FacebookPhotoPickerActivity()
    {
    super();

    mAlbumTable = new HashMap<>();
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
    mFacebookAgent = FacebookAgent.getInstance( this );

    setDefaultToolbarName();
    //register sthe AImagePickerActivity callback
    setCallback( this );

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

      mFacebookAgent.logOut();

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

    mFacebookAgent.onActivityResult( requestCode, resultCode, data );
    }


  ////////// FacebookAgent.PhotosCallback Method(s) //////////

  /*****************************************************
   *
   * Called when albums were successfully retrieved.
   *
   *****************************************************/
  @Override
  public void facOnAlbumsSuccess( List<FacebookAgent.Album> albumList, boolean moreAlbums )
    {
    // Add the albums to our table
    for ( FacebookAgent.Album album : albumList )
      {
      mAlbumTable.put( album.getId(), album );
      }

    mImagePickerGridView.onFinishedLoading( albumList, moreAlbums );
    }


  /*****************************************************
   *
   * Called when photos were successfully retrieved.
   *
   *****************************************************/
  @Override
  public void facOnPhotosSuccess( List<FacebookAgent.Photo> photoList, boolean morePhotos )
    {
    mImagePickerGridView.onFinishedLoading( photoList, morePhotos );
    }


  /*****************************************************
   *
   * Called when there was an error retrieving photos.
   *
   *****************************************************/
  @Override
  public void facOnError( Exception exception )
    {
    Log.e( LOG_TAG, "Facebook error", exception );

    RetryListener  retryListener  = new RetryListener();
    CancelListener cancelListener = new CancelListener();

    new AlertDialog.Builder( this )
        .setTitle( R.string.kitesdk_title_facebook_alert_dialog)
        .setMessage( getString( R.string.kitesdk_message_facebook_alert_dialog, exception.toString() ) )
        .setPositiveButton( R.string.kitesdk_button_text_retry, retryListener )
        .setNegativeButton( R.string.kitesdk_button_text_cancel, cancelListener )
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
  public void facOnCancel()
    {
    finish();
    }


  ////////// AImagePickerActivity Method(s) //////////

  @Override
  public void onSetDepth( int depth, String parentKey )
    {
    // Check the depth

    if ( depth == 0 )
      {
      mSelectedBucketName = null;
      setDefaultToolbarName();

      mFacebookAgent.resetAlbums();

      mFacebookAgent.getAlbums( this );
      }
    else if ( depth == 1 )
      {
      // Get the album corresponding to the key
      FacebookAgent.Album album = mAlbumTable.get( parentKey );

      if ( album != null )
        {
        mSelectedBucketName = album.getLabel();
        setToolbarName();

        mFacebookAgent.resetPhotos();

        mFacebookAgent.getPhotos( album, this );
        }
      }
    }


  @Override
  public void onLoadMoreItems( int depth, String parentKey )
    {
    if      ( depth == 0 ) mFacebookAgent.getAlbums( this );
    else if ( depth == 1 ) mFacebookAgent.getPhotos( null, this );
    }

  /*****************************************************
   *
   * Sets the toolbar name taking containing the number
   * of selected pictures and the default title
   *
   *****************************************************/
  public void setDefaultToolbarName()
    {
    String defaultTitle = getResources().getString( R.string.kitesdk_title_facebook_photo_picker );
    if( mPackSize == 1 || ( mMaxImageCount == 0 && !mSupportsMultiplePacks ))
      {
      setTitle( defaultTitle );
      }
    else
      {
      int totalImagesUsed = mSelectedImageCount;
      int outOf = mMaxImageCount;

      if ( mSupportsMultiplePacks )
        {
        totalImagesUsed += mAddedAssetCount;
        if( totalImagesUsed > 0 )
          {
          outOf = (int) (Math.ceil((double) totalImagesUsed / mPackSize) * mPackSize);
          }
        else
          {
          outOf = mPackSize;
          }
        }
      setTitle( "[" + totalImagesUsed + "/" + outOf + "] " + defaultTitle );
      }
    }

  /*****************************************************
   *
   * Sets the toolbar name taking containing the number
   * of selected pictures and bucket name
   *
   *****************************************************/
  public void setToolbarName()
    {
    if( mPackSize == 1 || ( mMaxImageCount == 0 && !mSupportsMultiplePacks ))
      {
      setTitle( mSelectedBucketName );
      }
    else
      {
      int totalImagesUsed = mSelectedImageCount;
      int outOf = mMaxImageCount;

      if ( mSupportsMultiplePacks )
        {
        totalImagesUsed += mAddedAssetCount;
        if( totalImagesUsed > 0 )
          {
          outOf = (int) (Math.ceil((double) totalImagesUsed / mPackSize) * mPackSize);
          }
        else
          {
          outOf = mPackSize;
          }
        }
      setTitle( "[" + totalImagesUsed + "/" + outOf + "] " + mSelectedBucketName );
      }
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
        logOutMenuItem.setEnabled( mFacebookAgent.isLoggedIn() );
        }
      }
    }

  ////////// AImagePicker.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when the selected image count changes
   *
   *****************************************************/
  @Override
  public void onSelectedCountChanged(int selectedImageCount)
    {
      mSelectedImageCount = selectedImageCount;
      if ( mSelectedBucketName == null )
        {
        setDefaultToolbarName();
        }
      else
        {
        setToolbarName();
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
