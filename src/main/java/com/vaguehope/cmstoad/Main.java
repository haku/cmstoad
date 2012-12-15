package com.vaguehope.cmstoad;

import java.io.IOException;
import java.io.PrintStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public final class Main {

	private Main () {
		throw new AssertionError();
	}

	public static void main (String[] rawArgs) throws IOException {
		final PrintStream out = System.out;
		final PrintStream err = System.err;
		final Args args = new Args();
		final CmdLineParser parser = new CmdLineParser(args);
		parser.setUsageWidth(80);
		try {
			parser.parseArgument(rawArgs);
			switch (args.getAction()) {
				case KEYGEN:
					new KeyGen(args).run(out, err);
					break;
				case INFO:
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
		catch (CmdLineException e) {
			err.println(e.getMessage());
			shortHelp(parser);
			return;
		}
	}

	private static void shortHelp (CmdLineParser parser) {
		System.err.print("Usage: ");
		System.err.print(C.APPNAME);
		parser.printSingleLineUsage(System.err);
		System.err.println();
	}

	private static void fullHelp (CmdLineParser parser) {
		shortHelp(parser);
		parser.printUsage(System.err);
		System.err.println();
	}
}
