package io.virtualan.core.util;

import java.util.Map;

import io.virtualan.core.model.MockRequest;
import io.virtualan.core.model.MockResponse;

public class ReturnMockResponse {
    int numberAttrMatch;
    Map<String, String> headerResponse;

    private boolean exactMatch;

    
    @Override
	public String toString() {
		return "ReturnMockResponse [numberAttrMatch=" + numberAttrMatch + ", headerResponse=" + headerResponse
				+ ", exactMatch=" + exactMatch + ", mockResponse=" + mockResponse + ", mockRequest=" + mockRequest
				+ "]";
	}

	public boolean isExactMatch() {
		return exactMatch;
	}

	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}

	public int getNumberAttrMatch() {
        return numberAttrMatch;
    }

    public void setNumberAttrMatch(int numberAttrMatch) {
        this.numberAttrMatch = numberAttrMatch;
    }

    MockResponse mockResponse;

    MockRequest mockRequest;

    public MockRequest getMockRequest() {
        return mockRequest;
    }

    public void setMockRequest(MockRequest mockRequest) {
        this.mockRequest = mockRequest;
    }

    public MockResponse getMockResponse() {
        return mockResponse;
    }

    public void setMockResponse(MockResponse mockResponse) {
        this.mockResponse = mockResponse;
    }

    public Map<String, String> getHeaderResponse() {
        return headerResponse;
    }

    public void setHeaderResponse(Map<String, String> headerResponse) {
        this.headerResponse = headerResponse;
    }



}
