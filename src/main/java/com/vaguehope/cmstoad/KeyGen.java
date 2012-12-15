package com.vaguehope.cmstoad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.openssl.PEMWriter;
import org.kohsuke.args4j.CmdLineException;

public class KeyGen implements CliAction {

	private final String name;
	private final int keysize;

	public KeyGen (String name, int keysize) {
		this.name = name;
		this.keysize = keysize;
	}

	public KeyGen (Args args) throws CmdLineException {
		this(args.getName(true), args.getKeysize(true));
	}

	@Override
	public void run (PrintStream out, PrintStream err) throws IOException {
		File d = new File(".");
		try {
			generateKeyPair(this.keysize, this.name, d, out);
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private static KeyPair generateKeyPair(int keysize, String name, File dir, PrintStream out) throws NoSuchAlgorithmException, IOException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA", C.PROVIDER);
		keygen.initialize(keysize);
		KeyPair keyPair = keygen.generateKeyPair();

		File pubF = new File(dir, name + ".public.pem");
		File privF = new File(dir, name + ".private.pem");

		writeKey(keyPair.getPublic(), pubF);
		out.println("Public key: " + pubF.getPath());

		writeKey(keyPair.getPrivate(), privF);
		out.println("Private key: " + privF.getPath());

		return keyPair;
	}

	private static void writeKey (Key k, File f) throws IOException {
		PEMWriter w = new PEMWriter(new FileWriter(f));
		try {
			w.writeObject(k);
		}
		finally {
			w.close();
		}
	}

}
