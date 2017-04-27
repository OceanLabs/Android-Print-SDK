/*****************************************************
 *
 * StringUtilsTests.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2017 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.util;


///// Import(s) /////

import junit.framework.Assert;

import ly.kite.KiteTestCase;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the string utils class.
 *
 *****************************************************/
public class StringUtilsTests extends KiteTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "StringUtilsTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Is null or blank tests
   *
   *****************************************************/

  public void testIsNullOrBlank()
    {
    Assert.assertTrue( StringUtils.isNullOrBlank( null ) );
    Assert.assertTrue( StringUtils.isNullOrBlank( "" ) );
    Assert.assertTrue( StringUtils.isNullOrBlank( " " ) );
    Assert.assertTrue( StringUtils.isNullOrBlank( "      " ) );

    Assert.assertFalse( StringUtils.isNullOrBlank( "a" ) );
    Assert.assertFalse( StringUtils.isNullOrBlank( "  blah   " ) );
    }


  /*****************************************************
   *
   * Is neither null nor blank tests
   *
   *****************************************************/

  public void testIsNeitherNullNorBlank()
    {
    Assert.assertFalse( StringUtils.isNeitherNullNorBlank( null ) );
    Assert.assertFalse( StringUtils.isNeitherNullNorBlank( "" ) );
    Assert.assertFalse( StringUtils.isNeitherNullNorBlank( " " ) );
    Assert.assertFalse( StringUtils.isNeitherNullNorBlank( "      " ) );

    Assert.assertTrue( StringUtils.isNeitherNullNorBlank( "a" ) );
    Assert.assertTrue( StringUtils.isNeitherNullNorBlank( "  blah   " ) );
    }


  /*****************************************************
   *
   * Are both null or equal tests
   *
   *****************************************************/

  public void testAreBothNullOrEqual()
    {
    Assert.assertTrue( StringUtils.areBothNullOrEqual( null, null ) );
    Assert.assertFalse( StringUtils.areBothNullOrEqual( "", null ) );
    Assert.assertFalse( StringUtils.areBothNullOrEqual( " ", null ) );
    Assert.assertFalse( StringUtils.areBothNullOrEqual( null, "" ) );
    Assert.assertFalse( StringUtils.areBothNullOrEqual( null, " " ) );

    Assert.assertTrue( StringUtils.areBothNullOrEqual( "", "" ) );
    Assert.assertFalse( StringUtils.areBothNullOrEqual( " ", "" ) );
    Assert.assertFalse( StringUtils.areBothNullOrEqual( "", "  blah " ) );
    Assert.assertTrue( StringUtils.areBothNullOrEqual( " ", " " ) );
    Assert.assertTrue( StringUtils.areBothNullOrEqual( " one ", " one " ) );
    }


  /*****************************************************
   *
   * Get digits tests
   *
   *****************************************************/

  public void testGetDigitString()
    {
    Assert.assertEquals( "", StringUtils.getDigitString( null ) );
    Assert.assertEquals( "", StringUtils.getDigitString( "" ) );
    Assert.assertEquals( "", StringUtils.getDigitString( "    " ) );
    Assert.assertEquals( "", StringUtils.getDigitString( "sfjsdjlfjlks[]@&$" ) );

    Assert.assertEquals( "6", StringUtils.getDigitString( "sdkljfds6sjhfsd" ) );
    Assert.assertEquals( "12345678", StringUtils.getDigitString( " 1 2 3 4 5 6 7 8" ) );
    Assert.assertEquals( "0123456789", StringUtils.getDigitString( "0f1j2g3n4l5b6h7v8j9" ) );
    }


  /*****************************************************
   *
   * Is digit string tests
   *
   *****************************************************/

  public void testIsDigitString()
    {
    Assert.assertFalse( StringUtils.isDigitString( null ) );
    Assert.assertFalse( StringUtils.isDigitString( "" ) );
    Assert.assertFalse( StringUtils.isDigitString( " " ) );
    Assert.assertFalse( StringUtils.isDigitString( "    " ) );

    Assert.assertFalse( StringUtils.isDigitString( "a" ) );
    Assert.assertFalse( StringUtils.isDigitString( "asaflsjflsjl" ) );

    Assert.assertTrue( StringUtils.isDigitString( "1" ) );
    Assert.assertFalse( StringUtils.isDigitString( " 1 " ) );
    Assert.assertTrue( StringUtils.isDigitString( "123" ) );
    Assert.assertFalse( StringUtils.isDigitString( "1228hhjhkds832hjk3" ) );
    }


  ////////// Inner Class(es) //////////

  }

