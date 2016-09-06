package payroll

import javax.annotation.PostConstruct
import java.time.DayOfWeek
import java.time.LocalDate

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PayrollController {
	private final Log logger = LogFactory.getLog(getClass())
	@Autowired
	private EmployeeRepository employeeRepo

	@Autowired
	private WorkRecordRepository workRecordRepo

	@Autowired
	private PayrollService payrollService

	
	@RequestMapping('/')
	index() {
		logger.debug 'hitting root service endpoint'
		return 'Welcome to Playground Payroll Services'
	}
	
	@RequestMapping('/ws/v1/employees')
	employees(@RequestParam(value="date", required = false) String date) {
		List<Employee> employees = employeeRepo.findAll()

		employees
	}

	@RequestMapping('/ws/v1/workrecords/{employeeId}')
	workRecords(@PathVariable("employeeId") Long employeeId, @RequestParam(name="day", required = false) String day) {
		LocalDate payday = LocalDate.now()
		if (day) {
			payday = LocalDate.parse(day)
		}
		List<WorkRecord> records = workRecordRepo.findByEmployeeIdAndWorkDayAfter(employeeId, payday)

		records
	}

	@RequestMapping('/ws/v1/payroll')
	pay(@RequestParam(name="day", required = false) String day) {
		LocalDate payday = LocalDate.now()
		if (day) {
			payday = LocalDate.parse(day)
		}
		payrollService.computePayRoll(payday).collectEntries {k, v -> [k.name, v.setScale(2, BigDecimal.ROUND_HALF_UP).toString()]}
	}

	@PostConstruct
	void dataBootstrapping() {
		LocalDate today = LocalDate.now()

		Employee hourly1 = new NonSalariedEmployee(id: 1, name: 'Justin', startDate: LocalDate.of(2016, 1, 1))

		employeeRepo.save(hourly1)

		workRecordRepo.save(createRecords(hourly1, LocalDate.of(2016, 8, 1), today, WorkRecordType.HOURS))

		List<WorkRecord> records = workRecordRepo.findAll()

		println records

		Employee hourly2 = new NonSalariedEmployee(id: 2, name: 'Dwayne', startDate: LocalDate.of(2016, 8, 1))

		employeeRepo.save(hourly2)

		workRecordRepo.save(createRecords(hourly2, LocalDate.of(2016, 8, 1), today, WorkRecordType.SALES))


		Employee salaried1 = new SalariedEmployee(id: 101, name: 'Maria', startDate: LocalDate.of(2016, 5, 20))

		employeeRepo.save(salaried1)

		Employee salaried2 = new SalariedEmployee(id: 102, name: 'Jose', startDate: LocalDate.of(2016, 2, 29))

		employeeRepo.save(salaried2)

		Employee commissioned1 = new NonSalariedEmployee(id: 1001, name: 'Adam', startDate: LocalDate.of(2015, 10, 10))

		employeeRepo.save(commissioned1)

		workRecordRepo.save(createRecords(commissioned1, LocalDate.of(2016, 7, 1), today, WorkRecordType.SALES))

		Employee commissioned2 = new NonSalariedEmployee(id: 1002, name: 'Jonathan', startDate: LocalDate.of(2015, 2, 10))

		employeeRepo.save(commissioned2)

		workRecordRepo.save(createRecords(commissioned2, LocalDate.of(2016, 7, 1), today, WorkRecordType.SALES))
	}

	private List<WorkRecord> createRecords(Employee employee, LocalDate from, LocalDate to, WorkRecordType type) {
		List<WorkRecord> records = []
		for(LocalDate date = from; date.isBefore(to); date = date.plusDays(1)) {
			if (date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY) {
				switch(type) {
					case WorkRecordType.HOURS:
						records << new WorkRecord(employeeId: employee.id, workDay: date,  hours: 8)
						break
					case WorkRecordType.SALES:
						records << new WorkRecord(employeeId: employee.id, workDay: date, sales: new BigDecimal(100.20))
						break
					case WorkRecordType.HOURS_SALES:
						records << new WorkRecord(employeeId: employee.id, workDay: date, hours: 4, sales: new BigDecimal(100.20))
						break
					default:
						break
				}
			}
		}
		return records
	}

	enum WorkRecordType {
		HOURS,
		SALES,
		HOURS_SALES
	}
}
