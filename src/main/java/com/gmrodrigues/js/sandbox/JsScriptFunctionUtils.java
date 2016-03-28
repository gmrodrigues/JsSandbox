package com.gmrodrigues.js.sandbox;

import com.gmrodrigues.dirurils.DirUtils;
import com.gmrodrigues.js.sandbox.annotation.JsScriptFuntion;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.tools.shell.Global;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class JsScriptFunctionUtils extends ScriptableObject
{

    private static final long serialVersionUID = 6913045431710599245L;

    public static final List<File> loadOnDirs = new ArrayList<File>();
    public static final List<File> readOnDirs = new ArrayList<File>();

    public static Map<String, Boolean> permited = new HashMap<String, Boolean>();

    @Override
    public String getClassName()
    {
        return this.getClassName();
    }

    @JsScriptFuntion
    public static Object print(Context cx, Scriptable thisObj, Object[] args,
                               Function funObj)
    {
        PrintStream out = System.out;
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                out.print(" ");
            }

            // Convert the arbitrary JavaScript value into a string form.
            String s = Context.toString(args[i]);

            out.print(args[i]);
        }
        out.println();
        return Context.getUndefinedValue();
    }

    @JsScriptFuntion
    public static void load(Context cx, Scriptable thisObj, Object[] args,
                            Function funObj)
    {
        List<File> dirList = loadOnDirs;
        for (int i = 0; i < args.length; i++) {
            String filename = Context.toString(args[i]);
            if (!isPathInsideOneOfListedDirs(dirList, filename)) {
                throw new RuntimeException("File " + filename
                        + " must be inside on one of those dirs: " + dirList);
            }
            cx.setOptimizationLevel(-1);
            processSource(cx, thisObj, filename);
        }
    }


    @JsScriptFuntion
    public static Object readUrl(Context cx, Scriptable thisObj, Object[] args,
                                 Function funObj) throws IOException
    {
        return Global.readUrl(cx, thisObj, args, funObj);
    }

    @JsScriptFuntion
    public static Object readFile(Context cx, Scriptable thisObj,
                                  Object[] args, Function funObj) throws IOException
    {
        List<File> dirList = readOnDirs;
        for (int i = 0; i < args.length; i++) {
            String filename = Context.toString(args[i]);
            if (!isPathInsideOneOfListedDirs(dirList, filename)) {
                throw new RuntimeException("File " + filename
                        + " must be inside on one of those dirs: " + dirList);
            }
        }
        return Global.readFile(cx, thisObj, args, funObj);
    }

    private static boolean isPathInsideOneOfListedDirs(List<File> dirList,
                                                       String filename)
    {
        if (!dirList.isEmpty()) {
            Boolean isPermited = permited.get(filename);
            if (isPermited != null) {
                return isPermited;
            }
            boolean isInsideListedDirs = false;
            File file;
            file = new File(filename);
            for (File dir : dirList) {
                try {
                    if (DirUtils.isFileInsideDirectoryHierarchy(file, dir)) {
                        isInsideListedDirs = true;
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            permited.put(filename, isInsideListedDirs);
            return isInsideListedDirs;
        }
        return true;
    }

    private static void processSource(Context cx, Scriptable scope, String filename)
    {
        if (filename == null) {
            BufferedReader in = new BufferedReader
                    (new InputStreamReader(System.in));
            String sourceName = "<stdin>";
            int lineno = 1;
            boolean hitEOF = false;
            do {
                int startline = lineno;
                System.err.print("js> ");
                System.err.flush();
                try {
                    String source = "";
                    // Collect lines of source to compile.
                    while (true) {
                        String newline;
                        newline = in.readLine();
                        if (newline == null) {
                            hitEOF = true;
                            break;
                        }
                        source = source + newline + "\n";
                        lineno++;
                        // Continue collecting as long as more lines
                        // are needed to complete the current
                        // statement.  stringIsCompilableUnit is also
                        // true if the source statement will result in
                        // any error other than one that might be
                        // resolved by appending more source.
                        if (cx.stringIsCompilableUnit(source)) {
                            break;
                        }
                    }
                    Object result = cx.evaluateString(scope, source,
                            sourceName, startline,
                            null);
                    if (result != Context.getUndefinedValue()) {
                        System.err.println(Context.toString(result));
                    }
                } catch (WrappedException we) {
                    // Some form of exception was caught by JavaScript and
                    // propagated up.
                    System.err.println(we.getWrappedException().toString());
                    we.printStackTrace();
                } catch (EvaluatorException ee) {
                    // Some form of JavaScript error.
                    System.err.println("js: " + ee.getMessage());
                } catch (JavaScriptException jse) {
                    // Some form of JavaScript error.
                    System.err.println("js: " + jse.getMessage());
                } catch (IOException ioe) {
                    System.err.println(ioe.toString());
                }
                boolean quitting = false;
                if (quitting) {
                    // The user executed the quit() function.
                    break;
                }
            } while (!hitEOF);
            System.err.println();
        } else {
            FileReader in = null;
            try {
                in = new FileReader(filename);
            } catch (FileNotFoundException ex) {
                Context.reportError("Couldn't open file \"" + filename + "\".");
                return;
            }

            try {
                // Here we evalute the entire contents of the file as
                // a script. Text is printed only if the print() function
                // is called.
                cx.evaluateReader(scope, in, filename, 1, null);
            } catch (WrappedException we) {
                System.err.println(we.getWrappedException().toString());
                we.printStackTrace();
            } catch (EvaluatorException ee) {
                System.err.println("js: " + ee.getMessage());
            } catch (JavaScriptException jse) {
                System.err.println("js: " + jse.getMessage());
            } catch (IOException ioe) {
                System.err.println(ioe.toString());
            } finally {
                try {
                    in.close();
                } catch (IOException ioe) {
                    System.err.println(ioe.toString());
                }
            }
        }
    }


}
