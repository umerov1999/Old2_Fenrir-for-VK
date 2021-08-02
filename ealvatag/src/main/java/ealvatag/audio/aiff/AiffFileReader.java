package ealvatag.audio.aiff;

import java.io.IOException;
import java.nio.channels.FileChannel;

import ealvatag.audio.AudioFileReader2;
import ealvatag.audio.GenericAudioHeader;
import ealvatag.audio.exceptions.CannotReadException;
import ealvatag.tag.TagFieldContainer;

/**
 * Reads Audio and Metadata information contained in Aiff file.
 */
public class AiffFileReader extends AudioFileReader2 {
    private final AiffInfoReader ir = new AiffInfoReader();
    private final AiffTagReader im = new AiffTagReader();

    @Override
    protected GenericAudioHeader getEncodingInfo(FileChannel channel, String fileName) throws CannotReadException, IOException {
        return ir.read(channel, fileName);
    }

    @Override
    protected TagFieldContainer getTag(FileChannel channel, String fileName, boolean ignoreArtwork) throws CannotReadException, IOException {
        return im.read(channel, fileName);
    }
}
