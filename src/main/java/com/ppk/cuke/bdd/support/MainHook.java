package com.ppk.cuke.bdd.support;

import static com.ppk.cuke.bdd.support.CucumberContext.prop;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import cucumber.api.java.After;
import cucumber.api.java.Before;

public class MainHook {

	public static final int ORDER = 0;

	public static void refresh() {
		prop = null;
	}

	@Before(order = ORDER)
	public void beforeHook() throws Exception {
		refresh();
		loadProperties();
		setupJsonConfig();
	}

	@After(order = ORDER)
	public void afterHook() throws IOException {
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

	public static void loadProperties() throws Exception {
		prop = new Properties();
		InputStream stream = CucumberTestUtil.getInputStreamFromFile("cucumber.properties");
		if (stream != null) {
			prop.load(stream);
		}
		prop.putAll(System.getProperties());
	}

}
