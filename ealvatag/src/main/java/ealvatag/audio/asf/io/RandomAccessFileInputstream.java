package ealvatag.audio.asf.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Wraps a {@link RandomAccessFile} into an {@link InputStream}.<br>
 *
 * @author Christian Laireiter
 */
public final class RandomAccessFileInputstream extends InputStream {

    /**
     * The file access to read from.<br>
     */
    private final RandomAccessFile source;

    /**
     * Creates an instance that will provide {@link InputStream} functionality
     * on the given {@link RandomAccessFile} by delegating calls.<br>
     *
     * @param file The file to read.
     */
    public RandomAccessFileInputstream(RandomAccessFile file) {
        if (file == null) {
            throw new IllegalArgumentException("null");
        }
        source = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        return source.read();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] buffer, int off, int len) throws IOException {
        return source.read(buffer, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(long amount) throws IOException {
        if (amount < 0) {
            throw new IllegalArgumentException("invalid negative value");
        }
        long left = amount;
        while (left > Integer.MAX_VALUE) {
            source.skipBytes(Integer.MAX_VALUE);
            left -= Integer.MAX_VALUE;
        }
        return source.skipBytes((int) left);
    }

}
