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
package org.apache.commons.jexl3.junit;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.jexl3.JexlEvalContext;
import org.apache.commons.jexl3.JexlArithmetic;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlScript;
import org.junit.Assert;

/**
 * A utility class for performing JUnit based assertions using Jexl
 * expressions. This class can make it easier to do unit tests using
 * JEXL navigation expressions.
 *
 * @since 1.0
 */
public class Asserter extends Assert {
    /** variables used during asserts. */
    private final Map<String, Object> variables = new HashMap<String, Object>();
    /** context to use during asserts. */
    private final JexlEvalContext context = new JexlEvalContext(variables);
    /** JEXL engine to use during Asserts. */
    private final JexlEngine engine;

    /**
     *
     * Create an asserter.
     * @param jexl the JEXL engine to use
     */
    public Asserter(final JexlEngine jexl) {
        engine = jexl;
    }

    /**
     * Retrieves the underlying JEXL engine.
     * @return the JEXL engine
     */
    public JexlEngine getEngine() {
        return engine;
    }

    /**
     * Retrieves the underlying JEXL context.
     * @return the JEXL context
     */
    public JexlContext getContext() {
        return context;
    }

    public void setStrict(final boolean s) {
        context.getEngineOptions().setStrict(s);
    }

    public void setStrict(final boolean es, final boolean as) {
        context.getEngineOptions().setStrict(es);
        context.getEngineOptions().setStrictArithmetic(as);
    }

    public void setSilent(final boolean silent) {
        context.getEngineOptions().setSilent(silent);
    }

    /**
     * Performs an assertion that the value of the given JEXL expression
     * evaluates to the given expected value.
     *
     * @param expression is the JEXL expression to evaluate
     * @param expected is the expected value of the expression
     * @throws Exception if the expression could not be evaluationed or an assertion
     * fails
     */
    public void assertExpression(final String expression, final Object expected) throws Exception {
        final JexlScript exp = engine.createScript(expression);
        final Object value = exp.execute(context);
        if (expected instanceof BigDecimal) {
            final JexlArithmetic jexla = engine.getArithmetic();
            Assert.assertEquals("expression: " + expression, 0,
                    ((BigDecimal) expected).compareTo(jexla.toBigDecimal(value)));
        }
        if (expected != null && value != null) {
            if (expected.getClass().isArray() && value.getClass().isArray()) {
                final int esz = Array.getLength(expected);
                final int vsz = Array.getLength(value);
                final String report = "expression: " + expression;
                Assert.assertEquals(report + ", array size", esz, vsz);
                for (int i = 0; i < vsz; ++i) {
                    Assert.assertEquals(report + ", value@[]" + i, Array.get(expected, i), Array.get(value, i));
                }
            } else {
                Assert.assertEquals("expression: " + expression + ", "
                        + expected.getClass().getSimpleName()
                        + " ?= "
                        + value.getClass().getSimpleName(),
                        expected, value);
            }
        } else {
            Assert.assertEquals("expression: " + expression, expected, value);
        }
    }

    /**
     * Performs an assertion that the expression fails throwing an exception.
     * If matchException is not null, the exception message is expected to match it as a regexp.
     * The engine is temporarily switched to strict * verbose to maximize error detection abilities.
     * @param expression the expression that should fail
     * @param matchException the exception message pattern
     * @throws Exception if the expression did not fail or the exception did not match the expected pattern
     */
    public void failExpression(final String expression, final String matchException) throws Exception {
        try {
            final JexlScript exp = engine.createScript(expression);
            exp.execute(context);
            fail("expression: " + expression);
        } catch (final JexlException xjexl) {
            if (matchException != null && !xjexl.getMessage().matches(matchException)) {
                fail("expression: " + expression + ", expected: " + matchException + ", got " + xjexl.getMessage());
            }
        }
    }

    /**
     * Puts a variable of a certain name in the context so that it can be used from
     * assertion expressions.
     *
     * @param name variable name
     * @param value variable value
     */
    public void setVariable(final String name, final Object value) {
        variables.put(name, value);
    }

    /**
     * Removes a variable of a certain name from the context.
     * @param name variable name
     * @return variable value
     */
    public Object removeVariable(final String name) {
        return variables.remove(name);
    }

    /**
     * Gets a variable of a certain name.
     *
     * @param name variable name
     * @return value variable value
     */
    public Object getVariable(final String name) {
        return variables.get(name);
    }

    /**
     * @return the variables map
     */
    public Map<String, Object> getVariables() {
        return variables;
    }
}
