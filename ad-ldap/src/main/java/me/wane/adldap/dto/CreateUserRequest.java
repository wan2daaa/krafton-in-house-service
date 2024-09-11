package me.wane.adldap.dto;

public record CreateUserRequest(
        String cn, //cn으로 , userPrincipalName, displayName에 사용하게 설정
        String password,
        String givenName,
        String name,
        String email
) {
}
