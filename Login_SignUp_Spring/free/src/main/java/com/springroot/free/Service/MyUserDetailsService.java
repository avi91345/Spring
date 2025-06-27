package com.springroot.free.Service;

import com.springroot.free.Entity.User;
import com.springroot.free.Repository.UserRepository;
import com.springroot.free.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user =userRepository.findByUsername(username);
       if(user==null){
           throw new UsernameNotFoundException("username not found");
       }
       return new UserPrincipal(user);
    }
}
