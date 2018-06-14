/*****************************************************
 *
 * FromPhoneImageSource.java
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

package ly.kite.photofromphone;


///// Import(s) /////

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import ly.kite.R;
import ly.kite.journey.AImageSource;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents an upload from phone image source.
 *
 *****************************************************/
public class FromPhoneImageSource extends AImageSource
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG  = "FromPhoneImageSource";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private PhotoFromPhoneFragment mPhotoFromPhotaFragment;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public FromPhoneImageSource()
    {
    super( R.color.image_source_background_from_phone,
            R.drawable.ic_image_source_device,
            R.string.kitesdk_image_source_from_phone,
            R.id.upload_image_from_phone,
            R.string.kitesdk_upload_photo_from_phone);
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
  public void onPick( Fragment fragment, int addedAssetCount, boolean supportsMultiplePacks, int packSize, int startingIndex )
    {
    // Start the photo from phone fragment

    mPhotoFromPhotaFragment = new PhotoFromPhoneFragment();

    mPhotoFromPhotaFragment.setTargetFragment( fragment, 0 );

    mPhotoFromPhotaFragment.show( fragment.getFragmentManager(), PhotoFromPhoneFragment.TAG );
    }


  /*****************************************************
   *
   * Adds any picked images to the supplied list. Note that
   * the result might either be from the built-in single image
   * picker, or the multiple photo picker.
   *
   *****************************************************/
  @Override
  public void getAssetsFromPickerResult( Activity activity, Intent data, IAssetConsumer assetConsumer )
    {
    // The upload photo from phone dialog is a fragment, not an activity, so it doesn't return
    // a result in the same way as other pickers. This method, therefore, is redundant for this
    // picker.
    }


  ////////// Inner Class(es) //////////

  }