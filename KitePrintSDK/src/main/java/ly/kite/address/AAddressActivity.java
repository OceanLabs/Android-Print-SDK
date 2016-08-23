/*****************************************************
 *
 * AddressActivity.java
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

package ly.kite.address;


///// Import(s) /////


///// Class Declaration /////

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import ly.kite.journey.AKiteActivity;

/*****************************************************
 *
 * This class is the parent class of address activities.
 *
 *****************************************************/
abstract public class AAddressActivity extends AKiteActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG           = "AddressActivity";

  static public  final String  KEY_ADDRESS       = "ly.kite.address";
  static public  final String  KEY_EMAIL_ADDRESS = "ly.kite.emailaddress";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an address bundled as an extra within an intent.
   *
   *****************************************************/
  static public Address getAddress( Intent data )
    {
    if ( data == null ) return ( null );

    return ( data.getParcelableExtra( KEY_ADDRESS ) );
    }


  /*****************************************************
   *
   * Returns an email address bundled as an extra within an intent.
   *
   *****************************************************/
  static public String getEmailAddress( Intent data )
    {
    if ( data == null ) return ( null );

    return ( data.getStringExtra( KEY_EMAIL_ADDRESS ) );
    }


  /*****************************************************
   *
   * Adds an address to an intent.
   *
   *****************************************************/
  static public void addAddressIfNotNull( Address address, Intent intent )
    {
    if ( address != null ) intent.putExtra( KEY_ADDRESS, (Parcelable)address );
    }


  /*****************************************************
   *
   * Adds an email address to an intent.
   *
   *****************************************************/
  static public void addEmailAddressIfNotNull( String emailAddress, Intent intent )
    {
    if ( emailAddress != null ) intent.putExtra( KEY_EMAIL_ADDRESS, emailAddress );
    }


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns an address result.
   *
   *****************************************************/
  public void returnResult( Address address, String emailAddress )
    {
    Intent data = new Intent();

    addAddressIfNotNull( address, data );
    addEmailAddressIfNotNull( emailAddress, data );

    setResult( Activity.RESULT_OK, data );
    }


  /*****************************************************
   *
   * Returns an address result.
   *
   *****************************************************/
  public void returnResult( Address address )
    {
    returnResult( address, null );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

