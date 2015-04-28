package com.hdivSample.Controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import com.hdivSample.Controller.Student;

@RestController
public class LoginController {
	
	@Autowired
	RequestDataValueProcessor requestDataValueProcessor;
	
	
	@RequestMapping(value = "/login", method=RequestMethod.GET)
	@ResponseBody
    public ModelAndView hdivFormValidation(HttpServletRequest request, HttpServletResponse res) {  
    
        return new ModelAndView("login", "command", new Student());        
    } 	
	
	
	@RequestMapping(value = "/showMessage", method = RequestMethod.POST)
	   public ModelAndView showMessage(@ModelAttribute("command") @Valid Student student, BindingResult result , ModelMap model) {		
		
		System.out.println(student.getName() + result.toString());
		
		if(result.hasErrors()){
			
			model.addAttribute("message", "Form has error: " + result.getAllErrors().get(0));			
		}
		else
			
		
		model.addAttribute("message", "hdiv test html in jsp : Form received from client");
		
		return new ModelAndView("showMessage");    
	}
	
	@RequestMapping(value = "/xssVulnerability", method=RequestMethod.GET)
	@ResponseBody
    public ModelAndView xssForm(HttpServletRequest request, HttpServletResponse res) {  
    
        return new ModelAndView("xssForm", "command", new xssFormBean());        
    }  
	
	@RequestMapping(value = "/showMessage3", method = RequestMethod.POST)
	   public ModelAndView showMessage3(@ModelAttribute("command")  @Valid xssFormBean text, BindingResult result, ModelMap model) {		
				
		if(result.hasErrors()){
			
			model.addAttribute("message", "Form has error: " + result.getAllErrors().get(0).getDefaultMessage());
			return new ModelAndView("errorMessage");    

		}
		
		else{
			model.addAttribute("message", text.getText());
			
			return new ModelAndView("showMessage");  
		}
		
  
	}
	
	
	@RequestMapping(value = "/maliciousExecution", method=RequestMethod.GET)
	@ResponseBody
    public ModelAndView maliciousFile(HttpServletRequest request, HttpServletResponse res) {  
		
		 
    
        return new ModelAndView("maliciousJsp", "command", new maliciousFileExecutionFormBean());        
    }  
	
	
	@RequestMapping(value = "/maliciousJsp", method = RequestMethod.GET)
	   public String maliciousJsp(HttpServletRequest request, @ModelAttribute("command")  @Valid maliciousFileExecutionFormBean fileName, BindingResult result, ModelMap model) throws Exception {		
				
		if(!result.hasErrors()){
			
			String dir = request.getServletContext().getRealPath("/");
			String file = fileName.getFileName();
			
			File f = new File((dir + "\\" + file).replaceAll("\\\\", "/"));
			
			
			return FileUtils.readFileToString(f);
		}
		
		return "Enter proper Filename";

	}
	
	
	@RequestMapping(value = "/directObjectReference", method=RequestMethod.GET)
	@ResponseBody
    public ModelAndView directObjectReference(HttpServletRequest request, HttpServletResponse res) {  
		
		 
    
        return new ModelAndView("dorJsp", "command", new maliciousFileExecutionFormBean());        
    }  
	
	
	@RequestMapping(value = "/objectReferenceJsp", method = RequestMethod.GET)
	   public void objectReferenceJsp(HttpServletRequest request, @ModelAttribute("command")  @Valid maliciousFileExecutionFormBean fileName, BindingResult result, ModelMap model) throws Exception {		
				
		
		
	}
	
	
	@RequestMapping(value = "/codeInjection", method=RequestMethod.GET)
	@ResponseBody
    public ModelAndView codeInjection(HttpServletRequest request, HttpServletResponse res) {  
		
		 
    
        return new ModelAndView("sqlInjectionForm", "command", new codeInjectionBean());        
    }  
	
	
	@RequestMapping(value = "/sqlInjection", method = RequestMethod.POST)
	   public String sqlInjection(@ModelAttribute("command")  @Valid codeInjectionBean injectedCode, BindingResult results,  ModelMap model) {
		
		
		if(results.hasErrors())
			return "query has Error";
		
		else			
			return "sql query run complete";

	}
	
	
	
	private static final String TEMPLATE = "Hello, %s!";

    @RequestMapping(value = "/showMessage7", method = RequestMethod.POST)
    @ResponseBody
    public HttpEntity<Greeting> greeting(
            @RequestParam(value = "name", required = false, defaultValue = "World") String name) {

        Greeting greeting = new Greeting(String.format(TEMPLATE, name));
        
        greeting.add(linkTo(methodOn(LoginController.class).greeting(name)).withSelfRel());
        
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		
		List<Link> links = greeting.getLinks();
		List<Link> processedLinks = new ArrayList<Link>();
		
		
		for (Link link : links) {
			String processedUrl = requestDataValueProcessor.processUrl(request, link.getHref());
			processedLinks.add(new Link(processedUrl, link.getRel()));
		}
		greeting.removeLinks();
		greeting.add(processedLinks);
        return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);
    }
}
