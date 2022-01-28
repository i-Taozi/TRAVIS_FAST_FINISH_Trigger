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
package org.apache.commons.jexl3;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests do while statement.
 * @since 3.2
 */
@SuppressWarnings({"UnnecessaryBoxing", "AssertEqualsBetweenInconvertibleTypes"})
public class DoWhileTest extends JexlTestCase {

    public DoWhileTest() {
        super("DoWhileTest");
    }

    @Test
    public void testSimpleWhileFalse() throws Exception {
        JexlScript e = JEXL.createScript("do {} while (false)");
        final JexlContext jc = new MapContext();

        Object o = e.execute(jc);
        Assert.assertNull("Result is not null", o);
        e = JEXL.createScript("do {} while (false); 23");
        o = e.execute(jc);
        Assert.assertEquals(23, o);

    }

    @Test
    public void testWhileExecutesExpressionWhenLooping() throws Exception {
        JexlScript e = JEXL.createScript("do x = x + 1 while (x < 10)");
        final JexlContext jc = new MapContext();
        jc.set("x", 1);

        Object o = e.execute(jc);
        Assert.assertEquals(10, o);
        Assert.assertEquals(10, jc.get("x"));

        e = JEXL.createScript("var x = 0; do x += 1; while (x < 23)");
        o = e.execute(jc);
        Assert.assertEquals(23, o);


        jc.set("x", 1);
        e = JEXL.createScript("do x += 1; while (x < 23); return 42;");
        o = e.execute(jc);
        Assert.assertEquals(23, jc.get("x"));
        Assert.assertEquals(42, o);
    }

    @Test
    public void testWhileWithBlock() throws Exception {
        final JexlScript e = JEXL.createScript("do { x = x + 1; y = y * 2; } while (x < 10)");
        final JexlContext jc = new MapContext();
        jc.set("x", new Integer(1));
        jc.set("y", new Integer(1));

        final Object o = e.execute(jc);
        Assert.assertEquals("Result is wrong", new Integer(512), o);
        Assert.assertEquals("x is wrong", new Integer(10), jc.get("x"));
        Assert.assertEquals("y is wrong", new Integer(512), jc.get("y"));
    }

    @Test
    public void testForEachBreakInsideFunction() throws Exception {
        try {
            final JexlScript e = JEXL.createScript("for (i : 1..2) {  y = function() { break; } }");
            Assert.fail("break is out of loop!");
        } catch (final JexlException.Parsing xparse) {
            final String str = xparse.detailedMessage();
            Assert.assertTrue(str.contains("break"));
        }
    }

    @Test
    public void testForEachContinueInsideFunction() throws Exception {
        try {
            final JexlScript e = JEXL.createScript("for (i : 1..2) {  y = function() { continue; } }");
            Assert.fail("continue is out of loop!");
        } catch (final JexlException.Parsing xparse) {
            final String str = xparse.detailedMessage();
            Assert.assertTrue(str.contains("continue"));
        }
    }

    @Test
    public void testForEachLambda() throws Exception {
        final JexlScript e = JEXL.createScript("(x)->{ for (i : 1..2) {  continue; var y = function() { 42; } break; } }");
        Assert.assertNotNull(e);
    }

    @Test
    public void testEmptyBody() throws Exception {
        final JexlScript e = JEXL.createScript("var i = 0; do ; while((i+=1) < 10); i");
        final JexlContext jc = new MapContext();
        final Object o = e.execute(jc);
        Assert.assertEquals(10, o);
    }

    @Test
    public void testEmptyStmtBody() throws Exception {
        final JexlScript e = JEXL.createScript("var i = 0; do {} while((i+=1) < 10); i");
        final JexlContext jc = new MapContext();
        final Object o = e.execute(jc);
        Assert.assertEquals(10, o);
    }

    @Test
    public void testWhileEmptyBody() throws Exception {
        final JexlScript e = JEXL.createScript("var i = 0; while((i+=1) < 10); i");
        final JexlContext jc = new MapContext();
        final Object o = e.execute(jc);
        Assert.assertEquals(10, o);
    }

    @Test
    public void testWhileEmptyStmtBody() throws Exception {
        final JexlScript e = JEXL.createScript("var i = 0; while((i+=1) < 10) {}; i");
        final JexlContext jc = new MapContext();
        final Object o = e.execute(jc);
        Assert.assertEquals(10, o);
    }
}
