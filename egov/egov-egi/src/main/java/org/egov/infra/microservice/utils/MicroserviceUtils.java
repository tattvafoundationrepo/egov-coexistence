/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.microservice.utils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.egov.infra.utils.ApplicationConstant.CITIZEN_ROLE_NAME;
import static org.egov.infra.utils.DateUtils.toDefaultDateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.egov.infra.admin.master.entity.CustomUserDetails;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.RoleService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.config.security.authentication.userdetail.CurrentUser;
import org.egov.infra.microservice.contract.ActionRequest;
import org.egov.infra.microservice.contract.ActionResponse;
import org.egov.infra.microservice.contract.CreateUserRequest;
import org.egov.infra.microservice.contract.Position;
import org.egov.infra.microservice.contract.PositionRequest;
import org.egov.infra.microservice.contract.PositionResponse;
import org.egov.infra.microservice.contract.RequestInfoWrapper;
import org.egov.infra.microservice.contract.ResponseInfo;
import org.egov.infra.microservice.contract.Task;
import org.egov.infra.microservice.contract.TaskResponse;
import org.egov.infra.microservice.contract.UserDetailResponse;
import org.egov.infra.microservice.contract.UserRequest;
import org.egov.infra.microservice.contract.UserSearchRequest;
import org.egov.infra.microservice.contract.UserSearchResponse;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.DepartmentResponse;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.DesignationResponse;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.EmployeeInfoResponse;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.infra.microservice.models.UserInfo;
import org.egov.infra.persistence.entity.enums.UserType;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.web.rest.handler.RestErrorHandler;
import org.egov.infra.web.support.ui.Inbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import redis.clients.jedis.JedisShardInfo;

@Service
public class MicroserviceUtils {

	private static final Logger LOGGER = Logger.getLogger(MicroserviceUtils.class);
	private static final String CLIENT_ID = "client.id";

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	private Environment environment;
	
	@Autowired
	public RedisTemplate<Object, Object> redisTemplate;
	
//	@Autowired
//	private RestTemplateBuilder restBuilder;
	
	@Autowired
	private RoleService roleService;

	@Value("${egov.services.workflow.url}")
	private String workflowServiceUrl;

	@Value("${egov.services.user.create.url}")
	private String userServiceUrl;

	@Value("${egov.services.user.deparment.url}")
	private String deptServiceUrl;

	@Value("${egov.services.user.designation.url}")
	private String designServiceUrl;

	@Value("${egov.services.user.approvers.url}")
	private String approverSrvcUrl;
	
	@Value("${egov.services.user.authsrvc.url}")
	private String authSrvcUrl;
	
	@Value("${egov.services.master.poistion.url}")
	private String positionSrvcUrl;
	
	@Value("${egov.services.master.actions.url}")
	private String actionSrvcUrl;
	
	@Value("${egov.services.user.search.url}")
	private String userSrcUrl;
	
	@Value("${egov.services.user.token.url}")
	private String tokenGenUrl;
	
//	@Value("${egov.services.employee.search.url}")
//	private String empSerchUrl;
	
	public RequestInfo createRequestInfo() {
		final RequestInfo requestInfo = new RequestInfo();
		requestInfo.setApiId("apiId");
		requestInfo.setVer("ver");
		requestInfo.setTs(new Date());
//		requestInfo.setUserInfo(getUserInfo());
		return requestInfo;
	}

	public UserInfo getUserInfo() {
		final User user = securityUtils.getCurrentUser();
		final List<org.egov.infra.microservice.models.RoleInfo> roles = new ArrayList<org.egov.infra.microservice.models.RoleInfo>();
		user.getRoles()
				.forEach(authority -> roles.add(new org.egov.infra.microservice.models.RoleInfo(authority.getName())));

		return new UserInfo(roles, user.getId(), user.getUsername(), user.getName(), user.getEmailId(),
				user.getMobileNumber(), user.getType().toString(), getTanentId());
	}

	public String getTanentId() {
		final String clientId = environment.getProperty(CLIENT_ID);
		String tenantId = ApplicationThreadLocals.getTenantID();
		if (isNotBlank(clientId)) {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(clientId).append('.').append(tenantId);
			tenantId = stringBuilder.toString();
		}
		return tenantId;
	}

	public void createUserMicroservice(final User user) {
		if (isNotBlank(userServiceUrl)) {

			if (user.getRoles().isEmpty() && user.getType().equals(UserType.CITIZEN))
				user.addRole(roleService.getRoleByName(CITIZEN_ROLE_NAME));

			final CreateUserRequest createUserRequest = new CreateUserRequest();
			final UserRequest userRequest = new UserRequest(user, getTanentId());
			createUserRequest.setUserRequest(userRequest);
			createUserRequest.setRequestInfo(createRequestInfo());

			final RestTemplate restTemplate = new RestTemplate();
			try {
				restTemplate.postForObject(userServiceUrl, createUserRequest, UserDetailResponse.class);
			} catch (final Exception e) {
				final String errMsg = "Exception while creating User in microservice ";
				// throw new ApplicationRuntimeException(errMsg, e);
				LOGGER.fatal(errMsg, e);
			}
		}
	}

	public List<Department> getDepartments(String access_token, String tenantId) {

		final RestTemplate restTemplate = new RestTemplate();
		final String dept_url = deptServiceUrl+"?tenantId="+tenantId;

		RequestInfo requestInfo = new RequestInfo();
		RequestInfoWrapper reqWrapper = new RequestInfoWrapper();
		
		requestInfo.setAuthToken(access_token);
		requestInfo.setTs(new Date());
		reqWrapper.setRequestInfo(requestInfo);

		DepartmentResponse depResponse = restTemplate.postForObject(dept_url, reqWrapper,
				DepartmentResponse.class);
		return depResponse.getDepartment();
	}
	
	public List<Department> getDepartmentsById(Long departmentId , String tenantId) {
		 HttpServletRequest request =  ServletActionContext.getRequest();
	        String access_token = (String) readFromRedis(request.getSession().getId(), "admin_token");
		final RestTemplate restTemplate = new RestTemplate();
		final String dept_url = deptServiceUrl+"?tenantId="+tenantId+"&id="+departmentId;

		RequestInfo requestInfo = new RequestInfo();
		RequestInfoWrapper reqWrapper = new RequestInfoWrapper();
		
		requestInfo.setAuthToken(access_token);
		requestInfo.setTs(new Date());
		reqWrapper.setRequestInfo(requestInfo);

		DepartmentResponse depResponse = restTemplate.postForObject(dept_url, reqWrapper,
				DepartmentResponse.class);
		return depResponse.getDepartment();
	}
	
	

	public List<Designation> getDesignation(String access_token, String tenantId) {

		final RestTemplate restTemplate = new RestTemplate();
		final String design_url = designServiceUrl+"?tenantId="+tenantId;

		RequestInfo requestInfo = new RequestInfo();
		RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

		requestInfo.setAuthToken(access_token);
		requestInfo.setTs(new Date());
		reqWrapper.setRequestInfo(requestInfo);

		DesignationResponse designResponse = restTemplate.postForObject(design_url, reqWrapper,
				DesignationResponse.class);
		return designResponse.getDesignation();
	}

	public List<EmployeeInfo> getApprovers(String access_token, String tenantId,String departmentId,String designationId) {

		final RestTemplate restTemplate = new RestTemplate();
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		final String approver_url = approverSrvcUrl 
					+"?tenantId="+tenantId
					+"&assignment.departmentId"+departmentId
					+"&assignment.designationId"+designationId
					+"&asOnDate"+dateFormat.format(new Date());

		RequestInfo requestInfo = new RequestInfo();
		RequestInfoWrapper reqWrapper = new RequestInfoWrapper();
		//tenantId=default&assignment.departmentId=1&assignment.designationId=1&asOnDate=28/07/2018

		requestInfo.setAuthToken(access_token);
		requestInfo.setTs(new Date());
		reqWrapper.setRequestInfo(requestInfo);

		EmployeeInfoResponse empResponse = restTemplate.postForObject(approver_url, reqWrapper,
				EmployeeInfoResponse.class);

		return empResponse.getEmployees();
	}

	public CustomUserDetails getUserDetails(String user_token,String admin_token){
		final RestTemplate restT = new RestTemplate();
    	final String authurl = authSrvcUrl+"?access_token="+user_token;
    	
    	RequestInfo reqInfo = new RequestInfo();
    	RequestInfoWrapper reqWrapper = new RequestInfoWrapper();
    	
    	reqInfo.setAuthToken(admin_token);
    	reqWrapper.setRequestInfo(reqInfo);
    	
    	
    	CustomUserDetails user = restT.postForObject(authurl, reqWrapper,CustomUserDetails.class);
    	return user;
	}
	
	public String generateAdminToken(){
		final RestTemplate restTemplate = new RestTemplate();
		RestErrorHandler errorHandler = new RestErrorHandler();
		restTemplate.setErrorHandler(errorHandler);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		header.add("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDplZ292LXVzZXItc2VjcmV0");
		
		MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
		map.add("username", "elzan");
		map.add("scope", "read");
		map.add("password", "demo");
		map.add("grant_type", "password");
		map.add("tenantId", "default");
		map.add("userType", "EMPLOYEE");
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map,header);
		
		try {
			Object response = restTemplate.postForObject(this.tokenGenUrl, request, Object.class);
			if(response!=null)
				return String.valueOf(((HashMap)response).get("access_token"));
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return null;
	}
	
	public UserSearchResponse getUserInfo(String auth_token,String tenantId,String userName){
		final RestTemplate restT = new RestTemplate();
		
		RequestInfo req_header = new RequestInfo();
		UserSearchRequest request = new UserSearchRequest();
		
		req_header.setAuthToken(auth_token);
		request.setRequestInfo(req_header);
		request.setUserName(userName);
		request.setTenantId(tenantId);
		
		UserSearchResponse response = restT.postForObject(this.userSrcUrl, request, UserSearchResponse.class);
		return response;
	}
	
	public PositionResponse createPosition(String access_token,String tenantId,List<Position> positions){
		
		final RestTemplate restT = new RestTemplate();
		PositionRequest posrequest = new PositionRequest();
		RequestInfo req_header = new RequestInfo();
		
		req_header.setAuthToken(access_token);
		posrequest.setRequestInfo(req_header);
		posrequest.setPosition(positions);
		
		PositionResponse response = restT.postForObject(this.positionSrvcUrl, posrequest, PositionResponse.class);
		
		return response;
		
	}
	
	public ActionResponse getActions(String authtoken,String tenantId,List<String> roles){
		
		final RestTemplate restT = new RestTemplate();
		ActionRequest request  = new ActionRequest();
		RequestInfo req_header = new RequestInfo();
		
		req_header.setAuthToken(authtoken);
		request.setRequestInfo(req_header);
		request.setTenantId(tenantId);
		request.setRoleCodes(roles);
		request.setActionMaster("actions-test");
		request.setEnabled(true);
		
		ActionResponse response = restT.postForObject(this.actionSrvcUrl, request, ActionResponse.class);
		
//		response.getActions()
		return response;
	}
	
	public EmployeeInfoResponse getEmployee(String authtoken,String tenantId,Long empId,Date toDay,String departmentId,String designationId){
		
		
		final RestTemplate restTemplate = new RestTemplate();
		RestErrorHandler errorHandler = new RestErrorHandler();
		restTemplate.setErrorHandler(errorHandler);
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		StringBuilder empUrl = new StringBuilder(this.approverSrvcUrl);
		empUrl.append("?tenantId="+tenantId);
		
		if(empId!=0) empUrl.append("&id="+empId);
		if(toDay!=null) empUrl.append("&asOnDate"+dateFormat.format(toDay));
		if(departmentId!=null) empUrl.append("&assignment.departmentId"+departmentId);
		if(designationId!=null) empUrl.append("&assignment.designationId"+designationId);
		

		RequestInfo requestInfo = new RequestInfo();
		RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

		requestInfo.setAuthToken(authtoken);
		requestInfo.setTs(new Date());
		reqWrapper.setRequestInfo(requestInfo);

		EmployeeInfoResponse empResponse = restTemplate.postForObject(empUrl.toString(), reqWrapper,EmployeeInfoResponse.class);
		return empResponse;
	}
	
	public List<Task> getTasks() {

		List<Task> tasks = new ArrayList<>();
		if (isNotBlank(workflowServiceUrl)) {
			final RestTemplate restTemplate = new RestTemplate();
			TaskResponse tresp;
			try {
				RequestInfo createRequestInfo = createRequestInfo();
				RequestInfoWrapper requestInfo = new RequestInfoWrapper();
				requestInfo.setRequestInfo(createRequestInfo);
				tresp = restTemplate.postForObject(workflowServiceUrl, requestInfo, TaskResponse.class);
				tasks = tresp.getTasks();
			} catch (final Exception e) {
				final String errMsg = "Exception while getting inbox items from microservice ";
				// throw new ApplicationRuntimeException(errMsg, e);
				LOGGER.fatal(errMsg, e);
			}
		}
		return tasks;
	}

	public List<Inbox> getInboxItems() {
		List<Inbox> inboxItems = new LinkedList<>();
		if (hasWorkflowService()) {
			for (Task t : getTasks()) {
				Inbox inboxItem = new Inbox();
				inboxItem.setId(t.getId());
				inboxItem.setCreatedDate(t.getCreatedDate());
				inboxItem.setDate(toDefaultDateTimeFormat(t.getCreatedDate()));
				inboxItem.setSender(t.getSenderName());
				inboxItem.setTask(t.getNatureOfTask());
				inboxItem.setStatus(t.getStatus());
				inboxItem.setDetails(t.getDetails());
				inboxItem.setLink(t.getUrl());
				inboxItem.setSender(t.getSenderName());
				inboxItems.add(inboxItem);
			}
		}
		return inboxItems;
	}
	

	public boolean hasWorkflowService() {
		return isNotBlank(workflowServiceUrl);
	}

//	public String getAccessTokenFromRedis(HttpServletRequest request) {
//
//		String access_token = null;
//
//		String sessionId = request.getSession().getId();
//
//		if (redisTemplate.hasKey(sessionId)) {
//			if (redisTemplate.opsForHash().hasKey(sessionId, "ACCESS_TOKEN")) {
//				access_token = String.valueOf(redisTemplate.opsForHash().get(sessionId, "ACCESS_TOKEN"));
//			}
//		}
//		return access_token;
//	}
	
	public void saveAuthToken(String auth_token,String sessionId){
		this.redisTemplate.opsForValue().set(auth_token, sessionId);
	}
	
	public void SaveSessionToRedis(String access_token,String sessionId,Map<String,String> values){
		
		if(null!=access_token && null!=values && values.size()>0){
			values.keySet().forEach(key->{
				this.redisTemplate.opsForHash().putIfAbsent(sessionId, key, values.get(key));
				});
			this.redisTemplate.opsForList().leftPush(access_token, sessionId);
		}
		
	}
	
	public void savetoRedis(String sessionId,String key, Object obj){
		 this.redisTemplate.opsForHash().putIfAbsent(sessionId,key,obj);
	}
	
	public Object readFromRedis(String sessionId,String key){
		return this.redisTemplate.opsForHash().get(sessionId, key);
	}
	
//	public Map<String,String> readSessionValuesFromRedis(String sessionId){
//		Map<String,String> sValues = new HashMap<>();
//		
//		if(this.redisTemplate.hasKey(sessionId)){
//			
//			this.redisTemplate.opsForHash().keys(sessionId).forEach(key->{
//				sValues.put(String.valueOf(key), String.valueOf(this.redisTemplate.opsForHash().get(sessionId, key)));
//				});;
//		}
//		
//		return sValues;
//	}
	
	
	public void removeSessionFromRedis(String access_token){
		LOGGER.info("Logout for access/auth token called :: "+access_token);
		if(this.redisTemplate.hasKey(access_token)){
			while(this.redisTemplate.opsForList().size(access_token)>0){
				this.redisTemplate.delete(this.redisTemplate.opsForList().leftPop(access_token));
			}
			this.redisTemplate.delete(access_token);
			}
		
	}
	
	public void refreshToken(String oldToken,String newToken){
		LOGGER.info("Refresh Token is called OLD::NEW"+oldToken+" :: "+newToken);
		if(this.redisTemplate.hasKey(oldToken)){
			
			while(this.redisTemplate.opsForList().size(oldToken)>0){
			
				Object sessionId =  this.redisTemplate.opsForList().leftPop(oldToken);
				if(this.redisTemplate.hasKey(sessionId)){
					if(oldToken.equals(this.redisTemplate.opsForHash().get(sessionId, "ACCESS_TOKEN"))){
						this.redisTemplate.opsForHash().delete(sessionId, "ACCESS_TOKEN");
						this.redisTemplate.opsForHash().put(sessionId, "ACCESS_TOKEN", newToken);
						this.redisTemplate.delete(oldToken);
						this.redisTemplate.opsForValue().set(newToken, sessionId);
					}
				}
				this.redisTemplate.opsForList().leftPush(newToken, sessionId);
			}
			this.redisTemplate.delete(oldToken);
			
		}
	}
	

}