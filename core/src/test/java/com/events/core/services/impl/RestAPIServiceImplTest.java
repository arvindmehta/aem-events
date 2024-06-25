package com.events.core.services.impl;

import com.events.core.services.RestAPIService;
import com.events.core.servlets.EventRegistrationServlet;
import com.events.core.utils.RestClientUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class RestAPIServiceImplTest {

    public AemContext context = new AemContext();

    @InjectMocks
    RestClientUtil restClientUtil;

    @Mock
    RestAPIService restAPIService;

    @Mock
    HttpClientBuilderFactory httpClientBuilderFactory;

    @Mock
    CloseableHttpClient closeableHttpClientMock;

    @Mock
    CloseableHttpResponse closeableHttpResponseMock;

    @Mock
    URIBuilder uriBuilder;

    RestAPIServiceImpl underTest;

    HttpClientBuilder builder;


    public static final String EVENT_REGISTRATION_SUCCESS = "{ \"status\": \"Success\", \"errors\": [], \"data\": { \"message\": \"You have been successfully registered for the event \" } }";

    @BeforeEach
    public void setUp() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("serviceEndpoint", "https://3682z.wiremockapi.cloud");
        context.registerService(RestClientUtil.class, restClientUtil);
        context.registerService(HttpClientBuilderFactory.class, httpClientBuilderFactory);
        builder = httpClientBuilderFactory.newBuilder();
        underTest = context.registerInjectActivateService(new RestAPIServiceImpl(), properties);
    }

    @Disabled
    @Test
    void testPostResponse_EventRegistration() throws Exception {
        Map<String, String> headersMap = new HashMap<>();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("firstName", "John");
        payload.put("lastName", "Doe");
        payload.put("email", "john.doe@yopmail.com");

        String apiResponse = EVENT_REGISTRATION_SUCCESS;
        when(httpClientBuilderFactory.newBuilder()).thenReturn(mock(HttpClientBuilder.class));
        when(httpClientBuilderFactory.newBuilder().build()).thenReturn(closeableHttpClientMock);
        when(closeableHttpClientMock.execute(any())).thenReturn(closeableHttpResponseMock);
        lenient().when(restClientUtil.post("https://3682z.wiremockapi.cloud","/api/register-event", payload, headersMap)).thenReturn(apiResponse);

        String response = underTest.postResponse("/api/register-event",
                headersMap, payload, context.request());
        Gson gson=new Gson();
        JsonObject apiResponseObject = gson.fromJson(response, JsonObject.class);
        assertEquals(200, apiResponseObject.get("status"));
        assertEquals(apiResponse, response);
    }

    @Test
    @Disabled
    void testPostResponse_Applicant_Error_HttpResponseException() throws Exception {
        Map<String, String> headersMap = new HashMap<>();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("firstName", "John");
        payload.put("lastName", "Doe");
        payload.put("email", "john.doe@yopmail.com");
        when(restClientUtil.post("https://3682z.wiremockapi.cloud","/api/register-event", payload, headersMap))
                .thenThrow(new HttpResponseException(500, "Reason"));
        MockSlingHttpServletRequest request = context.request();
        assertThrows(Exception.class,
                () -> underTest.postResponse("/api/register-event",
                        headersMap, payload, context.request()));
    }

    @Test
    @Disabled
    void testPostResponse_Applicant_Error_URISyntaxException() throws Exception {
        Map<String, String> headersMap = new HashMap<>();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("firstName", "John");
        payload.put("lastName", "Doe");
        payload.put("email", "john.doe@yopmail.com");
        when(restClientUtil.post("https://3682z.wiremockapi.cloud","/api/register-event", payload, headersMap))
                .thenThrow(new URISyntaxException("", "reason"));
        MockSlingHttpServletRequest request = context.request();
        assertThrows(Exception.class,
                () -> underTest.postResponse("/api/register-event",
                        headersMap, payload, context.request()));
    }

}
