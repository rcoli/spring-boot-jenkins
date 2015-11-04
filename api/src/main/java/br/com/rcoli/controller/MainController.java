package br.com.rcoli.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rcoli.model.Response;

@RestController
public class MainController {
	
	@Value("${my.property}")
	private String property;
	
	
	 @RequestMapping("/")
	    public Response index() {
		 
	        Response response = new Response();
	        
	    	
	        response.setMyString(property);
	        response.setMyDate(new Date());
	        response.setMyInteger(123);
	        response.setMyBoolean(true);

	        
	        
			return response;
	    }

}
