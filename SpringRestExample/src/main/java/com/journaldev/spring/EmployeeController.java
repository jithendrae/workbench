package com.journaldev.spring;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Handles requests for the Employee service.
 */
@Controller
@Api(value="employee", description="Operations pertaining to Employee")

public class EmployeeController {
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
	
	//Map to store employees, ideally we should use database
	Map<Integer, Employee> empData = new HashMap<Integer, Employee>();
	
	{
		Employee emp = new Employee();
		emp.setId(9999);
		emp.setName("Dummy");
		emp.setCreatedDate(new Date());
		empData.put(9999, emp);
	}
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate );		
		return "home.jsp";
	}

	@ApiOperation(value = "dummyEmployee",response = com.journaldev.spring.Employee.class)	
	@RequestMapping(value = EmpRestURIConstants.DUMMY_EMP, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Employee> getDummyEmployee() {		
		logger.info("Start getDummyEmployee");		
		if(empData.containsKey(9999))	
			return new ResponseEntity<Employee>(empData.get(9999), HttpStatus.OK);
		else
			return new ResponseEntity<Employee>(HttpStatus.NOT_FOUND);			
	}

	@ApiOperation(value = "employeeById",response = com.journaldev.spring.Employee.class)
	@RequestMapping(value = EmpRestURIConstants.GET_EMP, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Employee> getEmployee(@PathVariable("id") int empId) {		
		logger.info("Start getEmployee. ID="+empId);		
		if(empData.containsKey(empId))	
			return new ResponseEntity<Employee>(empData.get(empId), HttpStatus.OK);
		else
			return new ResponseEntity<Employee>(HttpStatus.NOT_FOUND);				}
	
	@ApiOperation(value = "employeesList",response = com.journaldev.spring.Employee.class, responseContainer = "List")	
	@RequestMapping(value = EmpRestURIConstants.GET_ALL_EMP, method = RequestMethod.GET)
	public @ResponseBody List<Employee> getAllEmployees() {
		logger.info("Start getAllEmployees.");
		List<Employee> emps = new ArrayList<Employee>();
		Set<Integer> empIdKeys = empData.keySet();
		for(Integer i : empIdKeys){
			emps.add(empData.get(i));
		}
		return emps;
	}
	
	@ApiOperation(value = "createEmployee",response = com.journaldev.spring.Employee.class)	
	@RequestMapping(value = EmpRestURIConstants.CREATE_EMP, method = RequestMethod.POST)
	public @ResponseBody Employee createEmployee(@RequestBody Employee emp) {
		logger.info("Start createEmployee.");
		emp.setCreatedDate(new Date());
		empData.put(emp.getId(), emp);
		return emp;
	}
	
	@ApiOperation(value = "addEmployee",response = com.journaldev.spring.Employee.class)	
	@RequestMapping(value = EmpRestURIConstants.ADD_EMP, method = RequestMethod.PUT)
	public @ResponseBody HttpStatus addEmployee(@RequestBody Employee emp) {
		logger.info("Start addEmployee.");
		emp.setCreatedDate(new Date());
		empData.put(emp.getId(), emp);
		return HttpStatus.CREATED;
	}
	
	@ApiOperation(value = "updateEmployee",response = com.journaldev.spring.Employee.class)	
	@RequestMapping(value = EmpRestURIConstants.UPDATE_EMP, method = RequestMethod.PUT)
	public @ResponseBody HttpStatus updateEmployee(@RequestBody Employee emp) {
		logger.info("Start updateEmployee.");
		emp.setCreatedDate(new Date());
		if(empData.containsKey(emp.getId()))
			empData.put(emp.getId(), emp);
		return HttpStatus.ACCEPTED;
	}
	
	@ApiOperation(value = "deleteEmployee",response = com.journaldev.spring.Employee.class)	
	@RequestMapping(value = EmpRestURIConstants.DELETE_EMP, method = RequestMethod.DELETE)
	public @ResponseBody HttpStatus deleteEmployee(@PathVariable("id") int empId) {
		logger.info("Start deleteEmployee.");
		if(empData.get(empId)!=null){
			empData.remove(empId);
			return HttpStatus.NO_CONTENT;
		}		
		else
			return HttpStatus.NOT_FOUND;
	}
	
}
