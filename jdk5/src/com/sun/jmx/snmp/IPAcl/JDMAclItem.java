/*
 * @(#)file      JDMAclItem.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   4.8
 * @(#)date      09/10/09
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */


/* Generated By:JJTree: Do not edit this line. JDMAclItem.java */

package com.sun.jmx.snmp.IPAcl;

/** 
 * @version     4.8     12/19/03 
 * @author      Sun Microsystems, Inc. 
 */ 
class JDMAclItem extends SimpleNode {
  protected JDMAccess access= null;
  protected JDMCommunities com= null;

  JDMAclItem(int id) {
    super(id);
  }

  JDMAclItem(Parser p, int id) {
    super(p, id);
  }

  public static Node jjtCreate(int id) {
      return new JDMAclItem(id);
  }

  public static Node jjtCreate(Parser p, int id) {
      return new JDMAclItem(p, id);
  }

  public JDMAccess getAccess() {
    return access;
  }

  public JDMCommunities getCommunities() {
    return com;
  }
}
