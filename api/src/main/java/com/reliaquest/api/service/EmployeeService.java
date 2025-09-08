package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.List;

/**
 * Service interface for managing employee operations.
 * Provides methods for CRUD operations and employee queries.
 */
public interface EmployeeService {
    /**
     * Retrieves all employees from the system.
     *
     * @return list of all employees
     * @throws RuntimeException if unable to retrieve employees
     */
    List<Employee> getAllEmployees();

    /**
     * Searches for employees whose names contain the specified search string using case-insensitive matching.
     *
     * @param searchString the string to search for in employee names
     * @return list of employees matching the search criteria, empty list if no matches found
     */
    List<Employee> getEmployeesByNameSearch(String searchString);

    /**
     * Retrieves a single employee by their unique identifier.
     *
     * @param id the employee ID
     * @return the employee if found
     * @throws IllegalArgumentException if the ID is null or invalid
     * @throws RuntimeException if employee not found
     */
    Employee getEmployeeById(String id);

    /**
     * Finds the highest salary among all employees.
     *
     * @return the highest salary value, or 0 if no employees exist
     * @throws RuntimeException if unable to retrieve employee data
     */
    Integer getHighestSalaryOfEmployees();

    /**
     * Gets the names of the top 10 highest earning employees, ordered by salary in descending order.
     *
     * @return list of employee names sorted by salary (highest first), limited to 10 results
     * @throws RuntimeException if unable to retrieve employee data
     */
    List<String> getTopTenHighestEarningEmployeeNames();

    /**
     * Creates a new employee with the provided information.
     *
     * @param employeeInput the employee data to create
     * @return the created employee
     * @throws IllegalArgumentException if the employee input is null or invalid
     * @throws RuntimeException if unable to create the employee
     */
    Employee createEmployee(EmployeeInput employeeInput);

    /**
     * Deletes an employee by their unique identifier.
     *
     * @param id the employee ID to delete
     * @return the name of the deleted employee
     * @throws IllegalArgumentException if the ID is null or invalid
     * @throws RuntimeException if employee not found or deletion fails
     */
    String deleteEmployeeById(String id);
}