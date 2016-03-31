package com.gmrodrigues.js.sandbox;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsSandboxConcurrentEvaluatorTest
{
    @Test
    public void testScriptName()
    {
        JsSandboxEvaluator evaluator = JsSandboxConcurrentEvaluator.newInstance();
        String scriptName = "test_script";
        evaluator.setScriptName(scriptName);
        assertThat("testSource: exec must return correct value", evaluator.getScriptName(), is(scriptName));
    }

    @Test
    public void testSource()
    {
        JsSandboxEvaluator evaluator = JsSandboxConcurrentEvaluator.newInstance();
        String source = "'Hello ' + 'World'";
        evaluator.setSource(source);
        String result = (String) evaluator.exec();
        assertThat("testSource: exec must return correct value", result, is("Hello World"));
        assertThat("testSource: source must match", evaluator.getSource(), is(source));
    }

    @Test
    public void testPutVar()
    {
        JsSandboxEvaluator evaluator = JsSandboxConcurrentEvaluator.newInstance();
        evaluator.putVar("name", "Glauber");
        evaluator.setSource("'Hello ' + name");
        String result = (String) evaluator.exec();
        String expected = "Hello Glauber";
        assertThat("testPutVar: exec must return correct value", result, is(expected));
    }

    @Test
    public void testLoadJs()
    {
        JsSandboxEvaluator evaluator = JsSandboxConcurrentEvaluator.newInstance();
        evaluator.setScriptName("load_test");
        evaluator.loadJs("testdata/js/script.js");
        evaluator.setSource("message");
        String result = (String) evaluator.exec();
        String expected = "Howdy Stranger!";
        assertThat("testLoadJs: exec must return correct value", result, is(expected));
    }

    @Test
    public void testMap()
    {
        JsSandboxEvaluator evaluator = JsSandboxConcurrentEvaluator.newInstance();
        File mapFile = new File("testdata/xml/map.xml");
        evaluator.addXmlMapFile("map", mapFile);
        evaluator.setSource("'Hello ' + map.name");
        String result = (String) evaluator.exec();
        Map map = evaluator.<Map>getVar("map");
        assertThat("testMap: exec must return correct value", result, is("Hello Glauber Machado Rodrigues"));
        assertThat("testMap: map name must return correct value", map.get("name"), is("Glauber Machado Rodrigues"));
        assertThat("testMap: map website return correct value", map.get("website"), is("http://gmrodrigues.com"));
        assertThat("testMap: map website return correct value", map.get("website"), is("http://gmrodrigues.com"));

        Map maps = evaluator.getMaps();
        assertThat("testMap: maps must match", maps.get("map"), is(mapFile));
    }

    @Test
    public void testAddClass() throws IllegalAccessException, InvocationTargetException, InstantiationException
    {
        JsSandboxEvaluator evaluator = JsSandboxConcurrentEvaluator.newInstance();
        evaluator.addClass(SayHello.class);
        evaluator.setSource("hello = new SayHello();\nhello.sayHello('Glauber');");
        String result = (String) evaluator.exec();
        assertThat("testMap: exec must return correct value", result, is("hello Glauber"));
    }

    @Test
    public void testRequire()
    {
        JsSandboxEvaluator evaluator = JsSandboxConcurrentEvaluator.newInstance();
        evaluator.addRequirePath(new File("testdata/js"));
        evaluator.setSource("load('testdata/js/script.js');\nmessage");
        String result = (String) evaluator.exec();
        assertThat("testRequire: exec must return correct value", result, is("Howdy Stranger!"));
    }
}
