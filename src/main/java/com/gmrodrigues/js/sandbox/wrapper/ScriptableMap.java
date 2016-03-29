package com.gmrodrigues.js.sandbox.wrapper;

import org.mozilla.javascript.Scriptable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScriptableMap implements Scriptable, Map
{
    public final Map<String, Object> map;

    @Override
    public String toString()
    {
        return "ScriptableMap " + map;
    }

    public ScriptableMap()
    {
        this(new HashMap());
    }

    public ScriptableMap(Map<String, Object> map)
    {
        this.map = map;
    }

    public void clear()
    {
        map.clear();
    }

    public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    public Set entrySet()
    {
        return map.entrySet();
    }

    public boolean equals(Object o)
    {
        return map.equals(o);
    }

    public Object get(Object key)
    {
        Object o = map.get(key);
        if (o == null) {
            return "";
        }
        return o;
    }

    public int hashCode()
    {
        return map.hashCode();
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public Set keySet()
    {
        return map.keySet();
    }

    public Object put(Object key, Object value)
    {
        return map.put(key.toString(), value);
    }

    public void putAll(Map m)
    {
        map.putAll(m);
    }

    public Object remove(Object key)
    {
        return map.remove(key);
    }

    public int size()
    {
        return map.size();
    }

    public Collection values()
    {
        return map.values();
    }

    @Override
    public void delete(String name)
    {
        map.remove(name);
    }

    @Override
    public void delete(int index)
    {
        map.remove(index);
    }

    @Override
    public Object get(String name, Scriptable start)
    {
        return map.get(name);
    }

    @Override
    public Object get(int index, Scriptable start)
    {
        return map.get(index);
    }

    @Override
    public String getClassName()
    {
        return map.getClass().getName();
    }

    @Override
    public Object getDefaultValue(Class<?> hint)
    {
        return toString();
    }

    @Override
    public Object[] getIds()
    {
        Object[] res = new Object[map.size()];
        int i = 0;
        for (Object k : map.keySet()) {
            res[i] = k;
            i++;
        }
        return res;
    }

    @Override
    public Scriptable getParentScope()
    {
        return null;
    }

    @Override
    public Scriptable getPrototype()
    {
        return null;
    }

    @Override
    public boolean has(String name, Scriptable start)
    {
        return map.containsKey(name);
    }

    @Override
    public boolean has(int index, Scriptable start)
    {
        return map.containsKey(index);
    }

    @Override
    public boolean hasInstance(Scriptable instance)
    {
        return false;
    }

    @Override
    public void put(String name, Scriptable start, Object value)
    {
        map.put(name, value);
    }

    @Override
    public void put(int index, Scriptable start, Object value)
    {
        map.put(Integer.toString(index), value);
    }

    @Override
    public void setParentScope(Scriptable parent)
    {
    }

    @Override
    public void setPrototype(Scriptable prototype)
    {
    }
}
