package org.acme.requests;

import lombok.Data;

@Data
public class AddRdzReq {
    private long IdZone;

    private String zone;

    private String nom;

    private String prenom;

    private String email;

    private String tel;

    private String tri;
}
