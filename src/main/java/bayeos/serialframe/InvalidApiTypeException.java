package bayeos.serialframe;

public class InvalidApiTypeException extends Exception {
  public InvalidApiTypeException(String msg) {
	  super(msg);
  }
  
  public InvalidApiTypeException() {
	  super();
  }
}
