/*
 * @(#)Delegate.java	1.37 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.omg.CORBA.portable;

import org.omg.CORBA.Request;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.SystemException;

/** 
 * Specifies a portable API for ORB-vendor-specific 
 * implementation of the org.omg.CORBA.Object methods.
 * 
 * Each stub (proxy) contains a delegate
 * object, to which all org.omg.CORBA.Object methods are forwarded.
 * This allows a stub generated by one vendor's ORB to work with the delegate
 * from another vendor's ORB.
 *
 * @see org.omg.CORBA.Object
 * @author OMG
 * @version 1.37 12/19/03
 */

public abstract class Delegate {

    /** 
     * Return an InterfaceDef for the object reference provided.
     * @param self The object reference whose InterfaceDef needs to be returned
     * @return the InterfaceDef
     */
    public abstract org.omg.CORBA.Object get_interface_def(
        org.omg.CORBA.Object self);

    /** 
     * Returns a duplicate of the object reference provided.
     * @param obj The object reference whose duplicate needs to be returned
     * @return the duplicate object reference
     */
    public abstract org.omg.CORBA.Object duplicate(org.omg.CORBA.Object obj);

    /** 
     * Releases resources associated with the object reference provided.
     * @param obj The object reference whose resources need to be released
     */
    public abstract void release(org.omg.CORBA.Object obj);

    /**
     * Checks if the object reference is an instance of the given interface.
     * @param obj The object reference to be checked.
     * @param repository_id The repository identifier of the interface 
     * to check against.
     * @return true if the object reference supports the interface
     */
    public abstract boolean is_a(org.omg.CORBA.Object obj, String repository_id);

    /**
     * Determines whether the server object for the object reference has been
     * destroyed.
     * @param obj The object reference which delegated to this delegate.
     * @return true if the ORB knows authoritatively that the server object does
     * not exist, false otherwise
     */
    public abstract boolean non_existent(org.omg.CORBA.Object obj);

    /** 
     * Determines if the two object references are equivalent.
     * @param obj The object reference which delegated to this delegate.
     * @param other The object reference to check equivalence against.
     * @return true if the objects are CORBA-equivalent.
     */
    public abstract boolean is_equivalent(org.omg.CORBA.Object obj,
					  org.omg.CORBA.Object other);

    /**
     * Returns an ORB-internal identifier (hashcode) for this object reference.
     * @param obj The object reference which delegated to this delegate.
     * @param max specifies an upper bound on the hash value returned by
     *            the ORB.
     * @return ORB-internal hash identifier for object reference
     */
    public abstract int hash(org.omg.CORBA.Object obj, int max);

    /**
     * Creates a Request instance for use in the Dynamic Invocation Interface.
     * @param obj The object reference which delegated to this delegate.
     * @param operation The name of the operation to be invoked using the
     *                  Request instance.
     * @return the created Request instance
     */
    public abstract Request request(org.omg.CORBA.Object obj, String operation);

    /**
     * Creates a Request instance for use in the Dynamic Invocation Interface.
     *
     * @param obj The object reference which delegated to this delegate.
     * @param ctx                      The context to be used.
     * @param operation                The name of the operation to be
     *                                 invoked.
     * @param arg_list         The arguments to the operation in the
     *                                 form of an NVList.
     * @param result           A container for the result as a NamedValue.
     * @return                 The created Request object.
     *
     */
    public abstract Request create_request(org.omg.CORBA.Object obj,
				           Context ctx,
				           String operation,
				           NVList arg_list,
				           NamedValue result);

    /**
     * Creates a Request instance for use in the Dynamic Invocation Interface.
     *
     * @param obj The object reference which delegated to this delegate.
     * @param ctx                      The context to be used.
     * @param operation                The name of the operation to be
     *                                 invoked.
     * @param arg_list         The arguments to the operation in the
     *                                 form of an NVList.
     * @param result           A container for the result as a NamedValue.
     * @param exclist          A list of possible exceptions the
     *                                 operation can throw.
     * @param ctxlist          A list of context strings that need
     *                                 to be resolved and sent with the
     *                                 Request.
     * @return                 The created Request object.
     */
    public abstract Request create_request(org.omg.CORBA.Object obj,
				           Context ctx,
				           String operation,
				           NVList arg_list,
				           NamedValue result,
				           ExceptionList exclist,
				           ContextList ctxlist);

    /**
     * Provides a reference to the orb associated with its parameter.
     *
     * @param obj  the object reference which delegated to this delegate.
     * @return the associated orb.
     * @see <a href="package-summary.html#unimpl"><code>portable</code>
     * package comments for unimplemented features</a>
     */
    public org.omg.CORBA.ORB orb(org.omg.CORBA.Object obj) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Returns the <code>Policy</code> object of the specified type
     * which applies to this object.
     *
     * @param self The object reference which delegated to this delegate.
     * @param policy_type The type of policy to be obtained.
     * @return A <code>Policy</code> object of the type specified by
     *         the policy_type parameter.
     * @exception org.omg.CORBA.BAD_PARAM raised when the value of policy type
     * is not valid either because the specified type is not supported by this
     * ORB or because a policy object of that type is not associated with this
     * Object.
     * @see <a href="package-summary.html#unimpl"><code>portable</code>
     * package comments for unimplemented features</a>
     */
    public org.omg.CORBA.Policy get_policy(org.omg.CORBA.Object self,
					   int policy_type) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    /**
     * Retrieves the <code>DomainManagers</code> of this object.
     * This allows administration services (and applications) to retrieve the
     * domain managers, and hence the security and other policies applicable
     * to individual objects that are members of the domain.
     *
     * @param self The object reference which delegated to this delegate.
     * @return The list of immediately enclosing domain managers of this object.
     *  At least one domain manager is always returned in the list since by
     * default each object is associated with at least one domain manager at
     * creation.
     * @see <a href="package-summary.html#unimpl"><code>portable</code>
     * package comments for unimplemented features</a>
     */
    public org.omg.CORBA.DomainManager[] get_domain_managers(
							     org.omg.CORBA.Object
							     self) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    /**
     * Associates the policies passed in 
     * with a newly created object reference that it returns. Only certain 
     * policies that pertain to the invocation of an operation at the client 
     * end can be overridden using this operation. Attempts to override any 
     * other policy will result in the raising of the CORBA::NO_PERMISSION
     * exception.
     * 
     * @param self The object reference which delegated to this delegate.
     * @param policies A sequence of references to Policy objects.
     * @param set_add Indicates whether these policies should be added 
     * onto any otheroverrides that already exist (ADD_OVERRIDE) in 
     * the object reference, or they should be added to a clean 
     * override free object reference (SET_OVERRIDE). 
     * @return  A new object reference with the new policies associated with it.
     * 
     * @see <a href="package-summary.html#unimpl"><code>portable</code>
     * package comments for unimplemented features</a>
     */
    public org.omg.CORBA.Object set_policy_override(org.omg.CORBA.Object self,
						    org.omg.CORBA.Policy[] policies,
						    org.omg.CORBA.SetOverrideType set_add) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    /**
     * Returns true if this object is implemented by a local servant. 
     *
     * @param self The object reference which delegated to this delegate.
     * @return true only if the servant incarnating this object is located in 
     * this Java VM. Return false if the servant is not local or the ORB 
     * does not support local stubs for this particular servant. The default 
     * behavior of is_local() is to return false.
     */
    public boolean is_local(org.omg.CORBA.Object self) {
        return false;
    }

    /**
     * Returns a Java reference to the servant which should be used for this 
     * request. servant_preinvoke() is invoked by a local stub.
     * If a ServantObject object is returned, then its servant field 
     * has been set to an object of the expected type (Note: the object may 
     * or may not be the actual servant instance). The local stub may cast 
     * the servant field to the expected type, and then invoke the operation 
     * directly. The ServantRequest object is valid for only one invocation, 
     * and cannot be used for more than one invocation.
     *
     * @param self The object reference which delegated to this delegate.
     *
     * @param operation a string containing the operation name.
     * The operation name corresponds to the operation name as it would be 
     * encoded in a GIOP request.
     *
     * @param expectedType a Class object representing the expected type of the servant.
     * The expected type is the Class object associated with the operations 
     * class of the stub's interface (e.g. A stub for an interface Foo, 
     * would pass the Class object for the FooOperations interface).
     *
     * @return a ServantObject object.
     * The method may return a null value if it does not wish to support 
     * this optimization (e.g. due to security, transactions, etc). 
     * The method must return null if the servant is not of the expected type.
     */
    public ServantObject servant_preinvoke(org.omg.CORBA.Object self,
                                           String operation,
					   Class expectedType) {
        return null;
    }

    /**
     * servant_postinvoke() is invoked by the local stub after the operation 
     * has been invoked on the local servant.
     * This method must be called if servant_preinvoke() returned a non-null 
     * value, even if an exception was thrown by the servant's method. 
     * For this reason, the call to servant_postinvoke() should be placed 
     * in a Java finally clause.
     *
     * @param self The object reference which delegated to this delegate.
     *
     * @param servant the instance of the ServantObject returned from 
     *  the servant_preinvoke() method.
     */
    public void servant_postinvoke(org.omg.CORBA.Object self,
				   ServantObject servant) {
    }

    /**
     * request is called by a stub to obtain an OutputStream for
     * marshaling arguments. The stub must supply the operation name,
     * and indicate if a response is expected (i.e is this a oneway
     * call).
     *
     * @param self The object reference which delegated to this delegate.
     * @param operation a string containing the operation name.
     * The operation name corresponds to the operation name as it would be
     * encoded in a GIOP request.
     * @param responseExpected false if the operation is a one way operation,
     * and true otherwise.
     * @return OutputStream the OutputStream into which request arguments 
     * can be marshaled.
     * @see <a href="package-summary.html#unimpl"><code>portable</code>
     * package comments for unimplemented features</a>
     */
    public OutputStream request(org.omg.CORBA.Object self,
				String operation,
				boolean responseExpected) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * invoke is called by a stub to invoke an operation. The stub provides an
     * OutputStream that was previously returned by a request()
     * call. invoke returns an InputStream which contains the
     * marshaled reply. If an exception occurs, invoke may throw an
     * ApplicationException object which contains an InputStream from
     * which the user exception state may be unmarshaled.
     *
     * @param self The object reference which delegated to this delegate.
     * @param output the OutputStream which contains marshaled arguments
     * @return input the InputStream from which reply parameters can be 
     * unmarshaled.
     * @throws ApplicationException thrown when implementation throws 
     * (upon invocation) an exception defined as part of its remote method 
     * definition.
     * @throws RemarshalException thrown when remarshalling fails. 
     * @see <a href="package-summary.html#unimpl"><code>portable</code>
     * package comments for unimplemented features</a>
     */
    public InputStream invoke(org.omg.CORBA.Object self,
			      OutputStream output)
	throws ApplicationException, RemarshalException {
	throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * releaseReply may optionally be called by a stub to release a
     * reply stream back to the ORB when the unmarshaling has
     * completed. The stub passes the InputStream returned by
     * invoke() or ApplicationException.getInputStream(). A null
     * value may also be passed to releaseReply, in which case the
     * method is a noop.
     *
     * @param self The object reference which delegated to this delegate.
     * @param input the InputStream returned from invoke().
     * @see <a href="package-summary.html#unimpl"><code>portable</code>
     * package comments for unimplemented features</a>
     */
    public void releaseReply(org.omg.CORBA.Object self,
			     InputStream input) {
	throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Provides the implementation to override the toString() method
     * of the delegating CORBA object.
     *
     * @param self the object reference that delegated to this delegate
     * @return a <code>String</code> object that represents the object
     *         reference that delegated to this <code>Delegate</code>
     *         object
     */

    public String toString(org.omg.CORBA.Object self) {
        return self.getClass().getName() + ":" + this.toString();
    }

    /**
     * Provides the implementation to override the hashCode() method
     * of the delegating CORBA object.
     *
     * @param self the object reference that delegated to this delegate
     * @return an <code>int</code> that represents the hashcode for the
     *         object reference that delegated to this <code>Delegate</code>
     *         object
     */
    public int hashCode(org.omg.CORBA.Object self) {
        return System.identityHashCode(self);
    }

    /**
     * Provides the implementation to override the equals(java.lang.Object obj) 
     * method of the delegating CORBA object.
     *
     * @param self the object reference that delegated to this delegate
     * @param obj the <code>Object</code> with which to compare 
     * @return <code>true</code> if <code>obj</code> equals <code>self</code>;
     *         <code>false</code> otherwise
     */
    public boolean equals(org.omg.CORBA.Object self, java.lang.Object obj) {
        return (self == obj);
    }
}
