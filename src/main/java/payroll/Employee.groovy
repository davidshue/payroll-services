package payroll

import groovy.transform.ToString
import service.model.EmployeeType

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

import javax.persistence.Entity
import javax.persistence.Id
import java.time.LocalDate

@Entity
@ToString(ignoreNulls=true,includeNames=true)
@JsonInclude(Include.NON_NULL)
class Employee {
	@Id
	Long id

	EmployeeType type

	String name

	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate startDate
}
