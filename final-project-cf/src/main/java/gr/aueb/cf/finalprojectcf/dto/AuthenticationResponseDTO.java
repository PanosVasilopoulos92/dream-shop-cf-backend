package gr.aueb.cf.finalprojectcf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDTO {
    private String username;
    private String token;
    private int expiresIn;
}
