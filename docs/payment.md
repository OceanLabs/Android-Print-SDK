Paying for and Submitting a Print Order
==============

If you don't want to use the checkout experience included with the Kite Android Print SDK (i.e. [Managed Checkout](../README.md#managed-checkout)) then you need to explicitly take payment from your users for the order to be printed and posted. You can create your own UI for capturing card details or you can use the [PayPal Android SDK](https://github.com/paypal/PayPal-Android-SDK).

Payments should be made directly to Kite's PayPal account rather than your own. We then pay you based on your desired margins that you have configured in the [developer dashboard](https://developer.psilov.eu). This is the recommended client side approach if you want to avoid paying for your own server(s) to validate customer payments.

Alternatively we do support a server side payment flow where the user pays you directly. In this approach you'll need a server to validate payment and issue a print request using our [REST API](https://developer.psilov.eu/docs/1.1/). Your client Android app deals solely with your server to submit the print order. If your server is happy with the proof of payment it submits the order to our server on behalf of the client app. See [payment workflows](https://developer.psilov.eu/docs/1.1/payment_workflows) for more details regarding this approach.

_If you haven't already, see the [README](../README.md) for an initial overview and instructions for adding the SDK to your project._

Prerequisites
--------
1. [Create a print order](create_print_order.md) representing the product(s) you wish to have printed and posted
2. [Set the shipping address](shipping.md) to which the order will be delivered

Overview
--------
1. Take payment from the user
    - Using the [PayPal Android SDK](https://github.com/paypal/PayPal-Android-SDK) payment flow if you don't want to create your own UI
    - Alternatively create your own UI and use `PayPalCard` to process the payment
2. Attach the proof of payment to the `PrintOrder` to be verified server side
3. Submit the `PrintOrder` to our server for printing and posting

Sample Code
-----------

1. Take payment from the user. There are two approaches available if you don't want to run your own servers
    - Using the [PayPal Android SDK](https://github.com/paypal/PayPal-Android-SDK) payment flow if you don't want to create your own UI. Follow the best practices laid out in the PayPal Android SDK [documentation](https://github.com/paypal/PayPal-Android-SDK) for making a payment. 
    
	    You'll need to *use our PayPal Client Id & Receiver Email* in your transactions or the proof of payment you receive from PayPal will be rejected by our servers when you submit the print order. Depending on whether your using the Live or Sandbox printing environment our PayPal Client Id & Receiver Email values are different. 
	
	    The Test/Sandbox print environment (`KitePrintSDK.Environment.TEST`) validates print order proof of payments against the Sandbox PayPal environment. The Live print environment (`KitePrintSDK.Environment.LIVE`) validates print order proof of payments against the Live PayPal Environment.
	    
	    The `KitePrintSDK.Environment` enum has handy methods (`getPayPalClientId()`, `getPayPalReceiverEmail()`) for getting the correct PayPal Client Id & Receiver Email for the environment you're using. You can also get hold of the current environment you initialized using `KitePrintSDK.getEnvironment()`.
	
        ```java
        KitePrintSDK.initialize("REPLACE_WITH_YOUR_API_KEY", KitePrintSDK.Environment.TEST);

        String paypalClientId = KitePrintSDK.getEnvironment().getPayPalClientId();
        String paypalReceiverEmail = KitePrintSDK.getEnvironment().getPayPalReceiverEmail();
        ```

    - Alternatively capture the users card details with your own UI and use `PayPalCard` to process the payment
    
        ```java
        PayPalCard card = new PayPalCard();
        card.setNumber("4121212121212127");
        card.setExpireMonth(12);
        card.setExpireYear(2012);
        card.setCvv2("123");
        
        card.chargeCard(PayPalCard.Environment.SANDBOX, printOrder.getCost(), PayPalCard.Currency.GBP, "A print order!", new PayPalCardChargeListener() {
            @Override
            public void onChargeSuccess(PayPalCard card, String proofOfPayment) {
                // set the PrintOrder proofOfPayment to the one provided and submit the order
            }

            @Override
            public void onError(PayPalCard card, Exception ex) {
                // handle gracefully
            }
        });
        ```
2. Attach the proof of payment to the `PrintOrder` to be verified server side

    ```java
    PrintOrder order = ...;
    order.setProofOfPayment(proofOfPayment);
    ```
3. Submit the `PrintOrder` to our server for printing and posting. 

     ```java
    printOrder.submitForPrinting(/*Context: */ this, new PrintOrderSubmissionListener() {
            @Override
            public void onProgress(PrintOrder printOrder, int totalAssetsUploaded, int totalAssetsToUpload, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite, long totalBytesWritten, long totalBytesExpectedToWrite) {
                // Show upload progress spinner, etc.
            }

            @Override
            public void onSubmissionComplete(PrintOrder printOrder, String orderIdReceipt) {
                // Print order was successfully submitted to the system, display success to the user
            }

            @Override
            public void onError(PrintOrder printOrder, Exception error) {
                // Handle error gracefully
            }
        });
    ```

Next Steps
----------

- [Register your payment details](https://developer.psilov.eu/accounts/billing/) with us so that we can pay you when your users place orders