package com.ppk.cuke.bdd.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CucumberRestContext extends CucumberContext {
	public static String service_url;
	public static String base_url;
	public static String method;
	public static String postBody;
	public static WebResource webresource;
	public static ClientResponse clientResponse;
	public static String restResponse;
	public static List<Map<String, String>> headerMapList = new ArrayList<>();
	public static String legacyHeader;
}
