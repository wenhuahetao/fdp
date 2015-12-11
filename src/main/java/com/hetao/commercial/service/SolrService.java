package com.hetao.commercial.service;

import java.util.Map;

public interface SolrService {

	Map<String,Object> listBySourceType(String sourceType,String startTime,String endTime,String tableName);

	Map<String,Object> listAutoLableDoc(String hdfsFilePath);
}
