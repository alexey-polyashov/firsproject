package common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserInfo implements Serializable {
    private String login;
    private String email;
    private String password;
}