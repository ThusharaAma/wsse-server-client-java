package com.soapwsse.endpoint;
 
import javax.xml.ws.Endpoint;

import com.soapwsse.ws.HelloWorldImpl;
/**
*
* @Author ThusharaAma
*/

//Endpoint publisher
public class HelloWorldPublisher{
 
	public static void main(String[] args) {
	   Endpoint.publish("http://localhost:9999/ws/hello", new HelloWorldImpl());
    }
 
}