package com.ppk.cuke.bdd.support;

import java.io.InputStream;

public final class CucumberTestUtil {

	public static InputStream getInputStreamFromFile(String fileName) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream(fileName);
		return stream;
	}

}
