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
package architecture.ee.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SharedByteArrayOutputStream extends SharedOutputStream {

	private ByteArrayOutputStream outputStream;

	public SharedByteArrayOutputStream() {
		outputStream = new ByteArrayOutputStream();
	}

	public SharedByteArrayOutputStream(int size) {
		outputStream = new ByteArrayOutputStream(size);
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	public int getSize() {
		return outputStream.size();
	}

	public void write(int b) throws IOException {
		outputStream.write(b);
	}

}