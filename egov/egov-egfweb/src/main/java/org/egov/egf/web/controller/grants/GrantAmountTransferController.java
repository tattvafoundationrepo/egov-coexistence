package org.egov.egf.web.controller.grants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.egov.commons.Bank;
import org.egov.commons.Bankaccount;
import org.egov.commons.Bankbranch;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.repository.CChartOfAccountsRepository;
import org.egov.egf.commons.bank.repository.BankRepository;
import org.egov.egf.commons.bankaccount.repository.BankAccountRepository;
import org.egov.egf.commons.bankbranch.repository.BankBranchRepository;
import org.egov.egf.masters.model.GrantAmountTransfer;
import org.egov.egf.masters.model.GrantAmountTransferSearchRequest;
import org.egov.egf.masters.services.GrantAmountTransferService;
import org.egov.egf.web.adaptor.GrantAmountTransferJsonAdaptor;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping(value = "/uddgrants") // Update the context path to match the URL
public class GrantAmountTransferController {


	private static final String url = "https://uat.dev-tattvafoundation.org/egov-mdms-service/v1/_search";
	private static final String requestBody = "{\"MdmsCriteria\":{\"tenantId\":\"pg\",\"moduleDetails\":[{\"moduleName\":\"tenant\",\"masterDetails\":[{\"name\":\"tenants\"},{\"name\":\"citymodule\"}]}]},\"RequestInfo\":{\"apiId\":\"Rainmaker\",\"msgId\":\"1714627621738|en_IN\",\"plainAccessRequest\":{}}}";
	private static final String STR_GRANTAMOUNTTRANSFER = "grantAmountTransfer";

	private static final String NEW = "grantAmountTransfer-new";
	private static final String STR_GRANTAMOUNTTRANSFER_SEARCH_REQUEST = "grantAmountTransferSearchRequest";
	private static final String RESULT = "grantAmountTransfer-result";
	private static final String EDIT = "grantAmountTransfer-edit";
	private static final String VIEW = "grantAmountTransfer-view";
	private static final String SEARCH = "grantAmountTransfer-search";
	private static final String CANCELLATION_SUCCESS = "grantAmountTransfer-cancellationsuccess";
	private static String schema = "";

	@Autowired
	private GrantAmountTransferService grantAmountTransferService;
	@Autowired
	private CChartOfAccountsRepository cChartOfAccountsRepository;
	@Autowired
	private BankRepository bankRepository;

	@Autowired
	private BankAccountRepository bankAccountRepository;

	@Autowired
	private BankBranchRepository bankBranchRepository;
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(GrantAmountTransferController.class);

	@Autowired
	private MessageSource messageSource;

	@PostMapping("/newform")
	public String showNewForm(final Model model) throws IOException {
		model.addAttribute(STR_GRANTAMOUNTTRANSFER, new GrantAmountTransfer());
		prepareNewForm(model);
	//	mdmstenants.consumeApi();
	//	demandRegisterReportClient.getDemandRegisterData(1714847400000l,1715020199000l);
//		receiptRegisterClient.getReceiptRegisterData(1709231400000l,1715020199000l);
	//	GrantAmountTransfer gt = new GrantAmountTransfer();
	//	model.addAttribute(STR_GRANTAMOUNTTRANSFER, gt);
	//	Map<String, String> responseMap = grantAmountTransferService.getTenantApi(url, requestBody);
	//	model.addAttribute("ulbMap", responseMap);
	//	grantAmountTransferService.alterSequenceTest();
		return NEW;
	}
	
	
	
	
	@RequestMapping(value = "/bankAccountMapping/{bankId}", method = RequestMethod.GET)
	    @ResponseBody
	    public List<String> populateAccountNumber(@PathVariable("bankId") String bankId) {
	    System.out.println(bankId);
	    
	    List<String> acNoList = grantAmountTransferService.getBankAccounts(bankId,schema);
	    
	        return acNoList;
	    }
	
	@RequestMapping(value = "/bankMapping/{bankAccountType}", method = RequestMethod.GET)
    @ResponseBody
    public List<String> populateAccountType(@PathVariable("bankAccountType") String bankAccountType) {
       System.out.println(bankAccountType);
       List<String> bankList = grantAmountTransferService.getBanks(bankAccountType,schema);
       return bankList;
    }
	
	@RequestMapping(value = "/ulbcodeMapping/{ulbName}", method = RequestMethod.POST)
    @ResponseBody
    public List<String> populateBank(@PathVariable("ulbName") String ulbName) {
        Map<String, String> responseMap = grantAmountTransferService.getTenantApi(url, requestBody);
        String ulbCode = responseMap.get(ulbName);
        schema = ulbCode.substring(ulbCode.indexOf('.') + 1);
        System.out.println(schema);
        List<String> accountTypeList = grantAmountTransferService.getAccounTypes(schema);
        return accountTypeList;
    }
	
	@RequestMapping(value = "/grantTypeSubList/{grantType}", method = RequestMethod.GET)
		public @ResponseBody List<CChartOfAccounts> getGrantTypeSubList(@PathVariable("grantType") String grantType) {
			List<CChartOfAccounts> byGlcodeStartsWith = new ArrayList<>();
			if (grantType != null && !grantType.isEmpty()) {
				byGlcodeStartsWith = cChartOfAccountsRepository.findByGlcodeStartsWith(grantType.trim());
				Predicate<CChartOfAccounts> predicate = b -> b.getGlcode().equalsIgnoreCase(grantType);
				byGlcodeStartsWith = byGlcodeStartsWith.stream().filter(predicate.negate()).collect(Collectors.toList());
			}

			return byGlcodeStartsWith;
		}


	
	  private List<String> getULBNames() { 
		  return Arrays.asList("citya", "cityb",
	  "doiwala", "dehradun"); }
	  
	 

	private void prepareNewForm(Model model) {
		
		Map<String, String> responseMap = grantAmountTransferService.getTenantApi(url, requestBody);
		model.addAttribute("ulbMap", responseMap);
		List<Bankaccount> bankAccounts = bankAccountRepository.findByIsactiveTrue();
		System.out.println("Bank accounts: " + bankAccounts);
		model.addAttribute("bankAccounts", bankAccounts);

		List<String> glcodes1 = Arrays.asList("32010", "32020", "32030", "32040", "32050", "32060", "32070", "32080");
		List<CChartOfAccounts> grantTypes = cChartOfAccountsRepository.findByGlcodeIn(glcodes1);
		model.addAttribute("grantTypes", grantTypes);
	}

	
	@ModelAttribute("grantAmountTransfer")
	public GrantAmountTransfer getGrantAmountTransfer() {
		return new GrantAmountTransfer();
	}

	@PostMapping("/create")
	public String create(@ModelAttribute("grantAmountTransfer") final GrantAmountTransfer grantAmountTransfer,
			final BindingResult errors, final Model model, final RedirectAttributes redirectAttrs) throws IOException {
		try {

			logger.info("Creating grant amount transfer: {}", grantAmountTransfer);
			GrantAmountTransfer grantAmountTransferDB = grantAmountTransferService.create(grantAmountTransfer,schema);
			redirectAttrs.addFlashAttribute("message","Fund Disbursement happened successfully");
//					messageSource.getMessage("msg.grantAmountTransfer.success", null, null));
			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxx ===== xxxxxx " + grantAmountTransferDB.getId());
			return "redirect:/uddgrants/result/" + grantAmountTransferDB.getId() + "/create";

		} catch (Exception e) {

			logger.error("Error occurred while creating grant amount transfer", e);
			return "errorPage";
		}
	}

	@GetMapping(value = "/edit/{id}")
	public String edit(@PathVariable("id") final Long id, final Model model) {
		final GrantAmountTransfer grantAmountTransfer = grantAmountTransferService.getById(id);
		prepareNewForm(model);
		model.addAttribute(STR_GRANTAMOUNTTRANSFER, grantAmountTransfer);
		model.addAttribute("currentItemId", id);
		return EDIT;
	}

	@PostMapping(value = "/cancel/{id}")
	public String cancelGrant(@PathVariable("id") final Long id,
			@Valid @ModelAttribute final GrantAmountTransfer grantAmountTransfer, final Model model,
			final RedirectAttributes redirectAttrs) {

		System.out.println("<====== CANCEL Disbursment " + grantAmountTransfer);
		System.out.println("Id is " + id);
		try {
			GrantAmountTransfer byId = grantAmountTransferService.getById(id);
			grantAmountTransferService.cancelGrant(byId);
			redirectAttrs.addFlashAttribute("message",
					messageSource.getMessage("msg.grantAmountTransfer.success", null, null));
			System.out.println(
					"HELLO this is inside of update function////..//................................................................................................");
			return "redirect:/uddgrants/cancellationsuccess";
		} catch (Exception ex) {
			logger.error("Error occurred while cancelling grant amount transfer with id: " + id, ex);
			return "errorPage";
		}

	}

	@GetMapping(value = "/cancellationsuccess")
	public String cancellationSuccess() {
		System.out.println("<=========== inside CANCELLATION SUCCESS");
		return CANCELLATION_SUCCESS;
	}

	@GetMapping(value = "/view/{id}")
	public String view(@PathVariable("id") final Long id, final Model model) {
		final GrantAmountTransfer grantAmountTransfer = grantAmountTransferService.getById(id);
		// prepareNewForm(model);
		model.addAttribute(STR_GRANTAMOUNTTRANSFER, grantAmountTransfer);
		model.addAttribute("mode", "view");
		return VIEW;
	}

	@PostMapping(value = "/search/{mode}")
	public String search(@PathVariable("mode") @SafeHtml final String mode, final Model model) {
		final GrantAmountTransferSearchRequest grantAmountTransferSearchRequest = new GrantAmountTransferSearchRequest();
		List<String> glcodes1 = Arrays.asList("32010", "32020", "32030", "32040", "32050", "32060", "32080");
		List<CChartOfAccounts> grantTypes = cChartOfAccountsRepository.findByGlcodeIn(glcodes1);
		model.addAttribute("grantTypes", grantTypes);
		model.addAttribute(STR_GRANTAMOUNTTRANSFER_SEARCH_REQUEST, grantAmountTransferSearchRequest);
		return SEARCH;
	}

	@PostMapping(value = "/ajaxsearch/{mode}", produces = MediaType.TEXT_PLAIN_VALUE)

	@ResponseBody
	public String ajaxsearch(@PathVariable("mode") @SafeHtml final String mode, final Model model,
			@Valid @ModelAttribute final GrantAmountTransferSearchRequest grantAmountTransferSearchRequest) {
		System.out.println("===> inside ajaxsearch method in controller ");
		final List<GrantAmountTransfer> searchResultList = grantAmountTransferService
				.search(grantAmountTransferSearchRequest);
		searchResultList.forEach(System.out::println);
		System.out.println("===> exiting method in controller ");
		return new StringBuilder("{ \"data\":").append(toSearchResultJson(searchResultList)).append("}").toString();
	}

	public Object toSearchResultJson(final Object object) {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		final Gson gson = gsonBuilder
				.registerTypeAdapter(GrantAmountTransfer.class, new GrantAmountTransferJsonAdaptor()).create();
		return gson.toJson(object);
	}

	@GetMapping(value = "/result/{id}/{mode}")
	public String result(@PathVariable("id") final Long id, @PathVariable("mode") @SafeHtml final String mode,
			final Model model) {
		System.out.println("<==== inside result method ===>");
		final GrantAmountTransfer grantAmountTransfer = grantAmountTransferService.getById(id);
		System.out.println("=====================================================>>>>>>>");
		System.out.println(grantAmountTransfer);
		model.addAttribute(STR_GRANTAMOUNTTRANSFER, grantAmountTransfer);
		model.addAttribute("mode", mode);
		return RESULT;
	}

}
