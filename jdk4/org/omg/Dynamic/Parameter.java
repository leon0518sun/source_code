package org.omg.Dynamic;


/**
* org/omg/Dynamic/Parameter.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from ../../../../src/share/classes/org/omg/PortableInterceptor/Interceptors.idl
* Friday, June 20, 2003 8:34:39 AM GMT
*/


/**
   * <code>NVList</code> PIDL represented by <code>ParameterList</code> IDL.  
   * This exists in order to keep the Portable Interceptor IDL from becoming 
   * PIDL.
   */
public final class Parameter implements org.omg.CORBA.portable.IDLEntity
{
  public org.omg.CORBA.Any argument = null;
  public org.omg.CORBA.ParameterMode mode = null;

  public Parameter ()
  {
  } // ctor

  public Parameter (org.omg.CORBA.Any _argument, org.omg.CORBA.ParameterMode _mode)
  {
    argument = _argument;
    mode = _mode;
  } // ctor

} // class Parameter
