/*****************************************************
 *
 * AddressBook.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import ly.kite.KiteSDK;


///// Class Declaration /////

/*****************************************************
 *
 * This class manages the address book. Addresses in the book
 * are stored in shared preferences using the customer session
 * scope.
 *
 *****************************************************/
public class AddressBook
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                       = "AddressBook";

  static private final String  PARAMETER_NAME_ADDRESS_IDS    = "ab_address_ids";
  static private final String  PARAMETER_NAME_PREFIX_ADDRESS = "ab_address_";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Saves an address to the address book.
   *
   * @return The address id.
   *
   *****************************************************/
  static public String save( Context context, Address address )
    {
    KiteSDK kiteSDK = KiteSDK.getInstance( context );


    // If the address doesn't already have an address id - create a new one:
    // Get a new UUID, then base64 it to remove any unwanted characters.

    String addressId = address.getId();

    if ( addressId == null )
      {
      String uuid = UUID.randomUUID().toString();

      addressId = Base64.encodeToString( uuid.getBytes(), Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE );
      }


    // Get the current set of address ids
    Set<String> addressIds = kiteSDK.getStringSetSDKParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_ADDRESS_IDS );


    // Save the address to shared preferences

    String parameterName = PARAMETER_NAME_PREFIX_ADDRESS + addressId;

    kiteSDK.setSDKParameter( KiteSDK.Scope.CUSTOMER_SESSION, parameterName, address );


    // If this was a new address (rather than an update to an existing one) - add the new
    // address id to the set, and save it.

    if ( ! addressIds.contains( addressId ) )
      {
      addressIds.add( addressId );

      kiteSDK.setSDKParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_ADDRESS_IDS, addressIds );
      }


    return ( addressId );
    }


  /*****************************************************
   *
   * Returns all saved addresses.
   *
   *****************************************************/
  static public List<Address> selectAll( Context context )
    {
    KiteSDK kiteSDK = KiteSDK.getInstance( context );

    // Get the current address ids
    Set<String> addressIds = kiteSDK.getStringSetSDKParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_ADDRESS_IDS );


    ArrayList<Address> addressList = new ArrayList<>( addressIds.size() );


    // Get each address and add it to the list

    for ( String addressId : addressIds )
      {
      String parameterName = PARAMETER_NAME_PREFIX_ADDRESS + addressId;

      Address address = kiteSDK.getAddressSDKParameter( KiteSDK.Scope.CUSTOMER_SESSION, parameterName );

      if ( address != null )
        {
        address.setId( addressId );

        addressList.add( address );
        }
      else
        {
        Log.e( LOG_TAG, "No address found for id: " + addressId );
        }
      }


    return ( addressList );
    }


  /*****************************************************
   *
   * Deletes an address from the address book.
   *
   *****************************************************/
  static public void delete( Context context, String addressId )
    {
    KiteSDK kiteSDK = KiteSDK.getInstance( context );


    // Get the current address ids
    Set<String> addressIds = kiteSDK.getStringSetSDKParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_ADDRESS_IDS );


    // Delete the address from shared preferences

    String parameterName = PARAMETER_NAME_PREFIX_ADDRESS + addressId;

    kiteSDK.clearAddressSDKParameter( KiteSDK.Scope.CUSTOMER_SESSION, parameterName );


    // Remove the address id from the set and save it

    addressIds.remove( addressId );

    kiteSDK.setSDKParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_ADDRESS_IDS, addressIds );
    }


  /*****************************************************
   *
   * Deletes an address from the address book.
   *
   *****************************************************/
  static public void delete( Context context, Address address )
    {
    String addressId = address.getId();

    if ( addressId != null ) delete( context, addressId );
    }


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

