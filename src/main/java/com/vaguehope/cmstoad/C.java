package com.vaguehope.cmstoad;

import java.security.Provider;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public interface C {

	String APPNAME = "csmtoad";
	Provider PROVIDER = new BouncyCastleProvider();
	ASN1ObjectIdentifier DEFAULT_ENCRYPTION_OID = CMSAlgorithm.AES128_CBC;

	void doNotImplement();

}
