/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package architecture.ee.spring.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import architecture.ee.util.io.SharedByteArrayOutputStream;

public class InputStreamRowMapper implements RowMapper<InputStream> {

	public InputStream mapRow(ResultSet rs, int rowNum) throws SQLException {
	    InputStream in = null;
	    InputStream inputstream;
	    try {
			in = rs.getBinaryStream(1);
			SharedByteArrayOutputStream out = new SharedByteArrayOutputStream();
			byte buf[] = new byte[1024];
			int len;
			while ((len = in.read(buf)) >= 0)
			    out.write(buf, 0, len);
			out.flush();
			inputstream = out.getInputStream();
	    } catch (IOException ioe) {
	    	throw new SQLException(ioe.getMessage());
	    }
	    return inputstream;
    }
}
