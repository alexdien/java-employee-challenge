package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing employee operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {
    private final EmployeeService employeeService;

    /**
     * Constructs an EmployeeController with the specified employee service.
     *
     * @param employeeService the service to handle employee operations
     */
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Retrieves all employees from the system.
     *
     * @return ResponseEntity containing a list of all employees
     */
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            var employees = employeeService.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error retrieving all employees", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Searches for employees by name using case-insensitive partial matching.
     *
     * @param searchString the name or partial name to search for
     * @return ResponseEntity containing employees whose names contain the search string
     */
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        try {
            var employees = employeeService.getEmployeesByNameSearch(searchString);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error searching employees by name: '{}'", searchString, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a specific employee by their unique identifier.
     *
     * @param id the unique employee identifier
     * @return ResponseEntity containing the employee with the specified ID
     * @throws IllegalArgumentException if the ID is null or invalid
     * @throws RuntimeException         if the employee is not found
     */
    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        try {
            var employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid employee ID provided: {}", id);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Employee not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            log.error("Error retrieving employee by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Finds and returns the highest salary among all employees.
     *
     * @return ResponseEntity containing the highest salary value as an Integer
     */
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        try {
            var highestSalary = employeeService.getHighestSalaryOfEmployees();
            return ResponseEntity.ok(highestSalary);
        } catch (Exception e) {
            log.error("Error retrieving highest salary of employees", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves the names of the top 10 highest earning employees, ordered by salary in descending order.
     *
     * @return ResponseEntity containing a list of the top 10 highest earning employee names
     */
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        try {
            var topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();
            return ResponseEntity.ok(topTenNames);
        } catch (Exception e) {
            log.error("Error retrieving highest salary of employees", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Creates a new employee with the provided information.
     *
     * @param employeeInput the employee data for creating a new employee
     * @return ResponseEntity containing the newly created employee
     * @throws IllegalArgumentException if the employee input is invalid or null
     */
    @Override
    public ResponseEntity<Employee> createEmployee(EmployeeInput employeeInput) {
        try {
            var employee = employeeService.createEmployee(employeeInput);
            return ResponseEntity.ok(employee);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid employee input: {}", employeeInput);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating employee: {}", employeeInput.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes an employee from the system by their unique identifier.
     *
     * @param id the unique identifier of the employee to delete
     * @return ResponseEntity containing a success message confirming the deletion
     * @throws IllegalArgumentException if the ID is null or invalid
     * @throws RuntimeException         if the employee is not found
     */
    @Override
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        try {
            employeeService.deleteEmployeeById(id);
            return ResponseEntity.ok("Deleted employee with ID: " + id);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid employee ID: {}", id);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Employee not found for deletion with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            log.error("Error deleting employee: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}