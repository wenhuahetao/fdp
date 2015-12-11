/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fline.hadoop.data.common.datasource;

import java.util.HashMap;

import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.datasource.impl.ElasticSearchDataSource.ESOutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource;
import com.fline.hadoop.data.common.datasource.impl.HdfsDataSource;

public class OutputDataSourceFactory {
	public static OutputDataSource createOutputDataSource(int datasourceCode,
			HashMap<String, String> configMap) {
		if (datasourceCode == Constant.HBASE_DATASOURCE) {
			return new HBaseDataSource.HBaseOutputDataSource(configMap,
					Constant.HBASE_DATASOURCE_NAME);
		} else if (datasourceCode == Constant.HDFS_DATASOURCE) {
			return new HdfsDataSource.HdfsOutputDataSource(configMap,
					Constant.HDFS_DATASOURCE_NAME);
		} else if (datasourceCode == Constant.ES_DATASOURCE) {
			return new ESOutputDataSource(configMap,
					Constant.ES_DATASOURCE_NAME);
		}
		return null;
	}
}
