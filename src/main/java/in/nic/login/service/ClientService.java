package in.nic.login.service;

import in.nic.login.config.DateConfig;
import in.nic.login.dto.KeyPairDto;
import in.nic.login.dto.SignUpRequestDto;
import in.nic.login.model.Client;
import in.nic.login.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    private Map<String, String> dataStore = new HashMap<>();

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

    public String pushData(String toBeEncrypted, String publicKeyString) {
        try {
            byte[] publicBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(toBeEncrypted.getBytes());
            String encryptedData = Base64.getEncoder().encodeToString(encryptedBytes);

            // Store the encrypted data with a transaction ID
            String transactionId = UUID.randomUUID().toString();
            dataStore.put(transactionId, encryptedData);
            return transactionId;
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    public Optional<String> getData(String transactionId) {
        return Optional.ofNullable(dataStore.get(transactionId));
    }
}