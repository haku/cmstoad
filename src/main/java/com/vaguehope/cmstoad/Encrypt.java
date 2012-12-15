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
	private List<File> sourceFiles;

	public Encrypt (Map<String, PublicKey> keys, List<File> files) {
		this.keys = keys;
		this.sourceFiles = files;
	}

	public Encrypt (Args args) throws CmdLineException, IOException {
		this(args.getPublicKeys(true), args.getFiles(true, true));
	}

	@Override
	public void run (PrintStream out, PrintStream err) throws IOException, CMSException, OperatorCreationException {
		File outputDir = new File(".");
		for (File sourceFile : this.sourceFiles) {
			File sinkFile = new File(outputDir, sourceFile.getName() + ".cms");
			if (sinkFile.exists()) throw new IOException("File already exists: " + sinkFile.getAbsolutePath());
			out.println("Output: " + sinkFile.getPath());
			encrypt(sourceFile, sinkFile);
		}
	}

	private void encrypt (File sourceFile, File sinkFile) throws IOException, CMSException, OperatorCreationException {
		CMSEnvelopedDataStreamGenerator cmsGen = new CMSEnvelopedDataStreamGenerator();
		for (Entry<String, PublicKey> kp : this.keys.entrySet()) {
			cmsGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(kp.getKey().getBytes(), kp.getValue()).setProvider(C.PROVIDER));
		}
		InputStream source = new FileInputStream(sourceFile);
		FileOutputStream sink = new FileOutputStream(sinkFile);
		try {
			encrypt(source, sink, cmsGen);
		}
		finally {
			IOUtils.closeQuietly(source);
			IOUtils.closeQuietly(sink);
		}
	}

	public void encrypt (InputStream source, OutputStream sink, CMSEnvelopedDataStreamGenerator cmsGen) throws CMSException, IOException {
		OutputStream target = cmsGen.open(
				sink,
				new JceCMSContentEncryptorBuilder(C.DEFAULT_ENCRYPTION_OID).setProvider(C.PROVIDER).build()
				);
		try {
			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = source.read(buffer)) != -1) {
				target.write(buffer, 0, bytesRead);
			}
		}
		finally {
			target.close();
		}
	}

}
