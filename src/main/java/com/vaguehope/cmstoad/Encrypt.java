package com.vaguehope.cmstoad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.OperatorCreationException;
import org.kohsuke.args4j.CmdLineException;

public class Encrypt implements CliAction {

	private final Map<String, PublicKey> keys;
	private final List<File> sourceFiles;
	private final File dir;

	public Encrypt (Map<String, PublicKey> keys, List<File> files, File dir) {
		this.keys = keys;
		this.sourceFiles = files;
		this.dir = dir;
	}

	public Encrypt (Args args, File dir) throws CmdLineException, IOException {
		this(args.getPublicKeys(true), args.getFiles(true, true), dir);
	}

	@Override
	public void run (PrintStream out, PrintStream err) throws IOException, CMSException, OperatorCreationException {
		for (File sourceFile : this.sourceFiles) {
			File sinkFile = new File(this.dir, sourceFile.getName() + C.ENCRYPTED_FILE_EXT);
			if (sinkFile.exists()) throw new IOException("File already exists: " + sinkFile.getAbsolutePath());
			out.println("Output: " + sinkFile.getPath());
			encrypt(this.keys, sourceFile, sinkFile, out);
		}
	}

	public static void encrypt (Map<String, PublicKey> keys, File sourceFile, File sinkFile, PrintStream out) throws IOException, CMSException, OperatorCreationException {
		InputStream source = new FileInputStream(sourceFile);
		OutputStream sink = new FileOutputStream(sinkFile);
		try {
			encrypt(keys, source, sink, out);
		}
		finally {
			IOUtils.closeQuietly(source);
			IOUtils.closeQuietly(sink);
		}
	}

	public static void encrypt (Map<String, PublicKey> keys, InputStream source, OutputStream sink, PrintStream out) throws OperatorCreationException, CMSException, IOException {
		CMSEnvelopedDataStreamGenerator cmsGen = new CMSEnvelopedDataStreamGenerator();
		for (Entry<String, PublicKey> k : keys.entrySet()) {
			out.println("Public key: " + k.getKey() + " (" + k.getValue().getAlgorithm() + ")");
			cmsGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(k.getKey().getBytes(), k.getValue()).setProvider(C.PROVIDER));
		}
		encrypt(source, sink, cmsGen, out);
	}

	private static void encrypt (InputStream source, OutputStream sink, CMSEnvelopedDataStreamGenerator cmsGen, PrintStream out) throws CMSException, IOException {
		OutputStream target = cmsGen.open(
				sink,
				new JceCMSContentEncryptorBuilder(C.DEFAULT_ENCRYPTION_OID).setProvider(C.PROVIDER).build()
				);
		try {
			long startTime = System.nanoTime();
			long sourceLength = IoHelper.copy(source, target);
			long endTime = System.nanoTime();
			Benchmark.printBenchmark(sourceLength, "Encrypted", startTime, endTime, out);
		}
		finally {
			target.close();
		}
	}

}
