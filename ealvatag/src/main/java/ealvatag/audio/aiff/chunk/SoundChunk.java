package ealvatag.audio.aiff.chunk;

import java.io.IOException;
import java.nio.ByteBuffer;

import ealvatag.audio.iff.Chunk;
import ealvatag.audio.iff.ChunkHeader;

/**
 * Sound chunk.
 * Doesn't actually read the content, but skips it.
 */
public class SoundChunk extends Chunk {

    /**
     * @param chunkHeader The header for this chunk
     * @param chunkData   The file from which the AIFF data are being read
     */
    public SoundChunk(ChunkHeader chunkHeader, ByteBuffer chunkData) {
        super(chunkData, chunkHeader);
    }

    /**
     * Reads a chunk and extracts information.
     *
     * @return <code>false</code> if the chunk is structurally
     * invalid, otherwise <code>true</code>
     */
    public boolean readChunk() throws IOException {
        return true;
    }

}
