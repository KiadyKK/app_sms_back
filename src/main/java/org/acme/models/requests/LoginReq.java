package org.acme.models.requests;

import lombok.Data;

@Data
public class LoginReq {
    private String tri;

    private String mdp;
}
