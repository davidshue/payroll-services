package payroll;

import service.model.EmployeeType;
import service.model.PaySchedule;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by dshue1 on 9/5/16.
 */
@Entity
@DiscriminatorValue("hourly")
public class NonSalariedEmployee extends Employee {
	public void setPaySchedule(PaySchedule schedule) {
		this.paySchedule = schedule;
	}


	@Override
	public EmployeeType getType() {
		return EmployeeType.non_salaried;
	}
}
