/*
 * @(#)EncodedKeySpec.java	1.20 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.spec;

/**
 * This class represents a public or private key in encoded format.
 *
 * @author Jan Luehe
 *
 * @version 1.20, 12/19/03
 *
 * @see java.security.Key
 * @see java.security.KeyFactory
 * @see KeySpec
 * @see X509EncodedKeySpec
 * @see PKCS8EncodedKeySpec
 *
 * @since 1.2
 */

public abstract class EncodedKeySpec implements KeySpec {

    private byte[] encodedKey;

    /**
     * Creates a new EncodedKeySpec with the given encoded key.
     *
     * @param encodedKey the encoded key. The contents of the 
     * array are copied to protect against subsequent modification.
     */
    public EncodedKeySpec(byte[] encodedKey) {
	this.encodedKey = (byte[])encodedKey.clone();
    }

    /**
     * Returns the encoded key.
     *
     * @return the encoded key. Returns a new array each time
     * this method is called.
     */
    public byte[] getEncoded() {
	return (byte[])this.encodedKey.clone();
    }

    /**
     * Returns the name of the encoding format associated with this
     * key specification.
     *
     * <p>If the opaque representation of a key
     * (see {@link java.security.Key Key}) can be transformed
     * (see {@link java.security.KeyFactory KeyFactory})
     * into this key specification (or a subclass of it),
     * <code>getFormat</code> called
     * on the opaque key returns the same value as the
     * <code>getFormat</code> method
     * of this key specification.
     *
     * @return a string representation of the encoding format.
     */
    public abstract String getFormat();
}
