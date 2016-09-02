package payroll

import groovy.transform.ToString
import service.model.EmployeeType

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@ToString(ignoreNulls=true,includeNames=true)
@JsonInclude(Include.NON_NULL)
class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id

	EmployeeType type

	String name
}
