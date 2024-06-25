package com.events.core.services;

import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Map;

public interface RestAPIService {
    String postResponse(String resourceUrl, Map<String, String> headersMap, Object obj, SlingHttpServletRequest request);
}
