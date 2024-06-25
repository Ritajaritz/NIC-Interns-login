package in.nic.login.controller;

import in.nic.login.dto.AuthRequestDto;
import in.nic.login.dto.AuthResponseDto;
import in.nic.login.dto.RegisterRequestDto;
import in.nic.login.repository.ClientRepository;
import in.nic.login.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    private final ClientRepository clientRepository;

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
}