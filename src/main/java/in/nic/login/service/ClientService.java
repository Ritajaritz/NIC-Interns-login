package in.nic.login.service;

import in.nic.login.config.DateConfig;
import in.nic.login.model.Client;
import in.nic.login.dto.SignUpRequestDto;
import in.nic.login.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
}