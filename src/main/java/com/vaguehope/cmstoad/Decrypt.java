package com.vaguehope.cmstoad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.kohsuke.args4j.CmdLineException;

public class Decrypt implements CliAction {

	private final Map<String, PrivateKey> keys;
	private final List<File> sourceFiles;
	private final File dir;

	public Decrypt (Map<String, PrivateKey> keys, List<File> files, File dir) {
		this.keys = keys;
		this.sourceFiles = files;
		this.dir = dir;
	}

	public Decrypt (Args args, File dir) throws CmdLineException, IOException {
		this(args.getPrivteKeys(true), args.getFiles(true, true), dir);
	}

	@Override
	public void run (PrintStream out, PrintStream err) throws IOException, CMSException, CmdLineException {
		for (File sourceFile : this.sourceFiles) {
			File sinkFile = new File(this.dir, sourceFile.getName() + C.DECRYPTED_FILE_EXT);
			if (sinkFile.exists()) throw new IOException("File already exists: " + sinkFile.getAbsolutePath());
			out.println("Output: " + sinkFile.getPath());
			decrypt(sourceFile, sinkFile, out);
		}
	}

	private void decrypt (File sourceFile, File sinkFile, PrintStream out) throws IOException, CMSException, CmdLineException {
		InputStream source = new FileInputStream(sourceFile);
		FileOutputStream sink = new FileOutputStream(sinkFile);
		try {
			decrypt(source, sink, out);
		}
		finally {
			IOUtils.closeQuietly(source);
			IOUtils.closeQuietly(sink);
		}
	}

	private void decrypt (InputStream source, OutputStream sink, PrintStream out) throws IOException, CMSException, CmdLineException {
		CMSEnvelopedDataParser cmsPar = new CMSEnvelopedDataParser(source);
		try {
			RecipientInformationStore recipientInfos = cmsPar.getRecipientInfos();
			Collection<RecipientInformation> recipients = recipientInfos.getRecipients();
			List<String> subjectKeyIdentifiers = new ArrayList<String>();
			for (RecipientInformation ri : recipients) {
				String subjectKeyIdentifier = new String(ri.getRID().getSubjectKeyIdentifier()).trim();
				subjectKeyIdentifiers.add(subjectKeyIdentifier);
				PrivateKey key = this.keys.get(subjectKeyIdentifier);
				if (key != null) {
					out.println("Decrypt key: " + subjectKeyIdentifier);
					decrypt(ri, sink, key, out);
					return;
				}
			}
			throw new CmdLineException(null, "No private key specified matches any subjectKeyIdentifiers: " + subjectKeyIdentifiers + ".");
		}
		finally {
			cmsPar.close();
		}
	}

	public void decrypt (RecipientInformation ri, OutputStream sink, PrivateKey key, PrintStream out) throws CMSException, IOException {
		CMSTypedStream cmsTs = ri.getContentStream(new JceKeyTransEnvelopedRecipient(key).setProvider(C.PROVIDER));
		InputStream source = cmsTs.getContentStream();
		try {
			long startTime = System.nanoTime();
			long sourceLength = IoHelper.copy(source, sink);
			long endTime = System.nanoTime();
			Benchmark.printBenchmark(sourceLength, "Decrypted", startTime, endTime, out);
		}
		finally {
			IOUtils.closeQuietly(source);
		}
	}

}
