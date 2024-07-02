package in.nic.login.service;

import in.nic.login.dto.KeyPairDto;
import in.nic.login.config.DateConfig;
import in.nic.login.model.Client;
import in.nic.login.dto.SignUpRequestDto;
import in.nic.login.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Optional<String> login(long mobileNo) {
        Optional<Client> optionalClient = clientRepository.findByMobileNo(mobileNo);
        return optionalClient.isPresent() ? Optional.of(generateOtp()) : Optional.empty();
    }

    private String generateOtp() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    public Optional<Client> signup(String clientId, SignUpRequestDto signUpRequestDto) {
        if (!DateConfig.isValidDate(signUpRequestDto.getDob())) {
            throw new IllegalArgumentException("Invalid date format. Please use dd-MM-yyyy");
        }

        return clientRepository.findByClientId(clientId)
                .map(client -> {
                    client.setMobileNo(signUpRequestDto.getMobileNo());
                    client.setEmailId(signUpRequestDto.getEmailId());
                    client.setName(signUpRequestDto.getName());
                    client.setGender(signUpRequestDto.getGender());
                    client.setDob(signUpRequestDto.getDob());
                    client.setAddress(signUpRequestDto.getAddress());
                    return clientRepository.save(client);
                });
    }

    public KeyPairDto getKeys(int keySize) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(keySize);
            KeyPair pair = keyGen.generateKeyPair();
            String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
            return new KeyPairDto(privateKey, publicKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating RSA keys", e);
        }
    }
}
