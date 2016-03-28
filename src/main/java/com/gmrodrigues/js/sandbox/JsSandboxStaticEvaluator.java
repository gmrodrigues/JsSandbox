package com.gmrodrigues.js.sandbox;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gmrodrigues.js.sandbox.util.XmlToJsonConverter;
import net.sf.json.JSON;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

class JsSandboxStaticEvaluator
{


    public static Script compile(Context cx, Scriptable scope, Map<String, File> maps, List<File> requireDirList, String source, String scriptName)
    {
        loadObjectsFromXmlFiles(scope, maps);
        loadModulesFromIncludeDirs(cx, scope, requireDirList);
        cx.setOptimizationLevel(9);
        Script script = cx.compileString(source, "<" + scriptName + ">", 1, null);
        return script;
    }

    public static void putVar(Scriptable scope, String name, Object object)
    {
        scope.put(name, scope, object);
    }

    public static <T> T getVar(Scriptable scope, String name)
    {
        Object o = scope.get(name, scope);
        if (o instanceof Map) {
            Map nativeResult = (Map) scope.get("itemset", scope);
            Map mappedResult = new LinkedHashMap();
            mappedResult.putAll(nativeResult);
            return (T) mappedResult;
        }
        if (o instanceof NativeArray) {
            NativeArray na = (NativeArray) o;
            return (T) na;
        }
        return (T) o;
    }

    public static void addClass(Scriptable scope, Class clazz) throws IllegalAccessException,
            InstantiationException, InvocationTargetException
    {
        ScriptableObject.defineClass(scope, clazz);
    }

    public static Object exec(Script script, Context cx, Scriptable scope, String source, String scriptName)
    {
        try {
            Object result = script.exec(cx, scope);
            return result;
        } catch (EcmaError e) {
            System.err.println(e.getMessage());
            int lineNum = e.getLineNumber();
            if (lineNum > 0) {
                String[] lines = source.split("\n");
                System.err.println("\t" + scriptName + ">#" + lineNum + ">>> "
                        + lines[lineNum - 1]);
            }

            System.err
                    .println("There are errors on script '"
                            + scriptName
                            + "', cannot continue. To continue, set doContinueOnErrorAndClearItemset=true");
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addXmlMapFile(String varname, File file, Map<String, File> maps)
    {
        maps.put(varname, file);
        return;
    }

    public static void addRequirePath(File dir, List<File> loadOnDirs, List<File> requireDirList)
    {
        if (dir == null || !dir.isDirectory() || requireDirList.contains(dir)
                || loadOnDirs.contains(dir)) {
            return;
        }
        loadOnDirs.add(dir);
        requireDirList.add(dir);
        return;
    }

    private static void loadObjectsFromXmlFiles(Scriptable scope, Map<String, File> maps)
    {
        // JsonParser jsonp = new JsonParser(cx, scope);
        for (String varname : maps.keySet()) {
            File file = maps.get(varname);
            JSON json = XmlToJsonConverter.getJsonFromXmlFile(file);
            scope.put(varname, scope, json);
            String _element_ = json.toString();
            scope.put("_" + varname + "_", scope, _element_);
            continue;
        }
        return;
    }

    private static void setJavaClassesVisibleInvisibleSandbox(Context cx)
    {
        cx.setClassShutter(new ClassShutter()
        {
            public boolean visibleToScripts(String className)
            {
                // No Java classes allowed inside scripts
                return false;
            }
        });
    }

    private static void loadModulesFromIncludeDirs(Context cx, Scriptable scope, List<File> requireDirList)
    {
        List<URI> uris = new ArrayList<URI>();
        for (File dir : requireDirList) {
            URI uri = dir.toURI();
            uris.add(uri);
            continue;
        }
        if (uris.isEmpty()) {
            return;
        }
        RequireBuilder rb = new RequireBuilder();
        rb.setModuleScriptProvider(new SoftCachingModuleScriptProvider(
                new UrlModuleSourceProvider(uris, null)));
        Require require = rb.createRequire(cx, scope);
        require.install(scope);
        return;
    }

    public static void loadJs(Context cx, Scriptable scope, String filename)
    {
        cx.evaluateString(scope, "load(\"" + filename + "\");", "<load>", 1,
                null);
        cx.setOptimizationLevel(9);
    }
}