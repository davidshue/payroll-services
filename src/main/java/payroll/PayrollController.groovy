package payroll

import service.model.EmployeeType

import javax.annotation.PostConstruct

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PayrollController {
	private final Log logger = LogFactory.getLog(getClass())
	@Value('${seed.count}')
	private int seeds = 20
	
	@Autowired
	private EmployeeRepository employeeRepo

	
	@RequestMapping('/')
	index() {
		logger.debug 'hitting root service endpoint'
		return 'Welcome to Playground Payroll Services'
	}
	
	@RequestMapping('/ws/v1/dailyPayroll')
	dailyPayroll(@RequestParam(value="date", required = false) String date) {
		List<Employee> employees = employeeRepo.findAll()

		employees
	}


	@PostConstruct
	void dataBootstrapping() {
		Employee hourly1 = new Employee(id: 1, name: 'Justine', type: EmployeeType.hourly)

		employeeRepo.save(hourly1)

		Employee salaried1 = new Employee(id: 101, name: 'Maria', type: EmployeeType.salaried)

		employeeRepo.save(salaried1)

		Employee commissioned1 = new Employee(id: 1001, name: 'Adam', type: EmployeeType.commissioned)

		employeeRepo.save(commissioned1)
	}
}
