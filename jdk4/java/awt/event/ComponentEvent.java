/*
 * @(#)ComponentEvent.java	1.25 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.Component;
import java.awt.Rectangle;

/**
 * A low-level event which indicates that a component moved, changed
 * size, or changed visibility (also, the root class for the other 
 * component-level events).
 * <P>
 * Component events are provided for notification purposes ONLY;
 * The AWT will automatically handle component moves and resizes
 * internally so that GUI layout works properly regardless of
 * whether a program is receiving these events or not.
 * <P>
 * In addition to serving as the base class for other component-related
 * events (InputEvent, FocusEvent, WindowEvent, ContainerEvent),
 * this class defines the events that indicate changes in
 * a component's size, position, or visibility. 
 * <P>
 * This low-level event is generated by a component object (such as a 
 * List) when the component is moved, resized, rendered invisible, or made
 * visible again. The event is passed to every <code>ComponentListener</code>
 * or <code>ComponentAdapter</code> object which registered to receive such
 * events using the component's <code>addComponentListener</code> method.
 * (<code>ComponentAdapter</code> objects implement the 
 * <code>ComponentListener</code> interface.) Each such listener object 
 * gets this <code>ComponentEvent</code> when the event occurs.
 *
 * @see ComponentAdapter
 * @see ComponentListener
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/componentlistener.html">Tutorial: Writing a Component Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @author Carl Quinn
 * @version 1.25 01/23/03
 * @since 1.1
 */
public class ComponentEvent extends AWTEvent {

    /**
     * The first number in the range of ids used for component events.
     */
    public static final int COMPONENT_FIRST		= 100;

    /**
     * The last number in the range of ids used for component events.
     */
    public static final int COMPONENT_LAST		= 103;

   /**
     * This event indicates that the component's position changed.
     */
    public static final int COMPONENT_MOVED	= COMPONENT_FIRST;

    /**
     * This event indicates that the component's size changed.
     */
    public static final int COMPONENT_RESIZED	= 1 + COMPONENT_FIRST;

    /**
     * This event indicates that the component was made visible.
     */
    public static final int COMPONENT_SHOWN	= 2 + COMPONENT_FIRST;

    /**
     * This event indicates that the component was rendered invisible.
     */
    public static final int COMPONENT_HIDDEN	= 3 + COMPONENT_FIRST;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 8101406823902992965L;

    /**
     * Constructs a <code>ComponentEvent</code> object.
     * <p>Note that passing in an invalid <code>id</code> results in
     * unspecified behavior.
     *
     * @param source the <code>Component</code> that originated the event
     * @param id     an integer indicating the type of event
     */
    public ComponentEvent(Component source, int id) {
        super(source, id);
    }

    /**
     * Returns the originator of the event.
     *
     * @return the <code>Component</code> object that originated 
     * the event, or <code>null</code> if the object is not a 
     * <code>Component</code>.  
     */
    public Component getComponent() {
        return (source instanceof Component) ? (Component)source : null;
    }

    /**
     * Returns a parameter string identifying this event.
     * This method is useful for event-logging and for debugging.
     *
     * @return a string identifying the event and its attributes
     */
    public String paramString() {
        String typeStr;
        Rectangle b = (source !=null
		       ? ((Component)source).getBounds()
		       : null);

        switch(id) {
          case COMPONENT_SHOWN:
              typeStr = "COMPONENT_SHOWN";
              break;
          case COMPONENT_HIDDEN:
              typeStr = "COMPONENT_HIDDEN";
              break;
          case COMPONENT_MOVED:
              typeStr = "COMPONENT_MOVED ("+ 
                         b.x+","+b.y+" "+b.width+"x"+b.height+")";
              break;
          case COMPONENT_RESIZED:
              typeStr = "COMPONENT_RESIZED ("+ 
                         b.x+","+b.y+" "+b.width+"x"+b.height+")";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr;
    }
}
