package org.egov.egf.ptscheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
public class PTschedulerdata {
	
	@Component
	class DataFetcher {

	    private final RestTemplate restTemplate;

	    @Autowired
	    public DataFetcher(RestTemplate restTemplate) {
	        this.restTemplate = restTemplate;
	    }

	    @Scheduled(cron = "0 0 0 * * ?") 
	    /*public DemandRegisterResponse fetchDataFromApis() {
	        
	        String demandRegisterApiResponse = restTemplate.postForObject(
	                "https://nagarsewa.uk.gov.in/report/rainmaker-pt/DemandRegisterReport/_get?tenantId=uk.almora&pageSize=false&offset=0",
	                "{\"tenantId\":\"uk.almora\",\"reportName\":\"DemandRegisterReport\",\"searchParams\":[{\"name\":\"fromDate\",\"input\":1711909800000},{\"name\":\"toDate\",\"input\":1714501799000}],\"RequestInfo\":{\"apiId\":\"emp\",\"ver\":\"1.0\",\"ts\":1714245527606,\"action\":\"create\",\"did\":\"1\",\"key\":\"abcdkey\",\"msgId\":\"20170310130900\",\"requesterId\":\"\",\"authToken\":\"97b50e9b-9277-4db7-81ee-bfe33cf3a6f5\"}}",
	                String.class);

	        String receiptRegisterApiResponse = restTemplate.postForObject(
	                "https://nagarsewa.uk.gov.in/report/rainmaker-pt/ReceiptRegister/_get?tenantId=uk.almora&pageSize=false&offset=0",
	                "{\"tenantId\":\"uk.almora\",\"reportName\":\"ReceiptRegister\",\"searchParams\":[{\"name\":\"fromDate\",\"input\":1712428200000},{\"name\":\"toDate\",\"input\":1714328999000}],\"RequestInfo\":{\"apiId\":\"emp\",\"ver\":\"1.0\",\"ts\":1714245844343,\"action\":\"create\",\"did\":\"1\",\"key\":\"abcdkey\",\"msgId\":\"20170310130900\",\"requesterId\":\"\",\"authToken\":\"97b50e9b-9277-4db7-81ee-bfe33cf3a6f5\"}}",
	                String.class);
	        
	       
	        }

	    }*/

		private Object createDemandRegisterRequest() {
			
			return null;
		}
	

/*private DemandRegisterResponse fetchDataFromDemandRegisterApi() {
    try {
        
        ResponseEntity<DemandRegisterResponse> responseEntity = restTemplate.postForEntity(
                "https://nagarsewa.uk.gov.in/report/rainmaker-pt/DemandRegisterReport/_get",
                createDemandRegisterRequest(), 
                DemandRegisterResponse.class);

       
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
           
            return null;
        }
    } catch (Exception e) {
        
        return null;
    }
    
    
    private ReceiptRegisterResponse fetchDataFromDemandRegisterApi() {
        try {
            
            ResponseEntity<DemandRegisterResponse> responseEntity = restTemplate.postForEntity(
                    "https://nagarsewa.uk.gov.in/report/rainmaker-pt/DemandRegisterReport/_get",
                    createDemandRegisterRequest(), 
                    DemandRegisterResponse.class);

           
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
               
                return null;
            }
        } catch (Exception e) {
            
            return null;
        }

}*/
}
}

