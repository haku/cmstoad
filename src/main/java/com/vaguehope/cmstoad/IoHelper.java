package com.vaguehope.cmstoad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IoHelper {

	private static final String STREAM = "-";

	private IoHelper () {
		throw new AssertionError();
	}

	public static long copy (InputStream source, OutputStream sink) throws IOException {
		byte[] buffer = new byte[C.DEFAULT_COPY_BUFFER_SIZE];
		long bytesReadTotal = 0L;
		int bytesRead;
		while ((bytesRead = source.read(buffer)) != -1) {
			sink.write(buffer, 0, bytesRead);
			bytesReadTotal += bytesRead;
		}
		return bytesReadTotal;
	}

	public static boolean fileExists (File f) {
		return IoHelper.STREAM.equals(f.getName()) || f.exists();
	}

	public static OutputStream resolveOutputFile (File f) throws IOException {
		if (STREAM.equals(f.getName())) return System.out;
		return new FileOutputStream(f);
	}

	public static InputStream resolveInputFile (File f) throws IOException {
		if (STREAM.equals(f.getName())) return System.in;
		return new FileInputStream(f);
	}

}
