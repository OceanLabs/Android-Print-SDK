/*****************************************************
 *
 * CatalogueTests.java
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

package ly.kite.catalogue;


///// Import(s) /////

import android.os.Parcel;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.json.JSONObject;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the single currency amount class.
 *
 *****************************************************/
public class CatalogueTests extends TestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "CatalogueTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Colour from string tests.
   *
   *****************************************************/

  public void testColourFromString()
    {
    Assert.assertEquals( Catalogue.NO_COLOUR, Catalogue.colourFromString( null ) );
    Assert.assertEquals( Catalogue.NO_COLOUR, Catalogue.colourFromString( "" ) );
    Assert.assertEquals( Catalogue.NO_COLOUR, Catalogue.colourFromString( "#" ) );
    Assert.assertEquals( Catalogue.NO_COLOUR, Catalogue.colourFromString( "#ZY" ) );
    Assert.assertEquals( Catalogue.NO_COLOUR, Catalogue.colourFromString( "#abc" ) );
    Assert.assertEquals( Catalogue.NO_COLOUR, Catalogue.colourFromString( "#aab06" ) );
    Assert.assertEquals( Catalogue.NO_COLOUR, Catalogue.colourFromString( "$112233" ) );
    Assert.assertEquals( Catalogue.NO_COLOUR, Catalogue.colourFromString( "0x112233" ) );
    Assert.assertEquals( Catalogue.NO_COLOUR, Catalogue.colourFromString( "#1122334455" ) );

    Assert.assertEquals( 0xff000000, Catalogue.colourFromString( "#000000" ) );
    Assert.assertEquals( 0xffaabbcc, Catalogue.colourFromString( "#aabbcc" ) );
    Assert.assertEquals( 0x00000000, Catalogue.colourFromString( "#00000000" ) );
    Assert.assertEquals( 0x7aaabbcc, Catalogue.colourFromString( "#7aaabbcc" ) );
    Assert.assertEquals( 0xa1112233, Catalogue.colourFromString( "#a1112233" ) );
    Assert.assertEquals( 0xff7f4a3c, Catalogue.colourFromString( "#ff7f4a3c" ) );
    Assert.assertEquals( 0xffffffff, Catalogue.colourFromString( "#ffffffff" ) );
    }


  /*****************************************************
   *
   * Theme colour tests.
   *
   *****************************************************/

  public void testThemeColours1()
    {
    Catalogue catalogue = new Catalogue();

    Assert.assertEquals( Catalogue.NO_COLOUR, catalogue.getPrimaryThemeColour() );
    Assert.assertEquals( Catalogue.NO_COLOUR, catalogue.getSecondaryThemeColour() );
    }

  public void testThemeColours2() throws Exception
    {
    Catalogue catalogue = new Catalogue();

    JSONObject userConfigData = new JSONObject();
    userConfigData.put( Catalogue.JSON_NAME_THEME_COLOUR_PRIMARY, "#112233" );

    catalogue.setUserConfigData( userConfigData );

    Assert.assertEquals( 0xff112233, catalogue.getPrimaryThemeColour() );
    Assert.assertEquals( Catalogue.NO_COLOUR, catalogue.getSecondaryThemeColour() );
    }

  public void testThemeColours3() throws Exception
    {
    Catalogue catalogue = new Catalogue();

    JSONObject userConfigData = new JSONObject();
    userConfigData.put( Catalogue.JSON_NAME_THEME_COLOUR_SECONDARY, "#a1112233" );

    catalogue.setUserConfigData( userConfigData );

    Assert.assertEquals( Catalogue.NO_COLOUR, catalogue.getPrimaryThemeColour() );
    Assert.assertEquals( 0xa1112233, catalogue.getSecondaryThemeColour() );
    }

  public void testThemeColours4() throws Exception
    {
    Catalogue catalogue = new Catalogue();

    JSONObject userConfigData = new JSONObject();
    userConfigData.put( Catalogue.JSON_NAME_THEME_COLOUR_PRIMARY,   "#4477fe" );
    userConfigData.put( Catalogue.JSON_NAME_THEME_COLOUR_SECONDARY, "#a1112233" );

    catalogue.setUserConfigData( userConfigData );

    Assert.assertEquals( 0xff4477fe, catalogue.getPrimaryThemeColour() );
    Assert.assertEquals( 0xa1112233, catalogue.getSecondaryThemeColour() );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

