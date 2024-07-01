package com.moadream.giftogether.bank.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankForm {

    @NotBlank(message = "은행 이름은 필수입니다.")
    private String bankName;

    @NotBlank(message = "은행 계좌는 필수입니다.")
    private String account;
 

    public BankForm(Bank bank){
        this.bankName = bank.getBankName();
        this.account = bank.getAccount();
    }
}
