package ealvatag.audio.asf.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import ealvatag.audio.asf.util.Utils;

/**
 * @author eric
 */
public class EncryptionChunk extends Chunk {
    /**
     * The read strings.
     */
    private final ArrayList<String> strings;
    private String keyID;
    private String licenseURL;
    private String protectionType;
    private String secretData;

    /**
     * Creates an instance.
     *
     * @param chunkLen Length of current chunk.
     */
    public EncryptionChunk(BigInteger chunkLen) {
        super(GUID.GUID_CONTENT_ENCRYPTION, chunkLen);
        strings = new ArrayList<String>();
        secretData = "";
        protectionType = "";
        keyID = "";
        licenseURL = "";
    }

    /**
     * This method appends a String.
     *
     * @param toAdd String to add.
     */
    public void addString(String toAdd) {
        strings.add(toAdd);
    }

    /**
     * This method gets the keyID.
     *
     * @return
     */
    public String getKeyID() {
        return keyID;
    }

    /**
     * This method appends a String.
     *
     * @param toAdd String to add.
     */
    public void setKeyID(String toAdd) {
        keyID = toAdd;
    }

    /**
     * This method gets the license URL.
     *
     * @return
     */
    public String getLicenseURL() {
        return licenseURL;
    }

    /**
     * This method appends a String.
     *
     * @param toAdd String to add.
     */
    public void setLicenseURL(String toAdd) {
        licenseURL = toAdd;
    }

    /**
     * This method gets the secret data.
     *
     * @return
     */
    public String getProtectionType() {
        return protectionType;
    }

    /**
     * This method appends a String.
     *
     * @param toAdd String to add.
     */
    public void setProtectionType(String toAdd) {
        protectionType = toAdd;
    }

    /**
     * This method gets the secret data.
     *
     * @return
     */
    public String getSecretData() {
        return secretData;
    }

    /**
     * This method adds the secret data.
     *
     * @param toAdd String to add.
     */
    public void setSecretData(String toAdd) {
        secretData = toAdd;
    }

    /**
     * This method returns a collection of all {@link String}s which were addid
     * due {@link #addString(String)}.
     *
     * @return Inserted Strings.
     */
    public Collection<String> getStrings() {
        return new ArrayList<String>(strings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prettyPrint(String prefix) {
        StringBuilder result = new StringBuilder(super.prettyPrint(prefix));
        result.insert(0, Utils.LINE_SEPARATOR + prefix + " Encryption:" + Utils.LINE_SEPARATOR);
        result.append(prefix).append("	|->keyID ").append(keyID).append(Utils.LINE_SEPARATOR);
        result.append(prefix).append("	|->secretData ").append(secretData).append(Utils.LINE_SEPARATOR);
        result.append(prefix).append("	|->protectionType ").append(protectionType).append(Utils.LINE_SEPARATOR);
        result.append(prefix).append("	|->licenseURL ").append(licenseURL).append(Utils.LINE_SEPARATOR);
        strings.iterator();
        for (String string : strings) {
            result.append(prefix).append("   |->").append(string).append(Utils.LINE_SEPARATOR);
        }
        return result.toString();
    }
}
