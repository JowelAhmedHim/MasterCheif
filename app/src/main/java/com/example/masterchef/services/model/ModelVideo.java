package com.example.masterchef.services.model;

public class ModelVideo {

    private String postId,videoTitle,videoDescription,videoCategory,videoThumbnail,videoUrl,videoLike,timeStamp,
            uid,userName,userEmail,userImage;

    public ModelVideo() {

    }

    public ModelVideo(String postId, String videoTitle, String videoDescription, String videoCategory, String videoThumbnail, String videoUrl, String videoLike, String timeStamp, String uid, String userName, String userEmail, String userImage) {
        this.postId = postId;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
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

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public String getVideoCategory() {
        return videoCategory;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getVideoLike() {
        return videoLike;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserImage() {
        return userImage;
    }
}
