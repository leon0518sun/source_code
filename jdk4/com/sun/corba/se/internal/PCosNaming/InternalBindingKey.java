/*
 * @(#)InternalBindingKey.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * @(#)InternalBindingKey.java	1.7 03/01/23
 * 
 * Copyright 1993-1997 Sun Microsystems, Inc. 901 San Antonio Road, 
 * Palo Alto, California, 94303, U.S.A.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * CopyrightVersion 1.2
 * 
 */

package com.sun.corba.se.internal.PCosNaming;

import java.io.Serializable;
import org.omg.CosNaming.NameComponent;


/**
 * Class InternalBindingKey implements the necessary wrapper code
 * around the org.omg.CosNaming::NameComponent class to implement the proper
 * equals() method and the hashCode() method for use in a hash table.
 * It computes the hashCode once and stores it, and also precomputes
 * the lengths of the id and kind strings for faster comparison.
 */
public class InternalBindingKey
	implements Serializable
{

    // computed by serialver tool
    private static final long serialVersionUID = -5410796631793704055L;

    public String id;
    public String kind;

    // Default Constructor
    public InternalBindingKey() {}

    // Normal constructor
    public InternalBindingKey(NameComponent n)
    {
	setup(n);
    }

    // Setup the object
    protected void setup(NameComponent n) {
	this.id = n.id;
	this.kind = n.kind;
    }

    // Compare the keys by comparing name's id and kind
    public boolean equals(java.lang.Object o) {
	if (o == null)
	    return false;
	if (o instanceof InternalBindingKey) {
	    InternalBindingKey that = (InternalBindingKey)o;
	    if( this.id != null && that.id != null )
	    {
	    	if (this.id.length() != that.id.length() )
		{
			return false;
		}
	    	// If id is set is must be equal
	    	if (this.id.length() > 0 && this.id.equals(that.id) == false) 
		{
			return false;
	    	}
	    }
	    else
	    {
		// If One is Null and the other is not then it's a mismatch
		// So, return false
		if( ( this.id == null && that.id != null )
		||  ( this.id !=null && that.id == null ) )
		{
			return false;
		}
	    }
	    if( this.kind != null && that.kind != null )
	    {
	    	if (this.kind.length() != that.kind.length() )
		{
			return false;
		}
	    	// If kind is set it must be equal
	    	if (this.kind.length() > 0 && this.kind.equals(that.kind) == false) 
		{
			return false;
	    	}
	    }
	    else
	    {
		// If One is Null and the other is not then it's a mismatch
		// So, return false
		if( ( this.kind == null && that.kind != null )
		||  ( this.kind !=null && that.kind == null ) )
		{
			return false;
		}
	    }
	    // We have checked all the possibilities, so return true
	    return true;
	} else {
	    return false;
	}
    }


    // Return precomputed value
    public int hashCode() {
	int hashVal = 0;
	if (this.id.length() > 0)
	{
	    hashVal += this.id.hashCode();
	}
	if (this.kind.length() > 0)
	{
	    hashVal += this.kind.hashCode();
	}
	return hashVal;
    }
}

