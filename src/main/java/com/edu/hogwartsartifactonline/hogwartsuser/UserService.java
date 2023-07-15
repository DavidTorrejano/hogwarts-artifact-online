package com.edu.hogwartsartifactonline.hogwartsuser;


import com.edu.hogwartsartifactonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<HogwartsUser> findAll(){
        return userRepository.findAll();
    }

    public HogwartsUser save(HogwartsUser newUser){
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

}
