package com.gmrodrigues.js.sandbox;

import com.gmrodrigues.js.sandbox.annotation.JsScriptFuntion;
import com.gmrodrigues.js.sandbox.wrapper.JavaToNativeWrapFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.mozilla.javascript.tools.shell.Environment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class JsSandboxEnvironment
{
    public final com.gmrodrigues.js.sandbox.JsScriptFunctionUtils funcs = new com.gmrodrigues.js.sandbox.JsScriptFunctionUtils();

    static RuntimeException reportRuntimeError(String msgId)
    {
        String message = ToolErrorReporter.getMessage(msgId);
        return Context.reportRuntimeError(message);
    }

    static RuntimeException reportRuntimeError(String msgId, String msgArg)
    {
        String message = ToolErrorReporter.getMessage(msgId, msgArg);
        return Context.reportRuntimeError(message);
    }

    private void initFuncs()
    {
        Context cx = Context.getCurrentContext();
        // cx.setWrapFactory(new PrimitiveWrapFactory());
        //cx.setWrapFactory(new EnhancedWrapFactory());
        cx.setWrapFactory(new JavaToNativeWrapFactory());

        cx.initStandardObjects(funcs);

        Class funcsClass = funcs.getClass();
        Method[] methods = funcsClass.getMethods();
        List<String> names = new ArrayList<String>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(JsScriptFuntion.class)) {
                names.add(method.getName());
            }
        }
        String[] funcNames = names.toArray(new String[0]);

        funcs.defineFunctionProperties(funcNames, funcsClass,
                ScriptableObject.DONTENUM);
        Environment.defineClass(funcs);
        Environment environment = new Environment(funcs);
        funcs.defineProperty("environment", environment,
                ScriptableObject.DONTENUM);
        cx.setOptimizationLevel(9);
    }

    public Scriptable getScope()
    {
        if (Context.getCurrentContext() == null) {
            getContext();
            initFuncs();
        }
        return funcs;
    }

    public Context getContext()
    {
        Context cx = Context.getCurrentContext();
        if (cx == null) {
            cx = Context.enter();
            initFuncs();
        }
        return cx;
    }
}
