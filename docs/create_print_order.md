Creating a Print Order
==============

This tutorial covers creating a print order to be submitted for printing and posting.

_If you haven't already, see the [README](../README.md) for an initial overview and instructions for adding the SDK to your project._


Overview
--------
1. Initialise the SDK
2. Create `Asset` representations of all the images you want to print
3. Retrieve an up-to-date catalogue of available products.
4. Create `PrintJob`'s for the desired products you want to print and attach the assets created in Step 2
5. Create an `Order` and attach your job(s) created in Step 3


Sample Code
-----------

1. Initialize the SDK and provide your API Keys (these can be found in the [Credentials](https://www.kite.ly/settings/credentials/) section of the development dashboard). You'll typically initialize the SDK once at application startup. You will also need to supply a context.

    ```java
    KiteSDK.initialise( MyActivity.this, "REPLACE_WITH_YOUR_API_KEY", KiteSDK.DefaultEnvironment.TEST );
    ```

    *Note: Test/Sandbox orders will not be printed and posted. The Test/Sandbox environment is purely for testing during development. If you want to submit a real order that will be printed and posted just use your live API key and the `KiteSDK.DefaultEnvironment.LIVE` environment*

2. Create `Asset` representations for every image you want to print. `Asset` has many constructors (including ones not listed below) to support any use case you may have.

    ```java
    ArrayList<Asset> assets = new ArrayList<Asset>();
    assets.add(new Asset(R.drawable.photo));
    assets.add(new Asset(new URL("http://psps.s3.amazonaws.com/sdk_static/4.jpg")));
    assets.add(new Asset("/mnt/external_sd/1.png"));
    ```

3. Retrieve an up-to-date catalogue of available products.

    ```java
    CatalogueLoader.getInstance( this ).requestCatalogue( new ICatalogueConsumer()
      {
      @Override
      public void onCatalogueSuccess( Catalogue catalogue )
        {
        /* Create your order */
        }

      @Override
      public void onCatalogueError( Exception exception )
        {
        /* Handle gracefully */
        }
      } );

    ```

4. Create `PrintJob`'s for every type of product you want to print in this order. A print order can have multiple print jobs attached.

    ```java
    PrintJob magnets      = Job.createPrintJob( catalogue.getProductById( "magnets" ),   assets );
    PrintJob polaroids    = Job.createPrintJob( catalogue.getProductById( "polaroids" ), assets );
    PrintJob squarePrints = Job.createPrintJob( catalogue.getProductById( "squares" ),   assets );

    ```
    
     *Note: The above shows only a small sample of the products available for printing with the SDK. Also, the product ids may vary according to those available for your API key.*

5. Create an `PrintOrder` and attach the print job(s) you created in the previous step

    ```java
    Order order = new Order();
    order.addJob( magnets );
    order.addJob( polaroids );
    order.addJob( squarePrints );
    ```
    
Next Steps
----------

- If you're using the [Managed Checkout](../README.md#managed-checkout) flow where you use our checkout and payment UI then
[create and start a `CheckoutActivity`](managed_checkout.md) passing it the `Order` object you created in Step 4
- Alternatively if you're building your own [Custom Checkout](../README.md#custom-checkout) UI then it's time to [set the shipping address](shipping.md) to which the order will be delivered
