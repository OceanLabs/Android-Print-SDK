/*****************************************************
 *
 * ACache.java
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

import java.util.ArrayList;
import java.util.HashMap;


///// Class Declaration /////

/*****************************************************
 *
 * This class provides a caching facility for retrievers /
 * agents, and assists with distributing values or errors
 * to consumers.
 *
 *****************************************************/
abstract public class ACache<K,V,C extends ACache.IConsumer>
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "ACache";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private HashMap<K,V>             mCacheTable;
  private HashMap<K,ArrayList<C>>  mConsumerTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  protected ACache()
    {
    mCacheTable    = new HashMap<>();
    mConsumerTable = new HashMap<>();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns a cached value for the supplied key, or null
   * if the key has no cached value.
   *
   *****************************************************/
  protected V getCachedValue( K key )
    {
    return ( mCacheTable.get( key ) );
    }


  /*****************************************************
   *
   * Stores a consumer.
   *
   * @return true, if there is already a request running
   *         for the same key, false otherwise.
   *
   *****************************************************/
  protected boolean requestAlreadyStarted( K key, C consumer )
    {
    // If we don't already have an entry for the key - create a new list
    // containing just this one callback.

    ArrayList<C> callbackList = mConsumerTable.get( key );

    if ( callbackList == null )
      {
      callbackList = new ArrayList<>();

      callbackList.add( consumer );

      mConsumerTable.put( key, callbackList );

      return ( false );
      }


    // We do already have an entry for the key, so add this callback if it
    // is not already in the list.

    if ( ! callbackList.contains( consumer ) ) callbackList.add( consumer );

    return ( true );
    }


  /*****************************************************
   *
   * Stores a value in the cache, and distributes it to
   * any callbacks.
   *
   *****************************************************/
  protected void onValueAvailable( K key, V value )
    {
    // Cache the value
    mCacheTable.put( key, value );


    // Remove the consumer list, and supply the value to them.

    ArrayList<C> consumerList = mConsumerTable.remove( key );

    if ( consumerList != null )
      {
      for ( C consumer : consumerList )
        {
        if ( consumer != null ) onValueAvailable( value, consumer );
        }
      }
    }


  /*****************************************************
   *
   * Distributes a value to a consumer. The consumer will
   * never be null.
   *
   *****************************************************/
  abstract protected void onValueAvailable( V value, C consumer );


  /*****************************************************
   *
   * Distributes an error to  any consumers.
   *
   *****************************************************/
  protected void onError( K key, Exception exception )
    {
    // Remove the consumer list, and supply the error to them.

    ArrayList<C> consumerList = mConsumerTable.remove( key );

    if ( consumerList != null )
      {
      for ( C consumer : consumerList )
        {
        if ( consumer != null ) onError( exception, consumer );
        }
      }
    }


  /*****************************************************
   *
   * Distributes an error to a consumer. The callback will
   * never be null.
   *
   *****************************************************/
  abstract protected void onError( Exception exception, C consumer );


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A consumer.
   *
   *****************************************************/
  public interface IConsumer
    {
    }

  }

