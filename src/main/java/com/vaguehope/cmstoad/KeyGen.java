package com.vaguehope.cmstoad;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

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

		File pubF = new File(dir, KeyHelper.publicKeyName(name));
		File privF = new File(dir, KeyHelper.privateKeyName(name));

		KeyHelper.writeKey(keyPair.getPublic(), pubF);
		out.println("Public key: " + pubF.getPath());

		KeyHelper.writeKey(keyPair.getPrivate(), privF);
		out.println("Private key: " + privF.getPath());

		return keyPair;
	}

}
