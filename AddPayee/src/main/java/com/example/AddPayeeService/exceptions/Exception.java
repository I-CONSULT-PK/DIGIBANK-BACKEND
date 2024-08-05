package com.example.AddPayeeService.exceptions;

public class Exception extends RuntimeException{

    public Exception(){
        super("Resource Not Found!");
    }

    public Exception(String message){
        super(message);
    }

}
