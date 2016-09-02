package payroll

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class PayrollServicesApplication {
    static void main(String[] args) {
        SpringApplication.run PayrollServicesApplication, args
    }
}
