package com.vaguehope.cmstoad;

import java.io.File;
import java.io.PrintStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public final class Main {

	private Main () {
		throw new AssertionError();
	}

	public static void main (String[] rawArgs) {
		ProviderHelper.initProvider();
		final PrintStream out = System.out;
		final PrintStream err = System.err;
		if (out == null || err == null) throw new AssertionError();
		final File dir = new File(".");
		final Args args = new Args();
		final CmdLineParser parser = new CmdLineParser(args);
		try {
			parser.parseArgument(rawArgs);
			switch (args.getAction()) {
				case KEYGEN:
					new KeyGen(args, dir).run(out, err);
					break;
				case ENCRYPT:
					new Encrypt(args, dir).run(out, err);
					break;
				case DECRYPT:
					new Decrypt(args, dir).run(out, err);
					break;
				case INFO:
					new Info(args).run(out, err);
					break;
				case BENCHMARK:
					new Benchmark().run(out, err);
					break;
				case HELP:
				default:
					fullHelp(parser, err);
			}
		}
		catch (CmdLineException e) {
			err.println(e.getMessage());
			shortHelp(parser, err);
			return;
		}
		catch (Exception e) {
			err.println("An unhandled error occured.");
			e.printStackTrace(err);
		}
	}

	private static void shortHelp (CmdLineParser parser, PrintStream ps) {
		ps.print("Usage: ");
		ps.print(C.APPNAME);
		parser.printSingleLineUsage(System.err);
		ps.println();
	}

	private static void fullHelp (CmdLineParser parser, PrintStream ps) {
		shortHelp(parser, ps);
		parser.printUsage(ps);
		ps.println();
	}
}
