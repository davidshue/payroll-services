package payroll;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by dshue1 on 9/6/16.
 */
@Converter
public class LocalDateTimePersistenceConverter implements AttributeConverter<LocalDateTime, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(LocalDateTime entityValue) {
		if (entityValue != null) {
			return Timestamp.valueOf(entityValue);
		}
		return null;
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Timestamp databaseValue) {
		if (databaseValue != null) {
			return databaseValue.toLocalDateTime();
		}
		return null;
	}
}
