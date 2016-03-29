package com.gmrodrigues.js.sandbox.wrapper;

import org.mozilla.javascript.Scriptable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MyScriptableList implements Scriptable, List
{
    public final List<Object> list;

    public MyScriptableList()
    {
        this.list = new ArrayList();
    }

    public MyScriptableList(List<Object> list)
    {
        this.list = list;
    }

    @Override
    public String toString()
    {
        return "ScriptableList " + list;
    }

    @Override
    public boolean add(Object e)
    {
        return list.add(e);
    }

    @Override
    public void add(int index, Object element)
    {
        list.add(index, element);
    }

    @Override
    public boolean addAll(Collection c)
    {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection c)
    {
        return list.addAll(index, c);
    }

    @Override
    public void clear()
    {
        list.clear();
    }

    @Override
    public boolean contains(Object o)
    {
        return list.contains(o);
    }

    @Override
    public boolean containsAll(Collection c)
    {
        return list.containsAll(c);
    }

    @Override
    public Object get(int index)
    {
        return list.get(index);
    }

    @Override
    public int indexOf(Object o)
    {
        return list.indexOf(o);
    }

    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    @Override
    public Iterator iterator()
    {
        return list.iterator();
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator()
    {
        return list.listIterator();
    }

    @Override
    public ListIterator listIterator(int index)
    {
        return list.listIterator(index);
    }

    @Override
    public boolean remove(Object o)
    {
        return list.remove(o);
    }

    @Override
    public Object remove(int index)
    {
        return list.remove(index);
    }

    @Override
    public boolean removeAll(Collection c)
    {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection c)
    {
        return list.retainAll(c);
    }

    @Override
    public Object set(int index, Object element)
    {
        return list.set(index, element);
    }

    @Override
    public int size()
    {
        return list.size();
    }

    @Override
    public List subList(int fromIndex, int toIndex)
    {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray()
    {
        return list.toArray();
    }

    @Override
    public Object[] toArray(Object[] a)
    {
        return list.toArray(a);
    }

    @Override
    public void delete(String key)
    {
        list.remove(key);
    }

    @Override
    public void delete(int index)
    {
        list.remove(index);
    }

    @Override
    public Object get(String strIndex, Scriptable arg1)
    {
        try {
            return list.get(new Integer(strIndex));
        }
        catch (NumberFormatException e) {
            if ("length".equals(strIndex)) {
                return list.size();
            }
            if ("push".equals(strIndex)) {
                return null;
            }
            throw new InvalidParameterException("Attr " + strIndex + "(" + arg1 + ") does not exist in Object " + this.getClassName());
        }
    }

    @Override
    public Object get(int index, Scriptable arg1)
    {
        return list.get(index);
    }

    @Override
    public String getClassName()
    {
        return list.getClass().getName();
    }

    @Override
    public Object getDefaultValue(Class<?> arg0)
    {
        return toString();
    }

    @Override
    public Object[] getIds()
    {
        Object[] indexes = new Object[list.size()];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = 1;
        }
        return indexes;
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
    public boolean has(String strIndex, Scriptable arg1)
    {
        try {
            return has(Integer.valueOf(strIndex), arg1);
        }
        catch (NumberFormatException e) {
            throw new InvalidParameterException("Attr " + strIndex + " does not exist in Object " + this.getClassName());
        }
    }

    @Override
    public boolean has(int index, Scriptable arg1)
    {
        return index < list.size();
    }

    @Override
    public boolean hasInstance(Scriptable arg0)
    {
        return false;
    }

    @Override
    public void put(String strIndex, Scriptable arg1, Object element)
    {
        put(Integer.valueOf(strIndex), arg1, element);
    }

    @Override
    public void put(int index, Scriptable arg1, Object element)
    {
        while (index >= list.size()) {
            list.add(null);
        }
        list.set(index, element);
    }

    @Override
    public void setParentScope(Scriptable arg0)
    {
    }

    @Override
    public void setPrototype(Scriptable arg0)
    {
    }

    public void push()
    {
    }
}
