package com.moadream.giftogether.member.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Friend {
	  private String friendId;
	    private String profileImage;
	    private String nickname;

	    public Friend(String friendId, String profileImage, String nickname) {
	        this.friendId = friendId;
	        this.profileImage = profileImage;
	        this.nickname = nickname;
	    }

	    
}
