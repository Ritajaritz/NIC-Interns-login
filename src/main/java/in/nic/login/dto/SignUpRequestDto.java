package in.nic.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SignUpRequestDto {
    private long mobileNo;
    private String emailId;
    private String name;
    private String gender;
    private String dob;
    private String address;
}
