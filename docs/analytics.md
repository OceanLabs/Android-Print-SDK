# Analytics

The Kite SDK allows developers to supply a custom analytics callback, which is called when various events occur within the SDK.

This guide demonstrates how to use this feature in your apps.


## Overview

To supply use a custom analytics callback, a **customiser** must be used.

The customiser will be called from time to time, requesting an analytics callback instance. To use a custom callback, override the following method:

```
  public IAnalyticsEventCallback getAnalyticsEventCallback( Context context )
    {
    return ( new MyAnalyticsEventCallback( context ) );
    }
```


Return your custom analytics callback. Note that this could implement the `IAnalyticsEventCallback` interface.

Your custom analytics callback should implement all the callback methods, which will then be called as events occur.


## Analytics Callback Events

The following is a list of the event callback methods, and a description of when they are called.

`void onSDKLoaded( String entryPoint )`

Called when the Kite SDK is loaded.


`void onCategoryListScreenViewed()`

Called when the category list / product group list screen is displayed.


`void onProductListScreenViewed()`

Called when the product list screen is displayed.


`void onProductDetailsScreenViewed( Product product )`

Called when the product detail screen is displayed.


`void onCreateProductScreenViewed( Product product )`

Called when the photo selection screen is displayed.


`void onPhotobookEditScreenViewed()`

Called when the photobook editing screen is displayed.


`void onImagePickerScreenViewed()`

Called when an image picker screen is displayed.


`void onProductOrderReviewScreenViewed( Product product )`

Called when the photo review screen is displayed.


`void onBasketScreenViewed()`

Called when the basket screen is displayed.


`void onContinueShoppingButtonTapped()`

Called when the *Continue Shopping* button on the basket screen is pressed. Note that this will not be called if the back button is pressed. 


`void onShippingScreenViewed( Order printOrder, String variant, boolean showPhoneEntryField )`

Called when the shipping screen is displayed.


`void onAddressSelectionScreenViewed()`

Called when the address list / selection screen is displayed.


`void onPaymentMethodScreenViewed( Order printOrder )`

Called when the payment screen is displayed.


`void onPaymentMethodSelected( String paymentMethod )`

Called when a payment method button is pressed.


`void onPaymentCompleted( Order printOrder, String paymentMethod )`

Called when a payment has been authorised.


`void onPrintOrderSubmission( Order printOrder )`

Called when a print order has been submitted. Note that this does not imply that the order has been successfully processed.
