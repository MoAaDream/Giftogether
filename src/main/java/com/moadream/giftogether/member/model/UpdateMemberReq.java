package com.moadream.giftogether.member.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMemberReq {
	
	@Size(min=2, max=30)
	private String nickname; // 실명 이슈 해결 여부에 따라 추후 수정 불가능으로 변경될 수 있음
	
	private String profile;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;
	
	@Size(max = 100)
	private String address;
	
	@Pattern(regexp = "^\\d{2,3}\\d{3,4}\\d{4}$", message = "전화번호 형식에 맞지 않습니다.")
	private String phoneNumber;
	
	
	
}
