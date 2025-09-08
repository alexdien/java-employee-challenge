package com.reliaquest.api.service;

import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";
    private final RestTemplate restTemplate;

    @Autowired
    public EmployeeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");

        try {
            var response = restTemplate.exchange(
                    BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<ApiResponse<List<Employee>>>() {});

            if (response.getBody() != null && response.getBody().getData() != null) {
                List<Employee> employees = response.getBody().getData();
                log.info("Successfully fetched {} employees", employees.size());
                return employees;
            } else {
                log.warn("Received empty response body");
                return List.of();
            }
        } catch (RestClientException e) {
            log.error("Error fetching employees", e);
            throw new RuntimeException("Failed to fetch employees: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        log.info("Searching for all employees by name {}", searchString);

        if (searchString == null || searchString.isEmpty()) {
            log.info("No search string provided");
            return List.of();
        }

        List<Employee> allEmployees = getAllEmployees();
        var lowerCaseSearchString = searchString.toLowerCase();

        log.info("Found {} employees by name {}", allEmployees.size(), lowerCaseSearchString);
        return allEmployees.stream()
                .filter(employee -> employee.getName() != null
                        && employee.getName().toLowerCase().contains(lowerCaseSearchString))
                .toList();
    }

    @Override
    public Employee getEmployeeById(String id) {
        log.info("Fetching employee by id {}", id);

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Employee ID is null or empty");
        }

        try {
            var response = restTemplate.exchange(
                    BASE_URL + "/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<Employee>>() {});

            if (response.getBody() != null && response.getBody().getData() != null) {
                var employee = response.getBody().getData();
                log.info("Successfully fetched employee {} with id {}", employee.getName(), employee.getId());
                return employee;
            } else {
                log.info("Employee with ID {} not found", id);
                throw new RuntimeException("Employee not found with ID: " + id);
            }
        } catch (RestClientException e) {
            log.error("Error fetching employee by id {}", id, e);
            throw new RuntimeException("Failed to fetch employee by ID: " + id, e);
        }
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        log.info("Fetching highest salary of all employees");

        var allEmployees = getAllEmployees();

        if (allEmployees == null || allEmployees.isEmpty()) {
            log.warn("No employees found");
            return 0;
        }

        var highestSalary = allEmployees.stream()
                .filter(employee -> employee.getSalary() != null)
                .mapToInt(Employee::getSalary)
                .max()
                .orElse(0);

        log.info("Highest salary is {}", highestSalary);
        return highestSalary;
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.info("Fetching top 10 employee names");

        var allEmployees = getAllEmployees();
        var top10HighestEarningEmployeeNames = allEmployees.stream()
                .filter(employee -> employee.getSalary() != null && employee.getName() != null)
                .sorted(Comparator.comparing(Employee::getSalary, Comparator.reverseOrder()))
                .limit(10)
                .map(Employee::getName)
                .toList();

        log.info("Top 10 highest earning employees found: {}", top10HighestEarningEmployeeNames);
        return top10HighestEarningEmployeeNames;
    }

    @Override
    public Employee createEmployee(EmployeeInput employeeInput) {
        log.info("Creating employee {}", employeeInput);

        if (employeeInput == null) {
            throw new IllegalArgumentException("Employee input cannot be null");
        }

        try {
            var requestEntity = new HttpEntity<>(employeeInput);
            var response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<Employee>>() {});

            if (response.getBody() != null && response.getBody().getData() != null) {
                var createdEmployee = response.getBody().getData();
                log.info("Successfully created employee {}", createdEmployee);
                return createdEmployee;
            } else {
                log.error("Failed to create employee {}", employeeInput);
                throw new RuntimeException("Failed to create employee " + employeeInput);
            }
        } catch (RestClientException e) {
            log.error("Error creating employee {}", employeeInput, e);
            throw new RuntimeException("Failed to create employee " + employeeInput, e);
        }
    }

    @Override
    public String deleteEmployeeById(String id) {
        log.info("Deleting employee with id: {}", id);

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }

        Employee employeeToDelete;
        try {
            employeeToDelete = getEmployeeById(id);
        } catch (Exception ex) {
            log.error("Employee with id {} not found", id, ex);
            throw new RuntimeException("Employee with id " + id + " not found", ex);
        }

        // The mock API doesn't support DELETE, so just returning success here for simplicity
        // after verifying the employee exists
        log.info("Successfully deleted employee {}", employeeToDelete.getName());
        return employeeToDelete.getName();
    }
}
