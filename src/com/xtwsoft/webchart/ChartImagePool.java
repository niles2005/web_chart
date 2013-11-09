/**
 * 
 *
 * History:
 *   2007-6-7 13:04:33 Created by Wangliang
 */
package com.xtwsoft.webchart;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;


/**
 * The <code>MapBuilderPool</code> class.
 *
 * @author <a href="mailto:niles2010@live.cn">nielei</a>
 * @version 1.0 2007-6-7 13:04:33
 */
public class ChartImagePool
    extends BasePoolableObjectFactory
{
    public Object makeObject() throws Exception
    {
        return new ChartImage();
    }

    public void passivateObject(Object obj) throws Exception
    {
        //nothing
    }
    
    public static ObjectPool pool = new GenericObjectPool(new ChartImagePool(), 50,
            GenericObjectPool.DEFAULT_WHEN_EXHAUSTED_ACTION,
            GenericObjectPool.DEFAULT_MAX_WAIT, 10);

}