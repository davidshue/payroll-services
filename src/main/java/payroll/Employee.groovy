package payroll

import groovy.transform.ToString
import service.model.EmployeeType
import service.model.PaySchedule

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

import javax.persistence.DiscriminatorColumn
import javax.persistence.DiscriminatorType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import java.time.LocalDate

@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="employee_type", discriminatorType = DiscriminatorType.STRING)
@ToString(ignoreNulls=true,includeNames=true)
@JsonInclude(Include.NON_NULL)
abstract class Employee {
	@Id
	Long id

	String name

	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate startDate

	protected PaySchedule paySchedule

	PaySchedule getPaySchedule() {
		paySchedule
	}

	abstract EmployeeType getType()
}
