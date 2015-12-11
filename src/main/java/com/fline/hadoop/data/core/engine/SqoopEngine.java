package com.fline.hadoop.data.core.engine;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.model.MConfigList;
import org.apache.sqoop.model.MConnector;
import org.apache.sqoop.model.MDriverConfig;
import org.apache.sqoop.model.MFromConfig;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.model.MLink;
import org.apache.sqoop.model.MLinkConfig;
import org.apache.sqoop.model.MSubmission;
import org.apache.sqoop.model.MToConfig;
import org.apache.sqoop.validation.Status;

import com.fline.hadoop.data.client.DataProgressListener;
import com.fline.hadoop.data.common.datasource.DataSource;
import com.fline.hadoop.data.common.datasource.InputDataSource;
import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.core.DataManagerPool;
import com.fline.hadoop.data.core.DataTransporterThread;

public class SqoopEngine {
	private static SqoopClient client = new SqoopClient(
			"http://fdp-master:12000/sqoop/");

	/**
	 * config input datasource
	 * 
	 * @param datasource
	 *            the definition of datasource
	 * @param config
	 */
	private static void configFromConfig(DataSource datasource,
			MConfigList config) {
		Map<String, String> configMap = datasource.getDataSourceConfig();
		Set<String> keys = configMap.keySet();
		for (String key : keys) {
			if (key.startsWith("fromJobConfig")) {
				try {
					config.getStringInput(key).setValue(configMap.get(key));
				} catch (Exception e) {
					System.err
							.println("when configNormalJobConfig, find a error..."
									+ e.getMessage());
				}
			}
		}

		try {
			// RDB from datasource
			String increColumn = datasource.getDataSourceConfig().get(
					"incrementalRead.checkColumn");
			if (config.getStringInput("incrementalRead.checkColumn") != null
					&& increColumn != null) {
				config.getStringInput("incrementalRead.checkColumn").setValue(
						increColumn);
			}
			String increColumnLastValue = datasource.getDataSourceConfig().get(
					"incrementalRead.lastValue");
			if (config.getStringInput("incrementalRead.lastValue") != null
					&& increColumnLastValue != null) {
				config.getStringInput("incrementalRead.lastValue").setValue(
						increColumnLastValue);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * config output datasource
	 * 
	 * @param datasource
	 *            the definition of datasource
	 * @param config
	 *            the remote sqoop configuration
	 */
	private static void configToConfig(DataSource datasource, MConfigList config) {
		Map<String, String> configMap = datasource.getDataSourceConfig();
		Set<String> keys = configMap.keySet();
		for (String key : keys) {
			if (key.startsWith("toJobConfig")) {
				try {
					config.getStringInput(key).setValue(configMap.get(key));
				} catch (Exception e) {
					System.err.println("when configtoConfig, find a error..."
							+ e.getMessage());
				}
			}
		}
	}

	/**
	 * config link.
	 * 
	 * @param datasource
	 *            definition of datasource
	 * @param config
	 *            the remote sqoop configuration
	 */
	private static void configLinkConfig(DataSource datasource,
			MConfigList config) {
		Map<String, String> configMap = datasource.getDataSourceConfig();
		Set<String> keys = configMap.keySet();
		for (String key : keys) {
			if (key.startsWith("linkConfig")) {
				try {
					config.getStringInput(key).setValue(configMap.get(key));
				} catch (Exception e) {
					System.err
							.println("when configNormalJobConfig, find a error..."
									+ e.getMessage());
				}
			}
		}
	}

	/**
	 * get sqoop connector id by datasourcename. datasourcename = the class of
	 * connector
	 * 
	 * @param client
	 *            sqoopclient
	 * @param datasource
	 *            inputdatasource or outputdatasource
	 * @return the sqoop connector id of datasource
	 * @throws Exception
	 *             unsupported exception for unknown datasource.
	 */
	private static long getConnectorId(SqoopClient client, DataSource datasource)
			throws Exception {
		String datasourcename = datasource.getDataSourceName();
		Collection<MConnector> connectors = client.getConnectors();
		for (MConnector connector : connectors) {
			if (connector.getClassName().equals(datasourcename)) {
				return connector.getPersistenceId();
			}
		}
		throw new Exception("Unsupported DataSource : " + datasourcename);
	}

	/**
	 * create link to datasource. this method will call getConnectorId when link
	 * doest not exists.
	 * 
	 * @param client
	 *            sqoop client
	 * @param datasource
	 *            inputdatasource or outputdatasource
	 * @return the linkid to datasource.
	 * @throws Exception
	 *             connector does not exists exception.
	 */
	private static long createLinkId(SqoopClient client, DataSource datasource)
			throws Exception {
		String linkname = datasource.getDataSourceDigest();
		List<MLink> links = client.getLinks();
		for (MLink link : links) {
			if (link.getName() == null) {
				continue;
			}
			if (link.getName().equals(linkname)) {
				return link.getPersistenceId();
			}
		}
		long connectorId = getConnectorId(client, datasource);
		MLink link = client.createLink(connectorId);
		link.setName(linkname);
		MLinkConfig linkconfig = link.getConnectorLinkConfig();
		configLinkConfig(datasource, linkconfig);
		Status status = client.saveLink(link);
		if (status.canProceed()) {
			return link.getPersistenceId();
		} else {
			return -1;
		}
	}

	public static int addSqoopJob(InputDataSource inputdatasource,
			OutputDataSource outputdatasource, DataProgressListener listener)
			throws Exception {
		String user = "default";
		long fromLinkId = createLinkId(client, inputdatasource);
		long toLinkId = createLinkId(client, outputdatasource);
		// if job does not exists , then create new.
		String jobname = "job_from_" + fromLinkId + "_" + toLinkId;
		List<MJob> jobs = client.getJobs();
		for (MJob tmpjob : jobs) {
			if (tmpjob.getName().equals(jobname)) {
				// set the "FROM" link job config values
				MFromConfig fromJobConfig = tmpjob.getFromJobConfig();
				configFromConfig(inputdatasource, fromJobConfig);
				// set the "TO" link job config values
				MToConfig toJobConfig = tmpjob.getToJobConfig();
				configToConfig(outputdatasource, toJobConfig);
				List<MSubmission> submissions = client
						.getSubmissionsForJob(tmpjob.getPersistenceId());
				if (submissions.size() > 0
						&& submissions.get(submissions.size() - 1).getStatus()
								.isRunning()) {
					client.stopJob(tmpjob.getPersistenceId());
					// System.err.println("Last Job is running. please wait...");
					// return 1;
				}
				MSubmission submission = client.startJob(tmpjob
						.getPersistenceId());
				if (submission.getExternalJobId() != null
						&& submission.getExternalJobId().length() > 0) {
					if (listener != null) {
						DataTransporterThread datatransporterunit = new DataTransporterThread(
								submission, listener);
						DataManagerPool
								.addDataTransporterUnit(datatransporterunit);
					}
					return 1;
				} else {
					return -1;
				}
			}
		}
		MJob job = client.createJob(fromLinkId, toLinkId);
		job.setName(jobname);
		job.setCreationUser(user);

		// set the "FROM" link job config values
		MFromConfig fromJobConfig = job.getFromJobConfig();
		configFromConfig(inputdatasource, fromJobConfig);
		// set the "TO" link job config values
		MToConfig toJobConfig = job.getToJobConfig();
		configToConfig(outputdatasource, toJobConfig);
		// set the driver config values
		MDriverConfig driverConfig = job.getDriverConfig();
		String extractorNum = inputdatasource.getDataSourceConfig().get(
				"throttlingConfig.numExtractors");
		System.out.println("extractorNum:**************:" + extractorNum);
		if (driverConfig.getIntegerInput("throttlingConfig.numExtractors") != null) {
			driverConfig.getIntegerInput("throttlingConfig.numExtractors")
					.setValue(
							Integer.valueOf(extractorNum == null ? "2"
									: extractorNum));
		}
		Status status = client.saveJob(job);
		if (status.canProceed()) {
			// start Job
			MSubmission submission = client.startJob(job.getPersistenceId());
			if (submission.getExternalJobId() != null
					&& submission.getExternalJobId().length() > 0) {
				DataTransporterThread datatransporterunit = new DataTransporterThread(
						submission, listener);
				DataManagerPool.addDataTransporterUnit(datatransporterunit);
				return 1;
			} else {
				return -1;
			}
		} else {
			// create job failed.
			return -2;
		}
	}

	public static SqoopClient getSqoopClient() {
		return client;
	}
}