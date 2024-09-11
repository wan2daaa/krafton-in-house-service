package me.wane.adldap;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import javax.naming.Name;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends LdapRepository<User> {

    Optional<User> findByUserPrincipalName(String userPrincipalName);

    List<User> findAllById(Name id);

    Optional<User> findByIdAndUserPassword(Name id, byte[] userPassword);
}
