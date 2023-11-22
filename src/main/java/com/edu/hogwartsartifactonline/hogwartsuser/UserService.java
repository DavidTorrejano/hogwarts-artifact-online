package com.edu.hogwartsartifactonline.hogwartsuser;


import com.edu.hogwartsartifactonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<HogwartsUser> findAll(){
        return userRepository.findAll();
    }

    public HogwartsUser save(HogwartsUser newUser){
        // We need to encode plain text password before saving to the DB TODO
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);
    }

    public HogwartsUser findById(Integer userId){
        return userRepository.findById(userId)
               .orElseThrow(()->
                        new ObjectNotFoundException("User", userId));
    }

    public HogwartsUser update(Integer userId, HogwartsUser hogwartsUser){
        return userRepository.findById(userId)
                .map(oldUser -> {
                    oldUser.setUsername(hogwartsUser.getUsername());
                    oldUser.setEnabled(hogwartsUser.isEnabled());
                    oldUser.setRoles(hogwartsUser.getRoles());
                    return userRepository.save(oldUser);

                }).orElseThrow(() -> new ObjectNotFoundException("User", userId));
    }

    public void deleteById(Integer userId){
        HogwartsUser userToBeDeleted = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User", userId));
        userRepository.deleteById(userToBeDeleted.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(hogwartsUser -> new MyUserPrincipal(hogwartsUser))
                .orElseThrow(() -> new UsernameNotFoundException("username " + username + " is not found"));
    }

}
