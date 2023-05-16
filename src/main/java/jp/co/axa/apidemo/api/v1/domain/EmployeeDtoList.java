package jp.co.axa.apidemo.api.v1.domain;

import java.util.List;

public class EmployeeDtoList {
    private final List<EmployeeDto> employees;

    public EmployeeDtoList(List<EmployeeDto> employees) {
        this.employees = employees;
    }

    public List<EmployeeDto> getUsers() {
        return employees;
    }

}
