package com.porta.porta.controller;

import com.porta.porta.exception.AppException;
import com.porta.porta.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porta.porta.entity.Role;
import com.porta.porta.entity.RoleName;
import com.porta.porta.entity.User;
import com.porta.porta.payload.ApiResponse;
import com.porta.porta.payload.ChangeRequest;
import com.porta.porta.payload.JwtAuthenticationResponse;
import com.porta.porta.payload.LoginRequest;
import com.porta.porta.payload.SignUpRequest;
import com.porta.porta.repository.RoleRepository;
import com.porta.porta.repository.UserRepository;
import com.porta.porta.security.JwtTokenProvider;
import com.porta.porta.service.EmailService;
import com.porta.porta.util.Util;
import com.porta.porta.vo.MensajeVO;
import com.porta.porta.vo.ResultadoVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.jose4j.lang.JoseException;

import javax.jws.soap.SOAPBinding.Use;
import javax.validation.Valid;
import java.net.URI;
import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        @Autowired
        EmailService emailService;

        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        UserRepository userRepository;

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        JwtTokenProvider tokenProvider;

        private final Logger log = LoggerFactory.getLogger(this.getClass());
        ResultadoVO salida = new ResultadoVO();
        String[] mensajes = new String[3];
        Key key = new AesKey(ByteUtil.randomBytes(16));
        @PostMapping("/signin")
        public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

                Authentication authentication = authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                String jwt = tokenProvider.generateToken(authentication);
                return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        }

        @PutMapping("/test/{id}")
        public ResponseEntity<?> update(@PathVariable(value = "id") Long userId) throws JoseException {

                // String entradaOriginal = Long.toString(userId);
                // String cadenaCodificada = Base64.getEncoder().encodeToString(entradaOriginal.getBytes());

                // System.out.println("codificado: " + cadenaCodificada);

                
                JsonWebEncryption jwe = new JsonWebEncryption();
                jwe.setPayload(Long.toString(userId));
                jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
                jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
                jwe.setKey(key);
                String serializedJwe = jwe.getCompactSerialization();
                System.out.println("Serialized Encrypted JWE: " + serializedJwe);


                return ResponseEntity.ok(serializedJwe);
        }

        @PutMapping("/changepassword/{id}")
        public ResponseEntity<ResultadoVO> update(@PathVariable(value = "id") String userId,
                        @Valid @RequestBody ChangeRequest changeRequest) throws ResourceNotFoundException,
                        JoseException {
                // byte[] bytesDecodificados = Base64.getDecoder().decode(userId);
                // String cadenaDecodificada = new String(bytesDecodificados);

                JsonWebEncryption jwe = new JsonWebEncryption();
                jwe = new JsonWebEncryption();
                jwe.setAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
                                KeyManagementAlgorithmIdentifiers.A128KW));
                jwe.setContentEncryptionAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
                                ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256));
                jwe.setKey(key);
                jwe.setCompactSerialization(userId);
                System.out.println("Payload: " + jwe.getPayload());


                Long id = Long.parseLong(jwe.getPayload());

                try {

                        User user = userRepository.findById(id)
                                        .orElseThrow(() -> new IllegalStateException("IdUsuario no existe."));
                        user.setPassword(passwordEncoder.encode(changeRequest.getPassword()));

                        mensajes = Util.Codigos.PASSWORDOK.split(";");
                        String[] timestamp = Util.getCurrentTimeStamp().split(";");
                        MensajeVO mensaje = new MensajeVO(timestamp[0], timestamp[1], mensajes[1], mensajes[0]);
                        userRepository.save(user);
                        salida.setPeticion(mensaje);
                } catch (Exception e) {
                        log.error("HA OCURRIDO UN ERROR");
                        mensajes = Util.Codigos.PASSWORDSNOCOINCIDENTES.split(";");
                        String[] timestampError = Util.getCurrentTimeStamp().split(";");
                        MensajeVO mensajeError = new MensajeVO(timestampError[0], timestampError[1], mensajes[1],
                                        mensajes[0]);
                        salida.setPeticion(mensajeError);
                        e.printStackTrace();
                        return new ResponseEntity<ResultadoVO>(salida, HttpStatus.OK);
                }
                return new ResponseEntity<ResultadoVO>(salida, HttpStatus.OK);
                // http://127.0.0.1:8090/api/auth/changepassword/1
        }

        @PutMapping("/signup")
        public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

                if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                        return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                                        HttpStatus.BAD_REQUEST);
                }

                if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                        return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                                        HttpStatus.BAD_REQUEST);
                }

                // Creating user's account
                User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
                                signUpRequest.getPassword());

                user.setPassword(passwordEncoder.encode(user.getPassword()));

                Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new AppException("User Role not set."));

                user.setRoles(Collections.singleton(userRole));
                user.setActive(false);

                User result = userRepository.save(user);

                URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{username}")
                                .buildAndExpand(result.getUsername()).toUri();
                emailService.emailSend(signUpRequest.getEmail(), signUpRequest.getName(), signUpRequest.getUsername(),
                                signUpRequest.getPassword());
                return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
        }

        @PostMapping("/signupwork")
        public ResponseEntity<?> registerWork(@Valid @RequestBody SignUpRequest signUpRequest) {

                if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                        return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                                        HttpStatus.BAD_REQUEST);
                }

                if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                        return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                                        HttpStatus.BAD_REQUEST);
                }

                // Creating user's account
                User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
                                signUpRequest.getPassword());

                user.setPassword(passwordEncoder.encode(user.getPassword()));

                if (signUpRequest.getRole().equals("1")) {
                        Role userRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                        .orElseThrow(() -> new AppException("User Role not set."));

                        user.setRoles(Collections.singleton(userRole));
                }
                if (signUpRequest.getRole().equals("3")) {
                        Role userRole = roleRepository.findByName(RoleName.ROLE_EMPLOYE)
                                        .orElseThrow(() -> new AppException("User Role not set."));

                        user.setRoles(Collections.singleton(userRole));

                }
                if (signUpRequest.getRole().equals("4")) {
                        Role userRole = roleRepository.findByName(RoleName.ROLE_PROVIDER)
                                        .orElseThrow(() -> new AppException("User Role not set."));

                        user.setRoles(Collections.singleton(userRole));
                } else {

                }

                User result = userRepository.save(user);

                URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{username}")
                                .buildAndExpand(result.getUsername()).toUri();

                return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
        }
}