package io.virtualan.core.util;

import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.ContentType;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import io.virtualan.service.VirtualService;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@EnableScheduling
@Configuration
public class MockDataBatchProcess implements SchedulingConfigurer {
	
	private final Logger log = LoggerFactory.getLogger(MockDataBatchProcess.class);
	
	@Autowired
	private VirtualServiceUtil virtualServiceUtil;
	
	@Autowired
	private VirtualService virtualService;

	@Value("${virtualan.task.pool.size:5}")
	private int poolSize;
	
	@Value("${virtualan.data.load:not-set}")
	private String dataLoadFileLocation;
	
	@Value("${virtualan.remove.unused-data.after:30}")
	private int removeMockDataUnusedAfter;

	@Value("${virtualan.do.cleanup:false}")
	private boolean doCleanup;

	
	@PostConstruct
	public void loadRequestdata()  {
		try {
			InputStream stream = MockDataBatchProcess.class.getClassLoader().getResourceAsStream(dataLoadFileLocation);
			if(stream != null) {
				String respData = new BufferedReader(
						new InputStreamReader(stream, StandardCharsets.UTF_8)).lines()
						.collect(Collectors.joining("\n"));
				JSONTokener parser = new JSONTokener(respData);
				List<VirtualServiceRequest> requestList = new LinkedList<>();
				JSONArray arrayList = (JSONArray) parser.nextValue();
				for (int i = 0; i < arrayList.length(); i++) {
					VirtualServiceRequest request = createRequest((JSONObject) arrayList.get(i));
					if(request.getOperationId() != null) {
						requestList.add(request);
					} else {
						log.warn("This API({} : {}) is not supported by this service any more: {}" , request.getMethod(), request.getUrl(), request);
					}
				}
				virtualService.importAllMockRequests(requestList);
				log.info("initial load of the file ({}) successful!!", dataLoadFileLocation);
				
			} else {
				log.warn("initial load of the file ({}) is missing...",dataLoadFileLocation);
			}
		}catch (Exception e){
			log.warn("Unable to load the file ({}) initial load -{}",dataLoadFileLocation , e.getMessage());
		}
	}

	private String hasValue(String value){
		if(value == null || value.trim().isEmpty()){
			return null;
		} else {
			return value;
		}
	}

	private VirtualServiceRequest createRequest(JSONObject jsonObject) {
		VirtualServiceRequest virtualServiceRequest = new VirtualServiceRequest();
		try {

			virtualServiceRequest.setResource(hasValue(jsonObject.optString("resource")));
			virtualServiceRequest.setInput(hasValue(jsonObject.optString("input")));
			virtualServiceRequest.setOutput(hasValue(jsonObject.optString("output")));
			virtualServiceRequest.setHttpStatusCode(hasValue(jsonObject.optString("httpStatusCode")));
			if (!"".equalsIgnoreCase(jsonObject.optString("method"))) {
				virtualServiceRequest.setMethod(hasValue(jsonObject.optString("method")));
			} else if (!"".equalsIgnoreCase(jsonObject.optString("responseTopicOrQueueName"))) {
				virtualServiceRequest.setMethod(hasValue(jsonObject.optString("responseTopicOrQueueName")));
			}
			virtualServiceRequest.setType(hasValue(jsonObject.optString("type")));
			virtualServiceRequest.setRequestType(hasValue(jsonObject.optString("requestType")));
			if(!jsonObject.optString("contentType").equals("")) {
				virtualServiceRequest
						.setContentType(ContentType.valueOf(jsonObject.optString("contentType")));
			}
			virtualServiceRequest.setRule(hasValue(jsonObject.optString("rule")));
			if(!"".equalsIgnoreCase(jsonObject.optString("url"))
					|| !"".equalsIgnoreCase(jsonObject.optString("brokerUrl"))) {
				if (!"".equalsIgnoreCase(jsonObject.optString("url"))) {
					virtualServiceRequest.setUrl(jsonObject.optString("url"));
				} else if (!"".equalsIgnoreCase(jsonObject.optString("brokerUrl"))) {
						virtualServiceRequest.setUrl(jsonObject.optString("brokerUrl"));
				}
			}
			virtualServiceRequest
					.setAvailableParams(getParams(jsonObject.optJSONArray("availableParams")));
			virtualServiceRequest.setHeaderParams(getParams(jsonObject.optJSONArray("headerParams")));
			virtualServiceRequest.setExcludeList(hasValue(jsonObject.optString("excludeList")));
			if (!"".equalsIgnoreCase(jsonObject.optString("requestTopicOrQueueName"))) {
				virtualServiceRequest.setOperationId(hasValue(jsonObject.optString("requestTopicOrQueueName")));
			} else if (jsonObject.optString("operationId").equals("")) {
				virtualServiceUtil.findOperationIdForService(virtualServiceRequest);
			} else {
				virtualServiceRequest.setOperationId(hasValue(jsonObject.optString("operationId")));
			}
		} catch (Exception e) {
			log.info(" unable to load the following data ({}) : Failed due to ::{} ", jsonObject, e.getMessage());
		}
		return virtualServiceRequest;
	}
	
	private List<VirtualServiceKeyValue> getParams(JSONArray params) {
		List<VirtualServiceKeyValue> virtualServiceKeyValueList = new LinkedList<>();
		if(params != null  && params.length() > 0) {
			for (int i = 0; i < params.length(); i++) {
				try {
					JSONObject object = params.getJSONObject(i);
					VirtualServiceKeyValue virtualServiceKeyValue = new VirtualServiceKeyValue();
					virtualServiceKeyValue.setKey(object.optString("key"));
					virtualServiceKeyValue.setValue(object.optString("value"));
					virtualServiceKeyValue.setParameterType(object.optString("parameterType"));
					virtualServiceKeyValueList.add(virtualServiceKeyValue);
				}catch (JSONException e){
					log.warn("Loader: {}" , e.getMessage());
				}
			}
		}
		return virtualServiceKeyValueList;
	}
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(poolSize);
		threadPoolTaskScheduler.setThreadNamePrefix("vCleanup-");
		threadPoolTaskScheduler.initialize();
		scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
	}

	@Scheduled(cron = "${virtualan.cron.expression:0 0 0 * * ?}")
	public void scheduleTaskCleanUnused() {
		virtualService.periodicalRemovalOfUnusedMocks(removeMockDataUnusedAfter, doCleanup);
	}
}
