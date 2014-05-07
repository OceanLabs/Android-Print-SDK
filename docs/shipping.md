Setting a Shipping Address
==============

If you don't want to use the checkout experience included with the Kite Android Print SDK (i.e. [Managed Checkout](../README.md#managed-checkout)) then you need to explicitly set the shipping address for a print order. Typically you will create your own UI to capture shipping details from your users. 

The Print SDK also includes worldwide address search/lookup functionality that you can use to improve the user experience of your app.

_If you haven't already, see the [README](../README.md) for an initial overview and instructions for adding the SDK to your project._

Prerequisites
--------
1. [Create a print order](create_print_order.md) representing the product(s) you wish to have printed and posted

Overview
--------
1. Create an `Address` containing details of the address that the print order will be shipped to. You have two options:
    - Create the `Address` manually
    - Search/lookup the `Address` using the SDK's worldwide address search and lookup functionality
2. Set the `PrintOrder` shipping address to the one you created in Step 1

Sample Code
-----------
1. You have two options when creating an `Address`
    - Create the address manually
    
        ```java
        Address a = new Address();
        a.setRecipientName("Deon Botha");
        a.setLine1("Eastcastle House");
        a.setLine2("27-28 Eastcastle Street");
        a.setCity("London");
        a.setStateOrCounty("London");
        a.setZipOrPostalCode("W1W 8DH");
        a.setCountry(Country.getInstance("GBR"));
        ```

    - Search for the address
    
        ```java
        Address.search("1 Infinite Loop", Country.getInstance("USA"), new AddressSearchRequestListener() {
            @Override
            public void onMultipleChoices(AddressSearchRequest req, List<Address> options) {
                // present choice of Address' to the user
            }

            @Override
            public void onUniqueAddress(AddressSearchRequest req, Address address) {
                // Search resulted in one unique address
            }

            @Override
            public void onError(AddressSearchRequest req, Exception error) {
                // Oops something went wrong
            }
        });
        ```
2. Set the `PrintOrder` shipping address to the one you created in Step 1

    ```java
    PrintOrder order = ...;
    order.setShippingAddress(addr);
    ```

Next Steps
----------

- [Take payment from the user](payment.md) for the order and submit it to our servers for printing and posting