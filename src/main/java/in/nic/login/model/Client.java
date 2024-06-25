package in.nic.login.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "client")
public class Client implements UserDetails {

    @Id
    @Field("client_id")
    private String clientId;

    @Field("client_secret")
    private String clientSecret;

    @Field("mobile_no")
    private long mobileNo;

    @Field("email_id")
    private String emailId;

    private String name;
    private String gender;
    private String dob;
    private String address;

    @Setter
    @Getter
    private Role role;

    public String getUsername() { return clientId; }
    public void setUsername(String clientId) { this.clientId = clientId; }
    public String getPassword() { return clientSecret; }
    public void setPassword(String clientSecret) { this.clientSecret = clientSecret; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}