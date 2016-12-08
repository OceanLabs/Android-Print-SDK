# SDK Customisation

The Kite SDK provides the ability to customise various aspects of its behaviour, such as providing alternative image pickers, or analytics callbacks.

This guide demonstrates how to use this feature in your apps.


## Overview

The Kite SDK uses a **customiser** to alter various aspects of its behaviour. For instance, when the SDK needs to know what image sources are available (e.g. Facebook, Instagram etc.), it makes a request to the customiser, which returns a list.

The SDK uses a default customiser: `SDKCustomiser`. This returns values that provide the default behaviour. For example, the `getImageSources` method returns the default image sources (the device camera and Instagram) available within the SDK screens :

```
    public AImageSource[] getImageSources()
      {
      return ( new AImageSource[] { new DeviceImageSource(), new InstagramImageSource() } );
      }
```


In order to customise the SDK behaviour, you first need to create a customiser class that extends the `SDKCustomiser`:
```
    ...

    import ly.kite.SDKCustomiser;

    ...

    public class MySDKCustomiser extends SDKCustomiser
      {

      ...

      }
```

Then you need to pass the name of your own customiser to the SDK when initialising it:
```
    KiteSDK.getInstance( this, apiKey, environment )
      .setCustomiser( MySDKCustomiser.class )
      .startShopping( this, assets );
```

If you do not override any methods within the customiser, you will simply get the default behaviour. You may, however, change this by overriding any methods that you choose.

For example, if you do not require the user to enter a contact telephone number on the shipping screen, you would override the `requestPhoneNumber` method as follows:

```
    ...

    import ly.kite.SDKCustomiser;

    ...

    public class MySDKCustomiser extends SDKCustomiser
      {
      ...
      public boolean requestPhoneNumber()
        {
        return ( false );
        }
      ...
      }
```

For a complete list of customisable behaviour, please see the `SDKCustomiser` class in the Kite SDK.

Note that the customiser is initialised with a context; you can access this context by calling the `getContext()` method.


## Order Submission Callback

One of the capabilities offered by the customiser is to call back to your application when an order is successfully submitted.

To use this facility, you should supply your own customiser and override the `getOrderSubmissionSuccessListener()` method. When this method is called, it should return an object that implements the `IOrderSubmissionSuccessListener` interface.

The `onOrderSubmissionSuccess` method in your object will be called when an order is successfully placed.

The sample app demonstrates its use:
```
    ...

    import ly.kite.SDKCustomiser;

    ...

    public class MySDKCustomiser extends SDKCustomiser
      {
      ...
      public IOrderSubmissionSuccessListener getOrderSubmissionSuccessListener()
        {
        return ( new IOrderSubmissionSuccessListener()
          {
          @Override
          public void onOrderSubmissionSuccess( Order sanitisedOrder )
            {
            Toast.makeText( getContext(), "Order success: " + sanitisedOrder.getReceipt(), Toast.LENGTH_SHORT ).show();
            }
          } );
        }
      ...
      }
```

Note that your callback method should not make any UI changes, nor should it perform any intensive processing.

Note also that the Order object returned by the callback is a sanitised copy of the order: it does not contain (e.g.) the jobs.
