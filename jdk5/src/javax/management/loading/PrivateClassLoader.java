/*
 * @(#)PrivateClassLoader.java	1.12 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.loading;

import javax.management.MBeanServer; // for Javadoc
import javax.management.ObjectName;  // for Javadoc

/**
 * Marker interface indicating that a ClassLoader should not be added
 * to the {@link ClassLoaderRepository}.  When a ClassLoader is
 * registered as an MBean in the MBean server, it is added to the
 * MBean server's ClassLoaderRepository unless it implements this
 * interface.
 *
 * @since 1.5
 * @since.unbundled JMX 1.2
 */
public interface PrivateClassLoader {}
