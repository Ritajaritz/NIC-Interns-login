package in.nic.login.service;

import in.nic.login.dto.AuthRequestDto;
import in.nic.login.dto.AuthResponseDto;
import in.nic.login.dto.RegisterRequestDto;
import in.nic.login.model.Client;
import in.nic.login.model.Role;
import in.nic.login.repository.ClientRepository;
import in.nic.login.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequestDto request) {
        var client = Client.builder()
                .clientId(request.getClientId())
                .clientSecret(passwordEncoder.encode(request.getClientSecret()))
                .role(Role.CLIENT).build();
        clientRepository.save(client);
        var jwtToken = jwtService.generateToken(client);
        return AuthResponseDto.builder().token(jwtToken).build();
    }

    public AuthResponseDto authenticate(AuthRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getClientId(), request.getClientSecret())
        );
        var user = clientRepository.findByClientId(request.getClientId()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDto.builder().token(jwtToken).build();
    }
}

