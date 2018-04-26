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
