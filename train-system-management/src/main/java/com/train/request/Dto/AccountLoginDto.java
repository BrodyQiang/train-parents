package com.train.request.Dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class AccountLoginDto {
    private String mobile;
    private String password;
}
