/*****************************************************
 *
 * StringUtils.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
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


///// Class Declaration /////

import java.util.ArrayList;
import java.util.List;

/*****************************************************
 *
 * This class provides methods for use with Strings.
 *
 *****************************************************/
public class StringUtils
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "StringUtils";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns true if the string is null or blank.
   *
   *****************************************************/
  static public boolean isNullOrBlank( String string )
    {
    if ( string == null || string.trim().equals( "" ) ) return ( true );

    return ( false );
    }


  /*****************************************************
   *
   * Returns true if the two strings are either both null,
   * or equal.
   *
   *****************************************************/
  static public boolean isNeitherNullNorBlank( String string )
    {
    return ( ! isNullOrBlank( string  ) );
    }


  /*****************************************************
   *
   * Returns true if the two strings are either both null,
   * or equal.
   *
   *****************************************************/
  static public boolean areBothNullOrEqual( String string1, String string2 )
    {
    if ( string1 == null && string2 == null ) return ( true );
    if ( string1 == null || string2 == null ) return ( false );

    return ( string1.equals( string2 ) );
    }


  /*****************************************************
   *
   * Extracts just the digits from a string.
   *
   *****************************************************/
  static public String getDigitString( String source )
    {
    if ( source == null ) return ( "" );

    StringBuilder stringBuilder = new StringBuilder( source.length() );

    for ( char c : source.toCharArray() )
      {
      if ( Character.isDigit( c ) ) stringBuilder.append( c );
      }

    return ( stringBuilder.toString() );
    }


  /*****************************************************
   *
   * Returns true if the supplied value consists of just
   * digits, and at least one.
   *
   *****************************************************/
  static public boolean isDigitString( String value )
    {
    if ( isNullOrBlank( value ) ) return ( false );

    for ( char c : value.toCharArray() )
      {
      if ( ! Character.isDigit( c ) )
        {
        return ( false );
        }
      }

    return ( true );
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

