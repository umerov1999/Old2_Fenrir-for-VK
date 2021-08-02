package ealvatag.audio.asf.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ealvatag.audio.asf.util.Utils;
import ealvatag.logging.ErrorMessage;

/**
 * This structure represents the data of the ASF language object.<br>
 * The language list is simply a listing of language codes which should comply
 * to RFC-1766.<br>
 * <b>Consider:</b> the index of a language is used by other entries in the ASF
 * metadata.
 *
 * @author Christian Laireiter
 */
public class LanguageList extends Chunk {

    /**
     * List of language codes, complying RFC-1766
     */
    private final List<String> languages = new ArrayList<>();

    /**
     * Creates an instance.
     *
     * @param pos  position within the ASF file.
     * @param size size of the chunk
     */
    public LanguageList(long pos, BigInteger size) {
        super(GUID.GUID_LANGUAGE_LIST, pos, size);
    }

    /**
     * This method adds a language.<br>
     *
     * @param language language code
     */
    public void addLanguage(String language) {
        if (language.length() < MetadataDescriptor.MAX_LANG_INDEX) {
            if (!languages.contains(language)) {
                languages.add(language);
            }
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(),
                    ErrorMessage.WMA_LENGTH_OF_LANGUAGE_IS_TOO_LARGE,
                    language.length() * 2 + 2));
        }
    }

    /**
     * Returns the language code at the specified index.
     *
     * @param index the index of the language code to get.
     * @return the language code at given index.
     */
    public String getLanguage(int index) {
        return languages.get(index);
    }

    /**
     * Returns the amount of stored language codes.
     *
     * @return number of stored language codes.
     */
    int getLanguageCount() {
        return languages.size();
    }

    /**
     * Returns all language codes in list.
     *
     * @return list of language codes.
     */
    public List<String> getLanguages() {
        return new ArrayList<>(languages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prettyPrint(String prefix) {
        StringBuilder result = new StringBuilder(super.prettyPrint(prefix));
        for (int i = 0; i < getLanguageCount(); i++) {
            result.append(prefix);
            result.append("  |-> ");
            result.append(i);
            result.append(" : ");
            result.append(getLanguage(i));
            result.append(Utils.LINE_SEPARATOR);
        }
        return result.toString();
    }

    /**
     * Removes the language entry at specified index.
     *
     * @param index index of language to remove.
     */
    void removeLanguage(@SuppressWarnings("SameParameterValue") int index) {
        languages.remove(index);
    }
}
