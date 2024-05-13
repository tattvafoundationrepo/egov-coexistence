package org.egov.egf.masters.services;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;

import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFiscalPeriod;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FiscalPeriodHibernateDAO;
import org.egov.commons.repository.CChartOfAccountsRepository;
import org.egov.commons.repository.CGeneralLedgerRepository;
import org.egov.commons.repository.CVoucherHeaderRepository;
import org.egov.commons.repository.VouchermisRepository;
import org.egov.commons.service.EntityTypeService;
//import org.egov.commons.utils.EntityType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import org.egov.egf.masters.model.GrantAmountTransfer;
import org.egov.egf.masters.model.GrantAmountTransferSearchRequest;
import org.egov.egf.masters.repository.GrantAmountTransferRepository;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.services.voucher.VoucherHeaderService;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GrantAmountTransferService implements EntityTypeService{
	
	@Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    
    @PersistenceContext
    private EntityManager entityManager; 
    
    @Autowired
    private GrantAmountTransferRepository grantAmountTransferRepository;
    
    @Autowired
    private FiscalPeriodHibernateDAO fphd;
    
    @Autowired
    private CChartOfAccountsRepository cChartOfAccountsRepository;
    public List<GrantAmountTransfer> getAllGrant(){
    	
    	List<GrantAmountTransfer> grants = grantAmountTransferRepository.findAll();
    	return grants;
    	
    }
    public Map<String, String> getTenantApi(String url, Object requestObject) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestObject, headers);
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(responseEntity.getBody());
                JsonNode tenantsNode = rootNode.path("MdmsRes").path("tenant").path("tenants");

                Map<String, String> tenantMap = new HashMap<>();
                for (JsonNode tenantNode : tenantsNode) {
                    String code = tenantNode.path("name").asText();
                    String name = tenantNode.path("code").asText();
                    tenantMap.put(code, name);
                }

                return tenantMap;
            } catch (Exception e) {	
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: " + responseEntity.getStatusCodeValue());
        }
        return null;
    }

  @Transactional
  public GrantAmountTransfer create(GrantAmountTransfer grantAmountTransfer,String ulbCode) {
      
      grantAmountTransfer = grantAmountTransferRepository.save(grantAmountTransfer);
 //   createGeneralLedgerEntry(grantAmountTransfer);
      //saveAccountDetailKey(grantAmountTransfer);
      executeNativeQuery(grantAmountTransfer,ulbCode);
     
      return grantAmountTransfer;
	
  }	
  @Transactional
    public List<String> getAccounTypes(String ulbCode){
	  
	  String sqlQuery = "SELECT accounttype FROM "+ulbCode+".bankaccount";
      Query query = entityManager.createNativeQuery(sqlQuery);
      List<String> accountTypes = query.getResultList();
	  return accountTypes;
    }
  
  @Transactional
  public List<String> getBankAccounts(String bankId,String ulbCode){
	  
	  String sqlQuery = "SELECT t.accountnumber FROM "+ulbCode+".bankaccount t " +
              "INNER JOIN "+ulbCode+".bankbranch b ON t.branchid = b.id " +
              "WHERE b.branchname = :bankId";

      Query query = entityManager.createNativeQuery(sqlQuery);
      query.setParameter("bankId", bankId);

      List<String> accountNumbers = query.getResultList();
      return accountNumbers;
  }
  @Transactional
  public List<String> getBanks(String bankAccountType,String ulbCode){
	  
	  String sqlQuery = "SELECT t.branchname FROM "+ulbCode+".bankbranch t " +
              "INNER JOIN "+ulbCode+".bankaccount b ON t.id = b.branchid " +
              "WHERE b.accounttype = :bankAccountType";

      Query query = entityManager.createNativeQuery(sqlQuery);
      query.setParameter("bankAccountType", bankAccountType);

      List<String> branchNames = query.getResultList();
	  return branchNames;
  }

	public List<GrantAmountTransfer> search(final GrantAmountTransferSearchRequest grantAmountTransferSearchRequest) { 
		  final CriteriaBuilder cb = entityManager.getCriteriaBuilder(); 
		  final CriteriaQuery<GrantAmountTransfer> createQuery = cb.createQuery(GrantAmountTransfer.class); 
		  final Root<GrantAmountTransfer> grantAmountTransfers = createQuery.from(GrantAmountTransfer.class);
	  createQuery.select(grantAmountTransfers); 
	  final Metamodel m =entityManager.getMetamodel(); 
	  final EntityType<GrantAmountTransfer> grantAmountTransferEntityType = m.entity(GrantAmountTransfer.class);
	  
	  final List<Predicate> predicates = new ArrayList<>(); 
	  if(grantAmountTransferSearchRequest.getName() != null) { 
		  final String name ="%" + grantAmountTransferSearchRequest.getName().toLowerCase() + "%";
	  predicates.add(cb.isNotNull(grantAmountTransfers.get("name")));
	  predicates.add(cb.like( cb.lower(grantAmountTransfers.get(grantAmountTransferEntityType.
	  getDeclaredSingularAttribute("name", String.class))), name)); } if(grantAmountTransferSearchRequest.getCode() != null) { 
		  final String code ="%" + grantAmountTransferSearchRequest.getCode().toLowerCase() + "%";
	 
		  predicates.add(cb.isNotNull(grantAmountTransfers.get("code")));
	  predicates.add(cb.like( cb.lower(grantAmountTransfers.get(grantAmountTransferEntityType.getDeclaredSingularAttribute("code", String.class))), code)); }
	  
	  createQuery.where(predicates.toArray(new Predicate[] {}));
	  createQuery.orderBy(cb.asc(grantAmountTransfers.get("name"))); 
	  final TypedQuery<GrantAmountTransfer> query =entityManager.createQuery(createQuery); 
	  return query.getResultList();
	  
	  }
	  
	@Override
	public List<? extends org.egov.commons.utils.EntityType> getAllActiveEntities(Integer accountDetailTypeId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<? extends org.egov.commons.utils.EntityType> filterActiveEntities(String filterKey, int maxRecords,
			Integer accountDetailTypeId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List getAssetCodesForProjectCode(Integer accountdetailkey) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<? extends org.egov.commons.utils.EntityType> validateEntityForRTGS(List<Long> idsList) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<? extends org.egov.commons.utils.EntityType> getEntitiesById(List<Long> idsList) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}
	@Transactional
	public GrantAmountTransfer executeNativeQuery(GrantAmountTransfer gt,String ulb) {
		final StringBuilder queryStr = new StringBuilder();
		 queryStr.append("") ;
	     SQLQuery query = persistenceService.getSession().createSQLQuery(queryStr.toString());
	   
		
	      List<CChartOfAccounts> byNameList= cChartOfAccountsRepository.findByName(gt.getName());
			  CChartOfAccounts var=byNameList.get(0);

	    LocalDate currentDates = LocalDate.now();
	    String currentMonth = String.format("%02d", currentDates.getMonthValue()); 
	    String currentFinancialYear = currentDates.format(DateTimeFormatter.ofPattern("2023-24")); 
		/*
		 * String newVoucherNumber = String.format("1/GJV/%08d/%s/%s", nextId,
		 * currentMonth, currentFinancialYear); System.out.println(newVoucherNumber);
		 */
		/*
			 * int startNumber = 1; String formattedId =
			 * String.format("%09d",startNumber+nextId);
			 * 
			 * String cgvn = "1/GVG/CGVN" + formattedId; System.out.println(cgvn);
		*/ 
	  Date currentDateAndTime = new Date();

	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(currentDateAndTime);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    Date currentDate = calendar.getTime();
	    
	 //voucher header   
	    
	   		    
			    String sqlQuery1 = "SELECT COALESCE(MAX(id), 0) FROM " + ulb + ".voucherheader";
			    BigInteger maxVcId = (BigInteger) entityManager.createNativeQuery(sqlQuery1).getSingleResult();
			    maxVcId = maxVcId.add(BigInteger.ONE);
			    
				/*
				 * int year = LocalDate.now().getYear(); int year = gt.getDate().getYear();
				 */
			    String financialYear = getFinancialYear(gt.getDate());
			    
			        CFiscalPeriod cp = fphd.getFiscalPeriodByDate(gt.getDate());
			        Long fid = cp.getId();
			    
			        // Format the counter with leading zeros
			        DecimalFormat counterFormat = new DecimalFormat("00000000");
			        String formattedCounter = counterFormat.format(maxVcId);

			        // Format the year for the code
			        // String formattedYear = String.format("%02d", year % 100);
			        int month = LocalDate.now().getMonthValue();
			        String formattedMonth = String.format("%02d", month);

			        // Generate the code using the formatted parts
			    //    String vnum = String.format("1/GJV/1%s/%s/%02d-%d", formattedCounter, formattedMonth, year % 100, (year + 1) % 100);
			        String vnum = String.format("1/GJV/1%s/%s/%s", formattedCounter, formattedMonth,financialYear);
			        String cgvn = "1/JVG/CGVN10000000000"+maxVcId;
			    
			    String sqlQuery2 = "INSERT INTO "+ ulb +".voucherheader " +
			            "(id, \"name\", \"type\", description, effectivedate, vouchernumber, " +
			            "voucherdate, fundid, fiscalperiodid,status,cgvn,createddate )" +
			            "VALUES(:maxVcId, 'JVGeneral', 'Journal Voucher', 'null', :currentDateAndTime, :vnum, :currentDate,1 ,21,0,:cgvn,:createddate)";

			  entityManager.createNativeQuery(sqlQuery2)
			    .setParameter("maxVcId",maxVcId)
			    .setParameter("currentDateAndTime", currentDateAndTime)
			    .setParameter("vnum", vnum)
			    .setParameter("currentDate", gt.getDate())
			    .setParameter("cgvn", cgvn)
			    .setParameter("createddate", currentDateAndTime)
			    .executeUpdate();
			   
	   //vouchermis 
  
	    String sqlQuery3 = "SELECT COALESCE(MAX(id), 0) FROM " + ulb + ".vouchermis";
			    BigInteger maxVmId = (BigInteger) entityManager.createNativeQuery(sqlQuery3).getSingleResult();
			     maxVmId = maxVmId.add(BigInteger.ONE);      
			   	
			    	 String sqlQuery4 ="INSERT INTO "+ ulb +".vouchermis " +
			                 "( id,departmentcode,voucherheaderid )" +
			                   "VALUES( :maxVmId,'DEPT_13',:maxVcId)";
			    	 
			       entityManager.createNativeQuery(sqlQuery4)
			    	 .setParameter("maxVmId", maxVmId)
			         .setParameter("maxVcId", maxVcId)
			         .executeUpdate();

			       

			List<CChartOfAccounts> coa1 = cChartOfAccountsRepository.findByName(gt.getName());
			String creditGlCode = coa1.get(0).getGlcode();
			Long creditGlcodeId = coa1.get(0).getId();
			
			
			String debitGlCode = "45021";
			Long debitGlcodeId = 311l;
			       List<String> creditGlCodeLIst = grantAmountTransferRepository.findBankAccountTypeByBankAccountNumber(gt.getBankAccountNumber());
					
					  ///generalLedger
					  
					  String sqlQuery5 = "SELECT COALESCE(MAX(id), 0) FROM " + ulb +
					  ".generalledger";
					  BigInteger maxGenId = (BigInteger) entityManager.createNativeQuery(sqlQuery5).getSingleResult(); 
					  
					  maxGenId =maxGenId.add(BigInteger.ONE);
					 
					  //  maxGenId1 =maxGenId.add(BigInteger.ONE);
				
					  
					  ////generalLedger entry for debit
					  
					  String sqlQuery6 = "INSERT INTO "+ ulb +".generalledger " +
					  "(id,voucherlineid, effectivedate, glcodeid, glcode, debitamount, creditamount, "
					  + "description, voucherheaderid ) " +
					  "VALUES(:maxGenId, 1, :currentDateAndTime, :glCodeId, :glCode, :debitAmount, :creditAmount, "
					  + ":description, :maxVcId)";
					  
					  entityManager.createNativeQuery(sqlQuery6) 
					  .setParameter("maxGenId",maxGenId)
					  .setParameter("currentDateAndTime", currentDateAndTime)
					  .setParameter("glCodeId", debitGlcodeId) 
					  .setParameter("glCode", debitGlCode) 
					  .setParameter("debitAmount", gt.getAmount().doubleValue())
					  .setParameter("creditAmount", 0.00) 
					  .setParameter("description",  "Fund Disbursement by UDD") 
					  .setParameter("maxVcId", maxVcId)
					  .executeUpdate();
					  
					  
					  ////generalLedger entry for credit
					  
					  maxGenId = maxGenId.add(BigInteger.ONE); 
					  String sqlQuery7 = "INSERT INTO "+
					  ulb +".generalledger " +
					  "(id,voucherlineid, effectivedate, glcodeid, glcode, debitamount, creditamount, "
					  + "description, voucherheaderid ) " +
					  "VALUES(:id, 2, :currentDateAndTime, :glCodeId, :glCode, :debitAmount, :creditAmount, "
					  + ":description, :maxVcId)";
					  
						/*
						 * BigInteger one = new BigInteger("1"); BigInteger id = maxGenId.add(one);
						 */
					  entityManager.createNativeQuery(sqlQuery7) 
					  .setParameter("id",maxGenId)
					  .setParameter("currentDateAndTime", currentDateAndTime)
					  .setParameter("glCodeId", creditGlcodeId) 
					  .setParameter("glCode",creditGlCode)	  
					  .setParameter("debitAmount", 0.00)
					  .setParameter("creditAmount", gt.getAmount().doubleValue())
					  .setParameter("description", "Fund Disbursement by UDD")
					  .setParameter("maxVcId", maxVcId) 
					  .executeUpdate();    
					 
					  
						
						  maxVcId = maxVcId.add(BigInteger.ONE); 
						  maxVmId = maxVmId.add(BigInteger.ONE);
						  maxGenId =maxGenId.add(BigInteger.ONE);
						  
						  String sqlQuery8 = "ALTER SEQUENCE " + ulb + ".seq_vouchermis RESTART WITH " + maxVmId.longValue();
						  entityManager.createNativeQuery(sqlQuery8).executeUpdate();
						  
						  String sqlQuery9 ="ALTER SEQUENCE "+ulb+".seq_voucherheader RESTART WITH "+  maxVcId.longValue();
						  entityManager.createNativeQuery(sqlQuery9).executeUpdate();
						  
						  String sqlQuery10 =  "ALTER SEQUENCE "+ulb+".seq_generalledger RESTART WITH "+ maxGenId.longValue();
						  entityManager.createNativeQuery(sqlQuery10).executeUpdate();
						 
					  
						
							/*
							 * String sqlQuery8 = "DO $$ " + "BEGIN " +
							 * "  ALTER SEQUENCE "+ulb+".seq_vouchermis RESTART WITH :maxVmId; " +
							 * "  ALTER SEQUENCE "+ulb+".seq_voucherheader RESTART WITH :maxVcId; " +
							 * "  ALTER SEQUENCE "+ulb+".seq_generalledger RESTART WITH :maxGenId; " +
							 * "END $$";
							 * 
							 * entityManager.createNativeQuery(sqlQuery8) .setParameter("maxVmId", maxVmId)
							 * .setParameter("maxVcId", maxVcId) .setParameter("maxGenId", maxGenId)
							 * .executeUpdate();
							 */
						  
							
							
					 
	    return gt; 
	}
	
	public void alterSequenceTest() {
		
		
		  String sqlQuery = "ALTER SEQUENCE seq_vouchermis RESTART WITH 838";
		  entityManager.createNativeQuery(sqlQuery).executeUpdate();
	}
	
	 public static String getFinancialYear(Date date) {
	       
		 LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	        int year = localDate.getYear(); 
	        int nextYear = year + 1; 
	        int month = localDate.getMonthValue();
	        if (month >= 4) {
	            return year + "-" + (nextYear % 100); 
	        } else {
	            return (year - 1) + "-" + (year % 100); 
	        }
	    }
  
	private Optional<CVoucherHeader> getDetailsForMaxIdFromVoucherHeader() {
	// TODO Auto-generated method stub
	return null;
}

	private Object SimpleDateFormat(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private void saveAccountDetailKey(GrantAmountTransfer grantAmountTransfer) {
		// TODO Auto-generated method stub
		
	}



	public GrantAmountTransfer getById(Long id) {
		// TODO Auto-generated method stub
		return grantAmountTransferRepository.findOne(id);
	}



	public void cancelGrant(GrantAmountTransfer grantAmountTransfer) {
		grantAmountTransferRepository.delete(grantAmountTransfer.getId());
		
	}



	
}
