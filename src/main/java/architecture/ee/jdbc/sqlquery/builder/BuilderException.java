package architecture.ee.jdbc.sqlquery.builder;

import architecture.ee.jdbc.sqlquery.SqlQueryException;

public class BuilderException extends SqlQueryException {

	public BuilderException(String msg) {
		super(msg);
	}

	public BuilderException(String msg, Throwable cause) {
		super(msg, cause);
	}


}
