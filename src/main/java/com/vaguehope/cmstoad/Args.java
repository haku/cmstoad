package com.vaguehope.cmstoad;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

public class Args {

	@Argument(index = 0, required = true, metaVar = "<action>", usage = Action.USAGE) private Action action;
	@Argument(index = 1, multiValued = true, metaVar = "FILE") private List<String> filePaths;

	@Option(name = "--name", aliases = "-n", metaVar = "my_key", usage = "name for the generated keypair") private String name;
	@Option(name = "--keysize", aliases = "-s", metaVar = "4096", usage = "length of the generated private key") private int keysize;
	@Option(name = "--publickey", aliases = "-p", metaVar = "my_key.public.pem", multiValued = true, usage = "public key to encrypt file to") private List<String> publicKeyPaths;

	public Action getAction () {
		return this.action;
	}

	public List<File> getFiles (boolean required, boolean mustExist) throws CmdLineException {
		if (required && (this.filePaths == null || this.filePaths.isEmpty())) throw new CmdLineException(null, "At least one file is required.");
		List<File> files = pathsToFiles(this.filePaths);
		if (mustExist) checkFilesExist(files);
		return files;
	}

	public String getName (boolean required) throws CmdLineException {
		if (required && (this.name == null || this.name.isEmpty())) throw new CmdLineException(null, "Name is required.");
		return this.name;
	}

	public int getKeysize (boolean required) throws CmdLineException {
		if (required && (this.keysize < 1024)) throw new CmdLineException(null, "Keysize is required and must be at least 1024.");
		return this.keysize;
	}

	public Map<String, PublicKey> getPublicKeys (boolean required) throws CmdLineException, IOException {
		if (required && (this.publicKeyPaths == null || this.publicKeyPaths.isEmpty())) throw new CmdLineException(null, "At least one public key is required.");
		List<File> files = pathsToFiles(this.publicKeyPaths);
		checkFilesExist(files);
		Map<String, PublicKey> keys = new HashMap<String, PublicKey>();
		for (File file : files) {
			PublicKey key = KeyHelper.readKey(file, PublicKey.class);
			keys.put(KeyHelper.keyBaseName(file), key);
		}
		return keys;
	}

	private static List<File> pathsToFiles (List<String> paths) {
		List<File> files = new ArrayList<File>();
		for (String path : paths) {
			files.add(new File(path));
		}
		return files;
	}

	private static void checkFilesExist (List<File> files) throws CmdLineException {
		for (File file : files) {
			if (!file.exists()) {
				throw new CmdLineException(null, "File not found: " + file.getAbsolutePath());
			}
		}
	}

	public static enum Action {
		HELP,
		INFO,
		KEYGEN,
		ENCRYPT,
		DECRYPT;
		private static final String USAGE = "" +
				"help    : display this help and exit\n" +
				"info    : display CMS header info for specified files\n" +
				"keygen  : generate a key pair\n" +
				"encrypt : encrypt files(s)\n" +
				"decrypt : decrypt files(s)";
	}

}
