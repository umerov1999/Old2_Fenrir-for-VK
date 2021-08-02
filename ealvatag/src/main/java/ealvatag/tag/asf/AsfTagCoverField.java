package ealvatag.tag.asf;

import static ealvatag.logging.EalvaTagLog.LogLevel.WARN;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import ealvatag.audio.Utils;
import ealvatag.audio.asf.data.AsfHeader;
import ealvatag.audio.asf.data.MetadataDescriptor;
import ealvatag.logging.EalvaTagLog;
import ealvatag.logging.EalvaTagLog.JLogger;
import ealvatag.logging.EalvaTagLog.JLoggers;
import ealvatag.logging.ErrorMessage;
import ealvatag.tag.id3.valuepair.ImageFormats;

/**
 * Encapsulates the WM/Pictures provides some convenience methods for decoding
 * the binary data it contains
 * <p>
 * The value of a WM/Pictures metadata descriptor is as follows:
 * <p>
 * byte0 Picture Type byte1-4 Length of the image data mime type encoded as
 * UTF-16LE null byte null byte description encoded as UTF-16LE (optional) null
 * byte null byte image data
 */
public class AsfTagCoverField extends AbstractAsfTagImageField {
    /**
     * Logger Object
     */
    private final static JLogger LOG = JLoggers.get(AsfTagCoverField.class, EalvaTagLog.MARKER);

    /**
     * Description
     */
    private String description;

    /**
     * We need this to retrieve the buffered image, if required
     */
    private int endOfName;

    /**
     * Image Data Size as read
     */
    private int imageDataSize;

    /**
     * Mimetype of binary
     */
    private String mimeType;

    /**
     * Picture Type
     */
    private int pictureType;

    /**
     * Create New Image Field
     *
     * @param imageData
     * @param pictureType
     * @param description
     * @param mimeType
     */
    public AsfTagCoverField(byte[] imageData, int pictureType,
                            String description, String mimeType) {
        super(new MetadataDescriptor(AsfFieldKey.COVER_ART.getFieldName(),
                MetadataDescriptor.TYPE_BINARY));
        getDescriptor()
                .setBinaryValue(
                        createRawContent(imageData, pictureType, description,
                                mimeType));
    }

    /**
     * Creates an instance from a metadata descriptor
     *
     * @param source The metadata descriptor, whose content is published.<br>
     */
    public AsfTagCoverField(MetadataDescriptor source) {
        super(source);

        if (!source.getName().equals(AsfFieldKey.COVER_ART.getFieldName())) {
            throw new IllegalArgumentException(
                    "Descriptor description must be WM/Picture");
        }
        if (source.getType() != MetadataDescriptor.TYPE_BINARY) {
            throw new IllegalArgumentException("Descriptor type must be binary");
        }

        try {
            processRawContent();
        } catch (UnsupportedEncodingException uee) {
            // Should never happen
            throw new RuntimeException(uee); // NOPMD by Christian Laireiter on 5/9/09 5:45 PM
        }
    }

    private byte[] createRawContent(byte[] data,
                                    int pictureType,
                                    String description,
                                    String mimeType) { // NOPMD by Christian Laireiter on 5/9/09 5:46 PM
        this.description = description;
        imageDataSize = data.length;
        this.pictureType = pictureType;
        this.mimeType = mimeType;

        // Get Mimetype from data if not already setField
        if (mimeType == null) {
            mimeType = ImageFormats.getMimeTypeForBinarySignature(data);
            // Couldnt identify lets default to png because probably error in
            // code because not 100% sure how to identify
            // formats
            if (mimeType == null) {
                LOG.log(WARN, ErrorMessage.GENERAL_UNIDENITIFED_IMAGE_FORMAT);
                mimeType = ImageFormats.MIME_TYPE_PNG;
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // PictureType
        baos.write(pictureType);

        // ImageDataSize
        baos.write(Utils
                .getSizeLEInt32(data.length), 0, 4);

        // mimetype
        byte[] mimeTypeData;
        try {
            mimeTypeData = mimeType.getBytes(AsfHeader.ASF_CHARSET.name());
        } catch (UnsupportedEncodingException uee) {
            // Should never happen
            throw new RuntimeException("Unable to find encoding:" // NOPMD by Christian Laireiter on 5/9/09 5:45 PM
                    + AsfHeader.ASF_CHARSET.name());
        }
        baos.write(mimeTypeData, 0, mimeTypeData.length);

        // Seperator
        baos.write(0x00);
        baos.write(0x00);

        // description
        if (description != null && description.length() > 0) {
            byte[] descriptionData;
            try {
                descriptionData = description.getBytes(AsfHeader.ASF_CHARSET
                        .name());
            } catch (UnsupportedEncodingException uee) {
                // Should never happen
                throw new RuntimeException("Unable to find encoding:" // NOPMD by Christian Laireiter on 5/9/09 5:45 PM
                        + AsfHeader.ASF_CHARSET.name());
            }
            baos.write(descriptionData, 0, descriptionData.length);
        }

        // Seperator (always write whther or not we have descriptor field)
        baos.write(0x00);
        baos.write(0x00);

        // Image data
        baos.write(data, 0, data.length);

        return baos.toByteArray();
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int getImageDataSize() {
        return imageDataSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getPictureType() {
        return pictureType;
    }

    /**
     * @return the raw image data only
     */
    @Override
    public byte[] getRawImageData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(getRawContent(), endOfName, toWrap
                .getRawDataSize()
                - endOfName);
        return baos.toByteArray();
    }

    private void processRawContent() throws UnsupportedEncodingException {
        // PictureType
        pictureType = getRawContent()[0];

        // ImageDataSize
        imageDataSize = Utils.getIntLE(getRawContent(), 1, 2);

        // Set Count to after picture type,datasize and two byte nulls
        int count = 5;
        mimeType = null;
        description = null; // Optional
        int endOfMimeType = 0;

        while (count < getRawContent().length - 1) {
            if (getRawContent()[count] == 0 && getRawContent()[count + 1] == 0) {
                if (mimeType == null) {
                    mimeType = new String(getRawContent(), 5, (count) - 5,
                            StandardCharsets.UTF_16LE);
                    endOfMimeType = count + 2;
                } else if (description == null) {
                    description = new String(getRawContent(),
                            endOfMimeType, count - endOfMimeType, StandardCharsets.UTF_16LE);
                    endOfName = count + 2;
                    break;
                }
            }
            count += 2; // keep on two byte word boundary
        }
    }

}
