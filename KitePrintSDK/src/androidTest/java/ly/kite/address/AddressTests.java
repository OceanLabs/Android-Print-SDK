/*****************************************************
 *
 * BleedTests.java
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

import android.os.Parcel;

import junit.framework.Assert;
import junit.framework.TestCase;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the address class.
 *
 *****************************************************/
public class AddressTests extends TestCase
{
    ////////// Static Constant(s) //////////

    @SuppressWarnings( "unused" )
    private static final String  LOG_TAG = "AddressTests";

    ////////// Static Variable(s) //////////


    ////////// Member Variable(s) //////////


    ////////// Static Initialiser(s) //////////


    ////////// Static Method(s) //////////

    public void testAreBothNullOrEqual1(){

        Address address1 = new Address();
        Address address2 = new Address();

        Assert.assertTrue(Address.areBothNullOrEqual(address1, address2));
    }

    public void testAreBothNullOrEqual2(){

        Address address1 = new Address();
        Address address2 = new Address(
            "Kite Tech Ltd.",
            "6-8 Bonhill Street",
            null,
            "London",
            null,
            "EC2A 4BX",
            Country.getInstance( "GBR" ) );

        Assert.assertFalse(Address.areBothNullOrEqual(address1, address2));
    }

    public void testAreBothNullOrEqual3(){

        Address address1 = new Address(
            "Kite Tech Ltd.",
            "6-8 Bonhill Street",
            null,
            "London",
            null,
            "EC2A 4BX",
            Country.getInstance( "GBR" ) );
        Address address2 = new Address(
            "Kite Tech Ltd.",
            "6-8 Bonhill Street",
            null,
            "London",
            null,
            "EC2A 4BX",
            Country.getInstance( "GBR" ) );

        Assert.assertTrue(Address.areBothNullOrEqual(address1, address2));
    }

    public void testAreBothNullOrEqual4(){

        Address address1 = new Address(
            "Kite Tech Ltd.",
            "White Collar Factory",
            "1 Old St Yard",
            "London",
            null,
            "EC1Y 8AF",
            Country.getInstance( "GBR" ) );
        Address address2 = new Address(
            "Kite Tech Ltd.",
            "6-8 Bonhill Street",
            null,
            "London",
            null,
            "EC2A 4BX",
            Country.getInstance( "GBR" ) );

        Assert.assertFalse(Address.areBothNullOrEqual(address1, address2));
    }


    ////////// Constructor(s) //////////


    ////////// Method(s) //////////

    /*****************************************************
     *
     * Constructor tests.
     *
     *****************************************************/

    public void testConstructor1(){
        Address address = new Address();

        Assert.assertEquals(null, address.getRecipientName());
        Assert.assertEquals(null, address.getLine1());
        Assert.assertEquals(null, address.getLine2());
        Assert.assertEquals(null, address.getCity());
        Assert.assertEquals(null, address.getStateOrCounty());
        Assert.assertEquals(null, address.getZipOrPostalCode());
        Assert.assertEquals(null, address.getCountry());
    }

    public void testConstructor2(){
        Address address = new Address(
            "Kite Tech Ltd.",
            "6-8 Bonhill Street",
            null,
            "London",
            null,
            "EC2A 4BX",
            Country.getInstance( "GBR" ) );

        Assert.assertEquals("Kite Tech Ltd.", address.getRecipientName());
        Assert.assertEquals("6-8 Bonhill Street", address.getLine1());
        Assert.assertEquals(null, address.getLine2());
        Assert.assertEquals("London", address.getCity());
        Assert.assertEquals(null, address.getStateOrCounty());
        Assert.assertEquals("EC2A 4BX", address.getZipOrPostalCode());
        Assert.assertEquals("GBR", address.getCountry().iso3Code());
    }

    /*****************************************************
     *
     * Parcel tests.
     *
     *****************************************************/

    public void testParcel1()
    {
        Address oldAddress = new Address(
            "Kite Tech Ltd.",
            "6-8 Bonhill Street",
            null,
            "London",
            null,
            "EC2A 4BX",
            Country.getInstance( "GBR" ) );

        Parcel parcel = Parcel.obtain();
        oldAddress.writeToParcel( parcel, 0 );
        parcel.setDataPosition( 0 );
        Address newAddress = Address.CREATOR.createFromParcel( parcel );

        Assert.assertEquals("Kite Tech Ltd.", newAddress.getRecipientName());
        Assert.assertEquals("6-8 Bonhill Street", newAddress.getLine1());
        Assert.assertEquals(null, newAddress.getLine2());
        Assert.assertEquals("London", newAddress.getCity());
        Assert.assertEquals(null, newAddress.getStateOrCounty());
        Assert.assertEquals("EC2A 4BX", newAddress.getZipOrPostalCode());
        Assert.assertEquals("GBR", newAddress.getCountry().iso3Code());

        parcel.recycle();
    }

    /*****************************************************
     *
     * Display tests.
     *
     *****************************************************/

    public void testDisplay1(){

        Address address = new Address(
            "Kite Tech Ltd.",
            "6-8 Bonhill Street",
            null,
            "London",
            null,
            "EC2A 4BX",
            Country.getInstance( "GBR" ) );

        Assert.assertEquals("Kite Tech Ltd., 6-8 Bonhill Street, London, EC2A 4BX, United Kingdom", address.toString());
    }

    ////////// Inner Class(es) //////////

    /*****************************************************
     *
     * ...
     *
     *****************************************************/

}

