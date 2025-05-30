package com.example.Final.service;

import com.example.Final.entity.listingservice.Images;
import com.example.Final.entity.paymentservice.UserPayment;
import com.example.Final.entity.securityservice.Roles;
import com.example.Final.entity.securityservice.User;
import com.example.Final.repository.RolesRepository;
import com.example.Final.repository.UserPaymentRepo;
import com.example.Final.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private UserPaymentRepo userPaymentRepo;

    public User create(User user) {
        User saveUser = new User();
        UserPayment userPayment = new UserPayment();
        userPaymentRepo.save(userPayment);
        saveUser.setEmail(user.getEmail());
        saveUser.setPassword(passwordEncoder.encode(user.getPassword()));
        saveUser.setFullName(user.getFullName());
        saveUser.setConfirmPassword(user.getConfirmPassword());
        saveUser.setImages(null);
        saveUser.setRoles(Collections.singletonList(rolesRepository.findRolesByName("ROLE_REALTOR")));
        saveUser.setPhone(user.getPhone());
        saveUser.setUserPayment(userPayment);
        saveUser.setActive(true);
        int randomNumber = new Random().nextInt(40000000) + 10000000;
        String paymentCode = "T7M" + randomNumber;
        saveUser.setPaymentCode(paymentCode);
        userRepository.save(saveUser);
        userPayment.setUser(saveUser);
        userPaymentRepo.save(userPayment);
        return saveUser;

    }
    @Transactional
    public void createAdmin(User user) {
        User saveUser = new User();
        UserPayment userPayment = new UserPayment();

        Roles adminRole = rolesRepository.findRolesByName("ROLE_ADMIN");
        if (adminRole == null) {
            throw new RuntimeException("ROLE_ADMIN not found");
        }
        saveUser.setEmail(user.getEmail());
        saveUser.setPassword(passwordEncoder.encode(user.getPassword()));
        saveUser.setFullName(user.getFullName());
        saveUser.setConfirmPassword(user.getConfirmPassword());
        saveUser.setImages(null);
        saveUser.setRoles(Collections.singletonList(adminRole)); // ← dùng managed entity
        saveUser.setPhone(user.getPhone());
        saveUser.setActive(true);

        int randomNumber = new Random().nextInt(40000000) + 10000000;
        String paymentCode = "T7M" + randomNumber;
        saveUser.setPaymentCode(paymentCode);
        userPayment.setUser(saveUser);
        saveUser.setUserPayment(userPayment);

        userRepository.save(saveUser);
        userPaymentRepo.save(userPayment);

    }

    public void save(User user) {
        userRepository.save(user);
    }


    public void updateByEmail(String email, String password, String confirmPassword) {
        User oldUser = userRepository.findUserByEmail(email);
        oldUser.setEmail(email);
        oldUser.setPassword(passwordEncoder.encode(password));
        oldUser.setConfirmPassword(confirmPassword);
        oldUser.setRoles(oldUser.getRoles());
        userRepository.save(oldUser);
    }

    public void updateImage(User user, Images image) {
        User oldUser = userRepository.findUserByEmail(user.getEmail());
        oldUser.setImages(image);
        userRepository.save(oldUser);
    }

    public void updateInfo(String email, String fullName, String phone) {
        User oldUser = userRepository.findUserByEmail(email);
        oldUser.setEmail(email);
        oldUser.setPhone(phone);
        oldUser.setFullName(fullName);
        oldUser.setImages(oldUser.getImages());
        userRepository.save(oldUser);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User findUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Couldn't find user'"));
    }

    public void deleteById(int id) {
        userRepository.deleteById(id);
    }
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password. Please try again!!");
        }
        Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        return new org.springframework.security.core.userdetails
                .User(user.getEmail(), user.getPassword(), authorities);
    }

    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Roles> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Roles tempRole : roles) {
            SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getName());
            authorities.add(tempAuthority);
        }
        return authorities;
    }
}
