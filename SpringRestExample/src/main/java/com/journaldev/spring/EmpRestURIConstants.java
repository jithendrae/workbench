package com.journaldev.spring;

import org.springframework.stereotype.Component;

@Component
public class EmpRestURIConstants {

	public static final String DUMMY_EMP = "/rest/emp/dummy";
	public static final String GET_EMP = "/rest/emp/{id}";
	public static final String GET_ALL_EMP = "/rest/emps";
	public static final String CREATE_EMP = "/rest/emp/create";
	public static final String DELETE_EMP = "/rest/emp/delete/{id}";
	public static final String ADD_EMP = "/rest/emp/add/";
	public static final String UPDATE_EMP = "/rest/emp/update/{id}";

}
