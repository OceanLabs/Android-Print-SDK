/*****************************************************
 *
 * DeviceImageSource.java
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

package ly.kite.journey;


///// Import(s) /////

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.List;

import ly.kite.R;
import ly.kite.catalogue.Asset;
import ly.kite.photopicker.Photo;
import ly.kite.photopicker.PhotoPicker;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a local device image source.
 *
 *****************************************************/
public class DeviceImageSource extends AImageSource
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "DeviceImageSource";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public DeviceImageSource()
    {
    super( R.color.image_source_background_device,
           R.drawable.ic_add_photo_white,
           R.string.image_source_device,
           R.id.add_photo_from_device,
           R.string.select_photo_from_device );
    }


  ////////// AImageSource Method(s) //////////

  /*****************************************************
   *
   * Returns true, since this image source is always
   * available.
   *
   *****************************************************/
  public boolean isAvailable( Context context )
    {
    return ( true );
    }


  /*****************************************************
   *
   * Called when the image source is picked to select
   * images.
   *
   *****************************************************/
  public void onPick( Fragment fragment, boolean preferSingleImage )
    {
    // If the caller would prefer a single image then use the system photo picker. Otherwise
    // use the photo picker.

    if ( preferSingleImage )
      {
      Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
      intent.setType( "image/*" );

      try
        {
        fragment.startActivityForResult( Intent.createChooser( intent, fragment.getString( R.string.select_photo_from_device ) ), getActivityRequestCode() );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to start activity for chooser intent", exception );
        }
      }
    else
      {
      PhotoPicker.startPhotoPickerForResult( fragment, getActivityRequestCode() );
      }
    }


  /*****************************************************
   *
   * Adds any picked images to the supplied list. Note that
   * the result might either be from the built-in single image
   * picker, or the multiple photo picker.
   *
   *****************************************************/
  @Override
  public void getAssetsFromPickerResult( Intent data, List<Asset> assetList )
    {
    if ( data != null )
      {
      // Check for single device image

      Uri imageURI = data.getData();

      if ( imageURI != null )
        {
        assetList.add( new Asset( imageURI ) );

        return;
        }


      // Check for multiple device images

      try
        {
        Photo[] devicePhotos = PhotoPicker.getResultPhotos( data );

        if ( devicePhotos != null )
          {
          for ( Photo devicePhoto : devicePhotos )
            {
            assetList.add( new Asset( devicePhoto.getUri() ) );
            }
          }
        }
      catch ( NullPointerException npe )
        {
        // Ignore
        }
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

