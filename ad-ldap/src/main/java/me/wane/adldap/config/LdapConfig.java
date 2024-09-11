package me.wane.adldap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@EnableLdapRepositories
public class LdapConfig {

    @Bean
    public ContextSource contextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();

        ldapContextSource.setUrl("ldap://windows-server:389");

        // 관리자 계정의 Distinguished Name (DN)과 비밀번호 설정
        ldapContextSource.setUserDn("CN=Administrator,CN=Users,DC=ad,DC=wan2daaa,DC=com");  // 관리자 계정의 DN
//        ldapContextSource.setUserDn("CN=test,OU=test,DC=ad,DC=wan2daaa,DC=com");  // 관리자 계정의 DN
        ldapContextSource.setPassword("userPassword!");  // 관리자 계정 비밀번호
        // 베이스 DN 설정
        ldapContextSource.setBase("CN=Users,DC=ad,DC=wan2daaa,DC=com");

//        ldapContextSource.afterPropertiesSet();
        return ldapContextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        // LDAP Template을 ContextSource와 함께 설정
        return new LdapTemplate(contextSource);
    }
}
