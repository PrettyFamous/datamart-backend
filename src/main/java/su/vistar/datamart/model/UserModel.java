package su.vistar.datamart.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserModel {
    @JsonProperty(value="id")
    private Long id;

    @JsonProperty(value="full_name")
    private String fullName;

    @JsonProperty(value="login")
    private String login;

    @JsonProperty(value="password")
    private String password;
}
