package payroll

import service.model.EmployeeType
import service.model.PaySchedule

import javax.annotation.PostConstruct
import java.time.DayOfWeek
import java.time.LocalDate

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by dshue1 on 9/6/16.
 */
@Component
class DataBootstrapper {
	@Autowired
	private EmployeeRepository employeeRepo

	@Autowired
	private WorkRecordRepository workRecordRepo

	private static def EMPLOYEES = [
		hourly_paidWeekly: [id: 1, name: 'Justin', salaried: false, hourly: true, commissioned: false, startDate: LocalDate.of(2016, 1, 1), paySchedule: PaySchedule.weekly],
		hourly_paidMonthly: [id: 2, name: 'Dwayne', salaried: false, hourly: true, commissioned: false, startDate: LocalDate.of(2016, 8, 1), paySchedule: PaySchedule.monthly],
		salaried: [id: 101, name: 'Maria', startDate: LocalDate.of(2016, 5, 20)],
		commissioned_paidBiweekly: [id: 1001, name: 'Adam', salaried: false, hourly: false, commissioned: true, startDate: LocalDate.of(2015, 10, 10), paySchedule: PaySchedule.biweekly],
		commissioned_paidMonthly: [id: 1002, name: 'Jonathan', salaried: false, hourly: false, commissioned: true, startDate: LocalDate.of(2015, 2, 10), paySchedule: PaySchedule.monthly],
		combined_paidWeekly: [id: 10001, name: 'Susan', salaried: false, hourly: true, commissioned: true, startDate: LocalDate.of(2016, 1, 1), paySchedule: PaySchedule.weekly],
		combined_paidMonthly: [id: 10002, name: 'Fiona', salaried: false, hourly: true, commissioned: true, startDate: LocalDate.of(2016, 8, 1), paySchedule: PaySchedule.monthly]
	]
	@PostConstruct
	void bootstrapData() {

		LocalDate today = LocalDate.now()

		EMPLOYEES.each {k, v ->
			Employee employee = new Employee(v)
			employeeRepo.save(employee)
			workRecordRepo.save(createWorkRecords(employee, LocalDate.of(2016, 8, 1), today))
		}
	}


	// create work records (timesheet) for each employee except salaried employes
	// it starts from 08-01-2016 and ends today, weekend entries are skipped
	// If an employee is hourly, each day the hours should be 8
	// If an employee is commissioned, each day the sales amount is set to be 100.20
	// If an employee is hourly and commissioned, each day hours is 4 and sales is 50.80
	private List<WorkRecord> createWorkRecords(Employee employee, LocalDate from, LocalDate to) {
		List<WorkRecord> records = []
		WorkRecordType recordType = getRecordType(employee)
		if (recordType == WorkRecordType.SALARIED) {
			return records
		}
		for(LocalDate date = from; date.isBefore(to); date = date.plusDays(1)) {
			// weekends are skipped
			if (date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY) {
				switch(recordType) {
					case WorkRecordType.HOURS:
						records << new WorkRecord(employeeId: employee.id, workDay: date,  hours: 8)
						break
					case WorkRecordType.SALES:
						records << new WorkRecord(employeeId: employee.id, workDay: date, sales: new BigDecimal(100.20))
						break
					case WorkRecordType.COMBINED:
						records << new WorkRecord(employeeId: employee.id, workDay: date, hours: 4, sales: new BigDecimal(50.80))
						break
					default:
						break
				}
			}
		}
		return records
	}

	private WorkRecordType getRecordType(Employee employee) {
		switch (employee.types) {
			case {it.contains(EmployeeType.salaried)}:
				return WorkRecordType.COMBINED.SALARIED
			case {it.contains(EmployeeType.hourly) && it.contains(EmployeeType.commissioned)}:
				return WorkRecordType.COMBINED
			case {it.contains(EmployeeType.hourly)}:
				return WorkRecordType.HOURS
			case {it.contains(EmployeeType.commissioned)}:
				return WorkRecordType.SALES
			default:
				return WorkRecordType.COMBINED.SALARIED
		}
	}

	private enum WorkRecordType {
		SALARIED,
		HOURS,
		SALES,
		COMBINED
	}
}
