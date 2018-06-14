/*****************************************************
 *
 * SDKCustomiser.java
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
import ly.kite.facebookphotopicker.FacebookImageSource;
import ly.kite.instagramphotopicker.InstagramImageSource;
import ly.kite.journey.AImageSource;
import ly.kite.journey.DeviceImageSource;
import ly.kite.journey.creation.ICustomImageEditorAgent;
import ly.kite.ordering.IOrderSubmissionSuccessListener;
import ly.kite.ordering.Order;


///// Class Declaration /////

/*****************************************************
 *
 * This class is used to customise various aspects of
 * the SDK behaviour. An app may create a child class,
 * and override any methods corresponding to things that
 * that they wish to change.
 *
 *****************************************************/
public class SDKCustomiser
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "SDKCustomiser";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context  mContext;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the context.
   *
   *****************************************************/
  final void setContext( Context context )
    {
    mContext = context;
    }


  /*****************************************************
   *
   * Returns the context.
   *
   *****************************************************/
  final protected Context getContext()
    {
    return ( mContext );
    }


  /*****************************************************
   *
   * Returns the analytics event callback.
   *
   *****************************************************/
  public IAnalyticsEventCallback getAnalyticsEventCallback( Context context )
    {
    return ( new NullAnalyticsEventCallback( context ) );
    }


  /*****************************************************
   *
   * Returns true if the inactivity timer is enabled,
   * false otherwise.
   *
   *****************************************************/
  public boolean inactivityTimerEnabled()
    {
    return ( false );
    }


  /*****************************************************
   *
   * Returns the scale type to be used for product images.
   *
   *****************************************************/
  public int getChooseProductImageAnchorGravity( String productId )
    {
    return ( Gravity.NO_GRAVITY );
    }


  /*****************************************************
   *
   * Returns the layout resource to use for the choose product
   * screen.
   *
   *****************************************************/
  public int getChooseProductLayoutResourceId()
    {
    return ( R.layout.screen_choose_product );
    }


  /*****************************************************
   *
   * Returns the layout resource to use for the choose product
   * screen grid footer, or 0 if there is no footer.
   *
   *****************************************************/
  public int getChooseProductGridFooterLayoutResourceId()
    {
    return ( 0 );
    }


  /*****************************************************
   *
   * Returns the image sources.
   *
   *****************************************************/
  public AImageSource[] getImageSources()
    {
    return ( new AImageSource[] { new DeviceImageSource(), new InstagramImageSource(), new FacebookImageSource()} );
    }


  /*****************************************************
   *
   * Returns true if the photobook cover page is a summary
   * of photos in the content pages.
   *
   *****************************************************/
  public boolean photobookFrontCoverIsSummary()
    {
    return ( false );
    }


  /*****************************************************
   *
   * Returns the custom image editor agent.
   *
   *****************************************************/
  public ICustomImageEditorAgent getCustomImageEditorAgent()
    {
    return ( null );
    }


  /*****************************************************
   *
   * Returns the class that implements the shipping activity.
   *
   *****************************************************/
  public Class<? extends AShippingActivity> getShippingActivityClass()
    {
    return ( ShippingActivity.class );
    }


  /*****************************************************
   *
   * Returns true if the user's phone number should be
   * entered on the shipping screen.
   *
   *****************************************************/
  public boolean requestPhoneNumber()
    {
    return ( true );
    }


  /*****************************************************
   *
   * Returns true if the address book is enabled.
   *
   *****************************************************/
  public boolean addressBookEnabled()
    {
    return ( true );
    }


  /*****************************************************
   *
   * Returns the payment fragment.
   *
   *****************************************************/
  public APaymentFragment getPaymentFragment()
    {
    return ( new DefaultPaymentFragment() );
    }


  /*****************************************************
   *
   * Returns the credit card agent. The default is now
   * the Stripe credit card agent.
   *
   *****************************************************/
  public ICreditCardAgent getCreditCardAgent()
    {
    return ( new StripeCreditCardAgent() );
    }


  /*****************************************************
   *
   * Returns a callback for successful order submission.
   * The default is null, i.e. there is no listener.
   *
   *****************************************************/
  public IOrderSubmissionSuccessListener getOrderSubmissionSuccessListener()
    {
    return ( null );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

