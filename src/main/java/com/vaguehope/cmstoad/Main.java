package com.vaguehope.cmstoad;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public final class Main {

	private Main () {
		throw new AssertionError();
	}

	public static void main (String[] rawArgs) {
		ProviderHelper.initProvider();
		final PrintStream err = System.err;
		final File dir = new File(".");
		final Args args = new Args();
		final CmdLineParser parser = new CmdLineParser(args);
		try {
			parser.parseArgument(rawArgs);
			run(args, parser, dir, err);
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

	private static void run (Args args, CmdLineParser parser, File dir, PrintStream err) throws CmdLineException, IOException, OperatorException, CMSException {
		switch (args.getAction()) {
			case KEYGEN:
				new KeyGen(args, dir).run(err);
				break;
			case ENCRYPT:
				new Encrypt(args, dir).run(err);
				break;
			case DECRYPT:
				new Decrypt(args, dir).run(err);
				break;
			case INFO:
				new Info(args).run(err);
				break;
			case BENCHMARK:
				new Benchmark().run(err);
				break;
			case HELP:
			default:
				fullHelp(parser, err);
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
