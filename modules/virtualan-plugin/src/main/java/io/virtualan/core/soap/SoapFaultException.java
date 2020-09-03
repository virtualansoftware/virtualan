package io.virtualan.core.soap;

public class SoapFaultException extends RuntimeException {
  public SoapFaultException(String msg) {
    super(msg);
  }

  public SoapFaultException(String msg, Throwable ex) {
    super(msg, ex);
  }

  public SoapFaultException(Throwable ex) {
    super("Could not access fault: " + ex.getMessage(), ex);
  }
}