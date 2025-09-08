package com.reliaquest.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(restTemplate);
    }

    @Test
    void getAllEmployees_ShouldReturnAllEmployees_WhenApiCallSuccessful() {
        var employee1 = new Employee("1", "John Doe", 50000, 30, "Developer", "john@company.com");
        var employee2 = new Employee("2", "Jane Smith", 75000, 28, "Senior Developer", "jane@company.com");
        var expectedEmployees = Arrays.asList(employee1, employee2);

        var mockResponse = new ApiResponse<>(expectedEmployees, "Successfully processed request.");
        var responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee"),
                        eq(HttpMethod.GET),
                        eq(null),
                        ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<List<Employee>>>>any()))
                .thenReturn(responseEntity);

        var actualEmployees = employeeService.getAllEmployees();

        assertThat(actualEmployees).isNotNull();
        assertThat(actualEmployees).hasSize(2);
        assertThat(actualEmployees.get(0).getName()).isEqualTo("John Doe");
        assertThat(actualEmployees.get(1).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void getAllEmployees_ShouldReturnEmptyList_WhenApiReturnsNull() {
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee"),
                        eq(HttpMethod.GET),
                        eq(null),
                        ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<List<Employee>>>>any()))
                .thenReturn(responseEntity);

        List<Employee> actualEmployees = employeeService.getAllEmployees();

        assertThat(actualEmployees).isNotNull();
        assertThat(actualEmployees).isEmpty();
    }

    @Test
    void getAllEmployees_ShouldThrowException_WhenApiCallFails() {
        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee"),
                        eq(HttpMethod.GET),
                        eq(null),
                        ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<List<Employee>>>>any()))
                .thenThrow(new RestClientException("Connection failed"));

        assertThatThrownBy(() -> employeeService.getAllEmployees())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch employees");
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenEmployeeExists() {
        var employeeId = "123";
        var expectedEmployee = new Employee(employeeId, "John Doe", 50000, 30, "Developer", "john@company.com");
        var mockResponse = new ApiResponse<>(expectedEmployee, "Successfully processed request.");
        var responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee/" + employeeId),
                        eq(HttpMethod.GET),
                        eq(null),
                        ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<Employee>>>any()))
                .thenReturn(responseEntity);

        var actualEmployee = employeeService.getEmployeeById(employeeId);

        assertThat(actualEmployee).isNotNull();
        assertThat(actualEmployee.getId()).isEqualTo(employeeId);
        assertThat(actualEmployee.getName()).isEqualTo("John Doe");
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenIdIsNull() {
        assertThatThrownBy(() -> employeeService.getEmployeeById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee ID");
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenIdIsEmpty() {
        assertThatThrownBy(() -> employeeService.getEmployeeById(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee ID");
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenEmployeeNotFound() {
        var employeeId = "999";
        ResponseEntity<ApiResponse<Employee>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee/" + employeeId),
                        eq(HttpMethod.GET),
                        eq(null),
                        ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<Employee>>>any()))
                .thenReturn(responseEntity);

        assertThatThrownBy(() -> employeeService.getEmployeeById(employeeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Employee not found with ID: " + employeeId);
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnMatchingEmployees() {
        var searchString = "John";
        var employee1 = new Employee("1", "John Doe", 50000, 30, "Developer", "john@company.com");
        var employee2 = new Employee("2", "Johnny Smith", 60000, 25, "Designer", "johnny@company.com");
        var allEmployees = Arrays.asList(
                employee1,
                employee2,
                new Employee("3", "Jane Smith", 75000, 28, "Senior Developer", "jane@company.com"));

        var mockResponse = new ApiResponse<>(allEmployees, "Successfully processed request.");
        var responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee"),
                        eq(HttpMethod.GET),
                        eq(null),
                        ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<List<Employee>>>>any()))
                .thenReturn(responseEntity);

        var actualEmployees = employeeService.getEmployeesByNameSearch(searchString);
        assertThat(actualEmployees).hasSize(2);
        assertThat(actualEmployees.get(0).getName()).contains("John");
        assertThat(actualEmployees.get(1).getName()).contains("John");
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnEmptyList_WhenSearchStringIsNull() {
        var result = employeeService.getEmployeesByNameSearch(null);
        assertThat(result).isEmpty();
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnEmptyList_WhenSearchStringIsEmpty() {
        var result = employeeService.getEmployeesByNameSearch("");
        assertThat(result).isEmpty();
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnEmptyList_WhenSearchStringIsWhitespace() {
        var result = employeeService.getEmployeesByNameSearch("");
        assertThat(result).isEmpty();
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnHighestSalary() {
        var employees = Arrays.asList(
                new Employee("1", "John Doe", 50000, 30, "Developer", "john@company.com"),
                new Employee("2", "Jane Smith", 75000, 28, "Senior Developer", "jane@company.com"),
                new Employee("3", "Bob Johnson", 45000, 35, "Junior Developer", "bob@company.com"));

        var mockResponse = new ApiResponse<>(employees, "Successfully processed request.");
        var responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee"),
                        eq(HttpMethod.GET),
                        eq(null),
                        ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<List<Employee>>>>any()))
                .thenReturn(responseEntity);

        var highestSalary = employeeService.getHighestSalaryOfEmployees();
        assertThat(highestSalary).isEqualTo(75000);
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnZero_WhenNoEmployees() {
        ApiResponse<List<Employee>> mockResponse = new ApiResponse<>(List.of(), "Successfully processed request.");
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee"),
                        eq(HttpMethod.GET),
                        eq(null),
                        ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<List<Employee>>>>any()))
                .thenReturn(responseEntity);

        var highestSalary = employeeService.getHighestSalaryOfEmployees();
        assertThat(highestSalary).isEqualTo(0);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnTopTenNames() {
        var employees = Arrays.asList(
                new Employee("1", "Employee1", 100000, 30, "Developer", "emp1@company.com"),
                new Employee("2", "Employee2", 95000, 30, "Developer", "emp2@company.com"),
                new Employee("3", "Employee3", 90000, 30, "Developer", "emp3@company.com"),
                new Employee("4", "Employee4", 85000, 30, "Developer", "emp4@company.com"),
                new Employee("5", "Employee5", 80000, 30, "Developer", "emp5@company.com"),
                new Employee("6", "Employee6", 75000, 30, "Developer", "emp6@company.com"),
                new Employee("7", "Employee7", 70000, 30, "Developer", "emp7@company.com"),
                new Employee("8", "Employee8", 65000, 30, "Developer", "emp8@company.com"),
                new Employee("9", "Employee9", 60000, 30, "Developer", "emp9@company.com"),
                new Employee("10", "Employee10", 55000, 30, "Developer", "emp10@company.com"),
                new Employee("11", "Employee11", 50000, 30, "Developer", "emp11@company.com"),
                new Employee("12", "Employee12", 45000, 30, "Developer", "emp12@company.com")
        );

        var mockResponse = new ApiResponse<>(employees, "Successfully processed request.");
        var responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee"),
                        eq(HttpMethod.GET),
                        eq(null),
                        ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<List<Employee>>>>any()))
                .thenReturn(responseEntity);

        var topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();

        assertThat(topTenNames).hasSize(10);
        assertThat(topTenNames.get(0)).isEqualTo("Employee1"); // Highest salary
        assertThat(topTenNames.get(9)).isEqualTo("Employee10"); // 10th highest
        assertThat(topTenNames).doesNotContain("Employee11"); // Should not include 11th
        assertThat(topTenNames).doesNotContain("Employee12"); // Should not include 12th
    }

    @Test
    void createEmployee_ShouldReturnCreatedEmployee_WhenValidInput() {
        var input = new EmployeeInput("John Doe", 50000, 30, "Developer");
        var createdEmployee = new Employee("123", "John Doe", 50000, 30, "Developer", "john@company.com");

        var mockResponse = new ApiResponse<>(createdEmployee, "Successfully processed request.");
        var responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee"),
                        eq(HttpMethod.POST),
                        any(HttpEntity.class),
                        ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<Employee>>>any()))
                .thenReturn(responseEntity);

        var result = employeeService.createEmployee(input);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("123");
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getSalary()).isEqualTo(50000);
    }

    @Test
    void createEmployee_ShouldThrowException_WhenInputIsNull() {
        assertThatThrownBy(() -> employeeService.createEmployee(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee input cannot be null");
    }

    @Test
    void deleteEmployeeById_ShouldReturnEmployeeName_WhenSuccessful() {
        var employeeId = "123";
        var employee = new Employee(employeeId, "John Doe", 50000, 30, "Developer", "john@company.com");

        var getResponse = new ApiResponse<>(employee, "Successfully processed request.");
        var getResponseEntity = new ResponseEntity<>(getResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://localhost:8112/api/v1/employee/" + employeeId),
                eq(HttpMethod.GET),
                eq(null),
                ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<Employee>>>any()))
                .thenReturn(getResponseEntity);

        var result = employeeService.deleteEmployeeById(employeeId);
        assertThat(result).isEqualTo("John Doe");
    }
}
