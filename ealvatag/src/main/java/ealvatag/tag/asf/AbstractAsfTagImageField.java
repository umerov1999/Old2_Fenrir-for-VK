package ealvatag.tag.asf;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ealvatag.audio.asf.data.MetadataDescriptor;
import ealvatag.tag.TagField;

/**
 * An <code>AbstractAsfTagImageField</code> is an abstract class for representing tag
 * fields containing image data.<br>
 *
 * @author Christian Laireiter
 */
abstract class AbstractAsfTagImageField extends AsfTagField {

    /**
     * Creates a image tag field.
     *
     * @param field the ASF field that should be represented.
     */
    public AbstractAsfTagImageField(AsfFieldKey field) {
        super(field);
    }

    /**
     * Creates an instance.
     *
     * @param source The descriptor which should be represented as a {@link TagField}.
     */
    public AbstractAsfTagImageField(MetadataDescriptor source) {
        super(source);
    }

    /**
     * Creates a tag field.
     *
     * @param fieldKey The field identifier to use.
     */
    public AbstractAsfTagImageField(String fieldKey) {
        super(fieldKey);
    }

    /**
     * This method returns an image instance from the
     * {@linkplain #getRawImageData() image content}.
     *
     * @return the image instance
     * @throws IllegalArgumentException
     */
    public Bitmap getImage() throws IllegalArgumentException {
        byte[] data = getRawImageData();
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * Returns the size of the {@linkplain #getRawImageData() image data}.<br>
     *
     * @return image data size in bytes.
     */
    public abstract int getImageDataSize();

    /**
     * Returns the raw data of the represented image.<br>
     *
     * @return raw image data
     */
    public abstract byte[] getRawImageData();

}
