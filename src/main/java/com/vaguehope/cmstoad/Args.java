package com.vaguehope.cmstoad;

import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

public class Args {

	@Argument(index = 0, required = true, metaVar = "<action>", usage = Action.USAGE) private Action action;
	@Argument(index = 1, multiValued = true, metaVar = "FILE") private List<String> files;

	@Option(name = "--name", aliases = "-n", metaVar = "my_key", usage = "name for the generated keypair") private String name;
	@Option(name = "--keysize", aliases = "-s", metaVar = "4096", usage = "length of the generated private key") private int keysize;

	public Action getAction () {
		return this.action;
	}

	public List<String> getFiles () {
		return this.files;
	}

	public String getName (boolean required) throws CmdLineException {
		if (required && (this.name == null || this.name.isEmpty())) throw new CmdLineException(null, "name is required");
		return this.name;
	}

	public int getKeysize (boolean required) throws CmdLineException {
		if (required && (this.keysize < 1024)) throw new CmdLineException(null, "keysize is required and must be at least 1024");
		return this.keysize;
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
