/*****************************************************
 *
 * InstagramImageSource.java
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
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import ly.kite.R;
import ly.kite.KiteSDK;
import ly.kite.journey.AImageSource;
import ly.kite.util.Asset;
import ly.kite.instagramphotopicker.InstagramPhoto;
import ly.kite.instagramphotopicker.InstagramPhotoPicker;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a local device image source.
 *
 *****************************************************/
public class InstagramImageSource extends AImageSource
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "InstagramImageSource";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public InstagramImageSource()
    {
    super( R.color.image_source_background_instagram,
           R.drawable.ic_add_instagram_white,
           R.string.image_source_instagram,
           R.id.add_photo_from_instagram,
           R.string.select_photo_from_instagram );
    }


  ////////// AImageSource Method(s) //////////

  /*****************************************************
   *
   * Returns true if the Instagram image source is available.
   * This will be the case if we have credentials.
   *
   *****************************************************/
  public boolean isAvailable( Context context )
    {
    return ( KiteSDK.getInstance( context ).haveInstagramCredentials() );
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

        return ( R.layout.grid_item_image_source_instagram_horizontal );

      case VERTICAL:

        return ( R.layout.grid_item_image_source_instagram_vertical );
      }

    return ( 0 );
    }


  /*****************************************************
   *
   * Called when the image source is picked to select
   * images.
   *
   *****************************************************/
  public void onPick( Fragment fragment, int maxImageCount )
    {
    // TODO: Max image count is ignored for now

    // Clicking on the Instagram image source starts our Instagram image picker library

    KiteSDK kiteSDK = KiteSDK.getInstance( fragment.getActivity() );

    String instagramClientId    = kiteSDK.getInstagramClientId();
    String instagramRedirectURI = kiteSDK.getInstagramRedirectURI();

    InstagramPhotoPicker.startPhotoPickerForResult( fragment, instagramClientId, instagramRedirectURI, getActivityRequestCode() );
    }


  /*****************************************************
   *
   * Returns picked photos as assets.
   *
   *****************************************************/
  @Override
  public void getAssetsFromPickerResult( Activity activity, Intent data, IAssetConsumer assetConsumer )
    {
    InstagramPhoto instagramPhotos[] = InstagramPhotoPicker.getResultPhotos( data );

    if ( instagramPhotos != null )
      {
      // Create an asset list, populate it, and call back to the consumer immediately.

      List<Asset> assetList = new ArrayList<>( instagramPhotos.length );

      for ( InstagramPhoto instagramPhoto : instagramPhotos )
        {
        assetList.add( new Asset( instagramPhoto.getFullURL() ) );
        }

      assetConsumer.isacOnAssets( assetList );
      }
    }


  /*****************************************************
   *
   * Called to end the customer session.
   *
   *****************************************************/
  @Override
  public void endCustomerSession( Context context )
    {
    InstagramPhotoPicker.logout( context );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

