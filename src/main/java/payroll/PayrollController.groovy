package payroll

import service.model.PaySchedule

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

		Employee hourlyWeekly1 = new Employee(id: 1, name: 'Justin', salaried: false, commissioned: false,  startDate: LocalDate.of(2016, 1, 1), paySchedule: PaySchedule.weekly)

		employeeRepo.save(hourlyWeekly1)

		workRecordRepo.save(createRecords(hourlyWeekly1, LocalDate.of(2016, 8, 1), today, WorkRecordType.HOURS))

		List<WorkRecord> records = workRecordRepo.findAll()

		println records

		Employee hourlyWeekly2 = new Employee(id: 2, name: 'Dwayne', salaried: false, commissioned: false, startDate: LocalDate.of(2016, 8, 1), paySchedule: PaySchedule.monthly)

		employeeRepo.save(hourlyWeekly2)

		workRecordRepo.save(createRecords(hourlyWeekly2, LocalDate.of(2016, 8, 1), today, WorkRecordType.SALES))


		Employee salaried1 = new Employee(id: 101, name: 'Maria', salaried: true, commissioned: false, startDate: LocalDate.of(2016, 5, 20))

		employeeRepo.save(salaried1)

		Employee salaried2 = new Employee(id: 102, name: 'Jose', salaried: true, commissioned: false, startDate: LocalDate.of(2016, 2, 29))

		employeeRepo.save(salaried2)

		Employee commissionedBiweekly1 = new Employee(id: 1001, name: 'Adam', salaried: false, commissioned: true, startDate: LocalDate.of(2015, 10, 10), paySchedule: PaySchedule.biweekly)

		employeeRepo.save(commissionedBiweekly1)

		workRecordRepo.save(createRecords(commissionedBiweekly1, LocalDate.of(2016, 7, 1), today, WorkRecordType.SALES))

		Employee commissionedMonthly1 = new Employee(id: 1002, name: 'Jonathan', salaried: false, commissioned: true, startDate: LocalDate.of(2015, 2, 10), paySchedule: PaySchedule.monthly)

		employeeRepo.save(commissionedMonthly1)

		workRecordRepo.save(createRecords(commissionedMonthly1, LocalDate.of(2016, 7, 1), today, WorkRecordType.SALES))
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
