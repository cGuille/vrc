package com.guillaumecharmetant.vlchttpcontroller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class VlcHttpResponse {
	protected HttpResponse httpResponse;
	private int statusCode;
	private String statusText;
	private Document responseData = null;
	private Exception error = null;
	
	public VlcHttpResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
		StatusLine statusLine = httpResponse.getStatusLine();
		this.statusCode = statusLine.getStatusCode();
		this.statusText = statusLine.getReasonPhrase();
		HttpEntity entity = httpResponse.getEntity();
		if (entity.getContentType().getValue().startsWith("text/xml")) {
			// XML
			try {
				InputStream dataStream = new BufferedInputStream(entity.getContent());
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				this.responseData = documentBuilder.parse(dataStream);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public VlcHttpResponse(Exception e) {
		 this.error = e;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusText() {
		return statusText;
	}

	public Document getResponseData() {
		return responseData;
	}

	public Exception getError() {
		return error;
	}
}
