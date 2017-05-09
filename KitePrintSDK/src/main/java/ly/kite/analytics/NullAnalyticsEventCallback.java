/*****************************************************
 *
 * NullAnalyticsEventCallback.java
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

import android.content.Context;
import ly.kite.ordering.Order;
import ly.kite.catalogue.Product;

/*****************************************************
 *
 * A no-op implementation of IAnalyticsEventCallback
 * interface
 *
 *****************************************************/
public class NullAnalyticsEventCallback implements IAnalyticsEventCallback
  {

  ////////// Member Variable(s) //////////
  private final Context mContext;

  ////////// Constructor(s) //////////
  public NullAnalyticsEventCallback( Context context )
      {
        this.mContext = context;
      }

  ////////// Method(s) //////////

  @Override
  public void onSDKLoaded(String entryPoint) {}

  @Override
  public void onCategoryListScreenViewed() {}

  @Override
  public void onProductListScreenViewed() {}

  @Override
  public void onProductDetailsScreenViewed( Product product) {}

  @Override
  public void onCreateProductScreenViewed(Product product) {}

  @Override
  public void onPhotobookEditScreenViewed() {}

  @Override
  public void onImagePickerScreenViewed() {}

  @Override
  public void onProductOrderReviewScreenViewed(Product product) {}

  @Override
  public void onBasketScreenViewed() {}

  @Override
  public void onContinueShoppingButtonTapped() {}

  @Override
  public void onShippingScreenViewed( Order printOrder, String variant, boolean showPhoneEntryField) {}

  @Override
  public void onAddressSelectionScreenViewed() {}

  @Override
  public void onPaymentMethodScreenViewed( Order printOrder ) {}

  @Override
  public void onPaymentMethodSelected( String paymentMethod ) {}

  @Override
  public void onPaymentCompleted( Order printOrder, String paymentMethod) {}

  @Override
  public void onPrintOrderSubmission( Order printOrder) {}
  }
