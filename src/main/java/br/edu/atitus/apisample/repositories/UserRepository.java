package br.edu.atitus.apisample.repositories;

import br.edu.atitus.apisample.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository // Somente a título de informação
public interface UserRepository extends JpaRepository<User, UUID> {
    //save
    //findAll
    //findById
    //deleteById
    //delete

    // select count(*) from tb_user where email = {}
    Boolean existsByEmail(String email);

    // select count(*) from tb_user where email = {} and name = {}
    Boolean existsByEmailAndName(String email, String name);

    // select * from tb_user where email = {}
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.password = 'algumacoisa'")
    List<User> listarParaRelatorio();
}
