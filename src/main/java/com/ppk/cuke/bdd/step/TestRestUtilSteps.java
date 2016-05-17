package com.ppk.cuke.bdd.step;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import com.ppk.cuke.bdd.support.RestTestConstants;
import com.ppk.cuke.bdd.support.CucumberRestContext;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class TestRestUtilSteps {

	@Given("^I use \"([^\"]*)\" service$")
	public void i_use_service(String service) throws Throwable {
		String hostName = CucumberRestContext.prop.getProperty("ms.hostName");
		String context = CucumberRestContext.prop.getProperty("ms.context");
		CucumberRestContext.service_url = hostName + context + service;
	}

	@When("^I make \"([^\"]*)\" api \"([^\"]*)\" call$")
	public void i_make_api_call(String method, String endpoint) throws Throwable {
		CucumberRestContext.method = method.toUpperCase();
		CucumberRestContext.base_url = CucumberRestContext.service_url + endpoint;
		CucumberRestContext.webresource = Client.create().resource(CucumberRestContext.base_url);
	}

	@When("^I provide request parameter \"([^\"]*)\" as \"([^\"]*)\"$")
	public void i_provide_parameter_as(String key, String value) throws Throwable {
		CucumberRestContext.webresource = CucumberRestContext.webresource.queryParam(key, value);
	}

	@When("^a postBody as:$")
	public void add_postBody_as(String postBody) throws Throwable {
		CucumberRestContext.postBody = postBody;
	}

	@When("^add below headers:$")
	public void add_below_headers(DataTable headerTable) throws Throwable {
		List<Map<String, String>> headerMaps = headerTable.asMaps(String.class, String.class);
		if (headerMaps != null && headerMaps.size() > 0) {
			CucumberRestContext.headerMapList = headerMaps;
		}
	}

	@When("^I retrieve (JSON|XML|TXT|HTML) results$")
	public void i_retrieve_results(String resourceType) throws Throwable {
		System.out.println(CucumberRestContext.method + " - " + CucumberRestContext.webresource.getURI() + ", headers -"
				+ CucumberRestContext.headerMapList);

		Builder requestBuilder = CucumberRestContext.webresource.getRequestBuilder();
		for (Map<String, String> headerMap : CucumberRestContext.headerMapList) {
			requestBuilder = requestBuilder.header(headerMap.get(RestTestConstants.HEADER_NAME),
					headerMap.get(RestTestConstants.HEADER_VALUE));
		}
		// add a header that we always send
		requestBuilder = requestBuilder.header("client-tranid", "cucumber-agent");
		if (null != CucumberRestContext.legacyHeader) {
			requestBuilder.header("Authorization", CucumberRestContext.legacyHeader);
		}
		CucumberRestContext.clientResponse = requestBuilder.method(CucumberRestContext.method, ClientResponse.class,
				CucumberRestContext.postBody);
		CucumberRestContext.restResponse = CucumberRestContext.clientResponse.getEntity(String.class);
		CucumberRestContext.postBody = null;
		CucumberRestContext.headerMapList = new ArrayList<>();
		System.out.println(CucumberRestContext.restResponse);
	}

	@Then("^expect a status code (\\d+)$")
	public void the_status_code_should_be(int status) throws Throwable {
		System.out
				.println("RestTestContext.clientResponse = " + CucumberRestContext.clientResponse + " status = " + status);
		assertEquals(status, CucumberRestContext.clientResponse.getStatus());
	}

	@Then("^The response should contain \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void the_response_should_contain_with_value(String achvAttrPathToCheck, String valueExpected)
			throws Throwable {
		List<String> achvAttrValuesActual = JsonPath.parse(CucumberRestContext.restResponse).read(achvAttrPathToCheck,
				new TypeRef<List<String>>() {
				});
		assertTrue("Response does not contain the element to be validated - " + achvAttrPathToCheck,
				!achvAttrValuesActual.isEmpty());
		for (String achvAttrValueActual : achvAttrValuesActual) {
			assertEquals(valueExpected, achvAttrValueActual);
		}
	}

	@Then("^The response should contain \"([^\"]*)\" with json value:$")
	public void the_response_should_contain_with_json_value(String achvAttrPathToCheck, String valueExpected)
			throws Throwable {
		List<String> achvAttrValuesActual = JsonPath.parse(CucumberRestContext.restResponse).read(achvAttrPathToCheck,
				new TypeRef<List<String>>() {
				});
		assertTrue("Response does not contain the element to be validated - " + achvAttrPathToCheck,
				!achvAttrValuesActual.isEmpty());
		ObjectMapper mapper = new ObjectMapper();
		Map expectedJsonObj = mapper.readValue(valueExpected, Map.class);
		for (String achvAttrValueActual : achvAttrValuesActual) {
			Map actualJsonObj = mapper.readValue(achvAttrValueActual, Map.class);
			assertEquals(expectedJsonObj, actualJsonObj);
		}
	}

	@Then("^The response should contain \"([^\"]*)\"$")
	public void the_response_should_contain(String searchString) throws Throwable {
		assertTrue(StringUtils.contains(CucumberRestContext.restResponse, searchString));
	}

	@Then("^The response should contain \"([^\"]*)\" with values:$")
	public void the_response_should_contain_with_values(String achvAttrPathToCheck, DataTable valueExpected)
			throws Throwable {
		List<String> achvAttrValuesActual = JsonPath.parse(CucumberRestContext.restResponse).read(achvAttrPathToCheck);
		List<String> achvAttrValuesExpected = valueExpected.topCells();
		assertThat(achvAttrValuesActual, contains(achvAttrValuesExpected.toArray()));
	}

	@Then("^The response should contain \"([^\"]*)\" is empty array$")
	public void the_response_should_contain_is_empty_array(String arrayPath) throws Throwable {
		List<Object> achvAttrValuesActual = JsonPath.parse(CucumberRestContext.restResponse).read(arrayPath,
				new TypeRef<List<Object>>() {
				});
		assertThat(achvAttrValuesActual, emptyIterable());
	}

	@Then("^The response should contain \"([^\"]*)\" contains (\\d+) elements$")
	public void the_response_should_contain_contains_elements(String arrayPath, int arraySize) throws Throwable {
		List<Object> achvAttrValuesActual = JsonPath.parse(CucumberRestContext.restResponse).read(arrayPath,
				new TypeRef<List<Object>>() {
				});
		assertThat(achvAttrValuesActual, iterableWithSize(arraySize));
	}

	@Then("^The response is empty$")
	public void the_response_is_empty() throws Throwable {
		assertThat(CucumberRestContext.restResponse, isEmptyOrNullString());
	}

}
