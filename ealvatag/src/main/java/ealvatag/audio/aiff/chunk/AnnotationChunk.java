package ealvatag.audio.aiff.chunk;

import java.io.IOException;
import java.nio.ByteBuffer;

import ealvatag.audio.aiff.AiffAudioHeader;
import ealvatag.audio.iff.ChunkHeader;

/**
 * Contains a comment. Use of this chunk is discouraged within FORM AIFF. The more powerful {@link CommentsChunk}
 * should be used instead. The Annotation Chunk is optional. Many Annotation Chunks may exist within a FORM AIFF.
 *
 * @see CommentsChunk
 */
public class AnnotationChunk extends TextChunk {

    /**
     * @param chunkHeader     The header for this chunk
     * @param chunkData       The buffer from which the AIFF data are being read
     * @param aiffAudioHeader The AiffAudioHeader into which information is stored
     */
    public AnnotationChunk(ChunkHeader chunkHeader, ByteBuffer chunkData, AiffAudioHeader aiffAudioHeader) {
        super(chunkHeader, chunkData, aiffAudioHeader);
    }

    @Override
    public boolean readChunk() throws IOException {
        aiffAudioHeader.addAnnotation(readChunkText());
        return true;
    }

}
