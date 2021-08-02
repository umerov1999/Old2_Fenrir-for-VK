package ealvatag.audio.asf.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This output stream wraps around another {@link OutputStream} and delegates
 * the write calls.<br>
 * Additionally all written bytes are counted and available by
 * {@link #getCount()}.
 *
 * @author Christian Laireiter
 */
public class CountingOutputstream extends OutputStream {

    /**
     * The stream to forward the write calls.
     */
    private final OutputStream wrapped;
    /**
     * Stores the amount of bytes written.
     */
    private long count;

    /**
     * Creates an instance which will delegate the write calls to the given
     * output stream.
     *
     * @param outputStream stream to wrap.
     */
    public CountingOutputstream(OutputStream outputStream) {
        assert outputStream != null;
        wrapped = outputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        wrapped.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        wrapped.flush();
    }

    /**
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] bytes) throws IOException {
        wrapped.write(bytes);
        count += bytes.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        wrapped.write(bytes, off, len);
        count += len;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int toWrite) throws IOException {
        wrapped.write(toWrite);
        count++;
    }

}
