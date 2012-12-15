package com.vaguehope.cmstoad;

import java.util.List;

import org.kohsuke.args4j.Argument;

public class Args {

	@Argument(index = 0, required = true, metaVar = "action", usage = Action.USAGE) private Action action;
	@Argument(index = 1, multiValued = true, metaVar = "FILE") private List<String> files;

	public Action getAction () {
		return this.action;
	}

	public List<String> getFiles () {
		return this.files;
	}

	public static enum Action {
		HELP,
		INFO,
		KEYGEN,
		ENCRYPT,
		DECRYPT;
		private static final String USAGE =
				"help    : display this help and exit\n" +
				"info    : display CMS header info for specified files\n" +
				"keygen  : generate a key pair\n" +
				"encrypt : encrypt files(s)\n" +
				"decrypt : decrypt files(s)";
	}

}
