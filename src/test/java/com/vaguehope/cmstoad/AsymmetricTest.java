package com.vaguehope.cmstoad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsymmetricTest {

	private static final String SUBJECT_KEY_IDENTIFIER_0 = "desu";
	private static final String SUBJECT_KEY_IDENTIFIER_1 = "foobar";

	private static final File WORK_DIR = new File("/tmp/enc");
	private static final File SOURCE_FILE = new File(WORK_DIR, "source.png");
	private static final File SINK_ENC_FILE = new File(WORK_DIR, "enc.png");
	private static final File SINK_PLAIN_FILE_0 = new File(WORK_DIR, "plain0.png");
	private static final File SINK_PLAIN_FILE_1 = new File(WORK_DIR, "plain1.png");

	private static BouncyCastleProvider PROVIDER;
	private static Map<String, KeyPair> KEY_PAIRS = new HashMap<String, KeyPair>();
	private static Map<String, File> SINK_PLAIN_FILES = new HashMap<String, File>();

	@BeforeClass
	public static void beforeClass() throws Exception {
		PROVIDER = new BouncyCastleProvider();
		Security.addProvider(PROVIDER);

		KEY_PAIRS.put(SUBJECT_KEY_IDENTIFIER_0, generateKeyPair(1024, SUBJECT_KEY_IDENTIFIER_0, WORK_DIR));
		KEY_PAIRS.put(SUBJECT_KEY_IDENTIFIER_1, generateKeyPair(2048, SUBJECT_KEY_IDENTIFIER_1, WORK_DIR));

		SINK_PLAIN_FILES.put(SUBJECT_KEY_IDENTIFIER_0, SINK_PLAIN_FILE_0);
		SINK_PLAIN_FILES.put(SUBJECT_KEY_IDENTIFIER_1, SINK_PLAIN_FILE_1);

		SINK_ENC_FILE.delete();
		SINK_PLAIN_FILE_0.delete();
		SINK_PLAIN_FILE_1.delete();
	}

	private static KeyPair generateKeyPair(int keysize, String name, File dir) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA", "BC");
		keygen.initialize(keysize);
		KeyPair keyPair = keygen.generateKeyPair();

		PEMWriter pubW = new PEMWriter(new FileWriter(new File(dir, name + ".public.pem")));
		pubW.writeObject(keyPair.getPublic());
		pubW.close();

		PEMWriter privW = new PEMWriter(new FileWriter(new File(dir, name + ".private.pem")));
		privW.writeObject(keyPair.getPrivate());
		privW.close();

		return keyPair;
	}

	@Test
	public void a_itEncryptsWithCms() throws Exception {
		CMSEnvelopedDataStreamGenerator cmsGen = new CMSEnvelopedDataStreamGenerator();
		for (Entry<String, KeyPair> kp : KEY_PAIRS.entrySet()) {
			cmsGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(kp.getKey().getBytes(), kp.getValue().getPublic()).setProvider(PROVIDER));
		}

		InputStream in = new FileInputStream(SOURCE_FILE);
		OutputStream out = cmsGen.open(
				new FileOutputStream(SINK_ENC_FILE),
				new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC).setProvider(PROVIDER).build()
				);

		try {
			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	@Test
	public void b_itDecryptsWithCms() throws Exception {
		CMSEnvelopedDataParser cmsPar = new CMSEnvelopedDataParser(new FileInputStream(SINK_ENC_FILE));

		Map<String, OutputStream> outs = new HashMap<String, OutputStream>();
		for (Entry<String, File> f : SINK_PLAIN_FILES.entrySet()) {
			outs.put(f.getKey(), new FileOutputStream(f.getValue()));
		}

		try {
			RecipientInformationStore recipientInfos = cmsPar.getRecipientInfos();
			Collection<RecipientInformation> recipients = recipientInfos.getRecipients();
			for (RecipientInformation ri : recipients) {
				String subjectKeyIdentifier = new String(ri.getRID().getSubjectKeyIdentifier()).trim();

				KeyPair kp = KEY_PAIRS.get(subjectKeyIdentifier);
				if (kp != null) {
					CMSEnvelopedDataParser cmsPar2 = new CMSEnvelopedDataParser(new FileInputStream(SINK_ENC_FILE));
					CMSTypedStream cmsTs = cmsPar2.getRecipientInfos().get(ri.getRID()).getContentStream(new JceKeyTransEnvelopedRecipient(kp.getPrivate()).setProvider(PROVIDER));
					InputStream in = cmsTs.getContentStream();
					OutputStream out = outs.get(subjectKeyIdentifier);

					byte[] buffer = new byte[8192];
					int bytesRead;
					while ((bytesRead = in.read(buffer)) != -1) {
						out.write(buffer, 0, bytesRead);
					}

				}
				else {
					System.err.println("Unknown subjectKeyIdentifier: '" + subjectKeyIdentifier + "'.");
				}
			}
		}
		finally {
			for (OutputStream o : outs.values()) {
				IOUtils.closeQuietly(o);
			}
			cmsPar.close();
		}
	}

}
