package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.api.v1.domain.EmployeeDto;
import jp.co.axa.apidemo.api.v1.domain.EmployeeDtoList;
import jp.co.axa.apidemo.security.perms.CreatePermission;
import jp.co.axa.apidemo.security.perms.DeletePermission;
import jp.co.axa.apidemo.security.perms.ReadPermission;
import jp.co.axa.apidemo.security.perms.UpdatePermission;
import jp.co.axa.apidemo.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @ReadPermission
    @GetMapping(value = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDtoList getEmployees() {
        return employeeService.retrieveEmployees();
    }

    @ReadPermission
    @GetMapping(value = "/employees/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDto getEmployee(@PathVariable(name="employeeId")Long employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    @CreatePermission
    @PostMapping(value = "/employees", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void saveEmployee(@RequestBody EmployeeDto employeeDto){
        employeeService.saveEmployee(employeeDto);
    }

    @DeletePermission
    @DeleteMapping("/employees/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteEmployee(@PathVariable(name="employeeId")Long employeeId){
        employeeService.deleteEmployee(employeeId);
        System.out.println("Employee Deleted Successfully");
    }

    @UpdatePermission
    @PutMapping(value = "/employees/{employeeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDto updateEmployee(@RequestBody EmployeeDto employeeDto,
                                      @PathVariable(name="employeeId")Long employeeId){
        return employeeService.updateEmployee(employeeId, employeeDto);
    }
}
