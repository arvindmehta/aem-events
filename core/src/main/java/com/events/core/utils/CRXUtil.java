package com.events.core.utils;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class CRXUtil {

    public static <T> T getServiceReference(final Class<T> serviceClass) {
        T serviceRef = null;
        /**
         * Get the BundleContext associated with the passed class reference.
         */
        final BundleContext bundleContext = FrameworkUtil.getBundle(serviceClass).getBundleContext();
        final ServiceReference osgiRef = bundleContext.getServiceReference(serviceClass.getName());
        if(null != osgiRef)
            serviceRef = (T) bundleContext.getService(osgiRef);
        return serviceRef;
    }
}
