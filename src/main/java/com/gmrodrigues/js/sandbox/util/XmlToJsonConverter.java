package com.gmrodrigues.js.sandbox.util;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.xml.XMLSerializer;

import java.io.File;

public class XmlToJsonConverter
{
    private static final XMLSerializer xmlSerializer = new XMLSerializer();

    private XmlToJsonConverter(){}

    public static JSON getJsonFromXmlFile(File fromFile)
    {
        JSON json = xmlSerializer.readFromFile(fromFile);
        return getJSONObject(json);
    }

    public static JSON getJsonFromXmlString(String xml)
    {
        JSON json = xmlSerializer.read(xml);
        return getJSONObject(json);
    }

    private static JSON getJSONObject(JSON json)
    {
        if (json instanceof JSONArray) {
            JSONArray ja = (JSONArray) json;
            if (ja.size() == 1) {
                JSON element = ja.getJSONObject(0);
                return element;
            }
        }
        return json;
    }
}
