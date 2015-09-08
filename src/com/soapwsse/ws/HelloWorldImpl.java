package com.soapwsse.ws;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import com.soapwsse.handler.HeaderHandler;
/**
*
* @Author ThusharaAma
*/

//Service Implementation
@WebService(endpointInterface = "com.soapwsse.ws.HelloWorld")
@HandlerChain(file="handlers.xml")
public class HelloWorldImpl implements HelloWorld {
	@Resource
	private WebServiceContext ctx;

	@Override
	public String getHelloWorldAsString(String name) {
	
		
		String usernameFromHeader = (String) ctx.getMessageContext().get("USERNAME");
		
		String passwordFromHeader = (String) ctx.getMessageContext().get("PASSWORD");
		
		return "Hello, " + name + " (invoked by "
				+ (usernameFromHeader == null ? "[err or no 'Security' header found]" : usernameFromHeader+" key "+passwordFromHeader) + ")";
	}

}
