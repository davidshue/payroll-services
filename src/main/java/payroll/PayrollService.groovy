package payroll

import service.model.EmployeeType

import java.time.DayOfWeek
import java.time.LocalDate

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
/**
 * Created by dshue1 on 9/2/16.
 */
@Service
class PayrollService {
	@Autowired
	private EmployeeRepository employeeRepo

	@Autowired
	private WorkRecordRepository workRecordRepo

	Map<Employee, BigDecimal> computePayRoll(LocalDate payRollDay) {
		List<Employee> employees = employeeRepo.findAll()
		return employees.inject([:]) {result, it ->
			BigDecimal pay = pay(it, payRollDay)
			if (pay) {
				result[it] = pay
			}
			result
		}
	}

	private BigDecimal pay(Employee employee, LocalDate date) {
		LocalDate start = getPayDayStartDate(employee, date)
		if (!start) return null

		switch (employee.type) {
			case EmployeeType.salaried:
				return getFulltimeSalary(start, date)
			case EmployeeType.non_salaried:
				return getNonFulltimeSalary(employee, start)
			default:
				break
		}

		return null
	}

	private BigDecimal getNonFulltimeSalary(Employee employee, LocalDate from) {
		List<WorkRecord> records = workRecordRepo.findByEmployeeIdAndWorkDayAfter(employee.id, from.minusDays(-1))
		def pay = records.inject(0.0) {result, it ->
			result += 15.0 * it.hours
			result += it.sales?.multiply(0.1d)
			result
		}
		new BigDecimal(pay)
	}

	private BigDecimal getFulltimeSalary(LocalDate from, LocalDate to) {
		int total = 0
		for(LocalDate start = from; start.isBefore(to); start = start.plusDays(1) ) {
			if (start.dayOfWeek != DayOfWeek.SATURDAY && start.dayOfWeek != DayOfWeek.SUNDAY) {
				total++
			}
		}
		return new BigDecimal(total * 150.0)
	}


	private LocalDate getPayDayStartDate(Employee employee, LocalDate date) {
		switch (employee.type) {
			case EmployeeType.non_salaried:
				if (date.dayOfWeek == DayOfWeek.MONDAY) {
					LocalDate start = date.minusDays(7)
					return start.isAfter(employee.startDate) ? start: employee.startDate
				}
				return null
			case EmployeeType.salaried:
				if (date.dayOfMonth == 1) {
					LocalDate start = date.minusMonths(1)
					return start.isAfter(employee.startDate) ? start : employee.startDate
				}
				return null
			/*
			case EmployeeType.commissioned:
				TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()
				if (date.dayOfWeek == DayOfWeek.MONDAY && date.get(woy) % 2 == 0) {
					LocalDate start = date.minusWeeks(2)
					return start.isAfter(employee.startDate) ? start : employee.startDate
				}
				return null
				*/
			default:
				return null
		}
	}

}
