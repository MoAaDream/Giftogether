package com.moadream.giftogether.member.model;

public class Friend {
	  private String id;
	    private String profileImage;
	    private String nickname;

	    public Friend(String id, String profileImage, String nickname) {
	        this.id = id;
	        this.profileImage = profileImage;
	        this.nickname = nickname;
	    }

	    // Getters and setters
	    public String getId() {
	        return id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }

	    public String getProfileImage() {
	        return profileImage;
	    }

	    public void setProfileImage(String profileImage) {
	        this.profileImage = profileImage;
	    }

	    public String getNickname() {
	        return nickname;
	    }

	    public void setNickname(String nickname) {
	        this.nickname = nickname;
	    }
}
