package jp.co.axa.apidemo.api.v1.mapper;

import jp.co.axa.apidemo.api.v1.domain.EmployeeDto;
import jp.co.axa.apidemo.entities.Employee;

public interface EmployeeMapper {
    EmployeeDto employeeToEmployeeDtoMapper(Employee employee);
    Employee employeeDtoToEmployeeMapper(EmployeeDto employeeDto);
}
