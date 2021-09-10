package dev.ragnarok.fenrir.view.natives.video;

import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

import androidx.annotation.NonNull;

import java.io.File;

import dev.ragnarok.fenrir.module.video.AnimatedFileDrawable;

class CHBVideoDrawable extends AnimatedFileDrawable {
    private float targetSaturation = 1f;

    public CHBVideoDrawable(File file, long seekTo, int w, int h, @NonNull DecoderListener decoderListener) {
        super(file, seekTo, w, h, decoderListener);
    }

    @Override
    public void draw(Canvas canvas) {
        if (targetSaturation > 0) {
            targetSaturation -= 0.009f;
        }
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(targetSaturation);
        getPaint().setColorFilter(new ColorMatrixColorFilter(cm));
        super.draw(canvas);
    }
}