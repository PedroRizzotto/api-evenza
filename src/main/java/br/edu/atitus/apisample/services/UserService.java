package br.edu.atitus.apisample.services;

import br.edu.atitus.apisample.entities.User;
import br.edu.atitus.apisample.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public User save(User newUser) throws Exception {

        if (newUser == null)
            throw new Exception("Objeto Nulo!");

        if (newUser.getName() == null || newUser.getName().isBlank())
            throw new Exception("Nome informado inválido!");
        newUser.setName(newUser.getName().trim());

        if (newUser.getEmail() == null || newUser.getEmail().isBlank())
            throw new Exception("E-mail informado inválido!");
        newUser.setEmail(newUser.getEmail().trim().toLowerCase());
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(\\.[a-zA-Z]{2,})*$";
        if (!newUser.getEmail().matches(emailRegex))
            throw new Exception("E-mail inválido! Deve conter @ e dois ou mais domínios (ex: gmail.com, bol.com.br).");

        if (newUser.getPassword() == null || newUser.getPassword().length() < 8)
            throw new Exception("Senha deve ter no mínimo 8 caracteres!");
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        if (!newUser.getPassword().matches(passwordRegex))
            throw new Exception("Senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número!");


        newUser.setPassword(encoder.encode(newUser.getPassword()));


        if (newUser.getType() == null)
            throw new Exception("Tipo de usuário nulo!");

        if (repository.existsByEmail(newUser.getEmail()))
            throw new Exception("Já existe um usuário cadastrado com esse e-mail");

        //persistir no banco atravpes do repository
        return repository.save(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com este e-mail."));
    }
}
