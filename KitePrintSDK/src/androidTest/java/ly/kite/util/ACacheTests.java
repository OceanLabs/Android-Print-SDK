/*****************************************************
 *
 * ACacheTests.java
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

import junit.framework.Assert;
import junit.framework.TestCase;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the abstract cache class.
 *
 *****************************************************/
public class ACacheTests extends TestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "ACacheTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Tests.
   *
   *****************************************************/

  public void testEmpty()
    {
    Cache cache = new Cache();

    Assert.assertEquals( null, cache.getCachedValue( "key" ) );
    }

  public void testRegistration()
    {
    Cache cache = new Cache();

    Assert.assertEquals( false, cache.registerForValue( "key", new Consumer() ) );
    Assert.assertEquals( true, cache.registerForValue( "key", new Consumer() ) );
    }

  public void testDeliver1()
    {
    Cache cache = new Cache();

    Consumer consumer1 = new Consumer();
    Consumer consumer2 = new Consumer();

    Assert.assertEquals( false, cache.registerForValue( "key", consumer1 ) );
    Assert.assertEquals( true, cache.registerForValue( "key", consumer2 ) );


    cache.saveAndDistributeValue( "wrong_key", "wrong_value" );

    Assert.assertEquals( null, consumer1.value );
    Assert.assertEquals( null, consumer2.value );

    Assert.assertEquals( null, cache.getCachedValue( "key" ) );


    cache.saveAndDistributeValue( "key", "right_value" );

    Assert.assertEquals( "right_value", consumer1.value );
    Assert.assertEquals( "right_value", consumer2.value );

    Assert.assertEquals( "right_value", cache.getCachedValue( "key" ) );
    }

  public void testDeliver2()
    {
    Cache cache = new Cache();

    Consumer consumer1 = new Consumer();
    Consumer consumer2 = new Consumer();

    Assert.assertEquals( false, cache.registerForValue( "key", consumer1 ) );
    Assert.assertEquals( true, cache.registerForValue( "key", consumer2 ) );


    Exception exception = new Exception( "exception_message" );

    cache.onError( "key", exception );

    Assert.assertEquals( null, consumer1.value );
    Assert.assertEquals( null, consumer2.value );

    Assert.assertEquals( exception, consumer1.exception );
    Assert.assertEquals( exception, consumer2.exception );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Consumer implementation.
   *
   *****************************************************/
  private class Consumer
    {
    String     value;
    Exception  exception;

    protected void onValue( String value )
      {
      this.value = value;
      }

    protected void onError( Exception exception )
      {
      this.exception = exception;
      }
    }


  /*****************************************************
   *
   * Cache implementation.
   *
   *****************************************************/
  private class Cache extends ACache<String,String,Consumer>
    {
    @Override
    protected void onValueAvailable( String value, Consumer consumer )
      {
      consumer.onValue( value );
      }

    @Override
    protected void onError( Exception exception, Consumer consumer )
      {
      consumer.onError( exception );
      }

    }

  }

