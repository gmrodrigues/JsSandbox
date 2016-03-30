package com.gmrodrigues.js.sandbox;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsSandboxFastEvaluatorTest
{
    @Test
    public void runTests() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        /* JsSandboxFastEvaluatorTest is a singleton and cannot run in threads */
        /* tests must be sequential */
        testScriptName();
        testSource();
        testPutVar();
        testLoadJs();
        testMap();
        testAddClass();
        testRequire();
    }

    private void testScriptName()
    {
        JsSandboxEvaluator evaluator = JsSandboxFastEvaluator.getInstance();
        String scriptName = "test_script";
        evaluator.setScriptName(scriptName);
        assertThat("testSource: exec must return correct value", evaluator.getScriptName(), is(scriptName));
    }

    private void testSource()
    {
        JsSandboxEvaluator evaluator = JsSandboxFastEvaluator.getInstance();
        String source = "'Hello ' + 'World'";
        evaluator.setSource(source);
        String result = (String) evaluator.exec();
        assertThat("testSource: exec must return correct value", result, is("Hello World"));
        assertThat("testSource: source must match", evaluator.getSource(), is(source));
    }

    private void testPutVar()
    {
        JsSandboxEvaluator evaluator = JsSandboxFastEvaluator.getInstance();
        evaluator.putVar("name", "Glauber");
        evaluator.setSource("'Hello ' + name");
        String result = (String) evaluator.exec();
        String expected = "Hello Glauber";
        assertThat("testPutVar: exec must return correct value", result, is(expected));
    }

    private void testLoadJs()
    {
        JsSandboxEvaluator evaluator = JsSandboxFastEvaluator.getInstance();
        evaluator.setScriptName("load_test");
        evaluator.loadJs("testdata/js/script.js");
        evaluator.setSource("message");
        String result = (String) evaluator.exec();
        String expected = "Howdy Stranger!";
        assertThat("testLoadJs: exec must return correct value", result, is(expected));
    }

    private void testMap()
    {
        JsSandboxEvaluator evaluator = JsSandboxFastEvaluator.getInstance();
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

    private void testAddClass() throws IllegalAccessException, InvocationTargetException, InstantiationException
    {
        JsSandboxEvaluator evaluator = JsSandboxFastEvaluator.getInstance();
        evaluator.addClass(SayHello.class);
        evaluator.setSource("hello = new SayHello();\nhello.sayHello('Glauber');");
        String result = (String) evaluator.exec();
        assertThat("testMap: exec must return correct value", result, is("hello Glauber"));
    }

    private void testRequire()
    {
        JsSandboxEvaluator evaluator = JsSandboxFastEvaluator.getInstance();
        evaluator.addRequirePath(new File("testdata/js"));
        evaluator.setSource("load('testdata/js/script.js');\nmessage");
        String result = (String) evaluator.exec();
        assertThat("testRequire: exec must return correct value", result, is("Howdy Stranger!"));
    }
}
