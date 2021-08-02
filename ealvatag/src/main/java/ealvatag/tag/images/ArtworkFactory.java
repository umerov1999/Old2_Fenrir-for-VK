package ealvatag.tag.images;

import java.io.File;
import java.io.IOException;

import ealvatag.audio.flac.metadatablock.MetadataBlockDataPicture;

/**
 * Get appropriate Artwork class
 */
public class ArtworkFactory {

    public static Artwork getNew() {
        return new StandardArtwork();
    }

    public static Artwork createArtworkFromMetadataBlockDataPicture(MetadataBlockDataPicture coverArt) {
        return StandardArtwork.createArtworkFromMetadataBlockDataPicture(coverArt);
    }

    public static Artwork createArtworkFromFile(File file) throws IOException {
        return StandardArtwork.createArtworkFromFile(file);
    }

    public static Artwork createLinkedArtworkFromURL(String link) throws IOException {
        return StandardArtwork.createLinkedArtworkFromURL(link);
    }
}
