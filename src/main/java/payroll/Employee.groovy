package payroll

import groovy.transform.ToString
import service.model.EmployeeType
import service.model.PaySchedule

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import java.time.LocalDate

@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@ToString(ignoreNulls=true,includeNames=true)
@JsonInclude(Include.NON_NULL)
class Employee {
	@Id
	Long id

	String name

	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate startDate

	boolean salaried

	boolean commissioned

	PaySchedule paySchedule


	List<EmployeeType> getTypes() {
		def types = []
		salaried ? types << EmployeeType.salaried : types << EmployeeType.hourly
		if (commissioned) types << EmployeeType.commissioned
		return types
	}
}
