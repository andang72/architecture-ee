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
package architecture.ee.component;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.springframework.core.io.Resource;

import architecture.ee.service.ConfigRoot;

public class ConfigRootImpl implements ConfigRoot {

	private Resource rootResource;

	public ConfigRootImpl(Resource rootResource) {
		this.rootResource = rootResource;
	}

	public File getFile(String name) {
		try {
			return getRootResource().createRelative(name).getFile();
		} catch (IOException e) {
		}
		return null;
	}

	private Resource getRootResource() {
		return rootResource;
	}

	public URI getRootURI() {
		try {
			return rootResource.getURI();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
