package com.example.AddPayeeService.model.mapper;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddPayeeResponseEntity<T> {
    private T data;
    private String error;

    public static <T> AddPayeeResponseEntity<T> success(T data) {
        AddPayeeResponseEntity<T> response = new AddPayeeResponseEntity<>();
        response.setData(data);
        return response;
    }
    public static <T> AddPayeeResponseEntity<T> error(String error) {
        AddPayeeResponseEntity<T> response = new AddPayeeResponseEntity<>();
        response.setError(error);
        return response;
    }
}
