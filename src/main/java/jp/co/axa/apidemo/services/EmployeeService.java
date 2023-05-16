package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.api.v1.domain.EmployeeDto;
import jp.co.axa.apidemo.api.v1.domain.EmployeeDtoList;

public interface EmployeeService {

    public EmployeeDtoList retrieveEmployees();

    public EmployeeDto getEmployee(Long employeeId);

    public void saveEmployee(EmployeeDto employeeDto);

    public void deleteEmployee(Long employeeId);

    public EmployeeDto updateEmployee(Long id, EmployeeDto employee);
}