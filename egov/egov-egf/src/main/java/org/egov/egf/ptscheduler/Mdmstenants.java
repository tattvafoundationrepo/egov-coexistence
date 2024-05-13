package org.egov.egf.ptscheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

@Component
public class Mdmstenants {
	
	private static final String API_URL = "https://nagarsewa.uk.gov.in/egov-mdms-service/v1/_search?tenantId=uk";

	 public static String consumeApi() throws IOException {
	        CloseableHttpClient httpClient = HttpClients.createDefault();
	        HttpPost postRequest = new HttpPost(API_URL);
	        postRequest.addHeader("Content-Type", "application/json");

	        Map<String, Object> requestBody = new HashMap();
	        requestBody.put("RequestInfo", new HashMap<String, Object>() {{
	            put("apiId", "Rainmaker");
	            put("ver", ".01");
	            put("ts", "");
	            put("action", "_search");
	            put("did", "1");
	            put("key", "");
	            put("msgId", "20170310130900|hi_IN");
	            put("authToken", null);
	        }});
	        requestBody.put("MdmsCriteria", new HashMap<String, Object>() {{
	            put("tenantId", "uk");
	            put("moduleDetails", new Object[]{
	                    new HashMap<String, Object>() {{
	                        put("moduleName", "common-masters");
	                        put("masterDetails", new Object[]{
	                                new HashMap<String, Object>() {{
	                                    put("name", "StateInfo");
	                                }}
	                        });
	                    }},
	                    new HashMap<String, Object>() {{
	                        put("moduleName", "tenant");
	                        put("masterDetails", new Object[]{
	                                new HashMap<String, Object>() {{
	                                    put("name", "tenants");
	                                }},
	                                new HashMap<String, Object>() {{
	                                    put("name", "citymodule");
	                                }}
	                        });
	                    }}
	            });
	        }});

	        ObjectMapper mapper = new ObjectMapper();
	        String jsonRequestBody = mapper.writeValueAsString(requestBody);
	        StringEntity entity = new StringEntity(jsonRequestBody);
	        postRequest.setEntity(entity);

	        CloseableHttpResponse response = httpClient.execute(postRequest);
	        HttpEntity responseEntity = response.getEntity();

	        if (response.getStatusLine().getStatusCode() == 200) {
	            InputStream inputStream = responseEntity.getContent();
	            String responseData = org.apache.commons.io.IOUtils.toString(inputStream, StandardCharsets.UTF_8);
	            // Log or print the response data
	            System.out.println("API response data: " + responseData);

	            // Extract codes from the response data
	            JsonNode rootNode = mapper.readTree(responseData);
	            JsonNode tenantsNode = rootNode.path("MdmsRes").path("tenant").path("tenants");
	            StringBuilder codesBuilder = new StringBuilder();
	            for (JsonNode tenant : tenantsNode) {
	                String code = tenant.path("code").asText();
	                codesBuilder.append(code).append(", ");
	            }
	            String codes = codesBuilder.toString().trim();
	            System.out.println("Extracted codes: " + codes);
	            return codes;
	        } else {
	            throw new RuntimeException("API request failed with status code: " + response.getStatusLine().getStatusCode());
	        }
	    }
	}