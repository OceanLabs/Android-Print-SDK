Installing the library - Eclipse
==============

1. To use the Kite Android Print SDK inside of Eclipse download the latest library release [from Github](https://github.com/OceanLabs/Android-Print-SDK/releases) and extract it.
2. Import the Kite library into your project's workspace:

	To have access to the Kite library, you need to import it into your workspace by going to *File -> Import -> Android -> Existing Android Code Into Workspace*. Browse for the folder where you extracted the library and then choose Finish.
3. Associate the library as a dependency of your project:

	Now that the library is in your workspace, you need to associate it as a dependency of your project. You can do this by going to your project properties, going to the 'Android' tab, and then adding the Kite library in the library section.
	
4. Add permissions to your AndroidManifest.xml:

	In order for the library to work, you need to ensure that you're requesting the following permissions in your AndroidManifest.xml:
	
	```xml 
	<!-- hardware features (Used by Card.IO for credit card scanning in the Kite Managed Checkout) -->
    <uses-feature android:name="android.hardware.camera" android:required="false" />
    <uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />
    <uses-feature android:name="android.hardware.camera.flash" android:required="false" />

    <!-- for card.io card scanning -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <!-- for most things, including kite, card.io & paypal -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    ```

5. Add Kite & PayPal's activites/services if you're using Kite's [Managed Checkout](../README.md#managed-checkout):

    ```xml
    <!-- Kite Print SDK Activities: -->
    <activity
        android:name="ly.kite.checkout.PaymentActivity"
        android:label="@string/title_activity_payment"
        android:screenOrientation="portrait" >
    </activity>
    <activity
        android:name="ly.kite.checkout.CheckoutActivity"
        android:label="@string/title_activity_checkout"
        android:screenOrientation="portrait" >
    </activity>
    <activity
        android:name="ly.kite.checkout.OrderReceiptActivity"
        android:label="@string/title_activity_order_receipt"
        android:screenOrientation="portrait" >
    </activity>
    <activity
        android:name="ly.kite.address.AddressBookActivity"
        android:label="@string/title_activity_address_book"
        android:screenOrientation="portrait" >
    </activity>

    <activity
        android:name="ly.kite.address.AddressEditActivity"
        android:label="@string/title_activity_address_edit"
        android:screenOrientation="portrait"
        android:windowSoftInputMode="adjustResize" >
    </activity>
    <activity
        android:name="ly.kite.address.AddressSearchActivity"
        android:label="@string/title_activity_address_search"
        android:screenOrientation="portrait" >
    </activity>

    <!-- PayPal activites & services: -->
    <service
        android:name="com.paypal.android.sdk.payments.PayPalService"
        android:exported="false" />

    <activity android:name="com.paypal.android.sdk.payments.PaymentActivity" />
    <activity android:name="com.paypal.android.sdk.payments.LoginActivity" />
    <activity android:name="com.paypal.android.sdk.payments.PaymentMethodActivity" />
    <activity android:name="com.paypal.android.sdk.payments.PaymentConfirmActivity" />
    <activity
        android:name="io.card.payment.CardIOActivity"
        android:configChanges="keyboardHidden|orientation" />
    <activity android:name="io.card.payment.DataEntryActivity" />
    ```





