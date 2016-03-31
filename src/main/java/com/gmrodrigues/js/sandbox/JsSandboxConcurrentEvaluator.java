package com.gmrodrigues.js.sandbox;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsSandboxConcurrentEvaluator implements
        JsSandboxEvaluator
{
    private String source = "";

    private Map<String, File> maps = new ConcurrentHashMap<String, File>();
    private String scriptName = "js-sandbox";
    private List<File> requireDirList = Collections.synchronizedList(new ArrayList<File>());

    private Map<String, ThreadContext> threadContexts = new ConcurrentHashMap<String, ThreadContext>();

    private JsSandboxConcurrentEvaluator()
    {
    }

    public static JsSandboxEvaluator newInstance()
    {
        return new JsSandboxConcurrentEvaluator();
    }

    public class ThreadContext
    {
        public JsSandboxEnvironment env;
        public Context cx;
        public Scriptable scope;
        public Script script;

        public ThreadContext()
        {
            env = new JsSandboxEnvironment();
            cx = env.getContext();
            scope = env.getScope();
        }
    }

    private ThreadContext getThreadContext()
    {
        String tname = Thread.currentThread().getName();
        ThreadContext tc = threadContexts.get(tname);
        if (tc == null) {
            tc = new ThreadContext();
            threadContexts.put(tname, tc);
        }
        return tc;
    }

    @Override
    public void putVar(String name, Object object)
    {
        ThreadContext tc = getThreadContext();
        JsSandboxEvaluators.putVar(tc.scope, name, object);
    }

    @Override
    public <T> T getVar(String name)
    {
        ThreadContext tc = getThreadContext();
        return JsSandboxEvaluators.<T>getVar(tc.scope, name);
    }

    @Override
    public void addClass(Class clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        ThreadContext tc = getThreadContext();
        JsSandboxEvaluators.addClass(tc.scope, clazz);
    }

    @Override
    public Object exec()
    {
        ThreadContext tc = getThreadContext();
        if (tc.script == null) {
            tc.script = JsSandboxEvaluators.compile(tc.cx, tc.scope, maps,
                    requireDirList, source, scriptName);
        }
        return JsSandboxEvaluators.exec(tc.script, tc.cx, tc.scope, source, scriptName);
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
        synchronized (source) {
            return source;
        }
    }

    @Override
    public void setSource(String source)
    {
        synchronized (this.source) {
            this.source = source;
            getThreadContext().script = null;
            return;
        }
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
        ThreadContext tc = getThreadContext();
        JsSandboxEvaluators.addRequirePath(dir, tc.env.funcs.loadOnDirs, requireDirList);
    }

    @Override
    public void loadJs(String filename)
    {
        ThreadContext tc = getThreadContext();
        JsSandboxEvaluators.loadJs(tc.cx, tc.scope, filename);
    }
}
