package com.vaguehope.cmstoad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;

public class Benchmark implements CliAction {

	private static final int DEFAULT_BENCHMARK_KEYSIZE = 1024;
	private static final int INITIAL_SOURCE_LENGTH = 1024 * 1024;
	private static final int SOURCE_LENGTH_MULTIPLIER = 2;
	private static final int TARGET_DURATION_SECONDS = 30;
	private static final long NANOS_IN_SECOND = 1000000000L;

	private final Random random;
	private final OutputStream devNull;
	private final PrintStream devNullW;

	public Benchmark () {
		this.random = new Random();
		this.devNull = new NullOutputStream();
		this.devNullW = new PrintStream(this.devNull);
	}

	@Override
	public void run (PrintStream out, PrintStream err) throws NoSuchAlgorithmException, OperatorCreationException, CMSException, IOException {
		Map<String, PublicKey> keys = makeKeyMap();
		long sourceLength = INITIAL_SOURCE_LENGTH;
		while (true) {
			InputStream source = new RandomDataInputStream(this.random, sourceLength);
			long startTime = System.nanoTime();
			Encrypt.encrypt(keys, source, this.devNull, this.devNullW);
			long endTime = System.nanoTime();
			printBenchmark(sourceLength, "Encrypted", startTime, endTime, out);

			if (TimeUnit.NANOSECONDS.toSeconds(endTime - startTime) > TARGET_DURATION_SECONDS) break;
			sourceLength *= SOURCE_LENGTH_MULTIPLIER;
		}
	}

	public static void printBenchmark (long sourceLength, String action, long startTime, long endTime, PrintStream out) {
		long durationNanos = endTime - startTime;
		long durationSeconds = TimeUnit.NANOSECONDS.toSeconds(durationNanos);
		long bytesPerSecond = bytesPerSecond(sourceLength, durationNanos);
		out.println(MessageFormat.format(
				"{0} {1} in {2} seconds = {3} per second.",
				action,
				FileUtils.byteCountToDisplaySize(sourceLength),
				String.valueOf(durationSeconds),
				FileUtils.byteCountToDisplaySize(bytesPerSecond)));
	}

	public static long bytesPerSecond (long sourceLength, long durationNanos) {
		return (long) ((sourceLength / (double) durationNanos) * NANOS_IN_SECOND);
	}

	private static Map<String, PublicKey> makeKeyMap () throws NoSuchAlgorithmException {
		Map<String, PublicKey> keys = new HashMap<String, PublicKey>();
		keys.put("desu", makeKeyPair().getPublic());
		return keys;
	}

	private static KeyPair makeKeyPair () throws NoSuchAlgorithmException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(C.DEFAULT_KEY_TYPE, C.PROVIDER);
		keygen.initialize(DEFAULT_BENCHMARK_KEYSIZE);
		return keygen.generateKeyPair();
	}

	/**
	 * This is not thread safe.
	 */
	private static class RandomDataInputStream extends InputStream {

		private final Random r;
		private long length;

		public RandomDataInputStream (Random r, long aproxSourceLength) {
			this.r = r;
			this.length = aproxSourceLength;
		}

		@Override
		public int read () throws IOException {
			if (this.length <= 0) return -1;
			this.length -= 1;
			return this.r.nextInt();
		}

		@Override
		public int read (byte[] b) throws IOException {
			if (this.length <= 0) return -1;
			this.length -= b.length;
			this.r.nextBytes(b);
			return b.length;
		}

	}

}
