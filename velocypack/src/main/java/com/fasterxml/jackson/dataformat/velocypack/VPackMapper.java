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

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author Mark Vollmary
 */
public class VPackMapper extends ObjectMapper {

	private static final long serialVersionUID = 1L;

	public static class Builder extends MapperBuilder<VPackMapper, VPackMapper.Builder> {
		public Builder(VPackMapper m) {
			super(m);
		}

		public VPackMapper.Builder enable(JsonReadFeature... features) {
			for (JsonReadFeature f : features) {
				_mapper.enable(f.mappedFeature());
			}
			return this;
		}

		public VPackMapper.Builder disable(JsonReadFeature... features) {
			for (JsonReadFeature f : features) {
				_mapper.disable(f.mappedFeature());
			}
			return this;
		}

		public VPackMapper.Builder configure(JsonReadFeature f, boolean state) {
			if (state) {
				_mapper.enable(f.mappedFeature());
			} else {
				_mapper.disable(f.mappedFeature());
			}
			return this;
		}

		public VPackMapper.Builder enable(JsonWriteFeature... features) {
			for (JsonWriteFeature f : features) {
				_mapper.enable(f.mappedFeature());
			}
			return this;
		}

		public VPackMapper.Builder disable(JsonWriteFeature... features) {
			for (JsonWriteFeature f : features) {
				_mapper.disable(f.mappedFeature());
			}
			return this;
		}

		public VPackMapper.Builder configure(JsonWriteFeature f, boolean state) {
			if (state) {
				_mapper.enable(f.mappedFeature());
			} else {
				_mapper.disable(f.mappedFeature());
			}
			return this;
		}
	}

	public static VPackMapper.Builder builder() {
		return new VPackMapper.Builder(new VPackMapper());
	}

	public VPackMapper() {
		super(new VelocypackFactory());
		configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		final SimpleModule module = new SimpleModule();
		registerModule(module);
	}

}
