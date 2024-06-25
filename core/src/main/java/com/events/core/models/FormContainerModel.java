package com.events.core.models;

import com.adobe.cq.wcm.core.components.models.form.Container;
import lombok.experimental.Delegate;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;


@Model(adaptables = SlingHttpServletRequest.class, adapters = Container.class, resourceType = "aem-events/components/form/container", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FormContainerModel implements Container {

    @Self
    @Via(type = ResourceSuperType.class)
    @Delegate(excludes = DelegationExclusion.class)
    private Container delegate;

    @ValueMapValue
    private String actionType;

    @Self
    private SlingHttpServletRequest request;

    private interface DelegationExclusion {
        String getAction();
    }

    @Override
    public String getAction() {
        return request.getRequestPathInfo().getResourcePath().concat(".").concat(actionType).concat(".json");
    }
}
