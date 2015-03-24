package com.hdivSample.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

	@RequestMapping(value = "/login", method=RequestMethod.GET)
    public ModelAndView login(HttpServletRequest request,HttpServletResponse res) {  
    
        return new ModelAndView("login");        
    }  
	
	@RequestMapping(value = "/showMessage", method = RequestMethod.GET)
	   public ModelAndView showMessage(@ModelAttribute("command")String string, ModelMap model) {
		
		model.addAttribute("message", "hdiv test html in jsp : Form received from client");
		
		 return new ModelAndView("showMessage");    
	}
}
