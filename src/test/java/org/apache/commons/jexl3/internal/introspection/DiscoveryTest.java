/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jexl3.internal.introspection;

import java.io.Serializable;
import org.apache.commons.jexl3.JexlTestCase;
import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.jexl3.introspection.JexlPropertyGet;
import org.apache.commons.jexl3.introspection.JexlPropertySet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.jexl3.introspection.JexlMethod;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for checking introspection discovery.
 *
 * @since 2.0
 */
public class DiscoveryTest extends JexlTestCase {
    public DiscoveryTest() {
        super("DiscoveryTest");
    }

    public static class Duck {
        private String value;
        private String eulav;

        public Duck(final String v, final String e) {
            value = v;
            eulav = e;
        }

        public String get(final String prop) {
            if ("value".equals(prop)) {
                return value;
            }
            if ("eulav".equals(prop)) {
                return eulav;
            }
            return "no such property";
        }

        public void set(final String prop, final String v) {
            if ("value".equals(prop)) {
                value = v;
            } else if ("eulav".equals(prop)) {
                eulav = v;
            }
        }
    }

    public static class Bean {
        private String value;
        private String eulav;
        private boolean flag;

        public Bean(final String v, final String e) {
            value = v;
            eulav = e;
            flag = true;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String v) {
            value = v;
        }

        public String getEulav() {
            return eulav;
        }

        public void setEulav(final String v) {
            eulav = v;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(final boolean f) {
            flag = f;
        }
    }

    @Test
    public void testBeanIntrospection() throws Exception {
        final Uberspect uber = Engine.getUberspect(null, null);
        final Bean bean = new Bean("JEXL", "LXEJ");

        final JexlPropertyGet get = uber.getPropertyGet(bean, "value");
        final JexlPropertySet set = uber.getPropertySet(bean, "value", "foo");
        Assert.assertTrue("bean property getter", get instanceof PropertyGetExecutor);
        Assert.assertTrue("bean property setter", set instanceof PropertySetExecutor);
        // introspector and uberspect should return same result
        Assert.assertEquals(get, uber.getPropertyGet(bean, "value"));
        Assert.assertEquals(set, uber.getPropertySet(bean, "value", "foo"));
        // different property should return different setter/getter
        Assert.assertNotEquals(get, uber.getPropertyGet(bean, "eulav"));
        Assert.assertNotEquals(set, uber.getPropertySet(bean, "eulav", "foo"));
        // setter returns argument
        final Object bar = set.invoke(bean, "bar");
        Assert.assertEquals("bar", bar);
        // getter should return last value
        Assert.assertEquals("bar", get.invoke(bean));
        // tryExecute should succeed on same property
        final Object quux = set.tryInvoke(bean, "value", "quux");
        Assert.assertEquals("quux", quux);
        Assert.assertEquals("quux", get.invoke(bean));
        // tryExecute should fail on different property
        Assert.assertEquals(AbstractExecutor.TRY_FAILED, set.tryInvoke(bean, "eulav", "nope"));

    }

    @Test
    public void testDuckIntrospection() throws Exception {
        final Uberspect uber = Engine.getUberspect(null, null);
        final Duck duck = new Duck("JEXL", "LXEJ");

        final JexlPropertyGet get = uber.getPropertyGet(duck, "value");
        final JexlPropertySet set = uber.getPropertySet(duck, "value", "foo");
        Assert.assertTrue("duck property getter", get instanceof DuckGetExecutor);
        Assert.assertTrue("duck property setter", set instanceof DuckSetExecutor);
        // introspector and uberspect should return same result
        Assert.assertEquals(get, uber.getPropertyGet(duck, "value"));
        Assert.assertEquals(set, uber.getPropertySet(duck, "value", "foo"));
        // different property should return different setter/getter
        Assert.assertNotEquals(get, uber.getPropertyGet(duck, "eulav"));
        Assert.assertNotEquals(set, uber.getPropertySet(duck, "eulav", "foo"));
        // setter returns argument
        final Object bar = set.invoke(duck, "bar");
        Assert.assertEquals("bar", bar);
        // getter should return last value
        Assert.assertEquals("bar", get.invoke(duck));
        // tryExecute should succeed on same property
        final Object quux = set.tryInvoke(duck, "value", "quux");
        Assert.assertEquals("quux", quux);
        Assert.assertEquals("quux", get.invoke(duck));
        // tryExecute should fail on different property
        Assert.assertEquals(AbstractExecutor.TRY_FAILED, set.tryInvoke(duck, "eulav", "nope"));
    }

    @Test
    public void testListIntrospection() throws Exception {
        final Uberspect uber = Engine.getUberspect(null, null);
        final List<Object> list = new ArrayList<Object>();
        list.add("LIST");
        list.add("TSIL");

        final JexlPropertyGet get = uber.getPropertyGet(list, 1);
        final JexlPropertySet set = uber.getPropertySet(list, 1, "foo");
        Assert.assertTrue("list property getter", get instanceof ListGetExecutor);
        Assert.assertTrue("list property setter", set instanceof ListSetExecutor);
        // introspector and uberspect should return same result
        Assert.assertEquals(get, uber.getPropertyGet(list, 1));
        Assert.assertEquals(set, uber.getPropertySet(list, 1, "foo"));
        // different property should return different setter/getter
        Assert.assertNotEquals(get, uber.getPropertyGet(list, 0));
        Assert.assertNotEquals(get, uber.getPropertySet(list, 0, "foo"));
        // setter returns argument
        final Object bar = set.invoke(list, "bar");
        Assert.assertEquals("bar", bar);
        // getter should return last value
        Assert.assertEquals("bar", get.invoke(list));
        // tryExecute should succeed on integer property
        final Object quux = set.tryInvoke(list, 1, "quux");
        Assert.assertEquals("quux", quux);
        // getter should return last value
        Assert.assertEquals("quux", get.invoke(list));
        // tryExecute should fail on non-integer property class
        Assert.assertEquals(AbstractExecutor.TRY_FAILED, set.tryInvoke(list, "eulav", "nope"));
    }

    @Test
    public void testMapIntrospection() throws Exception {
        final Uberspect uber = Engine.getUberspect(null, null);
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("value", "MAP");
        map.put("eulav", "PAM");

        final JexlPropertyGet get = uber.getPropertyGet(map, "value");
        final JexlPropertySet set = uber.getPropertySet(map, "value", "foo");
        Assert.assertTrue("map property getter", get instanceof MapGetExecutor);
        Assert.assertTrue("map property setter", set instanceof MapSetExecutor);
        // introspector and uberspect should return same result
        Assert.assertEquals(get, uber.getPropertyGet(map, "value"));
        Assert.assertEquals(set, uber.getPropertySet(map, "value", "foo"));
        // different property should return different setter/getter
        Assert.assertNotEquals(get, uber.getPropertyGet(map, "eulav"));
        Assert.assertNotEquals(get, uber.getPropertySet(map, "eulav", "foo"));
        // setter returns argument
        final Object bar = set.invoke(map, "bar");
        Assert.assertEquals("bar", bar);
        // getter should return last value
        Assert.assertEquals("bar", get.invoke(map));
        // tryExecute should succeed on same property class
        final Object quux = set.tryInvoke(map, "value", "quux");
        Assert.assertEquals("quux", quux);
        // getter should return last value
        Assert.assertEquals("quux", get.invoke(map));
        // tryExecute should fail on different property class
        Assert.assertEquals(AbstractExecutor.TRY_FAILED, set.tryInvoke(map, 1, "nope"));
    }

    public static class Bulgroz {
        public Object list(final int x) {
            return 0;
        }
        public Object list(final String x) {
            return 1;
        }
        public Object list(final Object x) {
            return 2;
        }
        public Object list(final int x, final Object...y) {
            return 3;
        }
        public Object list(final int x, final int y) {
            return 4;
        }
        public Object list(final String x, final Object...y) {
            return 5;
        }
        public Object list(final String x, final String y) {
            return 6;
        }
        public Object list(final Object x, final Object...y) {
            return 7;
        }
        public Object list(final Object x, final Object y) {
            return 8;
        }
        public Object amb(final Serializable x) {
            return -1;
        }
        public Object amb(final Number x) {
            return -2;
        }
    }

    @Test
    public void testMethodIntrospection() throws Exception {
        final Uberspect uber = new Uberspect(null, null);
        final Bulgroz bulgroz = new Bulgroz();
        JexlMethod jmethod;
        Object result;
        jmethod = uber.getMethod(bulgroz, "list", 0);
        result = jmethod.invoke(bulgroz, 0);
        Assert.assertEquals(0, result);
        jmethod = uber.getMethod(bulgroz, "list", "1");
        result = jmethod.invoke(bulgroz, "1");
        Assert.assertEquals(1, result);
        jmethod = uber.getMethod(bulgroz, "list", bulgroz);
        result = jmethod.invoke(bulgroz, bulgroz);
        Assert.assertEquals(2, result);
        jmethod = uber.getMethod(bulgroz, "list", 1, bulgroz);
        result = jmethod.invoke(bulgroz, 1, bulgroz);
        Assert.assertEquals(3, result);
        jmethod = uber.getMethod(bulgroz, "list", 1, bulgroz, bulgroz);
        result = jmethod.invoke(bulgroz, 1, bulgroz, bulgroz);
        Assert.assertEquals(3, result);
        jmethod = uber.getMethod(bulgroz, "list", 1, 2);
        result = jmethod.invoke(bulgroz, 1, 2);
        Assert.assertEquals(4, result);
        jmethod = uber.getMethod(bulgroz, "list", "1", bulgroz);
        result = jmethod.invoke(bulgroz, "1", bulgroz);
        Assert.assertEquals(5, result);
        jmethod = uber.getMethod(bulgroz, "list", "1", "2");
        result = jmethod.invoke(bulgroz, "1", "2");
        Assert.assertEquals(6, result);
        jmethod = uber.getMethod(bulgroz, "list", bulgroz, bulgroz);
        result = jmethod.invoke(bulgroz, bulgroz, bulgroz);
        Assert.assertEquals(8, result);
        jmethod = uber.getMethod(bulgroz, "list", bulgroz, 1, bulgroz);
        result = jmethod.invoke(bulgroz, bulgroz, 1, bulgroz);
        Assert.assertEquals(7, result);
        jmethod = uber.getMethod(bulgroz, "list", bulgroz, 1, "1");
        result = jmethod.invoke(bulgroz, bulgroz, 1, "1");
        Assert.assertEquals(7, result);
        jmethod = uber.getMethod(bulgroz, "list", (Object) null);
        result = jmethod.invoke(bulgroz,  (Object) null);
        Assert.assertEquals(2, result);
        jmethod = uber.getMethod(bulgroz, "list", bulgroz, (Object) null);
        result = jmethod.invoke(bulgroz, bulgroz, (Object) null);
        Assert.assertEquals(8, result);
        jmethod = uber.getMethod(bulgroz, "list", null, "1");
        result = jmethod.invoke(bulgroz, null, "1");
        Assert.assertEquals(8, result);
        jmethod = uber.getMethod(bulgroz, "list", bulgroz, null, null);
        result = jmethod.invoke(bulgroz, bulgroz, null, null);
        Assert.assertEquals(7, result);

        jmethod = uber.getMethod(bulgroz, "amb", 3d);
        Assert.assertNotNull(null, jmethod);
    }
}
