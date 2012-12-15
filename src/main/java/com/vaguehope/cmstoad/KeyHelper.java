package com.vaguehope.cmstoad;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;

public final class KeyHelper {

	private KeyHelper () {
		throw new AssertionError();
	}

	public static void writeKey (Key k, File f) throws IOException {
		PEMWriter w = new PEMWriter(new FileWriter(f));
		try {
			w.writeObject(k);
		}
		finally {
			w.close();
		}
	}

	public static <T extends Key> T readKey (File f, Class<T> type) throws IOException {
		PEMReader r = new PEMReader(new FileReader(f));
		try {
			Object obj = r.readObject();
			return type.cast(obj);
		}
		finally {
			r.close();
		}
	}

	public static String keyBaseName (File file) {
		String name = file.getName();
		return name.substring(0, name.indexOf("."));
	}

	public static String publicKeyName (String baseName) {
		return baseName + ".public.pem";
	}

	public static String privateKeyName (String baseName) {
		return baseName + ".private.pem";
	}

}
