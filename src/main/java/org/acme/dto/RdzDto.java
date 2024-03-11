package org.acme.dto;

import lombok.Getter;
import lombok.Setter;
import org.acme.entities.Rdz;
import org.acme.entities.Zone;

@Getter
@Setter
public class RdzDto {
    private long id;

    private String tri;

    private String nom;

    private String prenom;

    private String tel;

    private String email;

    private ZoneDto zone;
}
