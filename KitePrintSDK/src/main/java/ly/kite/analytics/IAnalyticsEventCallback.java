/*****************************************************
 *
 * IAnalyticsEventCallback.java
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

package ly.kite.analytics;


///// Import(s) /////

import ly.kite.ordering.Order;
import ly.kite.catalogue.Product;

///// Class Declaration /////

/*****************************************************
 *
 * Analytics callbacks for the bundled Kite SDK
 * Shopping journey started via Kite.startShopping(...)
 * Your class implementation MUST provide a constructor
 * with a single argument constructor where the argument
 * is a android.content.Context: See NullAnalyticsEventCallback
 * for an example.
 *
 *****************************************************/
public interface IAnalyticsEventCallback
  {

  ////////// Method(s) //////////


  /*****************************************************
   *
   * This method is called at the point the SDK is launched
   *
   *****************************************************/
  void onSDKLoaded( String entryPoint );


  /*****************************************************
   *
   * Called when the user enters the category list screen.
   *
   *****************************************************/
  void onCategoryListScreenViewed();


  /*****************************************************
   *
   * Called when the user enters the product list screen.
   *
   *****************************************************/
  void onProductListScreenViewed();


  /*****************************************************
   *
   * Called when the user enters the product overview screen.
   *
   *****************************************************/
  void onProductDetailsScreenViewed( Product product );


  /*****************************************************
   *
   * Called when the user enters the photo selection screen
   *
   *****************************************************/
  void onCreateProductScreenViewed( Product product );


  /*****************************************************
   *
   * Called when the user enters the photobook edit screen
   *
   *****************************************************/
  void onPhotobookEditScreenViewed();


  /*****************************************************
   *
   * Called when the user enters an image picker screen
   *
   *****************************************************/
  void onImagePickerScreenViewed();


  /*****************************************************
   *
   * Called when the user enters the photo review screen
   *
   *****************************************************/
  void onProductOrderReviewScreenViewed( Product product );


  /*****************************************************
   *
   * Called when the user enters the basket screen
   *
   *****************************************************/
  void onBasketScreenViewed();


  /*****************************************************
   *
   * Called when the user taps the continue shopping button
   *
   *****************************************************/
  void onContinueShoppingButtonTapped();


  /*****************************************************
   *
   * Called when the user enters the shipping screen.
   *
   *****************************************************/
  void onShippingScreenViewed( Order printOrder, String variant, boolean showPhoneEntryField );


  /*****************************************************
   *
   * Called when the user enters the address selection screen.
   *
   *****************************************************/
  void onAddressSelectionScreenViewed();


  /*****************************************************
   *
   * Called when the user enters the payment screen.
   *
   *****************************************************/
  void onPaymentMethodScreenViewed( Order printOrder );


  /*****************************************************
   *
   * Called when the user selects a payment method.
   *
   *****************************************************/
  void onPaymentMethodSelected( String paymentMethod );


  /*****************************************************
   *
   * Called when the user completes payment
   *
   *****************************************************/
  void onPaymentCompleted( Order printOrder, String paymentMethod );


  /*****************************************************
   *
   * Called when the order is submitted to Kite
   *
   *****************************************************/
  void onPrintOrderSubmission( Order printOrder );

  }

