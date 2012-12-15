package com.vaguehope.cmstoad;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.kohsuke.args4j.CmdLineException;

public class Info implements CliAction {

	private final List<File> sourceFiles;

	public Info (List<File> files) {
		this.sourceFiles = files;
	}

	public Info (Args args) throws CmdLineException {
		this(args.getFiles(true, true));
	}

	@Override
	public void run (PrintStream out, PrintStream err) throws IOException, CMSException {
		for (File sourceFile : this.sourceFiles) {
			out.println("File: " + sourceFile.getPath());
			info(sourceFile, out);
			out.println();
		}
	}

	private static void info (File sourceFile, PrintStream out) throws IOException, CMSException {
		InputStream source = new FileInputStream(sourceFile);
		try {
			info(source, out);
		}
		finally {
			IOUtils.closeQuietly(source);
		}
	}

	private static void info (InputStream source, PrintStream out) throws IOException, CMSException {
		CMSEnvelopedDataParser cmsPar = new CMSEnvelopedDataParser(source);
		try {
			printInfo(out, "  encryptionAlgOID=", cmsPar.getEncryptionAlgOID());
			RecipientInformationStore recipientInfos = cmsPar.getRecipientInfos();
			Collection<RecipientInformation> recipients = recipientInfos.getRecipients();
			for (RecipientInformation ri : recipients) {
				String subjectKeyIdentifier = new String(ri.getRID().getSubjectKeyIdentifier()).trim();
				printInfo(out, "  Key: ", subjectKeyIdentifier);
				printInfo(out, "    keyEncryptionAlgOID=", ri.getKeyEncryptionAlgOID());
			}
		}
		finally {
			cmsPar.close();
		}
	}

	private static void printInfo (PrintStream out, String... args) {
		for (String a : args) {
			out.print(a);
		}
		out.println();
	}

}
