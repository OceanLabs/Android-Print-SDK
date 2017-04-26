/*****************************************************
 *
 * DelimitedStringBuilderTests.java
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
 * This class tests the delimited string builder class.
 *
 *****************************************************/
public class DelimitedStringBuilderTests extends KiteTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "DelimitedStringBuilderTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Constructor tests
   *
   *****************************************************/

  public void testConstructor1()
    {
    try
      {
      new DelimitedStringBuilder( null );

      Assert.fail();
      }
    catch ( IllegalArgumentException iae )
      {
      // Test passed
      }
    }

  public void testConstructor2()
    {
    try
      {
      new DelimitedStringBuilder( "" );

      Assert.fail();
      }
    catch ( IllegalArgumentException iae )
      {
      // Test passed
      }
    }


  /*****************************************************
   *
   * Builder tests
   *
   *****************************************************/

  public void test1()
    {
    DelimitedStringBuilder builder = new DelimitedStringBuilder( "|" );

    Assert.assertEquals( "", builder.toString() );
    }

  public void test2()
    {
    DelimitedStringBuilder builder = new DelimitedStringBuilder( "|" );

    builder.append( "cat" );

    Assert.assertEquals( "cat", builder.toString() );
    }

  public void test3()
    {
    DelimitedStringBuilder builder = new DelimitedStringBuilder( "|" );

    builder.append( "the" );
    builder.append( "cat" );

    Assert.assertEquals( "the|cat", builder.toString() );
    }

  public void test4()
    {
    DelimitedStringBuilder builder = new DelimitedStringBuilder( "|" );

    builder.append( "the" );
    builder.append( "cat" );
    builder.append( "sat" );
    builder.append( "on" );
    builder.append( "the" );
    builder.append( "mat" );

    Assert.assertEquals( "the|cat|sat|on|the|mat", builder.toString() );
    }

  public void test5()
    {
    DelimitedStringBuilder builder = new DelimitedStringBuilder( "blah" );

    builder.append( "who" );
    builder.append( "goes" );
    builder.append( "there" );

    Assert.assertEquals( "whoblahgoesblahthere", builder.toString() );
    }

  public void test6()
    {
    DelimitedStringBuilder builder = new DelimitedStringBuilder( "[]" );

    builder.append( "one" );
    builder.append( "" );
    builder.append( "two" );
    builder.append( null );
    builder.append( "three" );
    builder.append( "" );

    Assert.assertEquals( "one[][]two[][]three[]", builder.toString() );
    }


  ////////// Inner Class(es) //////////

  }

