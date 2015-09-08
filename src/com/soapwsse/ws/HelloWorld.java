package com.soapwsse.ws;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
/**
*
* @Author ThusharaAma
*/

//Service Endpoint Interface
@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public interface HelloWorld {

	@WebMethod
	String getHelloWorldAsString(String name);

}