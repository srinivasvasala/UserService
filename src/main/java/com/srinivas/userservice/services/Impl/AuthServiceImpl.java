package com.srinivas.userservice.services.Impl;

import com.srinivas.userservice.dtos.UserDto;
import com.srinivas.userservice.models.Role;
import com.srinivas.userservice.models.Session;
import com.srinivas.userservice.models.SessionStatus;
import com.srinivas.userservice.models.User;
import com.srinivas.userservice.repositories.SessionRepository;
import com.srinivas.userservice.repositories.UserRepository;
import com.srinivas.userservice.services.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthServiceImpl(SessionRepository sessionRepository, UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @Override
    public ResponseEntity<UserDto> login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            return null;
        }
        User user = optionalUser.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
             throw new RuntimeException("Wrong password entered");
        }
        //json -> key:value
        Map<String,Object>jsonMap = new HashMap<>();
        jsonMap.put("email",user.getEmail());
        jsonMap.put("roles",user.getRoles());
        jsonMap.put("createdAt",new Date());
        jsonMap.put("expiryAt", DateUtils.addDays(new Date(), 30));

        //creating test key suitable for h-mac sha algorithm

        MacAlgorithm alg = Jwts.SIG.HS256;
        SecretKey key = alg.key().build();

        //creating JWT
        String jws = Jwts.builder()
                .claims(jsonMap)
                .signWith(key,alg)
                .compact();


        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(jws);
        session.setUser(user);
        sessionRepository.save(session);


        UserDto userDto = new UserDto();
        userDto.setEmail(email);

        MultiValueMapAdapter<String ,String>headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE,"auth-token:"+jws);
        ResponseEntity<UserDto>response = new ResponseEntity<>(userDto,headers, HttpStatus.OK);


        return response;
    }

    @Override
    public ResponseEntity<Void> logout(String token, Long userId) {
        Optional<Session>sessionOptional = sessionRepository.findByTokenAndUser_Id(token,userId);
        if(sessionOptional.isEmpty()){
            return null;
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
        return ResponseEntity.ok().build();
    }

    @Override
    public UserDto signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }

    @Override
    public SessionStatus validate(String token, Long userId) {
        Optional<Session>optionalSession = sessionRepository.findByTokenAndUser_Id(token,userId);
        if(optionalSession.isEmpty()){
            return null;
        }
        Session session = optionalSession.get();
        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
            return SessionStatus.ENDED;
        }
        Date currentDate = new Date();
        if(session.getExpiringAt().before(currentDate)){
            return SessionStatus.ENDED;
        }

        //jwt decoding
        Jws<Claims>jwsClaims = Jwts.parser().build().parseSignedClaims(token);

        String email = (String) jwsClaims.getPayload().get("email");
        List<Role> roles = (List<Role>) jwsClaims.getPayload().get("roles");
        Date createdAt = (Date)jwsClaims.getPayload().get("createdAt");
        return SessionStatus.ACTIVE;
    }
}
