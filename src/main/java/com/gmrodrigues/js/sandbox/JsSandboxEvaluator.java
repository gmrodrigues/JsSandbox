package com.gmrodrigues.js.sandbox;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface JsSandboxEvaluator {

	
	public abstract void putVar(String name, Object object);

	public abstract <T> T getVar(String name);

	public abstract void addClass(Class clazz) throws IllegalAccessException,
			InstantiationException, InvocationTargetException;

	/**
	 * executes the script and returns the value of last evaluated line as Object
 	 * @return
	 */
	public abstract Object exec();

	public abstract String getScriptName();

	public abstract void setScriptName(String scriptName);

	public abstract String getSource();

	public abstract void setSource(String source);

	public abstract Map<String, File> getMaps();

	public abstract void addXmlMapFile(String varname, File file);

	public abstract void addRequirePath(File dir);

	public abstract void loadJs(String filename);

}