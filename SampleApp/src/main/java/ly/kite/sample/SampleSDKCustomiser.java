/*****************************************************
 *
 * SampleSDKCustomiser.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.sample;


///// Import(s) /////

import android.widget.Toast;

import ly.kite.SDKCustomiser;
import ly.kite.checkout.IOrderSubmissionResultListener;
import ly.kite.facebookphotopicker.FacebookImageSource;
import ly.kite.instagramphotopicker.InstagramImageSource;
import ly.kite.journey.AImageSource;
import ly.kite.journey.DeviceImageSource;
import ly.kite.ordering.IOrderSubmissionSuccessListener;
import ly.kite.ordering.Order;
import ly.kite.photofromphone.FromPhoneImageSource;


///// Class Declaration /////

/*****************************************************
 *
 * This class demonstrates the use of a customiser to
 * change the behaviour of the SDK.
 *
 * To customise any aspect of the SDK, simply override
 * the default methods in the SDKCustomiser class.
 *
 *****************************************************/
public class SampleSDKCustomiser extends SDKCustomiser
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "SampleSDKCustomiser";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns true if the user's phone number should be
   * entered on the shipping screen.
   *
   *****************************************************/
  public boolean requestPhoneNumber()
    {
    return ( false );
    }


  /*****************************************************
   *
   * Returns specific image sources.
   *
   *****************************************************/
  public AImageSource[] getImageSources()
    {
    // Default:
    return ( new AImageSource[] { new DeviceImageSource(), new InstagramImageSource(), new FacebookImageSource()} );
    }


  /*****************************************************
   *
   * Returns a callback for order completion.
   *
   *****************************************************/
  public IOrderSubmissionSuccessListener getOrderSubmissionSuccessListener()
    {
    return ( new IOrderSubmissionSuccessListener()
      {
      @Override
      public void onOrderSubmissionSuccess( Order sanitisedOrder )
        {
        // Toast.makeText( getContext(), "Order success: " + sanitisedOrder.getReceipt(), Toast.LENGTH_SHORT ).show();
        }
      } );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

