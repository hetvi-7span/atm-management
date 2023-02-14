package com.solution.atmmanagement.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrentUserDto {

    String username;
    Double currentBalance;
    Integer userId;
}
