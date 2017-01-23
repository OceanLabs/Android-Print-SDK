/*****************************************************
 *
 * PhotobookSummaryCustomiser.java
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

package ly.kite;


///// Import(s) /////

import android.content.Context;
import android.view.Gravity;

import ly.kite.analytics.IAnalyticsEventCallback;
import ly.kite.analytics.NullAnalyticsEventCallback;
import ly.kite.checkout.APaymentFragment;
import ly.kite.checkout.AShippingActivity;
import ly.kite.checkout.DefaultPaymentFragment;
import ly.kite.checkout.ICreditCardAgent;
import ly.kite.checkout.IOrderSubmissionResultListener;
import ly.kite.checkout.ShippingActivity;
import ly.kite.checkout.StripeCreditCardAgent;
import ly.kite.instagramphotopicker.InstagramImageSource;
import ly.kite.journey.AImageSource;
import ly.kite.journey.DeviceImageSource;
import ly.kite.journey.creation.ICustomImageEditorAgent;
import ly.kite.ordering.IOrderSubmissionSuccessListener;
import ly.kite.ordering.Order;


///// Class Declaration /////

/*****************************************************
 *
 * This is a test customiser that specifies a summary
 * front page for photobooks.
 *
 *****************************************************/
public class PhotobookSummaryCustomiser extends SDKCustomiser
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "PhotobookSummaryCustomiser";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context  mContext;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns true if the photobook cover page is a summary
   * of photos in the content pages.
   *
   *****************************************************/
  public boolean photobookFrontCoverIsSummary()
    {
    return ( true );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

