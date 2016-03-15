# Adding Custom Image Pickers

The Kite SDK provides a few built-in image pickers, which allow users to import images from their local device, Instagram, and Facebook, enabling them to produce print products based on those images.

Additionally, however, developers may create their own custom image pickers, which allows the selection of images from other sources. For instance, Kite implemented a custom image picker for a customer that allowed users to select pre-defined images from an 'art gallery'.


## Overview

In order to create a custom image picker, you will need to perform the following steps:

1. Create an image source for the custom picker.
2. Include the new image source when configuring the Kite SDK.


## Create an image source for the custom picker

Image sources within the Kite SDK extend the `AImageSource` class, and provide custom implementations for particular methods. As an example of how to implement your own image source, look at the `FacebookImageSource` contained within the **SampleApp** module in the Kite SDK. The important parts of this implementation are:

### Constructor

The constructor should be a zero argument constructor, but must call the super constructor with the following arguments:

* backgroundColourResourceId The resource id of the background colour to be used on image selection screens
* iconResourceId             The resource id of the icon to be used on image selection screens
* labelResourceId            The resource id of the textual label to be used on image selection screens
* menuItemId                 The item id of the image source when used in menus
* menuItemTitleResourceId    The resource id of the title used in menus


An example of how your constructor might look is as follows;

```
  public FacebookImageSource()
    {
    super( R.color.image_source_background_facebook,
            R.drawable.ic_add_facebook_white,
            R.string.image_source_facebook,
            R.id.add_photo_from_facebook,
            R.string.select_photo_from_facebook );
    }
```


### `public boolean isAvailable( Context context )`

The SDK calls this method to establish whether the image source is available. You might use it to check whether you have the necessary credentials before making the image source available. Alternatively the image source might always be available, in which case you can simply return `true`.

```
  public boolean isAvailable( Context context )
    {
    return ( true );
    }
```


### `public void onPick( Fragment fragment, int maxImageCount )`

The SDK calls this method when the user has selected your image source, and you should launch your custom image picker. the `maxImageCount` provides an indication of they maximum number of images that are required. For example, a phone case only uses a single image, so you may wish to restrict the user to selecting just one. You are not required to respect this value, since unrequired images will simply be ignored.


### `public void getAssetsFromPickerResult( Activity activity, Intent data, IAssetConsumer assetConsumer )`

This method is called once the your image picker has completed, images have been selected, and your picker activity returns a result. Your implementation must supply the details of the chosen images as extras within the result Intent, and return them. Your code may look similar to the following:

```
Intent resultIntent = new Intent();

resultIntent.putExtra( EXTRA_SELECTED_PHOTOS, photoArray );

setResult( Activity.RESULT_OK, resultIntent );

finish();
````

When the `getAssetsFromPickerResult` method is called, it is supplied the intent that you returned with the `setResult` method. You should then create Assets for the chosen images, and return them to the `IAssetConsumer`. Note that the `getAssetsFromPickerResult` method may return the assets immediately (from within the method itself) or asynchronously according to your particular implementation.


## Include the new image source when configuring the Kite SDK.

When you first initialise the SDK, you can set various configuration parameters. Your code may look something like this:

```
    KiteSDK.getInstance( this, apiKey, environment )
      .setRequestPhoneNumber( false ) );
      .setInstagramCredentials( INSTAGRAM_API_KEY, INSTAGRAM_REDIRECT_URI )
      .startShopping( this, assets );
```

By default, the **DeviceImageSource** and **InstagramImageSource**s are enabled. You may, however, manually set the available image sources, including your custom images source:

```
.setImageSources( new DeviceImageSource(), new InstagramImageSource(), new FacebookImageSource(), new MyImageSource() )
```

Note that the order in which you specify the image sources in this method call corresponds to the order in which they are shown on image selection screens, and within menus. You should also remember that `setImageSources` replaces the current image sources with those that you supply. You must include *all* the sources (including the device and Instagram image sources) that you wish to use.

