package com.wks.calorieapp.pojos;

public enum StatusCode
{
    OK(0,"Success"),
    SERVICE_FAILED(1,"Calorie App Web Service failed unexpectedly"),
    FILE_IO_ERROR(2,"An error occurred while reading the file. "),
    FILE_NULL(3,"No file provided."),
    FILE_NOT_FOUND(4,"Operation could not be completed because file not found. "),
    FILE_TYPE_INVALID(5,"Invalid file type."),
    FILE_UPLOAD_FAILED(6,"File upload failed"),
    AUTHENTICATION_FAILED(7,"Incorrect username or password provided. "),
    NOT_REGISTERED(8,"The username does not exist."),
    TOO_FEW_ARGS(9,"Insufficient arguments provided for this operation."),
    INVALID_ARG(10,"Invalid argument provided"),
    NULL_KEY(11,"Operation could not be completed because consumer key not provided."),
    FAT_SECRET_ERROR(12,"API Error"),
    INDEX_ERROR(13,"An error occurred while indexing the file."),
    PARSE_ERROR(14,"Operation failed because results could not be parsed."),
    DB_NULL_CONNECTION(15,"Operation could not be completed because a connection to database could not be established."),
    DB_SQL_EXCEPTION(16,"Database could not be queried"),
    DB_INTEGRITY_VIOLATION(17,"Query could not be completed because of violation with db constraints"),
    DB_INSERT_FAILED(18,"Insertion to db failed possibly because record already exists."),
    UPDATE_FAILED(19,"Image could not be linked with food.");
    
    private final int code;
    private final String description;
    
    private StatusCode(int code, String description)
    {
	this.code = code;
	this.description = description;
    }
    
    public int getCode()
    {
	return code;
    }
    
    public String getDescription()
    {
	return description;
    }
    
    @Override
    public String toString()
    {
        return String.format("%d - %s", code,description);
    }
    
}
