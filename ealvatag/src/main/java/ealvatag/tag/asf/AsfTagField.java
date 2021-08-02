/*
 * Entagged Audio Tag library
 * Copyright (c) 2004-2005 Christian Laireiter <liree@web.de>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package ealvatag.tag.asf;

import ealvatag.audio.asf.data.MetadataDescriptor;
import ealvatag.tag.TagField;

/**
 * This class encapsulates a
 * {@link ealvatag.audio.asf.data.MetadataDescriptor}and provides access
 * to it. <br>
 * The metadata descriptor used for construction is copied.
 *
 * @author Christian Laireiter (liree)
 */
public class AsfTagField implements TagField, Cloneable {

    /**
     * This descriptor is wrapped.
     */
    protected MetadataDescriptor toWrap;

    /**
     * Creates a tag field.
     *
     * @param field the ASF field that should be represented.
     */
    public AsfTagField(AsfFieldKey field) {
        assert field != null;
        toWrap = new MetadataDescriptor(field.getHighestContainer(), field
                .getFieldName(), MetadataDescriptor.TYPE_STRING);
    }

    /**
     * Creates an instance.
     *
     * @param source The descriptor which should be represented as a
     *               {@link TagField}.
     */
    public AsfTagField(MetadataDescriptor source) {
        assert source != null;
        // XXX Copy ? maybe not really.
        toWrap = source.createCopy();
    }

    /**
     * Creates a tag field.
     *
     * @param fieldKey The field identifier to use.
     */
    public AsfTagField(String fieldKey) {
        assert fieldKey != null;
        toWrap = new MetadataDescriptor(AsfFieldKey.getAsfFieldKey(
                fieldKey).getHighestContainer(), fieldKey,
                MetadataDescriptor.TYPE_STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * {@inheritDoc}
     */
    public void copyContent(TagField field) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Returns the wrapped metadata descriptor (which actually stores the
     * values).
     *
     * @return the wrapped metadata descriptor
     */
    public MetadataDescriptor getDescriptor() {
        return toWrap;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return toWrap.getName();
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getRawContent() {
        return toWrap.getRawData();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBinary() {
        return toWrap.getType() == MetadataDescriptor.TYPE_BINARY;
    }

    /**
     * {@inheritDoc}
     */
    public void isBinary(boolean value) {
        if (!value && isBinary()) {
            throw new UnsupportedOperationException("No conversion supported.");
        }
        toWrap.setBinaryValue(toWrap.getRawData());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCommon() {
        // HashSet is safe against null comparison
        return AsfTag.COMMON_FIELDS.contains(AsfFieldKey.getAsfFieldKey(getId()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return toWrap.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toWrap.getString();
    }

}
