package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response wrapper for API calls.
 * Encapsulates the response data along with status information
 * from external service calls.
 *
 * @param <T> the type of data contained in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    @JsonProperty("data")
    private T data;

    @JsonProperty("status")
    private String status;
}
