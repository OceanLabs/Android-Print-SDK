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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ly.kite.R;
import ly.kite.devicephotopicker.DevicePhotoPicker;
import ly.kite.util.Asset;


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
  static private final String  LOG_TAG  = "DeviceImageSource";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns any selected images as a list of assets.
   *
   *****************************************************/
  static public ArrayList<Asset> getAssets( Intent data )
    {
    ArrayList<Asset> assetList = new ArrayList<>();

    List<String> photoURLStringList = DevicePhotoPicker.getResultPhotos( data );

    if ( photoURLStringList != null )
      {
      for ( String urlString : photoURLStringList )
        {
        try
          {
          assetList.add( Asset.create( new URL( urlString ) ) );
          }
        catch ( MalformedURLException mue )
          {
          Log.e( LOG_TAG, "Unable to create asset from device photo URL: " + urlString, mue );
          }
        }
      }

    return ( assetList );
    }


  ////////// Constructor(s) //////////

  public DeviceImageSource()
    {
    super( R.color.image_source_background_device,
           R.drawable.ic_image_source_device,
           R.string.image_source_device,
           R.id.add_image_from_device,
           R.string.select_photo_from_device );
    }


  protected DeviceImageSource( int horizontalBackgroundColourResourceId,
                                  int verticalBackgroundColourResourceId,
                                  int horizontalLayoutIconResourceId,
                                  int verticalLayoutIconResourceId,
                                  int labelResourceId,
                                  int menuItemId,
                                  int menuItemTitleResourceId )
    {
    super( horizontalBackgroundColourResourceId,
            verticalBackgroundColourResourceId,
            horizontalLayoutIconResourceId,
            verticalLayoutIconResourceId,
            labelResourceId,
            menuItemId,
            menuItemTitleResourceId );
    }


  ////////// AImageSource Method(s) //////////

  /*****************************************************
   *
   * Returns true, since this image source is always
   * available.
   *
   *****************************************************/
  @Override
  public boolean isAvailable( Context context )
    {
    return ( true );
    }


  /*****************************************************
   *
   * Returns the layout resource id to be used to display
   * this image source for the supplied layout type.
   *
   *****************************************************/
  @Override
  public int getLayoutResource( LayoutType layoutType )
    {
    switch ( layoutType )
      {
      case HORIZONTAL:

        return ( R.layout.grid_item_image_source_device_horizontal );

      case VERTICAL:

        return ( R.layout.grid_item_image_source_device_vertical );
      }

    return ( 0 );
    }


  /*****************************************************
   *
   * Called when the image source is picked to select
   * images.
   *
   *****************************************************/
  @Override
  public void onPick( Fragment fragment, int addedAssetCount, boolean supportsMultiplePacks, int packSize, int maxImageCount )
    {
    requestPermission( Manifest.permission.READ_EXTERNAL_STORAGE, new StartPickerRunnable( fragment, addedAssetCount, supportsMultiplePacks, packSize, maxImageCount ) );
    }


  /*****************************************************
   *
   * Returns picked photos as assets.
   *
   *****************************************************/
  @Override
  public void getAssetsFromPickerResult( Activity activity, Intent data, IAssetConsumer assetConsumer )
    {
    ArrayList<Asset> assetList = getAssets( data );

    if ( assetList.size() > 0 )
      {
      assetConsumer.isacOnAssets( assetList );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A runnable that simply calls the onPick method. Used
   * to call the method once permissions have been granted.
   *
   *****************************************************/
  private class StartPickerRunnable extends AStartPickerRunnable
    {
    StartPickerRunnable( Fragment fragment, int addedAssetCount, boolean supportsMultiplePacks, int packSize, int maxImageCount )
      {
      super( fragment, addedAssetCount, supportsMultiplePacks, packSize, maxImageCount );
      }


    @Override
    public void run()
      {
      // There seems to be a bug with determining the orientation of URI-based images on
      // (e.g.) Samsung S6, so we need to always use the photo picker (which returns file
      // paths) rather than the built-in gallery picker (which returns URIs).

      DevicePhotoPicker.startPhotoPickerForResult( mFragment, mAddedAssetCount, mSupportsMultiplePacks, mPackSize, mMaxImageCount, getActivityRequestCode() );
      }
    }

  }