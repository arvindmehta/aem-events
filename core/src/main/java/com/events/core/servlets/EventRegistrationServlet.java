package com.events.core.servlets;

import com.day.cq.commons.jcr.JcrUtil;
import com.events.core.pojo.EventRegistrationDTO;
import com.events.core.services.EventRegistrationService;
import com.events.core.utils.CRXUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.value.StringValue;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.apache.sling.xss.XSSAPI;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "aem-events/components/form/container",
        methods = HttpConstants.METHOD_POST,selectors="register-event", extensions = "json")
@ServiceDescription("Event Registration Servlet")
public class EventRegistrationServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventRegistrationServlet.class);

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";

    private static final String REGEX_EMAIL = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private static final Pattern VALID_TEXT_FIELD_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(REGEX_EMAIL, Pattern.CASE_INSENSITIVE);
    //Setting This as a Hidden Input Field in a Form
    public static final String NODE_TO_STORE_API_RESPONSE = "nodeToStoreAPIResponse";

    @Reference
    EventRegistrationService eventRegistrationService;

    @Reference
    XSSAPI xssapi;

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        LOGGER.debug("Enter doPost method for {0} of EventRegistrationServlet Post method");
        EventRegistrationDTO eventRegistrationDTO=new EventRegistrationDTO();
        if (StringUtils.isNotBlank(request.getParameter(FIRST_NAME)) && validateTextFields(request.getParameter(FIRST_NAME))){
            eventRegistrationDTO.setFirstName(xssapi.filterHTML(request.getParameter(FIRST_NAME)));
            LOGGER.debug("First Name is {}", request.getParameter(FIRST_NAME));
        }
        if (StringUtils.isNotBlank(request.getParameter(LAST_NAME)) && validateTextFields(request.getParameter(LAST_NAME))) {
            eventRegistrationDTO.setLastName(xssapi.filterHTML(request.getParameter(LAST_NAME)));
            LOGGER.debug("Last Name is {}", request.getParameter(LAST_NAME));
        }
        if (StringUtils.isNotBlank(request.getParameter(EMAIL)) && validateEmail(request.getParameter(EMAIL))) {
            eventRegistrationDTO.setEmail(xssapi.filterHTML(request.getParameter(EMAIL)));
            LOGGER.debug("Email Address is {}", request.getParameter(EMAIL));
        }
        String apiResponse=eventRegistrationService.registerEvent(request,eventRegistrationDTO);
        if (StringUtils.isNotBlank(apiResponse) && apiResponse.contains("status\": \"Success")) {
            response.setStatus(HttpServletResponse.SC_OK);
            try {
                saveResponseInJCR(request,apiResponse);
            } catch (RepositoryException | LoginException ex) {
                LOGGER.error("Unable to save API response in JCR:[{}]", ex.getMessage(), ex);
            }
            response.sendRedirect(request.getResourceResolver().map(request.getParameter(":redirect")));
        }
    }

    public static boolean validateEmail(final String email) {
        final Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    private static boolean validateTextFields(final String s) {
        return VALID_TEXT_FIELD_PATTERN.matcher(s).matches();
    }

    private static void saveResponseInJCR(SlingHttpServletRequest request, String apiResponse) throws RepositoryException, LoginException {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "getResourceResolver");
        final ResourceResolverFactory resourceResolverFactory = CRXUtil.getServiceReference(ResourceResolverFactory.class);
        ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(param);
        final Session session =resolver.adaptTo(Session.class);
        Node pageNode = JcrUtil.createPath(request.getParameter(NODE_TO_STORE_API_RESPONSE) + Math.random(), "nt:unstructured", session);
        LOGGER.debug("API Response is {}", apiResponse);
        pageNode.setProperty("apiResponse", new StringValue(apiResponse));
        session.save();
        LOGGER.info("API Response saved in JCR");
    }

}
