package com.vaguehope.cmstoad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IoHelper {

	public IoHelper () {
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

}
