/*****************************************************
 *
 * InstagramPhotoPicker.java
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

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import java.util.List;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the gateway to the Facebook photo picker
 * functionality.
 *
 *****************************************************/
public class InstagramPhotoPicker
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "InstagramPhotoPicker";

  static public  final String  EXTRA_SELECTED_PHOTOS = "ly.kite.instagramphotopicker.EXTRA_SELECTED_PHOTOS";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts the Instagram photo picker.
   *
   *****************************************************/
  static public void startPhotoPickerForResult( Fragment fragment, String clientId, String redirectUri, int maxImageCount, int activityRequestCode )
    {
    InstagramPhotoPickerActivity.startForResult( fragment, clientId, redirectUri, maxImageCount, activityRequestCode );
    }


  /*****************************************************
   *
   * Returns an array of picked photos.
   *
   *****************************************************/
  static public List<String> getResultPhotos( Intent data )
    {
    return ( InstagramPhotoPickerActivity.getImageURLListFromResult( data ) );
    }


  /*****************************************************
   *
   * Ends a customer session.
   *
   *****************************************************/
  static public void endCustomerSession( Context context )
    {
    InstagramAgent.clearAccessToken( context );
    }


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////


  ////////// Inner Class(es) //////////

  }

