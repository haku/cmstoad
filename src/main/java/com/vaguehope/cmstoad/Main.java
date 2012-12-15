package com.vaguehope.cmstoad;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public final class Main {

	private static final String APPNAME = "csmtoad";

	private Main () {
		throw new AssertionError();
	}

	public static void main (String[] rawArgs) {
		Args args = new Args();
		CmdLineParser parser = new CmdLineParser(args);
		parser.setUsageWidth(80);
		try {
			parser.parseArgument(rawArgs);
		}
		catch (CmdLineException e) {
			System.err.println(e.getMessage());
			shortHelp(parser);
			return;
		}

		switch (args.getAction()) {
			case INFO:
			case KEYGEN:
			case ENCRYPT:
			case DECRYPT:
				System.err.println("Files: " + args.getFiles());
				System.err.println("TODO: " + args.getAction());
				break;
			case HELP:
			default:
				fullHelp(parser);
		}
	}

	private static void shortHelp (CmdLineParser parser) {
		System.err.print("Usage: " + APPNAME);
		parser.printSingleLineUsage(System.err);
		System.err.println();
	}

	private static void fullHelp (CmdLineParser parser) {
		shortHelp(parser);
		parser.printUsage(System.err);
		System.err.println();
	}
}
