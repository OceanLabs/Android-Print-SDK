### 5.8.6
- Added: GPDR Update - Any tracking that could allow Kite to identify a user either directly or indirectly has now been removed from tracking entirely, to ensure all integrations are compliant with GPDR.
- Added: Kite analytics reporting has been disabled by default, if you would like to allow Kite to track analytics to help us further improve our SDK experience, you can re-enable it with kiteSDK.setKiteAnalyticsEnabled(true) in the configureSDK.
- Added: Privacy policy and terms of use links added to the checkout process
- Added: The basket now shows the product previews
- Added: Posters now preview in slightly higher quality making them easier to see
- Fixed: When selecting multiple images, the cancel button now works when it didn't before
- Fixed: Crash that occurs when using the 100% promocode
- Fixed: Crash that sometimes occurs when moving images around on the photobook
- Fixed: Issue with images not always loading after adding a product, updating and modifying it 
- Fixed: When a user puts the app into the background during payment, rather than crashing, not taking payment or making an order, the payment now continues to process properly.
- Fixed: Basket is no longer cleared when card details are incorrect

### 5.8.5
- Added: Dutch!

### 5.8.4
- Added: Check for address required fields and alert message when trying to proceed to checkout without filling in an address

### 5.8.3
- Fixed: Previous versions of the sdk that stored unencrypted data should now be compatible with current versions of the sdk and encrypt the existing data

### 5.8.2
- Fixed: nullPointerException on Country.getInstance now defaults to USA when country is returned null

### 5.8.1
- Fixed: The phonecase edit screen 'next' button is now displayed properly in landscape
- Fixed: Unsupported currency error for ISO 3166 country:en

## 5.8.0
- Added: Photobook page numbers are now displayed on the product title
- Fixed: Landscape and portrait photobooks no longer appear square
- Fixed: Order of pages on photobook should now be correct (with first and last pages correctly showing as blank)
- Fixed: Photobooks are correctly showing full page images
- Fixed: Correct dimensions for posters are now shown in preview and editing modes
