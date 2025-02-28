package ce2team1.mentoview.utils.jpaconverter;


import ce2team1.mentoview.entity.atrribute.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<Role,String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return attribute.getCode();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getCode().equals(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role code: " + dbData));
    }
}
