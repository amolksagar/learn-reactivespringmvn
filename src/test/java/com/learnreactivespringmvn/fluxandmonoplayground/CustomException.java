package com.learnreactivespringmvn.fluxandmonoplayground;

public class CustomException extends Throwable{

    private String message;
    public CustomException(Throwable e){
        this.message=e.getMessage();
    }
}
