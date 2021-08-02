package ealvatag.audio.asf.io;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ealvatag.audio.asf.data.GUID;

/**
 * Structure to tell the differences occurred by altering a chunk.
 *
 * @author Christian Laireiter
 */
final class ModificationResult {

    /**
     * Stores the difference of bytes.<br>
     */
    private final long byteDifference;

    /**
     * Stores the difference of the amount of chunks.<br>
     * &quot;-1&quot; if the chunk disappeared upon modification.<br>
     * &quot;0&quot; if the chunk was just modified.<br>
     * &quot;1&quot; if a chunk has been created.<br>
     */
    private final int chunkDifference;

    /**
     * Stores all GUIDs, which have been read.<br>
     */
    private final Set<GUID> occuredGUIDs = new HashSet<GUID>();

    /**
     * Creates an instance.<br>
     *
     * @param chunkCountDiff amount of chunks appeared, disappeared
     * @param bytesDiffer    amount of bytes added or removed.
     * @param occurred       all GUIDs which have been occurred, during processing
     */
    public ModificationResult(int chunkCountDiff, long bytesDiffer, GUID... occurred) {
        assert occurred != null && occurred.length > 0;
        chunkDifference = chunkCountDiff;
        byteDifference = bytesDiffer;
        occuredGUIDs.addAll(Arrays.asList(occurred));
    }

    /**
     * Creates an instance.<br>
     *
     * @param chunkCountDiff amount of chunks appeared, disappeared
     * @param bytesDiffer    amount of bytes added or removed.
     * @param occurred       all GUIDs which have been occurred, during processing
     */
    public ModificationResult(int chunkCountDiff, long bytesDiffer, Set<GUID> occurred) {
        chunkDifference = chunkCountDiff;
        byteDifference = bytesDiffer;
        occuredGUIDs.addAll(occurred);
    }

    /**
     * Returns the difference of bytes.
     *
     * @return the byte difference
     */
    public long getByteDifference() {
        return byteDifference;
    }

    /**
     * Returns the difference of the amount of chunks.
     *
     * @return the chunk count difference
     */
    public int getChunkCountDifference() {
        return chunkDifference;
    }

    /**
     * Returns all GUIDs which have been occurred during processing.
     *
     * @return see description.s
     */
    public Set<GUID> getOccuredGUIDs() {
        return new HashSet<GUID>(occuredGUIDs);
    }

}
