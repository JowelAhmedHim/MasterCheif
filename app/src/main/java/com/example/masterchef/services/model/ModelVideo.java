package com.example.masterchef.services.model;

public class ModelVideo {

    private String videoId,videoTitle,videDescription,videoCategory,videoLike,videoThumbnail,uid;

    public ModelVideo() {
    }

    public ModelVideo(String videoId, String videoTitle, String videDescription, String videoCategory, String videoLike, String videoThumbnail, String uid) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videDescription = videDescription;
        this.videoCategory = videoCategory;
        this.videoLike = videoLike;
        this.videoThumbnail = videoThumbnail;
        this.uid = uid;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideDescription() {
        return videDescription;
    }

    public void setVideDescription(String videDescription) {
        this.videDescription = videDescription;
    }

    public String getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(String videoCategory) {
        this.videoCategory = videoCategory;
    }

    public String getVideoLike() {
        return videoLike;
    }

    public void setVideoLike(String videoLike) {
        this.videoLike = videoLike;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
