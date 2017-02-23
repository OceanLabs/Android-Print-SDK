# Android Print SDK

![Kite](docs/kite.png)

The [Kite](https://www.kite.ly) Android Print SDK makes it easy to add print on demand functionality to your app.

Harness our worldwide print and distribution network. We'll take care of all the tricky printing and postage stuff for you!

To get started, you will need to have a free Kite developer account. Go to [kite.ly](https://www.kite.ly/) to sign up for free.

## Products

Use print to unlock hidden revenue streams and add value for your users. *In under an hour* you could be using our Android Print SDK to print:

- Magnets
- Polaroid Style Prints
- Square Prints
- Postcards
- A4 (invoices, letters, etc)
- New products being added monthly 

## Features
- Print a wide variety of [products](#products) on demand
- Dynamic control over the pricing of products in you app pricing using our web [Developer Dashboard](https://www.kite.ly/)
- Revenue & order volume analytics available in the web dashboard
- Review, refund or reprint any order within the web dashboard
- Localized currency support
- Worldwide address search & lookup
- No server infrastructure required. We can handle everything for you from processing payments to printing & postage
- Your branding not ours. You can create your own custom checkout & payment UI or customize ours

## Requirements

* Android API Level 14 - Android 4.0 (ICE_CREAM_SANDWICH)

## Installation
### Eclipse
See [Installing the library - Eclipse](docs/eclipse_install.md)
### Android Studio / Gradle
We publish builds of our SDK to the Maven central repository as an .aar file. This file contains all of the classes, resources, and configurations that you'll need to use the library. To install the library inside Android Studio, you can simply declare it as dependecy in your build.gradle file.

```java 
dependencies {
    compile 'ly.kite:kite-print-sdk:5.+'
}
```

Once you've updated your build.gradle file, you can force Android Studio to sync with your new configuration by selecting *Tools -> Android -> Sync Project with Gradle Files*

This should download the aar dependency at which point you'll have access to the Kite Print SDK API calls. If it cannot find the dependency, you should make sure you've specified mavenCentral() as a repository in your build.gradle

## Quick Integration

If you don't want to build your own shopping journey user experience you can integrate the SDK in a matter of minutes: 

```java 
private final boolean PRODUCTION_RELEASE = false;

public void onLaunchSDKButtonClicked(View button) {
    ArrayList<Asset> assets = new ArrayList<>();
    try {
        assets.add(new Asset(new URL( "http://psps.s3.amazonaws.com/sdk_static/4.jpg" )));        
    } catch (MalformedURLException ex) {/* ignore */}
    
    if (PRODUCTION_RELEASE) {
        KiteSDK.getInstance(this, "<YOUR_LIVE_API_KEY>", KiteSDK.DefaultEnvironment.LIVE).startShopping(this, assets);
    } else {
        KiteSDK.getInstance(this, "<YOUR_TEST_API_KEY>", KiteSDK.DefaultEnvironment.TEST).startShopping(this, assets);
    }
}
```

The `Asset` class has several constructors not shown above so that you can launch the SDK with your images in a manner that fits your application. You can find your Kite Print API credentials under the [Credentials](https://www.kite.ly/accounts/credentials/) section of the development dashboard.

## Product Filtering

When launching the SDK, it is possible to limit the set of products (by supplying product ids) that you wish to offer to the user:

```java
    ArrayList<Asset> assets = new ArrayList<>();

    try
      {
      assets.add(new Asset(new URL( "http://psps.s3.amazonaws.com/sdk_static/4.jpg" )));
      }
    catch (MalformedURLException ex) {/* Do something sensible */}

    KiteSDK.getInstance(this, "<YOUR_LIVE_API_KEY>", KiteSDK.DefaultEnvironment.LIVE).startShoppingForProducts( this, assets, "my_product_id" );

```


## Launch to Product / Group

The SDK may be launched directly into a product group or product:

```java

    KiteSDK.getInstance(this, "<YOUR_LIVE_API_KEY>", KiteSDK.DefaultEnvironment.LIVE).startShoppingForProductGroup( this, myAssets, "My Product Group Label" );

    KiteSDK.getInstance(this, "<YOUR_LIVE_API_KEY>", KiteSDK.DefaultEnvironment.LIVE).startShoppingForProductGroup( this, "My Product Group Label" );

    KiteSDK.getInstance(this, "<YOUR_LIVE_API_KEY>", KiteSDK.DefaultEnvironment.LIVE).startShoppingForProduct( this, myAssets, "my_product_id" );

    KiteSDK.getInstance(this, "<YOUR_LIVE_API_KEY>", KiteSDK.DefaultEnvironment.LIVE).startShoppingForProduct( this, "my_product_id" );

```


## Custom Checkout
You can build your own UI if you don't want to use or customize the provided checkout and payment experience. You can still use the Kite Print SDK to handle the print order creation and submission: 

1. [Create a print order](docs/create_print_order.md) representing the product(s) you wish to have printed and posted
2. [Set the shipping address](docs/shipping.md) to which the order will be delivered
3. [Take payment from the user](docs/payment.md) for the order and submit it to our servers for printing and posting
4. [Register your payment details](https://www.kite.ly/accounts/billing/) with us so that we can pay you when your users place orders

## Credentials & Environments
Your mobile app integration requires different API Key values for each environment: Live and Test (Sandbox).

You can find your Kite Print API credentials under the [Credentials](https://www.kite.ly/accounts/credentials/) section of the development dashboard.

### Sandbox

Your Sandbox API Key can be used to submit test print orders to our servers. These orders will not be printed and posted but will allow you to integrate the Print SDK into your app without incurring cost. During development and testing you'll primarily want to be using the sandbox environment to avoid moving real money around.

When you're ready to test the end to end printing and postage process; and before you submit your app to the App Store, you'll need to swap in your live API key.

### Live

Your Live API Key is used to submit print orders to our servers that will be printed and posted to the recipient specified. Live orders cost real money. This cost typically passed on to your end user (although this doesn't have to be the case if you want to cover it yourself). 

Logging in to our [Developer Dashboard](https://www.kite.ly/) allow's you to dynamically change the end user price i.e. the revenue you want to make on every order. Payment in several currencies is supported so that you can easily localize prices for your users. The dashboard also provides an overview of print order volume and the money you're making.

## Documentation

* These docs in the SDK, which include an overview of usage, step-by-step integration instructions, and sample code
* The sample app included in this SDK
* Source files are thoroughly documented; refer to them as needed for extra details about any given class or parameter
* The [Kite Print API & SDK Developer Docs](https://www.kite.ly/docs/1.1/), which cover error codes and optional server-side integration instructions


### Push Notifications

Push notifications are a powerful marketing tool that if used effectively can increase both user engagement and customer life time value by driving more sales within your application.

A guide to configuring push notifications in your app can be found [here](https://github.com/OceanLabs/Android-Print-SDK/blob/master/docs/push_notifications.md).

## Open Source Acknowledgements
The Android Print SDK uses software created by the Open Source community, you can find a full list of acknowledgements [here](https://kite.uservoice.com/knowledgebase/articles/1141681-open-source).

## License

Kite Android Print SDK is available under a modified MIT license. See the LICENSE file for more info.

