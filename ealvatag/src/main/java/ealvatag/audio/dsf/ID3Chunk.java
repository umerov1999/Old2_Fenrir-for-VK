package ealvatag.audio.dsf;

import static ealvatag.logging.EalvaTagLog.LogLevel.WARN;

import java.nio.ByteBuffer;

import ealvatag.audio.Utils;
import ealvatag.logging.EalvaTagLog;
import ealvatag.logging.EalvaTagLog.JLogger;
import ealvatag.logging.EalvaTagLog.JLoggers;

/**
 * Created by Paul on 28/01/2016.
 */
@SuppressWarnings("DefaultFileTemplate")
public class ID3Chunk {
    private static final JLogger LOG = JLoggers.get(ID3Chunk.class, EalvaTagLog.MARKER);

    private final ByteBuffer dataBuffer;

    private ID3Chunk(ByteBuffer dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

    public static ID3Chunk readChunk(ByteBuffer dataBuffer) {
        String type = Utils.readThreeBytesAsChars(dataBuffer);
        if (DsfChunkType.ID3.getCode().equals(type)) {
            return new ID3Chunk(dataBuffer);
        }
        LOG.log(WARN, "Invalid type:%s where expected ID3 tag", type);
        return null;
    }

    public ByteBuffer getDataBuffer() {
        return dataBuffer;
    }
}
