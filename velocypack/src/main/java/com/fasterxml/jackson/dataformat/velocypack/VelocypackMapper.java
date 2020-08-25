/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.fasterxml.jackson.dataformat.velocypack;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

/**
 * @author Mark Vollmary
 */
public class VelocypackMapper extends ObjectMapper {

	private static final long serialVersionUID = 1L;

	public static class Builder extends MapperBuilder<VelocypackMapper, VelocypackMapper.Builder> {
		public Builder(VelocypackMapper m) {
			super(m);
		}
	}

	public static VelocypackMapper.Builder builder() {
		return new VelocypackMapper.Builder(new VelocypackMapper());
	}

	public static VelocypackMapper.Builder builder(VelocypackFactory jf) {
		return new VelocypackMapper.Builder(new VelocypackMapper(jf));
	}

	public VelocypackMapper() {
		this(new VelocypackFactory());
	}

	public VelocypackMapper(VelocypackFactory jf) {
		super(jf);
	}

	protected VelocypackMapper(VelocypackMapper src) {
		super(src);
	}

	@Override
	public VelocypackMapper copy() {
		_checkInvalidCopy(VelocypackMapper.class);
		return new VelocypackMapper(this);
	}

	@Override
	public Version version() {
		return PackageVersion.VERSION;
	}

	@Override
	public VelocypackFactory getFactory() {
		return (VelocypackFactory) _jsonFactory;
	}

}
