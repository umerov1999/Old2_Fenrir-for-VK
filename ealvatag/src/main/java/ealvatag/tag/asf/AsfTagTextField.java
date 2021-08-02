package ealvatag.tag.asf;

import java.nio.charset.Charset;

import ealvatag.audio.asf.data.AsfHeader;
import ealvatag.audio.asf.data.MetadataDescriptor;
import ealvatag.audio.asf.util.Utils;
import ealvatag.tag.TagTextField;

/**
 * Represents a tag text field for ASF fields.<br>
 *
 * @author Christian Laireiter
 */
public class AsfTagTextField extends AsfTagField implements TagTextField {

    /**
     * Creates a tag text field and assigns the string value.
     *
     * @param field ASF field to represent.
     * @param value the value to assign.
     */
    public AsfTagTextField(AsfFieldKey field, String value) {
        super(field);
        toWrap.setString(value);
    }

    /**
     * Creates an instance.
     *
     * @param source The metadata descriptor, whose content is published.<br>
     *               Must not be of type {@link MetadataDescriptor#TYPE_BINARY}.
     */
    public AsfTagTextField(MetadataDescriptor source) {
        super(source);
        if (source.getType() == MetadataDescriptor.TYPE_BINARY) {
            throw new IllegalArgumentException(
                    "Cannot interpret binary as string.");
        }
    }

    /**
     * Creates a tag text field and assigns the string value.
     *
     * @param fieldKey The fields identifier.
     * @param value    the value to assign.
     */
    public AsfTagTextField(String fieldKey, String value) {
        super(fieldKey);
        toWrap.setString(value);
    }

    @Override
    public String getContent() {
        return getDescriptor().getString();
    }

    @Override
    public void setContent(String content) {
        getDescriptor().setString(content);
    }

    @Override
    public Charset getEncoding() {
        return AsfHeader.ASF_CHARSET;
    }

    @Override
    public void setEncoding(Charset encoding) {
        if (!AsfHeader.ASF_CHARSET.equals(encoding)) {
            throw new IllegalArgumentException(
                    "Only UTF-16LE is possible with ASF.");
        }
    }

    /**
     * @return true if blank or only contains whitespace
     */
    @Override
    public boolean isEmpty() {
        return Utils.isBlank(getContent());
    }
}
