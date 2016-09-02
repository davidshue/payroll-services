package payroll

import groovy.transform.ToString

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import java.time.LocalDate

@Entity
@ToString(ignoreNulls=true,includeNames=true)
@JsonInclude(Include.NON_NULL)
class WorkRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id

	@JsonIgnore
	Long employeeId

	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate workDay

	Integer hours

	@JsonSerialize(using = MoneySerializer.class)
	BigDecimal sales
}
