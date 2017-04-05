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
import android.widget.ImageView;

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


    setTitle( R.string.title_device_photo_picker );
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

      // TODO
      }
    else
      {
      ///// Root /////

      // TODO
      }
    }


  @Override
  public void onLoadMoreItems( int depth, String parentKey )
    {
    // TODO
    }


  ////////// Method(s) //////////


  ////////// Inner Class(es) //////////

  private class Bucket implements IImagePickerItem
    {
    @Override
    public String getImageURLString()
      {
      return null;
      }

    @Override
    public void loadThumbnailImageInto( Context context, ImageView imageView )
      {

      }

    @Override
    public String getLabel()
      {
      return null;
      }

    @Override
    public String getKeyIfParent()
      {
      return null;
      }

    @Override
    public ISelectableItem getSelectableItem()
      {
      return null;
      }

    @Override
    public int getSelectedCount( LinkedHashMap<String, ISelectableItem> selectableItemTable )
      {
      return 0;
      }
    }


  private class Image implements IImagePickerItem
    {

    @Override
    public String getImageURLString()
      {
      return null;
      }

    @Override
    public void loadThumbnailImageInto( Context context, ImageView imageView )
      {

      }

    @Override
    public String getLabel()
      {
      return null;
      }

    @Override
    public String getKeyIfParent()
      {
      return null;
      }

    @Override
    public ISelectableItem getSelectableItem()
      {
      return null;
      }

    @Override
    public int getSelectedCount( LinkedHashMap<String, ISelectableItem> selectableItemTable )
      {
      return 0;
      }
    }

  }
