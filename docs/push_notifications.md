# Configuring Push Notifications

Push notifications are a powerful marketing tool that if used effectively can increase both user engagement and customer life time value by driving more sales within your application. Kite supports sending push notifications to your customers with a bit of configuration.


## Overview

In order to set up push messaging, you will need to perform the following steps:

1. Set up Google Cloud Messaging (GCM)
2. Add Kite SDK and GCM modules to your app
3. Create a push notification action


## Set up Google Cloud Messaging (GCM)

Kite push notifications on Android make use of the Google Cloud Messaging framework. It is necessary, therefore, to set up Google Cloud Messaging before adding push notifications to your app.

Log in to Google's [API Console](https://console.developers.google.com/). If you do not already have a Google account, create one now.

If you have not already created a project, click on **Create Project** to create a new one.

When you have created a project, the console's dashboard will be displayed. Make a note of the **project number** near the top of the page - this 12-digit number will be your **Google Sender Id**, which you will need to include in your app as a string resource later.

From the Google Developer's Console dashboard, choose **Enable and manage APIs** from the **API** section.

In the list of APIs, look for **Google Cloud Messaging**, and enable it by clicking the switch at the top of the page. Click **Credentials** section in the sidebar. Select **API key** from the **Create credentials** dropdown button and choose to create a **Server key**.

Once you have obtained an API key, you should log in to the [Notification](https://www.kite.ly/settings/notifications) section of the Kite Dashboard and enter the key into the Android GCM API Key field.

![Entering GCM API Key](push_notifications_files/kite_dashboard_gcm_key.png)


## Add Kite SDK and GCM modules to your app

We publish builds of our SDK and the optional GCM support module to the Maven central repository as .aar files. These files contains all of the classes, resources, and configurations that you'll need to use the library with push notifications. To install the library inside Android Studio, you can simply declare it as dependecy in your build.gradle file:

```java 
dependencies {
    compile 'ly.kite:kite-print-sdk:4+'
    compile 'ly.kite:kite-print-sdk-gcm:4+'
}
```


Ensure that your app `AndroidManifest.xml` contains the following permissions:

```xml
    <permission android:name="<your-app-package>.permission.C2D_MESSAGE" android:protectionLevel="signature" />

    <uses-permission android:name="<your-app-package>.permission.C2D_MESSAGE"/>

```

Be sure to replace `<your-app-package>` with your application package. Also ensure that the `AndroidManifest.xml` contains the following declarations. Notice that the service declaration is for a listener service. The name should be adjusted to match the package and class name that you use. A template class `GCMListenerService` is provided for reference at the end of this document and in the **SampleApp** module included with the Kite SDK.

```xml
    <application
        ...>

        ...

        <receiver
            android:name="com.google.android.gms.gcm.GcmReceiver"
            android:exported="true"
            android:permission="com.google.android.c2dm.permission.SEND" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <category android:name="<your-app-package>" />
            </intent-filter>
        </receiver>

        <service
            android:name=".GCMListenerService"
            android:exported="false" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
            </intent-filter>
        </service>

        ...

    </application>
```

Again be sure to replace `<your-app-package>` with your application package in the above.

Add your **Google Sender Id** as a string resource. Remember from earlier that this is the 12-digit project number from the Google API Console.

```xml
    <resources>

        ...

        <string name="gcm_sender_id">your-12-digit-project-number</string>

        ...

    </resources>
```

### Call the GCM registration service from your app

The `GCMRegistrationService` class in the *Kite-GCM* module takes care of obtaining a Google Cloud Messaging token, and registering it with the Kite servers.

Where appropriate in your app (we recommend doing it right at application startup in your main activity), start the registration service as follows:

```java
GCMRegistrationService.start( this );
```

You do not need to keep track of whether you have already called this, or whether registration succeeded, as the service automatically takes care of this for you.


## Create a push notification action

Once a push notification has arrived, you must decide what to do with it. Often an app will create an Android notification to display the message to the user. Alternatively, you may choose to parse the message and perform an app action. However you decide to handle the notification, you must implement a listener service. As mentioned previously, a template is provided in the SampleApp module, but your implementation may look something like this:

```java
...

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;


public class GCMListenerService extends GcmListenerService
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG         = "GCMListenerService";

  private static final int     REQUEST_CODE    = 10;
  private static final int     NOTIFICATION_ID = 11;


  ...

  @Override
  public void onMessageReceived( String from, Bundle data )
    {
    String message = data.getString( "message" );

    Log.d( LOG_TAG, "From: " + from );
    Log.d( LOG_TAG, "Message: " + message );


    // We don't do anything with topics at the moment, so every message is assumed
    // to be a normal downstream message.


    // Check if the user is OK to receive notification messages

    ...


    // Create a notification


    // We want to go into the home activity when the notification is clicked

    Intent intent = new Intent( this, MainActivity.class );

    PendingIntent pendingIntent = PendingIntent.getActivity( this, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT );


    Notification.Builder notificationBuilder = new Notification.Builder( this );

    notificationBuilder
            .setSmallIcon( R.drawable.ic_notification )
            .setContentTitle( getString( R.string.app_name ) )
            .setContentText( message )
            .setContentIntent( pendingIntent )
            .setAutoCancel( true );


    NotificationManager notificationManager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );

    notificationManager.notify( NOTIFICATION_ID, notificationBuilder.getNotification() );
    }

  ...
  }
```

