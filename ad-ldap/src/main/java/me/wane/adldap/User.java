package me.wane.adldap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.wane.adldap.dto.CreateUserRequest;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.Name;

@ToString
@NoArgsConstructor
@Getter
@Entry(
        base = "DC=ad,DC=wan2daaa,DC=com",
        objectClasses = {
                "top", "person", "organizationalPerson", "user"
        }
)
public class User {

    @Id
    private Name id;
    private String userPrincipalName;  // 로그인 ID
    private String userPassword; // 비밀번호
    private String displayName;  // 표시 이름
    private String givenName;  // 성
    private String sn;  // 이름
    private String mail;  // 이메일 주소
    private String sAMAccountName;
    private String accountExpires = "0";


    public User(Name id, String userPrincipalName, String userPassword, String displayName, String givenName, String sn, String mail, String sAMAccountName
    ) {
        this.id = id;
        this.userPrincipalName = userPrincipalName;
        this.userPassword = userPassword;
        this.displayName = displayName;
        this.givenName = givenName;
        this.sn = sn;
        this.mail = mail;
        this.sAMAccountName = sAMAccountName;
    }

    public static User create(Name domainName, CreateUserRequest request, PasswordEncoder passwordEncoder) {
        return new User(
                domainName,
                request.cn(),
                passwordEncoder.encode(request.password()),
                request.cn(),
                request.givenName(),
                request.name(),
                request.email(),
                request.email()
        );
    }
}
