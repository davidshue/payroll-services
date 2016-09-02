package payroll

import service.model.EmployeeType

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

		Employee hourly1 = new Employee(id: 1, name: 'Justin', type: EmployeeType.hourly, startDate: LocalDate.of(2016, 1, 1))

		employeeRepo.save(hourly1)

		workRecordRepo.save(createRecords(hourly1, LocalDate.of(2016, 8, 1), today))

		Employee hourly2 = new Employee(id: 2, name: 'Dwayne', type: EmployeeType.hourly, startDate: LocalDate.of(2016, 8, 1))

		employeeRepo.save(hourly2)

		workRecordRepo.save(createRecords(hourly2, LocalDate.of(2016, 8, 1), today))


		Employee salaried1 = new Employee(id: 101, name: 'Maria', type: EmployeeType.salaried, startDate: LocalDate.of(2016, 5, 20))

		employeeRepo.save(salaried1)

		Employee salaried2 = new Employee(id: 102, name: 'Jose', type: EmployeeType.salaried, startDate: LocalDate.of(2016, 2, 29))

		employeeRepo.save(salaried2)

		Employee commissioned1 = new Employee(id: 1001, name: 'Adam', type: EmployeeType.commissioned, startDate: LocalDate.of(2015, 10, 10))

		employeeRepo.save(commissioned1)

		workRecordRepo.save(createRecords(commissioned1, LocalDate.of(2016, 7, 1), today))

		Employee commissioned2 = new Employee(id: 1002, name: 'Jonathan', type: EmployeeType.commissioned, startDate: LocalDate.of(2015, 2, 10))

		employeeRepo.save(commissioned2)

		workRecordRepo.save(createRecords(commissioned2, LocalDate.of(2016, 7, 1), today))
	}

	private List<WorkRecord> createRecords(Employee employee, LocalDate from, LocalDate to) {
		List<WorkRecord> records = []
		for(LocalDate date = from; date.isBefore(to); date = date.plusDays(1)) {
			if (date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY) {
				switch(employee.type) {
					case EmployeeType.hourly:
						records << new WorkRecord(employeeId: employee.id, workDay: date,  hours: 8)
						break
					case EmployeeType.commissioned:
						records << new WorkRecord(employeeId: employee.id, workDay: date, sales: new BigDecimal(100.20))
						break
					default:
						break
				}
			}
		}
		return records
	}
}
