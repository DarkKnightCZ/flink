/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.api.operators;

import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;

public class StreamFold<IN, OUT> extends ChainableStreamOperator<IN, OUT> {
	private static final long serialVersionUID = 1L;

	protected FoldFunction<IN, OUT> folder;
	private OUT accumulator;
	protected TypeSerializer<OUT> outTypeSerializer;

	public StreamFold(FoldFunction<IN, OUT> folder, OUT initialValue,
			TypeInformation<OUT> outTypeInformation) {
		super(folder);
		this.folder = folder;
		this.accumulator = initialValue;
		this.outTypeSerializer = outTypeInformation.createSerializer(executionConfig);
	}

	@Override
	public void run() throws Exception {
		while (isRunning && readNext() != null) {
			callUserFunctionAndLogException();
		}
	}

	@Override
	protected void callUserFunction() throws Exception {

		accumulator = folder.fold(outTypeSerializer.copy(accumulator), nextObject);
		collector.collect(accumulator);

	}
}
