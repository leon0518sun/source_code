/*
 * @(#)Autoscroller.java	1.14 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.awt.*;
import java.awt.event.*;

/**
 * Autoscroller is responsible for generating synthetic mouse dragged
 * events. It is the responsibility of the Component (or its MouseListeners)
 * that receive the events to do the actual scrolling in response to the
 * mouse dragged events.
 *
 * @version 1.14 12/19/03
 * @author Dave Moore
 * @author Scott Violet
 */
class Autoscroller implements ActionListener {
    /**
     * Global Autoscroller.
     */
    private static Autoscroller sharedInstance = new Autoscroller();

    // As there can only ever be one autoscroller active these fields are
    // static. The Timer is recreated as necessary to target the appropriate
    // Autoscroller instance.
    private static MouseEvent event;
    private static Timer timer;
    private static JComponent component;

    //
    // The public API, all methods are cover methods for an instance method
    //
    /**
     * Stops autoscroll events from happening on the specified component.
     */
    public static void stop(JComponent c) {
        sharedInstance._stop(c);
    }

    /**
     * Stops autoscroll events from happening on the specified component.
     */
    public static boolean isRunning(JComponent c) {
        return sharedInstance._isRunning(c);
    }

    /**
     * Invoked when a mouse dragged event occurs, will start the autoscroller
     * if necessary.
     */
    public static void processMouseDragged(MouseEvent e) {
        sharedInstance._processMouseDragged(e);
    }


    Autoscroller() {
    }

    /**
     * Starts the timer targeting the passed in component.
     */
    private void start(JComponent c, MouseEvent e) {
        Point screenLocation = c.getLocationOnScreen();

        if (component != c) {
            _stop(component);
        }
        component = c;
        event = new MouseEvent(component, e.getID(), e.getWhen(),
                               e.getModifiers(), e.getX() + screenLocation.x,
                               e.getY() + screenLocation.y,
                               e.getClickCount(), e.isPopupTrigger());

        if (timer == null) {
            timer = new Timer(100, this);
        }

        if (!timer.isRunning()) {
            timer.start();
        }
    }

    //
    // Methods mirror the public static API
    //

    /**
     * Stops scrolling for the passed in widget.
     */
    private void _stop(JComponent c) {
        if (component == c) {
            if (timer != null) {
                timer.stop();
            }
            timer = null;
            event = null;
            component = null;
        }
    }

    /**
     * Returns true if autoscrolling is currently running for the specified
     * widget.
     */
    private boolean _isRunning(JComponent c) {
        return (c == component && timer != null && timer.isRunning());
    }

    /**
     * MouseListener method, invokes start/stop as necessary.
     */
    private void _processMouseDragged(MouseEvent e) {
        JComponent component = (JComponent)e.getComponent();
	Rectangle visibleRect = component.getVisibleRect();
	boolean contains = visibleRect.contains(e.getX(), e.getY());

	if (contains) {
            _stop(component);
	} else {
            start(component, e);
	}
    }

    //
    // ActionListener
    //
    /**
     * ActionListener method. Invoked when the Timer fires. This will scroll
     * if necessary.
     */
    public void actionPerformed(ActionEvent x) {
        JComponent component = Autoscroller.component;

        if (component == null || !component.isShowing() || (event == null)) {
            _stop(component);
            return;
        }
        Point screenLocation = component.getLocationOnScreen();
        MouseEvent e = new MouseEvent(component, event.getID(),
                                      event.getWhen(), event.getModifiers(),
                                      event.getX() - screenLocation.x,
                                      event.getY() - screenLocation.y,
                                      event.getClickCount(),
                                      event.isPopupTrigger());
        component.superProcessMouseMotionEvent(e);
    }

}
