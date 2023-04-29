package com.train.common.response;

import lombok.Data;

@Data
public class AccountLoginRes {
    private Long id;

    private String mobile;

    private String accessToken;

}