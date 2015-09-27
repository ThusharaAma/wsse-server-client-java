package com.soapwsse.handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

/**
 *
 * @Author ThusharaAma
 */

public class HeaderHandler implements SOAPHandler<SOAPMessageContext> {

	private String wsseUsername;
	private String wssePassword;

	private static final String WSSE_NS_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	private static final QName QNAME_WSSE_USERNAMETOKEN = new QName(
			WSSE_NS_URI, "UsernameToken");
	private static final QName QNAME_WSSE_USERNAME = new QName(WSSE_NS_URI,
			"Username");
	private static final QName QNAME_WSSE_PASSWORD = new QName(WSSE_NS_URI,
			"Password");

	public boolean handleMessage(SOAPMessageContext context) {

		Boolean outboundProperty = (Boolean) context
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outboundProperty.booleanValue()) {

			SOAPMessage message = context.getMessage();

			try {

				SOAPEnvelope envelope = context.getMessage().getSOAPPart()
						.getEnvelope();

				if (envelope.getHeader() != null) {
					envelope.getHeader().detachNode();
				}
				SOAPHeader header = envelope.addHeader();

				SOAPElement security = header
						.addChildElement(
								"Security",
								"wsse",
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
				security.addAttribute(
						new QName("xmlns:wsu"),
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

		    	// This is used to get time in SOAP request in
				// yyyy-MM-dd'T'HH:mm:ss.SSS'Z' format
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:dd.SSS'Z'");
				formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
				// This is for TimeStamp element value
				java.util.Date create = new java.util.Date();
				java.util.Date expires = new java.util.Date(create.getTime()
						+ (5l * 60l * 1000l));
				// Adding Timestamp
				SOAPElement timestampElem = security.addChildElement(
						"Timestamp", "wsu").addAttribute(
						QName.valueOf("wsu:Id"),
						"TS-1EB4A2A52467EB9373141362942343119");
				SOAPElement elem = timestampElem.addChildElement("Created",
						"wsu");
				elem.addTextNode(formatter.format(create)); // formatter formats
															// the date to
															// String
				timestampElem.addChildElement(elem);
				elem = timestampElem.addChildElement("Expires", "wsu");
				elem.addTextNode(formatter.format(expires)); // formatter
																// formats the
																// date to
																// String
				timestampElem.addChildElement(elem);

				SOAPElement usernameToken = security.addChildElement(
						"UsernameToken", "wsse");
				usernameToken
						.addAttribute(
								new QName("xmlns:wsu"),
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

				SOAPElement username = usernameToken.addChildElement(
						"Username", "wsse");
				username.addTextNode(this.wsseUsername);

				SOAPElement password = usernameToken.addChildElement(
						"Password", "wsse");
				password.setAttribute(
						"Type",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
				password.addTextNode(this.wssePassword);

				message.saveChanges();

				// Print out the outbound SOAP message to System.out
				
				/* message.writeTo(System.out); System.out.println("");*/
				 
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

			try {
				SOAPHeader header = context.getMessage().getSOAPHeader();
				Iterator<?> headerElements = header.examineAllHeaderElements();
				while (headerElements.hasNext()) {
					SOAPHeaderElement headerElement = (SOAPHeaderElement) headerElements
							.next();
					if (headerElement.getElementName().getLocalName()
							.equals("Security")) {
						SOAPHeaderElement securityElement = headerElement;
						Iterator<?> it2 = securityElement.getChildElements();
						while (it2.hasNext()) {
							Node soapNode = (Node) it2.next();
							if (soapNode instanceof SOAPElement) {
								SOAPElement element = (SOAPElement) soapNode;
								QName elementQname = element.getElementQName();
								if (QNAME_WSSE_USERNAMETOKEN
										.equals(elementQname)) {
									SOAPElement usernameTokenElement = element;
									wsseUsername = getFirstChildElementValue(
											usernameTokenElement,
											QNAME_WSSE_USERNAME);
									wssePassword = getFirstChildElementValue(
											usernameTokenElement,
											QNAME_WSSE_PASSWORD);
									break;
								}
							}

							if (wsseUsername != null) {
								break;
							}
						}
					}
					
					context.put("USERNAME", wsseUsername);
					context.setScope("USERNAME", Scope.APPLICATION);

					context.put("PASSWORD", wssePassword);
					context.setScope("PASSWORD", Scope.APPLICATION);					
				}
				
				
			} catch (Exception e) {
				System.out.println("Error reading SOAP message context: " + e);
				e.printStackTrace();
			}
		}

		return true;

	}

	public HeaderHandler() {

	}

	public HeaderHandler(String userName, String password) {
		this.wsseUsername = userName;
		this.wssePassword = password;
	}

	public void setUserName(String userName) {
		this.wsseUsername = userName;
	}

	public void setPassword(String password) {
		this.wssePassword = password;
	}

	public String getWsseUsername() {
		return wsseUsername;
	}

	public String getWssePassword() {
		return wssePassword;
	}

	public Set getHeaders() {
		// The code below is added on order to invoke Spring secured WS.
		// Otherwise,
		// http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd
		// won't be recognised
		final QName securityHeader = new QName(
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
				"Security", "wsse");

		final HashSet headers = new HashSet();
		headers.add(securityHeader);

		return headers;
	}

	public boolean handleFault(SOAPMessageContext context) {
		// throw new UnsupportedOperationException("Not supported yet.");
		return true;
	}

	public void close(MessageContext context) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	private void generateSOAPErrMessage(SOAPMessage msg, String reason) {
		try {
			SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();
			SOAPFault soapFault = soapBody.addFault();
			soapFault.setFaultString(reason);
			throw new SOAPFaultException(soapFault);
		} catch (SOAPException e) {
		}
	}

	private String getFirstChildElementValue(SOAPElement soapElement,
			QName qNameToFind) {
		String value = null;
		Iterator<?> it = soapElement.getChildElements(qNameToFind);
		while (it.hasNext()) {
			SOAPElement element = (SOAPElement) it.next(); // use first
			value = element.getValue();
		}
		return value;
	}
}