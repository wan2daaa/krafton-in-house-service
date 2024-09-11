package me.wane.adldap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.wane.adldap.dto.CreateUserRequest;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapName;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final LdapTemplate ldapTemplate;

    private final PasswordEncoder passwordEncoder;

    public void saveUser(CreateUserRequest request) {
//        Name domainName = LdapNameBuilder.newInstance() //Name 내에는 Base DN //사용자 DN관련된 정보만 꼭 추가해야됨, userPrincipalName 같은걸 넣으면 에러남
//                .add("cn", request.cn())
//                .build();

        LdapName domainName = LdapUtils.newLdapName("cn=" + request.cn()); //위와 같은 결과
        User createUser = User.create(domainName, request, passwordEncoder);
        log.info("createUser: {}", createUser);
//         사용자 정보를 AD에 저장
        ldapTemplate.create(createUser);
        //javax.naming.NameAlreadyBoundException: [LDAP: error code 68 - 00000524: UpdErr: DSID-031A11FA, problem 6005 (ENTRY_EXISTS), data 0
        //중복된 값 입력시 에러 발생

        Attribute pwdLastSet = new BasicAttribute("pwdLastSet", "-1"); //비밀번호 변경 강제는 0


        ModificationItem modificationItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, pwdLastSet);

        ldapTemplate.modifyAttributes(domainName, new ModificationItem[]{modificationItem});
    }

    public Boolean isPasswordMatch(String cn, String password) {
        User user = findUserByCN(cn);

        byte[] bytes = password.getBytes(StandardCharsets.UTF_16);

        log.info("userPassword: {}", bytes);
        log.info("userPassword: {}", user.getUserPassword());

        return user.getUserPassword().equals(bytes.toString());
    }

    public User findUserByCN(String cn) {

        Name dn = LdapNameBuilder.newInstance()
                .add("cn", cn)
                .build();

        User user = userRepository.findById(dn)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        log.info("user: {}", user);
        return user;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }


    public User verifyUser(String cn, String password) {
        Name dn = LdapNameBuilder.newInstance()
                .add("cn", cn)
                .build();

//        DirContextOperations ctx = new DirContextAdapter(dn);
//        ctx.setAttributeValue("cn", cn);
//        ctx.setAttributeValue("userPassword", password.getBytes());

        return userRepository.findByIdAndUserPassword(dn, password.getBytes())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

    }

    public boolean authenticate(String cn, String password) {
        // 사용자 DN 생성
        String baseDn = "CN=Users,DC=ad,DC=wan2daaa,DC=com";
        String userDn = "cn=" + cn + "," + baseDn;

        try {
            // 사용자의 자격 증명으로 새로운 LdapContextSource 생성
            LdapContextSource userContextSource = new LdapContextSource();
            userContextSource.setUrl("ldap://3.39.187.187:389");
            userContextSource.setBase(baseDn);
            userContextSource.setUserDn(userDn);
            userContextSource.setPassword(password);
            userContextSource.afterPropertiesSet(); // ContextSource 초기화
            userContextSource.setAnonymousReadOnly(true);

            // 사용자 바인딩 시도
            DirContext ctx = userContextSource.getContext(userDn, password);
            ctx.close();
            return true; // 인증 성공
        } catch (AuthenticationException e) {
            // 비밀번호가 틀린 경우
            return false;
        } catch (NamingException e) {
            // 연결 문제 등 다른 에러 발생
            e.printStackTrace();
            return false;
        }
    }

    public void updateUser(String cn, String newDisplayName, String newEmail) {
        // 변경할 사용자 DN을 생성
        Name userDn = LdapNameBuilder.newInstance()
                .add("cn", cn)
                .build();

        // 수정할 속성 정의
        Attribute displayNameAttr = new BasicAttribute("displayName", newDisplayName);
        Attribute mailAttr = new BasicAttribute("mail", newEmail);

        ModificationItem modificationItem1 = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, displayNameAttr);
        ModificationItem modificationItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mailAttr);
        List<ModificationItem> list = List.of(modificationItem1, modificationItem);
        try {
            // 사용자 속성 수정
            ldapTemplate.modifyAttributes(userDn, list.toArray(new ModificationItem[list.size()]));

            System.out.println("User updated successfully");
        } catch (NoSuchElementException e) {
            System.err.println("User not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to update user: " + e.getMessage());
        }
    }

    public Boolean updatePassword(String cn, String newPassword) { //실패.
        try {
            Name userDn = LdapNameBuilder.newInstance()
                    .add("cn", cn)
                    .build();

            // 수정할 속성 정의
            Attribute passwordAttr = new BasicAttribute("userPassword", newPassword);
            ldapTemplate.modifyAttributes(userDn, new ModificationItem[]{new ModificationItem(DirContext.REPLACE_ATTRIBUTE, passwordAttr)});
            return true;

        }catch (Exception e) {
            System.err.println("Failed to update password: " + e.getMessage());
            return false;
        }
    }
}
