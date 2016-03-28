package com.gmrodrigues.js.sandbox.wrapper;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;
import org.ringojs.wrappers.ScriptableList;
import org.ringojs.wrappers.ScriptableMap;

import java.util.List;
import java.util.Map;

public class JavaToNativeWrapFactory extends WrapFactory
{
    public JavaToNativeWrapFactory()
    {
        setJavaPrimitiveWrap(false);
    }

    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope,
                                       Object javaObject, Class staticType)
    {
        if (javaObject instanceof Map) {
            Map map = (Map) javaObject;
            return new ScriptableMap(scope, map);
        } else if (javaObject instanceof List) {
            List list = (List) javaObject;
            return new ScriptableList(scope, list);
        } else {
            return new NativeJavaObject(scope, javaObject, staticType);
        }
    }

}
