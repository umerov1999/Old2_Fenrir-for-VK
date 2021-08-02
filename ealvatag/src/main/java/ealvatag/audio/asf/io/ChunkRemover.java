package ealvatag.audio.asf.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import ealvatag.audio.asf.data.GUID;
import ealvatag.audio.asf.util.Utils;

/**
 * This {@link ChunkModifier} implementation is meant to remove selected chunks.<br>
 *
 * @author Christian Laireiter
 */
@SuppressWarnings("ManualArrayToCollectionCopy")
public class ChunkRemover implements ChunkModifier {

    /**
     * Stores the GUIDs, which are about to be removed by this modifier.<br>
     */
    private final Set<GUID> toRemove;

    /**
     * Creates an instance, for removing selected chunks.<br>
     *
     * @param guids the GUIDs which are about to be removed by this modifier.
     */
    public ChunkRemover(GUID... guids) {
        toRemove = new HashSet<GUID>();
        for (GUID current : guids) {
            toRemove.add(current);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isApplicable(GUID guid) {
        return toRemove.contains(guid);
    }

    /**
     * {@inheritDoc}
     */
    public ModificationResult modify(GUID guid, InputStream source, OutputStream destination) throws IOException {
        ModificationResult result;
        if (guid == null) {
            // Now a chunk should be added, however, this implementation is for
            // removal.
            result = new ModificationResult(0, 0);
        } else {
            assert isApplicable(guid);
            // skip the chunk length minus 24 bytes for the already read length
            // and the guid.
            long chunkLen = Utils.readUINT64(source);
            source.skip(chunkLen - 24);
            result = new ModificationResult(-1, -1 * chunkLen, guid);
        }
        return result;
    }

}
