package com.soapwsse.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jws.HandlerChain;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;

import com.soapwsse.handler.HeaderHandler;
import com.soapwsse.ws.HelloWorld;
/**
*
* @Author ThusharaAma
*/

public class HelloWorldClient {

	public static void main(String[] args) throws Exception {

		URL url = new URL("http://localhost:9999/ws/hello?wsdl");

		// 1st argument service URI, refer to wsdl document above
		// 2nd argument is service name, refer to wsdl document above
		QName qname = new QName("http://ws.soapwsse.com/", "HelloWorldImplService");

		/*
		 * Service service = Service.create(url, qname);
		 * 
		 * HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver();
		 * service.setHandlerResolver(handlerResolver);
		 * 
		 * HelloWorld hello = service.getPort(HelloWorld.class);
		 * 
		 * System.out.println(hello.getHelloWorldAsString("Thushara"));
		 */

		try {
			Service service = Service.create(url, qname);
			HelloWorld port = service.getPort(HelloWorld.class);

			// Generate a handler chain to be set to a port
			List<Handler> handlerChain = new ArrayList<Handler>();

			HeaderHandler headerHandler = new HeaderHandler();

			headerHandler.setUserName("Thushara");
			headerHandler.setPassword("thushara123");

			// Add a handler to the handler chain
			handlerChain.add(headerHandler);

			// Acquire javax.xml.ws.Binding
			Binding binding = ((BindingProvider) port).getBinding();

			// Set the handler chain to a port by using javax.xml.ws.Binding

			binding.setHandlerChain(handlerChain);

			System.out.println(port.getHelloWorldAsString("Thushara"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}