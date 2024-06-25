package com.events.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component(service = RestClientUtil.class, immediate = false)
public class RestClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientUtil.class);

    @Reference
    private HttpClientBuilderFactory httpClientBuilderFactory;

    public String post(String serviceUrl, String resourceUrl, Object payload,
                              Map<String, String> headerMap) throws IOException, URISyntaxException {
        try {
            HttpClientBuilder builder = httpClientBuilderFactory.newBuilder();
            builder.setDefaultRequestConfig(initRequestConfig());
            builder.setDefaultHeaders(buildHeaders(headerMap));
            final CloseableHttpClient client = builder.build();
            final StringEntity jsonString = new StringEntity(marshalRequest(payload),"UTF-8");
            jsonString.setContentType("application/json");
            HttpPost httpPost = new HttpPost(buildURI(serviceUrl, resourceUrl));
            httpPost.setEntity(jsonString);
            LOGGER.debug("RestClientUtil.post(): After Marshalling Payload Json:[{}] ", EntityUtils.toString(jsonString));
            LOGGER.debug("SERVICE_NAME" + serviceUrl + " :: " + "Json String = " + jsonString);
            HttpResponse response = client.execute(httpPost);
            handleResponse(response);
            return EntityUtils.toString(response.getEntity());
        } catch (final ProcessingException e) {
            LOGGER.error("Connection Exception while executing post.", e);
            if (e.getCause().getClass().isInstance(ConnectException.class)) {
                LOGGER.error("Error while connecting to  end point for :::", e);
                throw new IllegalStateException("Connection Timeout Exception");
            }
            throw new IllegalStateException(e);
        }
    }

    private RequestConfig initRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(Math.toIntExact(TimeUnit.SECONDS.toMillis(6000)))
                .setSocketTimeout(Math.toIntExact(TimeUnit.SECONDS.toMillis(6000)))
                .setConnectionRequestTimeout(
                        Math.toIntExact(TimeUnit.SECONDS.toMillis(6000)))
                .build();
    }

    private List<Header> buildHeaders(final Map<String, String> headerMap) {
        List<Header> headers = new ArrayList<>();
        if (MapUtils.isNotEmpty(headerMap)) {
            headerMap.forEach((key, value) -> {
                headers.add(new BasicHeader(key, value));
            });
        }
        return headers;
    }

    private  <T> String marshalRequest(final T object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(object);
    }

    private  void handleResponse(final HttpResponse response) throws IOException {
        int responseCode = response.getStatusLine().getStatusCode();
        LOGGER.debug("Response code after REST API call: [{}]", responseCode);
        if (responseCode != 200 && responseCode != 201) {
            String body = EntityUtils.toString(response.getEntity());
            LOGGER.info("The body with the error is [{}], reason phrase is:[{}] ", body,
                    response.getStatusLine().getReasonPhrase());
            throw new HttpResponseException(responseCode, response.getStatusLine().getReasonPhrase());
        }
    }

    private URI buildURI(String host, String path) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(host);
        builder.setPath(path);
        return builder.build();
    }
}
