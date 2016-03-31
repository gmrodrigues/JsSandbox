package com.gmrodrigues.js.sandbox;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsSandboxFastEvaluator implements JsSandboxEvaluator
{
    private String source = "";

    private Map<String, File> maps = new HashMap<String, File>();
    private String scriptName = "js-sandbox";
    private List<File> requireDirList = new ArrayList<File>();

    private JsSandboxEnvironment env = new JsSandboxEnvironment();
    private Context cx = env.getContext();
    private Scriptable scope = env.getScope();
    private Script script;

    private JsSandboxFastEvaluator(){}

    public static JsSandboxEvaluator newInstance()
    {
        return new JsSandboxFastEvaluator();
    }

    private void compile()
    {
        script = JsSandboxEvaluators.compile(cx, scope, maps, requireDirList, source, scriptName);
    }

    @Override
    public void putVar(String name, Object object)
    {
        JsSandboxEvaluators.putVar(scope, name, object);
    }

    @Override
    public <T> T getVar(String name)
    {
        return JsSandboxEvaluators.<T>getVar(scope, name);
    }

    @Override
    public void addClass(Class clazz) throws IllegalAccessException,
            InstantiationException, InvocationTargetException
    {
        JsSandboxEvaluators.addClass(scope, clazz);
    }

    @Override
    public Object exec()
    {
        if (script == null) {
            compile();
        }
        return JsSandboxEvaluators.exec(script, cx, scope, source, scriptName);
    }

    @Override
    public String getScriptName()
    {
        return scriptName;
    }

    @Override
    public void setScriptName(String scriptName)
    {
        this.scriptName = scriptName;
    }

    @Override
    public String getSource()
    {
        return source;
    }

    @Override
    public void setSource(String source)
    {
        this.source = source;
        this.script = null;
    }

    @Override
    public Map<String, File> getMaps()
    {
        return maps;
    }

    @Override
    public void addXmlMapFile(String varname, File file)
    {
        JsSandboxEvaluators.addXmlMapFile(varname, file, maps);
    }

    @Override
    public void addRequirePath(File dir)
    {
        JsSandboxEvaluators.addRequirePath(dir, env.funcs.loadOnDirs, requireDirList);
    }

    @Override
    public void loadJs(String filename)
    {
        JsSandboxEvaluators.loadJs(cx, scope, filename);
    }
}
