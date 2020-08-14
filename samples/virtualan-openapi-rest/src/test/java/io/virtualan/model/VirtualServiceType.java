package io.virtualan.model;

public enum VirtualServiceType {
	SPRING("springVirtualServiceInfo"), CXF_JAX_RS("cxfVirtualServiceInfo"), OPEN_API("openApiVirtualServiceInfo");

	String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	VirtualServiceType(String type) {
		this.type = type;
	}

}