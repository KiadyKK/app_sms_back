package org.acme.models.requests;

import lombok.Data;

@Data
public class AddRdzReq {
    private long zone;

    private String nom;

    private String prenom;

    private String email;

    private String tel;

    private String tri;
}
