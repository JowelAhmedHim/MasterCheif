package com.example.masterchef.services.model;

public class ModelVideo {

    private String postId,videoTitle,videDescription,videoCategory,videoThumbnail,videoUrl,videoLike,timeStamp,
            uid,userName,userEmail,userImage;

    public ModelVideo() {
    }


    public ModelVideo(String postId, String videoTitle, String videDescription, String videoCategory, String videoThumbnail, String videoUrl, String videoLike, String timeStamp, String uid, String userName, String userEmail, String userImage) {
        this.postId = postId;
        this.videoTitle = videoTitle;
        this.videDescription = videDescription;
        this.videoCategory = videoCategory;
        this.videoThumbnail = videoThumbnail;
        this.videoUrl = videoUrl;
        this.videoLike = videoLike;
        this.timeStamp = timeStamp;
        this.uid = uid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userImage = userImage;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoLike() {
        return videoLike;
    }

    public void setVideoLike(String videoLike) {
        this.videoLike = videoLike;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
