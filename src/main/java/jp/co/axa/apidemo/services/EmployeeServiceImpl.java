package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.api.v1.domain.EmployeeDto;
import jp.co.axa.apidemo.api.v1.domain.EmployeeDtoList;
import jp.co.axa.apidemo.api.v1.mapper.EmployeeMapper;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.exceptions.AppException;
import jp.co.axa.apidemo.repositories.EmployeeRepo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepo employeeRepo;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepo employeeRepo, EmployeeMapper employeeMapper) {
        this.employeeRepo = employeeRepo;
        this.employeeMapper = employeeMapper;
    }

    public EmployeeDtoList retrieveEmployees() {
        logger.info("Fetching all employees...");
        return new EmployeeDtoList(employeeRepo
                .findAll()
                .stream()
                .map(employeeMapper::employeeToEmployeeDtoMapper)
                .collect(Collectors.toList()));
    }

    public EmployeeDto getEmployee(Long employeeId) {
        Optional<Employee> optEmp = employeeRepo.findById(employeeId);
        return employeeMapper.employeeToEmployeeDtoMapper(optEmp.get());
    }

    public void saveEmployee(EmployeeDto employeeDto){
        if (employeeDto == null || !validEmployeeDetail(employeeDto)) {
            logger.error("Error processing data in the update employee account method!");
            throw new AppException("Error processing data");
        }

        Employee employee = employeeMapper.employeeDtoToEmployeeMapper(employeeDto);
        employeeRepo.save(employee);
        logger.info("Employee successfully saved!");
    }

    public void deleteEmployee(Long employeeId){
        employeeRepo.deleteById(employeeId);
    }

    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        if (employeeDto == null || !validEmployeeDetail(employeeDto)) {
            logger.error("Error processing data in the update employee account method!");
            throw new AppException("Error processing data");
        }

        Employee employee = employeeMapper.employeeDtoToEmployeeMapper(employeeDto);
        employee.setId(id);
        Employee persistedEmployee = employeeRepo.save(employee);
        logger.info("Employee successfully updated!");
        return employeeMapper.employeeToEmployeeDtoMapper(persistedEmployee);
    }

    private Boolean validEmployeeDetail(EmployeeDto employeeDto) {
        if (StringUtils.isBlank(employeeDto.getName()) || StringUtils.isBlank(employeeDto.getDepartment())) {
            logger.error("Invalid details provided in the valid employee detail method.");
            throw new AppException("Invalid data provided.");
        }
        return true;
    }
}