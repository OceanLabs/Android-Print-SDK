# SDK Customisation

The Kite SDK provides the ability to customise various aspects of its behaviour, such as providing alternative image pickers, or analytics callbacks.

This guide demonstrates how to use this feature in your apps.


## Overview

The Kite SDK uses a **customiser** to alter various aspects of its behaviour. For instance, when the SDK needs to know what image sources are available (e.g. Facebook, Instagram etc.), it makes a request to the customiser, which returns a list.

The SDK has a default customiser - `SDKCustomiser`, which returns values that provide the default behaviour. For example, the `getImageSources` method returns the default image sources (the device camera and Instagram) available within the SDK screens :

```
    public AImageSource[] getImageSources()
      {
      return ( new AImageSource[] { new DeviceImageSource(), new InstagramImageSource() } );
      }
```

In order to alter the default behaviour, you need to create a customiser, and supply it when initialising the SDK:


```
    ...

    import ly.kite.SDKCustomiser;

    ...

    public class MySDKCustomiser extends SDKCustomiser
      {

      ...

      }
```

```
    KiteSDK.getInstance( this, apiKey, environment )
      .setCustomiser( MySDKCustomiser.class )
      .startShopping( this, assets );
```

If you do not override any methods within the customiser, you will simply get the default behaviour. You may, however, override any methods that you wish, to alter the behviour.

For example, if you do not require the user to enter a contact telephone number on the shipping screen, you would override the `requestPhoneNumber` method as follows:

```
    ...

    import ly.kite.SDKCustomiser;

    ...

    public class MySDKCustomiser extends SDKCustomiser
      {

      public boolean requestPhoneNumber()
        {
        return ( false );
        }

      }
```

For a complete list of customisable behaviour, please see the `SDKCustomiser` class in the Kite SDK.
