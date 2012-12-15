package com.vaguehope.cmstoad;

import java.security.Provider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public interface C {

	String APPNAME = "csmtoad";
	Provider PROVIDER = new BouncyCastleProvider();

	void doNotImplement();

}
