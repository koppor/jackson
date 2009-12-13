package org.codehaus.jackson.map.ser;

import org.codehaus.jackson.map.BaseMapTest;

import java.io.*;
import java.util.*;

import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * Unit tests for verifying JSON view functionality: ability to declaratively
 * suppress subset of properties from being serialized.
 */
public class TestViews
    extends BaseMapTest
{
    // Classes that represent views
    static class ViewA { }
    static class ViewAA extends ViewA { }
    static class ViewB { }
    static class ViewBB extends ViewB { }
    
    static class Bean
    {
        @JsonView(ViewA.class)
        public String a = "1";

        @JsonView({ViewAA.class, ViewB.class})
        public String aa = "2";

        @JsonView(ViewB.class)
        public String getB() { return "3"; }
    }

    @SuppressWarnings("unchecked")
    public void testSimple() throws IOException
    {
        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        // Ok, first, using no view whatsoever; all 3
        Bean bean = new Bean();
        Map<String,Object> map = writeAndMap(mapper, bean);
        assertEquals(3, map.size());

        // Then with "ViewA", just one property
        sw = new StringWriter();
        mapper.writeValueUsingView(sw, bean, ViewA.class);
        map = mapper.readValue(sw.toString(), Map.class);
        assertEquals(1, map.size());
        assertEquals("1", map.get("a"));

        // "ViewAA", 2 properties
        sw = new StringWriter();
        mapper.writeValueUsingView(sw, bean, ViewAA.class);
        map = mapper.readValue(sw.toString(), Map.class);
        assertEquals(2, map.size());
        assertEquals("1", map.get("a"));
        assertEquals("2", map.get("aa"));

        // "ViewB", 2 prop2
        sw = new StringWriter();
        mapper.writeValueUsingView(sw, bean, ViewB.class);
        map = mapper.readValue(sw.toString(), Map.class);
        assertEquals(2, map.size());
        assertEquals("2", map.get("aa"));
        assertEquals("3", map.get("b"));

        // and "ViewBB", 2 as well
        sw = new StringWriter();
        mapper.writeValueUsingView(sw, bean, ViewBB.class);
        map = mapper.readValue(sw.toString(), Map.class);
        assertEquals(2, map.size());
        assertEquals("2", map.get("aa"));
        assertEquals("3", map.get("b"));
    }
}