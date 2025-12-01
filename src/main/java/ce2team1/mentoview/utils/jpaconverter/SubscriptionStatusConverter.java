package ce2team1.mentoview.utils.jpaconverter;

import ce2team1.mentoview.subscription.domain.attribute.SubscriptionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SubscriptionStatusConverter implements AttributeConverter<SubscriptionStatus,String> {

    @Override
    public String convertToDatabaseColumn(SubscriptionStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name(); // Enum의 이름을 문자열로 변환
    }

    @Override
    public SubscriptionStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return SubscriptionStatus.valueOf(dbData); // 문자열을 Enum으로 변환
    }
}
