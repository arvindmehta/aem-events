package com.events.core.services.impl;

import com.events.core.services.RestAPIService;
import com.events.core.utils.RestClientUtil;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpResponseException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


@Component(service = RestAPIService.class, immediate = true, property = {
        org.osgi.framework.Constants.BUNDLE_NAME + "=REST service",
        org.osgi.framework.Constants.SERVICE_DESCRIPTION + "=Service responsible for invoking REST APIs"
})
@Designate(ocd = RestAPIServiceImpl.RestAPIConfig.class)
public class RestAPIServiceImpl implements RestAPIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAPIServiceImpl.class);

    private static String apiHostName;

    @Reference
    private RestClientUtil restClientUtil;

    @Activate
    @Modified
    protected void activate(RestAPIServiceImpl.RestAPIConfig config) {
        LOGGER.info("MuleSoftMSAServiceImpl.activate(): Entered with config:[{}]", config);
        this.apiHostName = config.apiHostName();
    }

    @Override
    public String postResponse(String resourceUrl, Map<String, String> headersMap, Object obj, SlingHttpServletRequest request) {
        LOGGER.info("Start postResponse of RestAPIServiceImpl with url {}", resourceUrl);
        String apiResponse=null;
        try {
            headersMap = new HashMap<>();
            headersMap.put(HttpHeaders.ACCEPT, "application/json");
            headersMap.put(HttpHeaders.CONTENT_TYPE, "application/json");
            apiResponse = restClientUtil.post(apiHostName, resourceUrl, obj, headersMap);
            LOGGER.info("End postResponse of RestAPIServiceImpl with response {}", apiResponse);
        } catch (HttpResponseException e) {
            LOGGER.error("RestAPIServiceImpl.postResponse(), Request failed with status code {}", e);
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("RestAPIServiceImpl.postResponse(), exception occurred , URL:[{}]", resourceUrl, e);
        }
        return apiResponse;
    }

    @ObjectClassDefinition(name = "Rest API Configuration")
    @interface RestAPIConfig {
        @AttributeDefinition(name = "API Host Name", description = "API host name", type = AttributeType.STRING)
        String apiHostName();
    }
}
