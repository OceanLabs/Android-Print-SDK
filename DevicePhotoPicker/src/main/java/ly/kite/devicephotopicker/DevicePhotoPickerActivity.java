/*****************************************************
 *
 * DevicePhotoPickerActivity.java
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

package ly.kite.devicephotopicker;


///// Import(s) /////

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ly.kite.imagepicker.AImagePickerActivity;
import ly.kite.imagepicker.IImagePickerItem;
import ly.kite.imagepicker.ISelectableItem;


///// Class Declaration /////

/*****************************************************
 *
 * This activity is the Facebook photo picker.
 *
 *****************************************************/
public class DevicePhotoPickerActivity extends AImagePickerActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                           = "DevicePhotoPicker...";

  static private final boolean DEBUGGING_ENABLED                 = false;

  static private final String[] FOLDER_SUMMARY_PROJECTION = new String[] {
          MediaStore.Images.Media._ID,
          MediaStore.Images.Media.BUCKET_ID,
          MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
          MediaStore.Images.Media.DATA,
          MediaStore.Images.Media.DATE_TAKEN };


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an intent to start this activity, with the max
   * image count added as an extra.
   *
   *****************************************************/
  static private Intent getIntent( Context context, int maxImageCount )
    {
    Intent intent = new Intent( context, DevicePhotoPickerActivity.class );

    addExtras( intent, maxImageCount );

    return ( intent );
    }


  /*****************************************************
   *
   * Starts this activity, returning the result to a calling
   * activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, int maxImageCount, int activityRequestCode )
    {
    Intent intent = getIntent( activity, maxImageCount );

    activity.startActivityForResult( intent, activityRequestCode );
    }


  /*****************************************************
   *
   * Starts this activity, returning the result to a calling
   * fragment.
   *
   *****************************************************/
  static public void startForResult( Fragment fragment, int maxImageCount, int activityRequestCode )
    {
    Intent intent = getIntent( fragment.getActivity(), maxImageCount );

    fragment.startActivityForResult( intent, activityRequestCode );
    }


  ////////// Constructor(s) //////////

  public DevicePhotoPickerActivity()
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


    super.onCreate( savedInstanceState );


    setTitle( R.string.kitesdk_title_device_photo_picker);
    }


  ////////// AImagePickerActivity Method(s) //////////

  @Override
  public void onSetDepth( int depth, String parentKey )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onSetDepth( depth = " + depth + ", parentKey = " + parentKey + " )" );


    // Check the depth

    if ( depth == 1 )
      {
      ///// Bucket /////

      List<Image> imageList = queryImages( parentKey );

      mImagePickerGridView.onFinishedLoading( imageList, false );
      }
    else
      {
      ///// Root /////

      List<Bucket> bucketList = queryBuckets();

      mImagePickerGridView.onFinishedLoading( bucketList, false );
      }
    }


  @Override
  public void onLoadMoreItems( int depth, String parentKey )
    {
    // TODO
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Queries the images and passes them to a receiver.
   *
   *****************************************************/
  private void queryImages( String bucketId, IImageReceiver imageReceiver )
    {
    Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    String   selection     = null;
    String[] selectionArgs = null;

    if ( bucketId != null )
      {
      selection = MediaStore.Images.Media.BUCKET_ID + "=?";

      selectionArgs = new String[ 1 ];
      selectionArgs[ 0 ] = bucketId;
      }

    String sortBy = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " COLLATE NOCASE ASC, "
            + MediaStore.Images.Media.DATE_TAKEN + " DESC";

    Cursor cursor = getContentResolver().query(baseUri, FOLDER_SUMMARY_PROJECTION, selection, selectionArgs, sortBy );


    // We're looking for a distinct set of buckets (folders)

    ArrayList<Bucket> bucketList                = new ArrayList<>();  // The world's saddest bucket list
    String            previousBucketDisplayName = null;

    while ( cursor.moveToNext() )
      {
      int    id                = cursor.getInt( cursor.getColumnIndex( MediaStore.Images.Media._ID ) );
             bucketId          = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.BUCKET_ID ) );
      String bucketDisplayName = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.BUCKET_DISPLAY_NAME ) );
      String data              = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA ) );

      //Uri    imageUri          = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString( id ) );

      //Log.d( LOG_TAG, "id = " + id + ", bucketId = " + bucketId + ", bucketDisplayName = " + bucketDisplayName + ", data = " + data + ", imageUri = " + imageUri.toString() );

        if ( data != null && data.startsWith("/") && (data.endsWith(".jpg") || data.endsWith("jpeg") || data.endsWith("png")))
        {
        imageReceiver.newImage( id, bucketId, bucketDisplayName, "file://" + data );
        }
      }

    cursor.close();
    }


  /*****************************************************
   *
   * Queries the images and passes them to a receiver.
   *
   *****************************************************/
  private void queryImages( IImageReceiver imageReceiver )
    {
    queryImages( null, imageReceiver );
    }


  /*****************************************************
   *
   * Queries the images and builds a set of buckets.
   *
   *****************************************************/
  private List<Bucket> queryBuckets()
    {
    BucketListBuilder listBuilder = new BucketListBuilder();

    queryImages( listBuilder );

    return ( listBuilder.getBucketList() );
    }


  /*****************************************************
   *
   * Queries the images and builds a set.
   *
   *****************************************************/
  private List<Image> queryImages( String bucketId )
    {
    ImageListBuilder listBuilder = new ImageListBuilder();

    queryImages( bucketId, listBuilder );

    return ( listBuilder.getImageList() );
    }


  ////////// Inner Class(es) //////////

  private interface IImageReceiver
    {
    public void newImage( int id, String bucketId, String bucketDisplayName, String imageURLString );
    }


  /*****************************************************
   *
   * An image receiver that builds a list of distinct
   * buckets.
   *
   *****************************************************/
  private class BucketListBuilder implements IImageReceiver
    {
    ArrayList<Bucket> mBucketList;  // The world's saddest bucket list
    String            mPreviousBucketDisplayName;

    BucketListBuilder()
      {
      mBucketList = new ArrayList<>();
      }

    @Override
    public void newImage( int id, String bucketId, String bucketDisplayName, String imageURLString )
      {
      if (bucketDisplayName.equals("Camera")){  // translates the default camera folder name if untranslated
        bucketDisplayName = getString(R.string.kitesdk_title_camera_folder);
      }

      if ( ! bucketDisplayName.equals( mPreviousBucketDisplayName ) )
        {
        mBucketList.add( new Bucket( bucketId, bucketDisplayName, imageURLString ) );
        }

      mPreviousBucketDisplayName = bucketDisplayName;
      }

    List<Bucket> getBucketList()
      {
      return ( mBucketList );
      }
    }


  /*****************************************************
   *
   * An image receiver that builds a list of images.
   *
   *****************************************************/
  private class ImageListBuilder implements IImageReceiver
    {
    ArrayList<Image> mImageList;

    ImageListBuilder()
      {
      mImageList = new ArrayList<>();
      }

    @Override
    public void newImage( int id, String bucketId, String bucketDisplayName, String imageURLString )
      {
      mImageList.add( new Image( id, imageURLString ) );
      }

    List<Image> getImageList()
      {
      return ( mImageList );
      }
    }


  private class Bucket implements IImagePickerItem
    {
    private String  mId;
    private String  mDisplayName;
    private String  mImageURLString;


    Bucket( String id, String displayName, String imageURLString )
      {
      mId             = id;
      mDisplayName    = displayName;
      mImageURLString = imageURLString;
      }


    @Override
    public String getImageURLString()
      {
      return ( mImageURLString );
      }

    @Override
    public void loadThumbnailImageInto( Context context, ImageView imageView )
      {
      Picasso.with( context )
              .load( mImageURLString )
              .fit()
              .centerCrop()
              .into( imageView );
      }

    @Override
    public String getLabel()
      {
      return ( mDisplayName );
      }

    @Override
    public String getKeyIfParent()
      {
      return ( mId );
      }

    @Override
    public ISelectableItem getSelectableItem()
      {
      return ( null );
      }

    @Override
    public int getSelectedCount( LinkedHashMap<String, ISelectableItem> selectableItemTable )
      {
      return ( 0 );
      }
    }


  private static class Image implements IImagePickerItem, ISelectableItem
    {
    ////////// Static Variable(s) //////////

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
      {
      public Image createFromParcel( Parcel in )
        {
        return ( new Image( in ) );
        }

      public Image[] newArray( int size )
        {
        return ( new Image[ size ] );
        }
      };


    private int     mId;
    private String  mImageURLString;


    Image( int id, String imageURLString )
      {
      mId             = id;
      mImageURLString = imageURLString;
      }

    Image( Parcel sourceParcel )
      {
      mId             = sourceParcel.readInt();
      mImageURLString = sourceParcel.readString();
      }


    ////////// Parcelable Method(s) //////////

    @Override
    public int describeContents()
      {
      return ( 0 );
      }

    @Override
    public void writeToParcel( Parcel targetParcel, int flags )
      {
      targetParcel.writeInt( mId );
      targetParcel.writeString( mImageURLString );
      }


    @Override
    public String getKey()
      {
      return ( String.valueOf( mId ) );
      }


    @Override
    public String getImageURLString()
      {
      return ( mImageURLString );
      }


    @Override
    public void loadThumbnailImageInto( Context context, ImageView imageView )
      {
      Picasso.with( context )
              .load( mImageURLString )
              .fit()
              .centerCrop()
              .into( imageView );
      }


    @Override
    public String getLabel()
      {
      return ( null );
      }


    @Override
    public String getKeyIfParent()
      {
      return ( null );
      }

    @Override
    public ISelectableItem getSelectableItem()
      {
      return ( this );
      }

    @Override
    public int getSelectedCount( LinkedHashMap<String, ISelectableItem> selectableItemTable )
      {
      return ( selectableItemTable.containsKey( String.valueOf( mId ) ) ? 1 : 0 );
      }
    }

  }
