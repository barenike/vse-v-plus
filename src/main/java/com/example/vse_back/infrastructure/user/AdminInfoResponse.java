package com.example.vse_back.infrastructure.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminInfoResponse {
    private List<InfoResponse> infoList;
}
