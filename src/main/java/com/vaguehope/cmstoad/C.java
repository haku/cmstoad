package com.vaguehope.cmstoad;

import java.security.Provider;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public interface C {

	String APPNAME = "csmtoad";

	Provider PROVIDER = new BouncyCastleProvider();
	ASN1ObjectIdentifier DEFAULT_ENCRYPTION_OID = CMSAlgorithm.AES256_CBC;
	String DEFAULT_KEY_TYPE = "RSA";
	int MIN_KEY_LENGTH = 1024;

	String ENCRYPTED_FILE_EXT = ".cms";
	String DECRYPTED_FILE_EXT = ".plain";

	int DEFAULT_COPY_BUFFER_SIZE = 8192;

	void doNotImplement();

}
