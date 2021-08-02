package ealvatag.audio.mp4.atom;

import com.google.common.base.Preconditions;

import java.io.IOException;

import ealvatag.audio.Utils;
import ealvatag.audio.mp4.EncoderType;
import ealvatag.audio.mp4.Mp4AtomIdentifier;
import ealvatag.audio.mp4.Mp4AudioHeader;
import okio.BufferedSource;

/**
 * AlacBox ( Apple Lossless Codec information description box),
 * <p>
 * Normally occurs twice, the first ALAC contains the default  values, the second ALAC within contains the real
 * values for this audio.
 */
class Mp4AlacBox extends AbstractMp4Box {
    private static final int OTHER_FLAG_LENGTH = 4;

    // Some vars not used. I'm keeping as comments and let the optimizer deal with them.
    @SuppressWarnings("unused")
    Mp4AlacBox(Mp4BoxHeader alacBoxHeader,
               BufferedSource bufferedSource,
               Mp4AudioHeader audioHeader,
               boolean isInner) throws IOException {
        Preconditions.checkArgument(Mp4AtomIdentifier.ALAC.matches(alacBoxHeader.getId()));

        int dataSize = alacBoxHeader.getDataLength();
        bufferedSource.skip(OTHER_FLAG_LENGTH);
        int maxSamplePerFrame = bufferedSource.readInt();
        int unknown1 = Utils.convertUnsignedByteToInt(bufferedSource.readByte());
        int sampleSize = Utils.convertUnsignedByteToInt(bufferedSource.readByte());
        int historyMult = Utils.convertUnsignedByteToInt(bufferedSource.readByte());
        int initialHistory = Utils.convertUnsignedByteToInt(bufferedSource.readByte());
        int kModifier = Utils.convertUnsignedByteToInt(bufferedSource.readByte());
        int channels = Utils.convertUnsignedByteToInt(bufferedSource.readByte());
        int unknown2 = bufferedSource.readShort();
        int maxCodedFrameSize = bufferedSource.readInt();
        int bitRate = bufferedSource.readInt();
        int sampleRate = bufferedSource.readInt();
        dataSize -= OTHER_FLAG_LENGTH + 24;  // int + 6 bytes + short + 3 ints

        if (isInner) {
            audioHeader.setEncodingType(EncoderType.APPLE_LOSSLESS.getDescription());
            audioHeader.setChannelNumber(channels);
            audioHeader.setBitRate(bitRate / 1000);
            audioHeader.setBitsPerSample(sampleSize);

            bufferedSource.skip(dataSize);
        } else {
            Mp4AlacBox alacBox = null;
            while (dataSize >= Mp4BoxHeader.HEADER_LENGTH && alacBox == null) {
                Mp4BoxHeader childHeader = new Mp4BoxHeader(bufferedSource);
                switch (childHeader.getIdentifier()) {
                    case ALAC:
                        alacBox = new Mp4AlacBox(childHeader, bufferedSource, audioHeader, true);
                        break;
                    default:
                        bufferedSource.skip(childHeader.getDataLength());
                }
                dataSize -= childHeader.getLength();
            }
            if (dataSize > 0) {
                // TODO: 2/4/17
                bufferedSource.skip(dataSize);
            }
        }
    }
}
