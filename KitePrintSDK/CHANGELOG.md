### 5.8.9 (2018-07-03)
- Added: Google Pay! The additional payment option will not show up by default as a process needs to be followed in order to activate it for production needs. Details [here](https://github.com/OceanLabs/Android-Print-SDK/blob/master/docs/google_pay.md)
- Added: You can now add an authentication header to any images you provide directly to the SDK. See the Sample App for an example of this
- Updated: The payment page UI has been updated to make space for the Google Pay option, make sure your layouts are compatible with the changes if you've created a custom view
- Fixed: An issue with photobook parameters that caused problems when ordering
- Fixed: The collage poster images no longer all refresh when a single image is moved around

### 5.8.8 (2018-06-18)
- Added: Counter for multi-image selection (squares/collages etc) to help the user keep track of how many images have been selected so far
- Updated: minSDK is now 16, due to PayPal requirements
- Updated: Facebook selection is now an integral part of the SDK offering
- Updated: Camera folder is now localised
- Removed: The QR code image picker journey from the sample app, the integration still exists for special integrations that use it, but the journey is incomplete and unnecessary for mobile
- Fixed: Images now filter by png/jpeg/jpg, previously other file types would show up (such as gifs) in the image pickers and cause a crash when used (Picasso does not support)
- Fixed: Default image pickers are Device/Instagram/Facebook, if credentials are not provided for any of them, the option does not appear an no longer needs to be set manually
- Fixed: Fragment crash that sometimes still occurs when the app is put into the background during checkout
- Fixed: Payment screen reloads properly again on orientation change
- Fixed: On certain phones, sometimes the product editor would become unresponsive when adding the product to the basket, now it should process fine
- Fixed: OOM crash that occurs on the review and edit screen when using a low memory device

### 5.8.7 (2018-06-10)
- Added: Hungarian and Polish!
- Added: Resources are now prefixed with kitesdk_ to avoid translation conflicts in the app
- Added: Backdated the CHANGELOG.md to the start of 2017, added dates
- Fixed: Localisation inconsistencies with the Order History where the product name for a previously stored order wouldn't update, and the currency used to pay would. These have now been corrected 
- Fixed: SDK version returned in request so the dashboard shows the correct number (instead of v1.0)
- Fixed: Transparency on some of the new foreground images which previous rendered as white
- Fixed: Crash that was caused when a collage poster, photobook or calendar had no first image
- Fixed: Crash in the SampleApp with the Order History button when no API key has been provided
- Fixed: Crash that occurs when images are selected on older devices with lower memory
- Fixed: Invalid es_LG locales now default to es_ES

### 5.8.6 (2018-05-18)
- Added: GPDR Update - Any tracking that could allow Kite to identify a user either directly or indirectly has now been removed from tracking entirely, to ensure all integrations are compliant with GPDR
- Added: Kite analytics reporting has been disabled by default, if you would like to allow Kite to track analytics to help us further improve our SDK experience, you can re-enable it with kiteSDK.setKiteAnalyticsEnabled(true) in the configureSDK
- Added: Privacy policy and terms of use links added to the checkout process
- Added: The basket now shows the product previews
- Added: Posters now preview in slightly higher quality making them easier to see
- Fixed: When selecting multiple images, the cancel button now works when it didn't before
- Fixed: Crash that occurs when using the 100% promocode
- Fixed: Crash that sometimes occurs when moving images around on the photobook
- Fixed: Issue with images not always loading after adding a product, updating and modifying it 
- Fixed: When a user puts the app into the background during payment, rather than crashing, not taking payment or making an order, the payment now continues to process properly.
- Fixed: Basket is no longer cleared when card details are incorrect

### 5.8.5 (2018-04-25)
- Added: Dutch!

### 5.8.4 (2018-03-14)
- Added: Check for address required fields and alert message when trying to proceed to checkout without filling in an address

### 5.8.3 (2018-02-16)
- Fixed: Previous versions of the sdk that stored unencrypted data should now be compatible with current versions of the sdk and encrypt the existing data

### 5.8.2 (2018-02-08)
- Fixed: nullPointerException on Country.getInstance now defaults to USA when country is returned null

### 5.8.1 (2018-02-01)
- Fixed: The phonecase edit screen 'next' button is now displayed properly in landscape
- Fixed: Unsupported currency error for ISO 3166 country:en

## 5.8.0 (2017-12-27)
- Added: Photobook page numbers are now displayed on the product title
- Fixed: Landscape and portrait photobooks no longer appear square
- Fixed: Order of pages on photobook should now be correct (with first and last pages correctly showing as blank)
- Fixed: Photobooks are correctly showing full page images
- Fixed: Correct dimensions for posters are now shown in preview and editing modes

### 5.7.6 (2017-09-13)
- Dropped Stripe version back to 4.1.3, until supported updates are made

### 5.7.5 (2017-09-11)
- Upgraded to Stripe version 5.0.0
- Fixed: HTML tags no longer show up in the editor

### 5.7.4 (2017-07-04)
- Added: Encryption to details stored on the device
- Updated Kite API endpoint to v4.0

### 5.7.3 (2017-05-10)
- Added: Analytics docs
- Added: New analytics events
- Fixed: Multi-line border text issue

### 5.7.2 (2017-04-21)
- Added: gradle.properties to the Image Picker
- Added: DevicePhotoPicker

### 5.7.1 (2017-04-04)
- Renamed and updated Instagram strings

## 5.7.0 (2017-04-03)
- Added: InstagramPhotoPicker
- The sample app strings are now non-translatable

### 5.6.28 (2017-03-16)
- Fixed: Issue with clearing default basket
- Fixed: Various translations

### 5.6.27 (2017-03-16)
- Changed Order timeout handling
- Fixed: Image load issue with rotated images
- Fixed: Various translations

### 5.6.26 (2017-03-06)
- Added: Default border test font
- Added: Previous image state is now restored when re-editing images
- Added: Open source acknowledgements section to the README.md

### 5.6.25 (2017-02-22)
- Added: If there is only one image source, the image picker now launches it directly
- Added: Basket displays the first edited photo if available
- Fixed: Removed all price overlays from displaying when the placeholder image is showing
- Fixed: Spanish wording updates

### 5.6.24 (2017-02-20)
- Fixed: Issue with basket not always being fully cleared
- Fixed: Border text being kept across orders no longer does

### 5.6.23 (2017-02-17)
- Added: Missing translations for order success/failure

### 5.6.22 (2017-02-16)
- Added: Lots of theming updates
- Changed the placeholder/receipt images to remove text
- Amended Stripe credit card processing

### 5.6.21 (2017-02-13)
- Added: Improved Stripe error handling

### 5.6.20 (2017-02-13)
- Added: Missing French strings


### 5.6.19 (2017-02-03)
- Updated French/Spanish translations
- Amended credit card validation

### 5.6.16 (2017-01-26)
- Added: Spanish strings for the FacebookPhotoPicker
- Added: View to placeholder layout
- Fixed: Removed untranslatable strings
- Updated a few other translations

### 5.6.15 (2017-01-23)
- Added: Border text
- Fixed: Issue with blank images
- Updated QR-code library to use ZXing.
- Updated the product launching/filtering information in the README.md
