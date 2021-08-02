package dev.ragnarok.fenrir.db.model.entity;

import java.util.List;


public class StickerEntity extends Entity {

    private final int id;

    private List<Img> images;

    private List<Img> imagesWithBackground;

    private List<AnimationEntity> animations;

    private String animationUrl;

    public StickerEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getAnimationUrl() {
        return animationUrl;
    }

    public StickerEntity setAnimationUrl(String animationUrl) {
        this.animationUrl = animationUrl;
        return this;
    }

    public List<Img> getImages() {
        return images;
    }

    public StickerEntity setImages(List<Img> images) {
        this.images = images;
        return this;
    }

    public List<AnimationEntity> getAnimations() {
        return animations;
    }

    public StickerEntity setAnimations(List<AnimationEntity> animations) {
        this.animations = animations;
        return this;
    }

    public List<Img> getImagesWithBackground() {
        return imagesWithBackground;
    }

    public StickerEntity setImagesWithBackground(List<Img> imagesWithBackground) {
        this.imagesWithBackground = imagesWithBackground;
        return this;
    }

    public static final class AnimationEntity {
        private final String type;
        private final String url;

        public AnimationEntity(String url, String type) {
            this.url = url;
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public String getType() {
            return type;
        }
    }

    public static final class Img {

        private final String url;
        private final int width;
        private final int height;

        public Img(String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public String getUrl() {
            return url;
        }
    }
}