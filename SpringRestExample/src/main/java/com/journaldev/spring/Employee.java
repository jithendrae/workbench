package com.journaldev.spring;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@Component
@Configuration
@ApiModel
public class Employee implements Serializable{

	private static final long serialVersionUID = -7788619177798333712L;
	
	private int id;
	private String name;
	private Date createdDate;
	
	@ApiModelProperty(position = 2, required = true, notes = "used to display employee id")
	public int getId() {
		return id;
	}
	
	public void setId(@NotNull int id) {
		this.id = id;
	}
	
	@ApiModelProperty(position = 1, required = true, notes = "used to display employee name")
	public String getName() {
		return name;
	}
	
	public void setName(@NotNull String name) {
		this.name = name;
	}
	
	@ApiModelProperty(position = 3, required = true, notes = "used to display employee created date")
	@JsonSerialize(using=DateSerializer.class)
	public Date getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	
}
