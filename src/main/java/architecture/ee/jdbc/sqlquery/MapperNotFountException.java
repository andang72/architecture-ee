package architecture.ee.jdbc.sqlquery;

public class MapperNotFountException extends SqlQueryException {

	public MapperNotFountException(String msg, Throwable cause) {
		super(msg, cause); 
	}

	public MapperNotFountException(String msg) {
		super(msg); 
	}


}
