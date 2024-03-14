package org.acme.requests;

import lombok.Data;

@Data
public class AddUserReq {
    private String tri;

    private String nom;

    private String prenom;

    private String email;

    private String tel;
}
