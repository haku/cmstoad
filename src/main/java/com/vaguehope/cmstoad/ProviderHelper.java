package com.vaguehope.cmstoad;

import java.security.Security;

public final class ProviderHelper {

	private ProviderHelper () {
		throw new AssertionError();
	}

	public static void initProvider () {
		if (Security.getProvider(C.PROVIDER.getName()) == null) Security.addProvider(C.PROVIDER);
	}

}
