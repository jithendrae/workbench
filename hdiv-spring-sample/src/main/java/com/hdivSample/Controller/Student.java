package com.hdivSample.Controller;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Student {
	   private Integer age;
	   private String name;
	   private Integer id;

	   @NotNull
	   @Min(value = 1)
	   public void setAge(Integer age) {
	      this.age = age;
	   }
	   
	   public Integer getAge() {
	      return age;
	   }

	   @NotNull
	   public void setName(String name) {
	      this.name = name;
	   }
	   
	   public String getName() {
	      return name;
	   }

	   @NotNull
	   @Min(value = 1)
	   public void setId(Integer id) {
	      this.id = id;
	   }
	   public Integer getId() {
	      return id;
	   }
	}
