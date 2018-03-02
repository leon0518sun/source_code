/*
 * @(#)file      SnmpSession.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.12
 * @(#)date      09/10/09
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */


package com.sun.jmx.snmp.daemon;


// java imports
//
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.InterruptedIOException;

// jmx imports
//
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.SnmpScopedPduRequest;
// SNMP runtime imports
//
import com.sun.jmx.trace.Trace;

/**
 * This class is used for sending INFORM REQUESTS from an agent to a manager.
 *
 * Creates, controls, and manages one or more inform requests.
 * 
 * The SnmpSession maintains the list of all active inform requests and inform responses.
 * Each SnmpSession has a dispatcher that is a thread used to service all the inform requests it creates 
 * and each SnmpSession uses a separate socket for sending/receiving inform requests/responses.
 *
 * An SnmpSession object is associated with an SNMP adaptor server. 
 * It is created the first time an inform request is sent by the SNMP adaptor server 
 * and is destroyed (with its associated SnmpSocket) when the SNMP adaptor server is stopped.
 *
 */

class SnmpSession implements SnmpDefinitions, Runnable {

    // PRIVATE VARIABLES
    //------------------

    /**
     * The SNMP adaptor associated with this SnmpSession.
     */
    protected transient SnmpAdaptorServer adaptor;
    /**
     * The SnmpSocket to be used to communicate with the manager
     * by all inform requests created in this session.
     */
    protected transient SnmpSocket informSocket = null;
    /**
     * This table maintains the list of inform requests.
     */
    private transient Hashtable informRequestList = new Hashtable();
    /**
     * This table maintains the list of inform responses.
     * A FIFO queue is needed here.
     */
    private transient Stack informRespq = new Stack();
    /**
     * The dispatcher that will service all inform responses to inform requests generated
     * using this session object. An SnmpSession object creates one or more inform requests.
     * Thus it services all inform requests, which are created by this session object, 
     * when an inform response arrives for an inform request generated by the session.
     */
    private transient Thread myThread = null;
    /**
     * Request being synchronized from session thread. This happens when
     * a user does sync operation from a callback.
     */
    private transient SnmpInformRequest syncInformReq ;
    
    SnmpQManager snmpQman = null;
    
    String dbgTag = "SnmpSession";
    
    private boolean isBeingCancelled = false;
    
    // PUBLIC CONSTRUCTORS
    //--------------------
    
    /**
     * Constructor for creating a new session.  
     * @param adp The SNMP adaptor associated with this SnmpSession.
     * @exception SocketException Unable to initialize the SnmpSocket.
     */
    public SnmpSession(SnmpAdaptorServer adp) throws SocketException {
        adaptor = adp;
	snmpQman = new SnmpQManager();
	SnmpResponseHandler snmpRespHdlr = new SnmpResponseHandler(adp, snmpQman);
        initialize(adp, snmpRespHdlr);
    }
    /**
     * Constructor for creating a new session. Allows subclassing.
     */
    public SnmpSession() throws SocketException {
    }
    // OTHER METHODS
    //--------------
    /**
     * Initializes the SnmpSession.  
     * @param adp The SNMP adaptor associated with this SnmpSession.
     * @exception SocketException Unable to initialize the SnmpSocket.
     */
    protected synchronized void initialize(SnmpAdaptorServer adp,
					   SnmpResponseHandler snmpRespHdlr)
	throws SocketException {
	informSocket = new SnmpSocket(snmpRespHdlr, adp.getAddress(), adp.getBufferSize().intValue());

        myThread = new Thread(this, "SnmpSession");
        myThread.start();
    }

    /**
     * Indicates whether the thread for this session is active and the SNMP adaptor server ONLINE.
     * @return true if active, false otherwise.
     */
    synchronized boolean isSessionActive() {
        //return ((myThread != null) && (myThread.isAlive()));
        return ((adaptor.isActive()) && (myThread != null) && (myThread.isAlive()));
    }
    
    /**
     * Gets the SnmpSocket which will be used by inform requests created in this session.
     * @return The socket which will be used in this session.
     */
    SnmpSocket getSocket() {
        return informSocket;
    }

    /**
     * Gets the SnmpQManager which will be used by inform requests created in this session.
     * @return The SnmpQManager which will be used in this session.
     */
    SnmpQManager getSnmpQManager() {
        return snmpQman;
    }

    /**
     * Indicates whether this session is performing synchronous operation for an inform request.
     * @return <CODE>true</CODE> if the session is performing synchronous operation, <CODE>false</CODE> otherwise.
     */
    private synchronized boolean syncInProgress() {
        return syncInformReq != null ;
    }
    
    private synchronized void setSyncMode(SnmpInformRequest req) {
        syncInformReq = req ;
    }

    private synchronized void resetSyncMode() {
        if (syncInformReq == null)
            return ;
        syncInformReq = null ;
        if (thisSessionContext())
            return ;
        this.notifyAll() ;
    }
    
    /**
     * Returns <CODE>true</CODE> if the current executing thread is this session's dispatcher.
     * Typically used to detect whether the user is doing a sync operation from
     * this dispatcher context. For instance, a user gives a sync command
     * from within a request callback using its associated session.
     * @return <CODE>true</CODE> if current thread is this session's dispatcher, <CODE>false</CODE> otherwise.
     */
    boolean thisSessionContext() {
        return (Thread.currentThread() == myThread) ;
    }
    
    /**
     * Sends an inform request to the specified InetAddress destination using the specified community string.
     * @param addr The InetAddress destination for this inform request.
     * @param cs The community string to be used for the inform request.
     * @param cb The callback that is invoked when a request is complete.
     * @param vblst A list of SnmpVarBind instances or null.
     * @exception SnmpStatusException SNMP adaptor is not ONLINE or session 
     *            is dead.
     */
    SnmpInformRequest makeAsyncRequest(InetAddress addr, String cs, 
				       SnmpInformHandler cb, 
				       SnmpVarBindList vblst, int port)
        throws SnmpStatusException {
        
        if (!isSessionActive()) {
            throw new SnmpStatusException("SNMP adaptor server not ONLINE");
        }
        SnmpInformRequest snmpreq = new SnmpInformRequest(this, adaptor, addr, cs, port, cb);
        snmpreq.start(vblst);
        return snmpreq;
    }

    /**
     * Performs sync operations on active requests. Any number of inform requests
     * can be done in sync mode but only one per thread.
     * The user can do synchronous operation using the request handle only.
     */ 
    void waitForResponse(SnmpInformRequest req, long waitTime) {
        
        if (! req.inProgress())
            return ;
        setSyncMode(req) ;

        if (isTraceOn()) {
            trace("waitForResponse", "Session switching to sync mode for inform request " + req.getRequestId());
        }
        long maxTime ;
        if (waitTime <= 0)
            maxTime = System.currentTimeMillis() + 6000 * 1000 ;
        else
            maxTime = System.currentTimeMillis() + waitTime ;

        while (req.inProgress() || syncInProgress()) {
            waitTime = maxTime - System.currentTimeMillis() ;
            if (waitTime <= 0)
                break ;
            synchronized (this) {
                if (! informRespq.removeElement(req)) {
                    try {
                        this.wait(waitTime) ;
                    } catch(InterruptedException e) {
                    }
                    continue ;
                }
            }
            try {
                processResponse(req) ;
            } catch (Exception e) {
                if (isDebugOn()) {
                    debug("waitForResponse", e);
                }
            }
        }
        resetSyncMode() ;
        return ;
    }
    
    /**
     * Dispatcher method for this session thread. This is the dispatcher method
     * which goes in an endless-loop and waits for servicing inform requests
     * which received a reply from the manager.
     */
    public void run() {
        myThread = Thread.currentThread();
        myThread.setPriority(Thread.NORM_PRIORITY);

        SnmpInformRequest reqc = null;
        while (myThread != null) {
            try {
                reqc = nextResponse();
                if (reqc != null) {
                    processResponse(reqc);
                }
            } catch (ThreadDeath d) {
                myThread = null;
                if (isDebugOn()) {
                    debug("run", "Session thread unexpectedly shutting down");
                }
                throw d ;
            }
        }
        if (isTraceOn()) {
            trace("run", "Session thread shutting down");
        }
        myThread = null ;
    }
    
    private void processResponse(SnmpInformRequest reqc) {
        
        while (reqc != null && myThread != null) {
            try {
                if (reqc != null) {
                    if (isTraceOn()) {
                        trace("processResponse", "Processing response to req = " + reqc.getRequestId());
                    }
                    reqc.processResponse() ;  // Handles out of memory.
                    reqc = null ;  // finished processing.
                }
				
            } catch (Exception e) {
                if (isDebugOn()) {
                    debug("processResponse", e);
                }
                reqc = null ;
            } catch (OutOfMemoryError ome) {
                if (isDebugOn()) {
                    debug("processResponse", "Out of memory error in session thread");
                    debug("processResponse", ome);
                }
                Thread.currentThread().yield();
                continue ;   // re-process the request.
            }
        }
    }
    
    // HANDLING INFORM REQUESTS LIST AND INFORM RESPONSES LIST
    //--------------------------------------------------------
    
    /**
     * Adds an inform request.
     * @param snmpreq The inform request to add.
     * @exception SnmpStatusException SNMP adaptor is not ONLINE or session is dead.
     */
    synchronized void addInformRequest(SnmpInformRequest snmpreq) throws SnmpStatusException {
        
        // If the adaptor is not ONLINE, stop adding requests.
        //
        if (!isSessionActive()) {
            throw new SnmpStatusException("SNMP adaptor is not ONLINE or session is dead...") ;
        }
        informRequestList.put(snmpreq, snmpreq);
    }
    
    /**
     * Deletes an inform request.
     * @param snmpreq The inform request to delete.
     */
    synchronized void removeInformRequest(SnmpInformRequest snmpreq) {
	// deleteRequest can be called from destroySnmpSession.
	//In such a case remove is done in cancelAllRequest method.
	if(!isBeingCancelled)
	    informRequestList.remove(snmpreq) ;

        if (syncInformReq != null && syncInformReq == snmpreq) {
            resetSyncMode() ;
        }
    }
            
    /**
     * Cancels all pending inform requests in this session.
     */
    private void cancelAllRequests() {
        final SnmpInformRequest[] list;
	
	synchronized(this) {
	    
	    if (informRequestList.isEmpty()) {
		return ;
	    }

	    isBeingCancelled = true;
	    
	    list = new SnmpInformRequest[informRequestList.size()];
	    java.util.Iterator it = informRequestList.values().iterator();
	    int i = 0;
	    while(it.hasNext()) {
		SnmpInformRequest req = (SnmpInformRequest)it.next();
		list[i++] = req;
		it.remove();
	    }
	    informRequestList.clear();
	}
	
	for(int i = 0; i < list.length; i++)
	    list[i].cancelRequest();
    }
    
    /**
     * Adds the inform request object which received a response to an inform request 
     * generated by the session.  This is added to a private store, which
     * will be eventually picked up by the dispatcher for processing.
     * @param reqc The inform request that received the response from the manager.
     */
    void addResponse(SnmpInformRequest reqc) {
        
        SnmpInformRequest snmpreq = (SnmpInformRequest) reqc ;
        if (isSessionActive()) {
            synchronized(this) {
                informRespq.push(reqc) ;
                this.notifyAll() ;
            }
        } else {
            if (isDebugOn()) {
                debug("addResponse", "Adaptor not ONLINE or session thread dead. So inform response is dropped..." + reqc.getRequestId());
            }
        }
        return ;
    }

    private synchronized SnmpInformRequest nextResponse() {
        
        if (informRespq.isEmpty()) {
            try {
                if (isTraceOn()) {
                    trace("nextResponse", "Blocking for response");
                }
                this.wait();
            } catch(InterruptedException e) {
            }
        }
        if (informRespq.isEmpty())
            return null;
        SnmpInformRequest reqc = (SnmpInformRequest) informRespq.firstElement();
        informRespq.removeElementAt(0) ;
        return reqc ;
    }
    
    private synchronized void cancelAllResponses() {
        if (informRespq != null) {
            syncInformReq = null ;
            informRespq.removeAllElements() ;
            this.notifyAll() ;
        }
    }
    
    /**
     * Destroys any pending inform requests and then stops the session.
     * The session will not be usable after this method returns.
     */
    final void destroySession() {
        
        cancelAllRequests() ;
        cancelAllResponses() ;
        synchronized(this) {
            informSocket.close() ;
            informSocket = null ;
        }
        snmpQman.stopQThreads() ;
        snmpQman = null ;
        killSessionThread() ;
    }
    
    /**
     * Make sure you are killing the thread when it is active. Instead
     * prepare for a graceful exit.
     */
    private synchronized void killSessionThread() {
        
        if ((myThread != null) && (myThread.isAlive())) {
            if (isTraceOn()) {
                trace("killSessionThread", "Destroying session");
            }
            if (!thisSessionContext()) {
                myThread = null ;
                this.notifyAll() ;
            } else
                myThread = null ;
        }
    }

    /**
     * Finalizer of the <CODE>SnmpSession</CODE> objects.
     * This method is called by the garbage collector on an object 
     * when garbage collection determines that there are no more references to the object.
     * <P>Removes all the requests for this SNMP session, closes the socket and
     * sets all the references to the <CODE>SnmpSession</CODE> object to <CODE>null</CODE>.
     */
    public void finalize() {
        
        if (informRespq != null)
            informRespq.removeAllElements() ;
        informRespq = null ;
        if (informSocket != null)
            informSocket.close() ;
        informSocket = null ;

        if (isTraceOn()) {
            trace("finalize", "Shutting all servers");
        }
        snmpQman = null ;
    }
    
    // TRACES & DEBUG
    //---------------
    
    boolean isTraceOn() {
        return Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_ADAPTOR_SNMP);
    }

    void trace(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_ADAPTOR_SNMP, clz, func, info);
    }

    void trace(String func, String info) {
        trace(dbgTag, func, info);
    }
    
    boolean isDebugOn() {
        return Trace.isSelected(Trace.LEVEL_DEBUG, Trace.INFO_ADAPTOR_SNMP);
    }

    void debug(String clz, String func, String info) {
        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_ADAPTOR_SNMP, clz, func, info);
    }

    void debug(String clz, String func, Throwable exception) {
        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_ADAPTOR_SNMP, clz, func, exception);
    }

    void debug(String func, String info) {
        debug(dbgTag, func, info);
    }
    
    void debug(String func, Throwable exception) {
        debug(dbgTag, func, exception);
    }
}
