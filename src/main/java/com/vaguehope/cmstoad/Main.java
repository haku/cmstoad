package com.vaguehope.cmstoad;

import java.io.PrintStream;
import java.security.Security;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public final class Main {

	private Main () {
		throw new AssertionError();
	}

	public static void main (String[] rawArgs) {
		Security.addProvider(C.PROVIDER);
		final PrintStream out = System.out;
		final PrintStream err = System.err;
		final Args args = new Args();
		final CmdLineParser parser = new CmdLineParser(args);
		try {
			parser.parseArgument(rawArgs);
			switch (args.getAction()) {
				case KEYGEN:
					new KeyGen(args).run(out, err);
					break;
				case ENCRYPT:
					new Encrypt(args).run(out, err);
					break;
				case DECRYPT:
					new Decrypt(args).run(out, err);
					break;
				case INFO:
					new Info(args).run(out, err);
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
		catch (Exception e) {
			err.println("An unhandled error occured.");
			e.printStackTrace(err);
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
