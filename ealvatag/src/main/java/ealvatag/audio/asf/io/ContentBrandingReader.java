package ealvatag.audio.asf.io;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import ealvatag.audio.asf.data.Chunk;
import ealvatag.audio.asf.data.ContentBranding;
import ealvatag.audio.asf.data.GUID;
import ealvatag.audio.asf.util.Utils;

/**
 * This reader is used to read the content branding object of ASF streams.<br>
 *
 * @author Christian Laireiter
 * @see ealvatag.audio.asf.data.ContainerType#CONTENT_BRANDING
 * @see ContentBranding
 */
public class ContentBrandingReader implements ChunkReader {
    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = {GUID.GUID_CONTENT_BRANDING};

    /**
     * Should not be used for now.
     */
    protected ContentBrandingReader() {
        // NOTHING toDo
    }

    /**
     * {@inheritDoc}
     */
    public boolean canFail() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public GUID[] getApplyingIds() {
        return APPLYING.clone();
    }

    /**
     * {@inheritDoc}
     */
    public Chunk read(GUID guid, InputStream stream, long streamPosition) throws IOException {
        assert GUID.GUID_CONTENT_BRANDING.equals(guid);
        BigInteger chunkSize = Utils.readBig64(stream);
        long imageType = Utils.readUINT32(stream);
        assert imageType >= 0 && imageType <= 3 : imageType;
        long imageDataSize = Utils.readUINT32(stream);
        assert imageType > 0 || imageDataSize == 0 : imageDataSize;
        assert imageDataSize < Integer.MAX_VALUE;
        byte[] imageData = Utils.readBinary(stream, imageDataSize);
        long copyRightUrlLen = Utils.readUINT32(stream);
        String copyRight = new String(Utils.readBinary(stream, copyRightUrlLen));
        long imageUrlLen = Utils.readUINT32(stream);
        String imageUrl = new String(Utils.readBinary(stream, imageUrlLen));
        ContentBranding result = new ContentBranding(streamPosition, chunkSize);
        result.setImage(imageType, imageData);
        result.setCopyRightURL(copyRight);
        result.setBannerImageURL(imageUrl);
        return result;
    }

}
