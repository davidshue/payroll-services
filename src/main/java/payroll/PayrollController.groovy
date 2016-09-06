package payroll

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
	
	@RequestMapping('/ws/v2/employees')
	employees(@RequestParam(value="date", required = false) String date) {
		List<Employee> employees = employeeRepo.findAll()

		employees
	}

	@RequestMapping('/ws/v2/workrecords/{employeeId}')
	workRecords(@PathVariable("employeeId") Long employeeId, @RequestParam(name="day", required = false) String day) {
		LocalDate payday = LocalDate.now()
		if (day) {
			payday = LocalDate.parse(day)
		}
		List<WorkRecord> records = workRecordRepo.findByEmployeeIdAndWorkDayAfter(employeeId, payday)

		records
	}

	@RequestMapping('/ws/v2/payroll')
	pay(@RequestParam(name="day", required = false) String day) {
		LocalDate payday = LocalDate.now()
		if (day) {
			payday = LocalDate.parse(day)
		}
		payrollService.computePayRoll(payday).collectEntries {k, v -> [k.name, v.setScale(2, BigDecimal.ROUND_HALF_UP).toString()]}
	}
}
