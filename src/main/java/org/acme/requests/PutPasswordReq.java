package org.acme.requests;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
public class PutPasswordReq {
    private String password;
    private String trigramme;
    private String newPassword;
}
