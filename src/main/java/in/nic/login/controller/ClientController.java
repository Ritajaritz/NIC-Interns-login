package in.nic.login.controller;

import in.nic.login.dto.KeyPairDto;
import in.nic.login.dto.SignUpRequestDto;
import in.nic.login.model.Client;
import in.nic.login.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/signup")
    public ResponseEntity<Client> signup(@RequestParam String clientId, @RequestBody SignUpRequestDto signUpRequestDto) {
        return clientService.signup(clientId, signUpRequestDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam long mobileNo) {
        Optional<String> otp = clientService.login(mobileNo);
        return otp.map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body("Invalid mobile number"));
    }

    @PostMapping("/getkeys")
    public ResponseEntity<KeyPairDto> getKeys(@RequestHeader("Authorization") String jwtToken, @RequestParam int keySize) {
        if (keySize != 1024) {
            return ResponseEntity.badRequest().build();
        }
        KeyPairDto keyPair = clientService.getKeys(keySize);
        return ResponseEntity.ok(keyPair);
    }
}
