package com.vaguehope.cmstoad;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
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
	@Option(name = "--publickey", aliases = "-u", metaVar = "my_key.public.pem", multiValued = true, usage = "public key to encrypt file to") private List<String> publicKeyPaths;
	@Option(name = "--privatekey", aliases = "-i", metaVar = "my_key.private.pem", multiValued = true, usage = "private key to decrypt file with") private List<String> privateKeyPaths;

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
		return readKeys(PublicKey.class, this.publicKeyPaths);
	}

	public Map<String, PrivateKey> getPrivteKeys (boolean required) throws CmdLineException, IOException {
		if (required && (this.privateKeyPaths == null || this.privateKeyPaths.isEmpty())) throw new CmdLineException(null, "At least one private key is required.");
		return readKeys(PrivateKey.class, this.privateKeyPaths);
	}

	public <T extends Key> Map<String, T> readKeys (Class<T> type, List<String> paths) throws CmdLineException, IOException {
		List<File> files = pathsToFiles(paths);
		checkFilesExist(files);
		return KeyHelper.readKeys(files, type);
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
