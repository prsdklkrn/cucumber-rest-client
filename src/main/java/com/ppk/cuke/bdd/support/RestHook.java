package com.ppk.cuke.bdd.support;

import static com.ppk.cuke.bdd.support.CucumberRestContext.base_url;
import static com.ppk.cuke.bdd.support.CucumberRestContext.clientResponse;
import static com.ppk.cuke.bdd.support.CucumberRestContext.headerMapList;
import static com.ppk.cuke.bdd.support.CucumberRestContext.legacyHeader;
import static com.ppk.cuke.bdd.support.CucumberRestContext.method;
import static com.ppk.cuke.bdd.support.CucumberRestContext.postBody;
import static com.ppk.cuke.bdd.support.CucumberRestContext.restResponse;
import static com.ppk.cuke.bdd.support.CucumberRestContext.service_url;
import static com.ppk.cuke.bdd.support.CucumberRestContext.webresource;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import cucumber.api.java.After;
import cucumber.api.java.Before;

public class RestHook {

	public static final int ORDER = MainHook.ORDER + 1;

	public static void refresh() {
		service_url = null;
		base_url = null;
		method = null;
		postBody = null;
		webresource = null;
		clientResponse = null;
		restResponse = null;
		headerMapList = new ArrayList<>();
		legacyHeader = null;
	}

	@After(order = ORDER)
	public void afterHook() {
		refresh();
	}

	public static void setupJsonConfig() {
		Configuration.setDefaults(new Configuration.Defaults() {

			private final JsonProvider jsonProvider = new JacksonJsonProvider();
			private final MappingProvider mappingProvider = new JacksonMappingProvider();

			@Override
			public JsonProvider jsonProvider() {
				return jsonProvider;
			}

			@Override
			public MappingProvider mappingProvider() {
				return mappingProvider;
			}

			@Override
			public Set<Option> options() {
				return EnumSet.noneOf(Option.class);
			}
		});
	}

	@Before(order = ORDER)
	public void beforeHook() {
		refresh();
	}

}
