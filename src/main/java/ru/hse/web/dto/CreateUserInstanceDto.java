package ru.hse.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import ru.hse.web.model.Rule;

import javax.validation.constraints.Email;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserInstanceDto {

    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    private String middleName;
    private Set<Rule> categories;
    @Email
    @NonNull
    private String mail;
    @NonNull
    private String password;
    @NonNull
    private String passport;
    @NonNull
    private String legalAddress;

}
