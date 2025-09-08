package com.reliaquest.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input DTO for creating new employees.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInput {
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
}
