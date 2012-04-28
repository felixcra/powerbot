package org.powerbot.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Paris
 */
public final class XOROutputStream extends FilterOutputStream {
	private final XOR xor;

	public XOROutputStream(final OutputStream out, final byte[] key, final int opmode) {
		super(out);
		xor = new XOR(key, opmode);
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		xor.rotate(b, off, len);
		super.out.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		super.flush();
	}

	@Override
	public void close() throws IOException {
		try {
			super.flush();
		} catch (final IOException ignored) {
		}
		super.close();
	}
}
