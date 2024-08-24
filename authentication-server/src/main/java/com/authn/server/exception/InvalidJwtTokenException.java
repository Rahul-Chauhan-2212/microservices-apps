package com.authn.server.exception;

/**
 * @author rahulchauhan
 */
public class InvalidJwtTokenException extends RuntimeException {

  public InvalidJwtTokenException(String msg) {
    super(msg);
  }

}
