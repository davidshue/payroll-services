package payroll;

import service.model.EmployeeType;
import service.model.PaySchedule;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by dshue1 on 9/5/16.
 */
@Entity
@DiscriminatorValue("salaried")
public class SalariedEmployee extends Employee {
	public SalariedEmployee() {
		this.paySchedule = PaySchedule.monthly;
	}

	@Override
	public EmployeeType getType() {
		return EmployeeType.salaried;
	}
}
