/*****************************************************
 *
 * APermissionsRequestingActivity.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.app;


///// Import(s) /////

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Random;

import ly.kite.journey.AKiteActivity;
import ly.kite.util.StringUtils;


///// Class Declaration /////

/*****************************************************
 *
 * This is a parent for activities that request permissions.
 *
 *****************************************************/
abstract public class APermissionsRequestingActivity extends Activity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                             = "APermissionsRequestingActivity";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private   int                   mPermissionsRequestCode;
  private   String[]              mPermissions;
  private   Runnable              mPermissionsRunnable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called with any granted permissions.
   *
   *****************************************************/
  public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults )
    {
    if ( requestCode == mPermissionsRequestCode )
      {
      if ( permissions  != null &&
              grantResults != null &&
              permissions.length == mPermissions.length &&
              permissions.length == grantResults.length )
        {
        // Make sure we have all the requested permissions

        int permissionIndex = 0;

        for ( String permission : permissions )
          {
          if ( ! StringUtils.areBothNullOrEqual( permission, mPermissions[ permissionIndex] ) ||
                  grantResults[ permissionIndex ] != PackageManager.PERMISSION_GRANTED )
            {
            return;
            }

          permissionIndex ++;
          }


        // We have all the permissions, so call the runnable
        if ( mPermissionsRunnable != null ) mPermissionsRunnable.run();

        mPermissionsRequestCode = -1;
        mPermissions            = null;
        mPermissionsRunnable    = null;
        }
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Requests permission(s), and calls a runnable when
   * they are granted.
   *
   *****************************************************/
  public void callRunnableWithPermissions( String[] permissions, Runnable runnable )
    {
    // If we are running on a pre-Marshmallow device, we already have the permissions
    // we need (because they were all granted at install time).

    if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.M )
      {
      runnable.run();

      return;
      }


    // We are running Marshmallow onwards


    // Create a list of the permissions we need

    ArrayList<String> requiredPermissionList = new ArrayList<>( permissions.length );

    for ( String permission: permissions )
      {
      if ( ContextCompat.checkSelfPermission( this, permission ) != PackageManager.PERMISSION_GRANTED )
        {
        requiredPermissionList.add( permission );
        }
      }


    // See if we already have the permissions we need

    int requiredPermissionCount = requiredPermissionList.size();

    if ( requiredPermissionCount < 1 )
      {
      runnable.run();

      return;
      }


    // Request the permissions we need

    String[] requiredPermissions = new String[ requiredPermissionCount ];

    requiredPermissionList.toArray( requiredPermissions );

    mPermissionsRequestCode = new Random().nextInt( 16384 );  // Must not be negative!
    mPermissions            = requiredPermissions;
    mPermissionsRunnable    = runnable;

    ActivityCompat.requestPermissions( this, mPermissions, mPermissionsRequestCode );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

