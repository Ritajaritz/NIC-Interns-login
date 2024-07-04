package in.nic.login.controller;

import in.nic.login.dto.AuthRequestDto;
import in.nic.login.dto.AuthResponseDto;
import in.nic.login.dto.RegisterRequestDto;
import in.nic.login.service.AuthService;
import in.nic.login.service.KeyService;
import in.nic.login.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;
    private final ClientRepository clientRepository;
    private final KeyService keyService; // Inject the KeyService

    @PostMapping("/init")
    public ResponseEntity<AuthResponseDto> init(@RequestBody AuthRequestDto request) {
        if (clientRepository.findByClientId(request.getClientId()).isPresent()) {
            try {
                AuthResponseDto response = service.authenticate(request);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        } else {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto();
            registerRequestDto.setClientId(request.getClientId());
            registerRequestDto.setClientSecret(request.getClientSecret());

            try {
                AuthResponseDto response = service.register(registerRequestDto);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        }
    }

//    @PostMapping("/getkeys")
//    public ResponseEntity<Map<String, String>> getKeys(
//            @RequestHeader("Authorization") String authToken,
//            @RequestBody int keySize) {
//
//        // Validate JWT token (Assuming a method `validateToken` is available)
//        if (!service.validateToken(authToken)) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//
//        try {
//            Map<String, String> keys = keyService.generateKeys(keySize);
//            return new ResponseEntity<>(keys, HttpStatus.OK);
//        } catch (NoSuchAlgorithmException e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
