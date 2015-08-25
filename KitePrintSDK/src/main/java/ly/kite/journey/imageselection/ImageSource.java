/*****************************************************
 *
 * ImageSource.java
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

package ly.kite.journey.imageselection;


///// Import(s) /////

import android.app.Fragment;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.instagramphotopicker.InstagramPhotoPicker;
import ly.kite.photopicker.PhotoPicker;


///// Class Declaration /////

/*****************************************************
 *
 * An image source.
 *
 *****************************************************/
public enum ImageSource
  {
    DEVICE ( R.color.image_source_background_device, R.drawable.ic_add_photo_white, R.string.IMAGES )
            {
            public void onClick( Fragment fragment, int requestCode )
              {
              // Clicking on the device image source starts a chooser to pick images from the device
              PhotoPicker.startPhotoPickerForResult( fragment, requestCode );
              }
            },

    INSTAGRAM ( R.color.image_source_background_instagram, R.drawable.ic_add_instagram_white, R.string.INSTAGRAM )
            {
            public void onClick( Fragment fragment, int requestCode )
              {
              // Clicking on the Instagram image source starts our Instagram image picker library
              String instagramClientId    = KiteSDK.getInstance( fragment.getActivity() ).getInstagramClientId();
              String instagramRedirectURI = KiteSDK.getInstance( fragment.getActivity() ).getInstagramRedirectURI();
              InstagramPhotoPicker.startPhotoPickerForResult( fragment, instagramClientId, instagramRedirectURI, requestCode );
              }
            }
//    ,FACEBOOK
    ;


  private int  mBackgroundColourResourceId;
  private int  mIconResourceId;
  private int  mLabelResourceId;


  private ImageSource( int backgroundColourResourceId, int iconResourceId, int labelResourceId )
    {
    mBackgroundColourResourceId = backgroundColourResourceId;
    mIconResourceId             = iconResourceId;
    mLabelResourceId            = labelResourceId;
    }


  int backgroundColourResourceId()
    {
    return ( mBackgroundColourResourceId );
    }

  int iconResourceId()
    {
    return ( mIconResourceId );
    }

  int labelResourceId()
    {
    return ( mLabelResourceId );
    }


  /*****************************************************
   *
   * Called when this image source is clicked.
   *
   *****************************************************/
  abstract public void onClick( Fragment fragment, int requestCode );

  }
