package com.gmrodrigues.js.sandbox;

import org.mozilla.javascript.ScriptableObject;

public class SayHello extends ScriptableObject
{
    public SayHello()
    {
    }

    @Override
    public String getClassName()
    {
        return "SayHello";
    }

    public String jsFunction_sayHello(String name)
    {
        return "hello " + name;
    }
}
