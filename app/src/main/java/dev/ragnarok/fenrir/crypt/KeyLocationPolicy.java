package dev.ragnarok.fenrir.crypt;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({KeyLocationPolicy.PERSIST, KeyLocationPolicy.RAM})
@Retention(RetentionPolicy.SOURCE)
public @interface KeyLocationPolicy {

    int PERSIST = 1;

    /**
     * Not yet implemented
     */
    int RAM = 2;
}
