package com.api.flux.courseed.projections.dtos;

import java.util.List;

import com.api.flux.courseed.projections.validators.groups.FirstValidationGroup;
import com.api.flux.courseed.projections.validators.groups.SecondValidationGroup;
import com.api.flux.courseed.projections.validators.groups.ThirdValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@GroupSequence({ UpdateUserRolesDto.class, FirstValidationGroup.class, SecondValidationGroup.class, ThirdValidationGroup.class })
public class UpdateUserRolesDto {
    
    @NotEmpty(message = "Es importante que selecciones los roles antes de continuar.", groups = FirstValidationGroup.class)
    @Size(min = 1, message = "Es importante que selecciones los roles antes de continuar.", groups = SecondValidationGroup.class)
    private List<@NotBlank(message = "Es importante que selecciones los roles antes de continuar.", groups = ThirdValidationGroup.class) String> roles;

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
