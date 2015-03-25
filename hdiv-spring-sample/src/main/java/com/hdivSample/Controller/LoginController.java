package com.hdivSample.Controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
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

@RestController
public class LoginController {
	
	@Autowired
	RequestDataValueProcessor requestDataValueProcessor;

	@RequestMapping(value = "/login", method=RequestMethod.GET)
	@ResponseBody
    public ModelAndView login(HttpServletRequest request,HttpServletResponse res) {  
    
        return new ModelAndView("login");        
    }  
	
	@RequestMapping(value = "/showMessage2", method = RequestMethod.GET)
	   public ModelAndView showMessage(@ModelAttribute("command")String string, ModelMap model) {
		
		model.addAttribute("message", "hdiv test html in jsp : Form received from client");
		
		return new ModelAndView("showMessage");    
	}
	
	private static final String TEMPLATE = "Hello, %s!";

    @RequestMapping(value = "/showMessage", method = RequestMethod.POST)
    @ResponseBody
    public HttpEntity greeting(
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
        return new ResponseEntity(greeting, HttpStatus.OK);
    }
}
