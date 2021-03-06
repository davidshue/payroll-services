package payroll

import groovy.transform.ToString
import service.model.EmployeeType
import service.model.PaySchedule

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import java.time.LocalDate

@Entity
@ToString(ignoreNulls=true,includeNames=true)
@JsonInclude(Include.NON_NULL)
class Employee {
	@Id
	Long id

	String name

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Convert(converter = LocalDatePersistenceConverter.class)
	LocalDate startDate = LocalDate.of(1990, 1, 1)

	@JsonIgnore
	boolean salaried = true

	@JsonIgnore
	boolean hourly = false

	@JsonIgnore
	boolean commissioned = false

	@Enumerated(EnumType.STRING)
	PaySchedule paySchedule = PaySchedule.monthly


	List<EmployeeType> getTypes() {
		if (salaried) return [EmployeeType.salaried]
		def types = []
		if (hourly) types << EmployeeType.hourly
		if (commissioned) types << EmployeeType.commissioned
		return types
	}
}
