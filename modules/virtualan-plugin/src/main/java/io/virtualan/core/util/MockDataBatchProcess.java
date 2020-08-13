package io.virtualan.core.util;

import io.virtualan.api.ApiType;
import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;
import org.apache.cxf.helpers.IOUtils;
import org.json.JSONArray;
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
			String charset = "UTF-8";
			InputStream stream = MockDataBatchProcess.class.getClassLoader().getResourceAsStream(dataLoadFileLocation);
			if(stream != null) {
				String respData = IOUtils.toString(stream, charset);
				JSONTokener parser = new JSONTokener(respData);
				List<VirtualServiceRequest> requestList = new LinkedList<>();
				JSONArray arrayList = (JSONArray) parser.nextValue();
				for (int i = 0; i < arrayList.length(); i++) {
					VirtualServiceRequest request = createRequest((JSONObject) arrayList.get(i));
					if(request.getOperationId() != null) {
						requestList.add(request);
					} else {
						log.warn("This API("+request.getMethod()+" : "+request.getUrl()+") is not supported by this service any more:" + request);
					}
				}
				virtualService.importAllMockRequests(requestList);
				log.info("initial load of the file ("+dataLoadFileLocation+") successful!!");
				
			} else {
				log.warn("initial load of the file ("+dataLoadFileLocation+") is missing...");
			}
		}catch (Exception e){
			log.warn("Unable to load the file ("+dataLoadFileLocation+") initial load -" + e.getMessage());
		}
	}
	
	private VirtualServiceRequest createRequest(JSONObject jsonObject) {
		
		VirtualServiceRequest virtualServiceRequest = new VirtualServiceRequest();
		virtualServiceRequest.setResource(jsonObject.optString("resource"));
		virtualServiceRequest.setInput(jsonObject.optString("input"));
		virtualServiceRequest.setOutput(jsonObject.optString("output"));
		virtualServiceRequest.setHttpStatusCode(jsonObject.optString("httpStatusCode"));
		virtualServiceRequest.setMethod(jsonObject.optString("method"));
		virtualServiceRequest.setType(jsonObject.optString("type"));
		virtualServiceRequest.setRule(jsonObject.optString("rule"));
		virtualServiceRequest.setUrl(jsonObject.optString("url"));
		virtualServiceRequest.setAvailableParams(getParams(jsonObject.optJSONArray("availableParams")));
		virtualServiceRequest.setHeaderParams(getParams(jsonObject.optJSONArray("headerParams")));
		virtualServiceRequest.setExcludeList(jsonObject.optString("excludeList"));
		if(jsonObject.optString("operationId").equals("") ) {
			virtualServiceUtil.findOperationIdForService(virtualServiceRequest);
		} else {
			virtualServiceRequest.setOperationId(jsonObject.optString("operationId"));
		}
		return virtualServiceRequest;
	}
	
	private List<VirtualServiceKeyValue> getParams(JSONArray params) {
		List<VirtualServiceKeyValue> virtualServiceKeyValueList = new LinkedList<>();
		if(params != null  && params.length() > 0) {
			for (int i = 0; i < params.length(); i++) {
				JSONObject object = params.getJSONObject(i);
				VirtualServiceKeyValue virtualServiceKeyValue = new VirtualServiceKeyValue();
				virtualServiceKeyValue.setKey(object.optString("key"));
				virtualServiceKeyValue.setValue(object.optString("value"));
				virtualServiceKeyValue.setParameterType(object.optString("parameterType"));
				virtualServiceKeyValueList.add(virtualServiceKeyValue);
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
