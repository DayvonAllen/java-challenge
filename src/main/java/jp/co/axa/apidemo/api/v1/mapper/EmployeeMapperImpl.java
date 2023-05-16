package jp.co.axa.apidemo.api.v1.mapper;

import jp.co.axa.apidemo.api.v1.domain.EmployeeDto;
import jp.co.axa.apidemo.entities.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapperImpl implements EmployeeMapper {
    @Override
    public EmployeeDto employeeToEmployeeDtoMapper(Employee employee) {
        if (employee == null) {
            return null;
        }
        return new EmployeeDto(employee.getId(),
                employee.getName(),
                employee.getSalary(),
                employee.getDepartment());
    }

    @Override
    public Employee employeeDtoToEmployeeMapper(EmployeeDto employeeDto) {
        if(employeeDto == null){
            return null;
        }
        return new Employee(employeeDto.getId(),
                employeeDto.getName(),
                employeeDto.getSalary(),
                employeeDto.getDepartment());
    }
}
