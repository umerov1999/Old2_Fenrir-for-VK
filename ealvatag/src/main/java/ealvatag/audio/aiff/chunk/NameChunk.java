package ealvatag.audio.aiff.chunk;

import java.io.IOException;
import java.nio.ByteBuffer;

import ealvatag.audio.aiff.AiffAudioHeader;
import ealvatag.audio.iff.ChunkHeader;

/**
 * Contains the name of the sampled sound. The Name Chunk is optional.
 * No more than one Name Chunk may exist within a FORM AIFF.
 */
public class NameChunk extends TextChunk {

    /**
     * @param chunkHeader     The header for this chunk
     * @param chunkData       The buffer from which the AIFF data are being read
     * @param aiffAudioHeader The AiffAudioHeader into which information is stored
     */
    public NameChunk(ChunkHeader chunkHeader, ByteBuffer chunkData, AiffAudioHeader aiffAudioHeader) {
        super(chunkHeader, chunkData, aiffAudioHeader);
    }

    @Override
    public boolean readChunk() throws IOException {
        aiffAudioHeader.setName(readChunkText());
        return true;
    }
}
