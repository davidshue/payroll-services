package payroll;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface WorkRecordRepository extends CrudRepository<WorkRecord, Long> {
	List<WorkRecord> findByEmployeeId(Long employeeId);

	List<WorkRecord> findByEmployeeIdAndWorkDayGreaterThanEqual(Long employeeId, LocalDate start);

	List<WorkRecord> findByEmployeeIdAndWorkDayGreaterThanEqualAndWorkDayBefore(Long employeeId, LocalDate start, LocalDate to);
}