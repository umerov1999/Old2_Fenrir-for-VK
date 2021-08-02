/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Rapha�l Slinckx <raphael@slinckx.net>
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
package ealvatag.audio.wav;

import java.io.IOException;
import java.nio.channels.FileChannel;

import ealvatag.audio.AudioFileReader2;
import ealvatag.audio.GenericAudioHeader;
import ealvatag.audio.exceptions.CannotReadException;
import ealvatag.tag.TagFieldContainer;
import ealvatag.tag.TagOptionSingleton;
import ealvatag.tag.wav.WavTag;

/**
 * Reads Audio and Metadata information contained in Wav file.
 */
public class WavFileReader extends AudioFileReader2 {
    public WavFileReader() {

    }

    protected GenericAudioHeader getEncodingInfo(FileChannel channel, String fileName) throws CannotReadException, IOException {
        return new WavInfoReader(fileName).read(channel);
    }

    @Override
    protected TagFieldContainer getTag(FileChannel channel, String fileName, boolean ignoreArtwork) throws IOException, CannotReadException {
        WavTag tag = new WavTagReader(fileName).read(channel);
        switch (TagOptionSingleton.getInstance().getWavOptions()) {
            case READ_ID3_ONLY_AND_SYNC:
            case READ_ID3_UNLESS_ONLY_INFO_AND_SYNC:
            case READ_INFO_ONLY_AND_SYNC:
            case READ_INFO_UNLESS_ONLY_ID3_AND_SYNC:
                tag.syncTagsAfterRead();
        }
        return tag;
    }
}
