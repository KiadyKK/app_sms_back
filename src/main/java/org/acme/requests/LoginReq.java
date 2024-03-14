package org.acme.requests;

import lombok.Data;

@Data
public class LoginReq {
    private String tri;

    private String mdp;
}
