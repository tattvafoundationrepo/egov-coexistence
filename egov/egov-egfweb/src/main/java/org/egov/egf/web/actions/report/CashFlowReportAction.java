
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
package org.egov.egf.web.actions.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.repository.CChartOfAccountsRepository;
import org.egov.commons.repository.CGeneralLedgerRepository;
import org.egov.commons.service.ChartOfAccountDetailService;
import org.egov.egf.model.Statement;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.reporting.util.ReportUtil;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.services.report.CashFlowScheduleService;
import org.egov.services.report.CashFlowService;
import org.egov.utils.Constants;
import org.egov.utils.ReportHelper;
import org.hibernate.FlushMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ParentPackage("egov")
@Results({ @Result(name = "report", location = "cashFlowReport-report.jsp"),
		@Result(name = "scheduleResults", location = "cashFlowReport-scheduleResults.jsp"),
		//@Result(name = "allScheduleResults", location = "cashFlowReport-allScheduleResults.jsp"),
		@Result(name = "detailResults", location = "cashFlowReport-detailResults.jsp"),
		@Result(name = "results", location = "cashFlowReport-results.jsp"),
		@Result(name = "PDF", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
				Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/pdf", Constants.CONTENT_DISPOSITION,
				"no-cache;filename=cashFlowStatement.pdf" }),
		@Result(name = "XLS", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
				Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/xls", Constants.CONTENT_DISPOSITION,
				"no-cache;filename=cashFlowStatement.xls" }) })
public class CashFlowReportAction extends BaseFormAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 91711010096900620L;
	private static final String INCOME_EXPENSE_PDF = "PDF";
	private static final String INCOME_EXPENSE_XLS = "XLS";
	private static SimpleDateFormat FORMATDDMMYYYY = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	InputStream inputStream;
	ReportHelper reportHelper;
	Statement cashFlowStatement = new Statement();
	CashFlowScheduleService cashFlowScheduleService;
	private String majorCode;
	private String minorCode;
	private String scheduleNo;
	private String financialYearId;
	// private String asOndate;
	private Date todayDate;
	private String asOnDateRange;
	private String period;
	private Integer fundId;
	private final StringBuffer heading = new StringBuffer();
	private StringBuffer scheduleheading = new StringBuffer();
	private StringBuffer statementheading = new StringBuffer();
	private Map<String, String> reportMap = new HashMap<>();
	List<CChartOfAccounts> listChartOfAccounts;
	private boolean detailReport = false;
	
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
	private EgovMasterDataCaching masterDataCache;

	@Autowired
	private CityService cityService;
	
	@Autowired
	private CashFlowService cashFlowService;
	
	

	public Map<String, String> getReportMap() {
		return reportMap;
	}

	public void setReportMap(Map<String, String> reportMap) {
		this.reportMap = reportMap;
	}

	public void setCashFlowService(final CashFlowService cashFlowService) {
		this.cashFlowService = cashFlowService;
	}

	public void setCashFlowScheduleService(final CashFlowScheduleService cashFlowScheduleService) {
		this.cashFlowScheduleService = cashFlowScheduleService;
	}

	public void setReportHelper(final ReportHelper reportHelper) {
		this.reportHelper = reportHelper;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public Statement getCashFlowStatement() {
		return cashFlowStatement;
	}

	public CashFlowReportAction() {
		addRelatedEntity("department", Department.class);
		addRelatedEntity("function", CFunction.class);
		addRelatedEntity("functionary", Functionary.class);
		addRelatedEntity("financialYear", CFinancialYear.class);
		addRelatedEntity("field", Boundary.class);
		addRelatedEntity("fund", Fund.class);
	}

	@Override
	public void prepare() {
		persistenceService.getSession().setDefaultReadOnly(true);
		persistenceService.getSession().setFlushMode(FlushMode.MANUAL);
		super.prepare();
		if (!parameters.containsKey("showDropDown")) {
			addDropdownData("departmentList", masterDataCache.get("egi-department"));
			addDropdownData("functionList", masterDataCache.get("egi-function"));
//            addDropdownData("functionaryList", masterDataCache.get("egi-functionary"));
			addDropdownData("fundDropDownList", masterDataCache.get("egi-fund"));
//            addDropdownData("fieldList", masterDataCache.get("egi-ward"));
			addDropdownData("financialYearList", getPersistenceService()
					.findAllBy("from CFinancialYear where isActive=true  order by finYearRange desc "));
		}
	}

	protected void setRelatedEntitesOn() {
		setTodayDate(new Date());
		if (cashFlowStatement.getFund() != null && cashFlowStatement.getFund().getId() != null
				&& cashFlowStatement.getFund().getId() != 0) {
			cashFlowStatement.setFund(
					(Fund) getPersistenceService().find("from Fund where id=?", cashFlowStatement.getFund().getId()));
			heading.append(" in " + cashFlowStatement.getFund().getName());
		}
		if (cashFlowStatement.getDepartment() != null && cashFlowStatement.getDepartment().getCode() != null
				&& !"null".equalsIgnoreCase(cashFlowStatement.getDepartment().getCode())) {
//            incomeExpenditureStatement.setDepartment((Department) getPersistenceService().find("from Department where id=?",
//                    incomeExpenditureStatement.getDepartment().getId()));
			Department dept = microserviceUtils.getDepartmentByCode(cashFlowStatement.getDepartment().getCode());
			cashFlowStatement.setDepartment(dept);
			heading.append(" in " + cashFlowStatement.getDepartment().getName() + " Department");
		} else
			cashFlowStatement.setDepartment(null);
		if (cashFlowStatement.getFinancialYear() != null && cashFlowStatement.getFinancialYear().getId() != null
				&& cashFlowStatement.getFinancialYear().getId() != 0) {
			cashFlowStatement.setFinancialYear((CFinancialYear) getPersistenceService()
					.find("from CFinancialYear where id=?", cashFlowStatement.getFinancialYear().getId()));
			heading.append(" for the Financial Year " + cashFlowStatement.getFinancialYear().getFinYearRange());
		}
		if (cashFlowStatement.getFunction() != null && cashFlowStatement.getFunction().getId() != null
				&& cashFlowStatement.getFunction().getId() != 0) {
			cashFlowStatement.setFunction((CFunction) getPersistenceService().find("from CFunction where id=?",
					cashFlowStatement.getFunction().getId()));
			heading.append(" in Function Code " + cashFlowStatement.getFunction().getName());
		}
		if (cashFlowStatement.getField() != null && cashFlowStatement.getField().getId() != null
				&& cashFlowStatement.getField().getId() != 0) {
			cashFlowStatement.setField((Boundary) getPersistenceService().find("from Boundary where id=?",
					cashFlowStatement.getField().getId()));
			heading.append(" in the field value" + cashFlowStatement.getField().getName());
		}

		if (cashFlowStatement.getFunctionary() != null && cashFlowStatement.getFunctionary().getId() != null
				&& cashFlowStatement.getFunctionary().getId() != 0) {
			cashFlowStatement.setFunctionary((Functionary) getPersistenceService().find("from Functionary where id=?",
					cashFlowStatement.getFunctionary().getId()));
			heading.append(" and " + cashFlowStatement.getFunctionary().getName() + " Functionary");
		}

	}

	public void setCashFlowStatement(final Statement cashFlowStatement) {
		this.cashFlowStatement = cashFlowStatement;
	}

	@Override
	public Object getModel() {
		return cashFlowStatement;
	}

	@Action(value = "/report/cashFlowReport-generateCashFlowReport")
	public String generateCashFlowReport() {
		// populateDataSource();

		return "report";
	}

	/*
	 * @ReadOnly
	 *      
	 * @Action(value = "/report/cashFlowReport-generateCashFlowSubReport") public
	 * String generateCashFlowSubReport() { setDetailReport(false);
	 * populateDataSourceForSchedule(); return "scheduleResults"; }
	 * 
	 * @ReadOnly
	 * 
	 * @Action(value = "/report/cashFlowReport-generateScheduleReport") public
	 * String generateScheduleReport() { populateDataSourceForAllSchedules(); return
	 * "allScheduleResults"; }
	 * 
	 * @ReadOnly
	 * 
	 * @Action(value = "/report/cashFlowReport-generateDetailCodeReport") public
	 * String generateDetailCodeReport() { setDetailReport(true);
	 * populateSchedulewiseDetailCodeReport(); return "scheduleResults"; }
	 * 
	 * private void populateSchedulewiseDetailCodeReport() { setRelatedEntitesOn();
	 * scheduleheading.append("Csh and Flow Schedule Statement").append(heading); if
	 * (cashFlowStatement.getFund() != null && cashFlowStatement.getFund().getId()
	 * != null && cashFlowStatement.getFund().getId() != 0) { final List<Fund>
	 * fundlist = new ArrayList<Fund>(); fundlist.add(cashFlowStatement.getFund());
	 * cashFlowStatement.setFunds(fundlist);
	 * cashFlowScheduleService.populateDetailcode(cashFlowStatement);
	 * 
	 * } else { cashFlowStatement.setFunds(cashFlowService.getFunds());
	 * cashFlowScheduleService.populateDetailcode(cashFlowStatement); } }
	 * 
	 * 
	 * private void populateDataSourceForSchedule() { setDetailReport(false);
	 * setRelatedEntitesOn();
	 * 
	 * scheduleheading.append("Cash Flow Schedule Statement").append(heading); if
	 * (cashFlowStatement.getFund() != null && cashFlowStatement.getFund().getId()
	 * != null && cashFlowStatement.getFund().getId() != 0) { final List<Fund>
	 * fundlist = new ArrayList<Fund>(); fundlist.add(cashFlowStatement.getFund());
	 * cashFlowStatement.setFunds(fundlist);
	 * cashFlowScheduleService.populateDataForLedgerSchedule(cashFlowStatement,
	 * parameters.get("majorCode")[0]);
	 * 
	 * } else { cashFlowStatement.setFunds(cashFlowService.getFunds());
	 * cashFlowScheduleService.populateDataForLedgerSchedule(cashFlowStatement,
	 * parameters.get("majorCode")[0]);
	 * 
	 * } }
	 * 
	 * private void populateDataSourceForAllSchedules() { setRelatedEntitesOn(); if
	 * (cashFlowStatement.getFund() != null && cashFlowStatement.getFund().getId()
	 * != null && cashFlowStatement.getFund().getId() != 0) { final List<Fund>
	 * fundlist = new ArrayList<Fund>(); fundlist.add(cashFlowStatement.getFund());
	 * cashFlowStatement.setFunds(fundlist);
	 * cashFlowScheduleService.populateDataForAllSchedules(cashFlowStatement); }
	 * else { //cashFlowStatement.setFunds(cashFlowService.getFunds());
	 * cashFlowScheduleService.populateDataForAllSchedules(cashFlowStatement); } }
	 * 
	 */
	public String printCashFlowReport() {
		populateDataSource();
		return "report";
	}

	
	
	@Action(value = "/report/cashFlowReport-ajaxPrintCashFlowReport")
	public String ajaxPrintCashFlowReport() {
		// populateDataSource();
		populateCashFlowReportResult();
		return "results";
	}

	
	//added by me
	@Action(value ="/report/cashFlowReport-generateDetailCodeReport")
	public String ajaxPrintCashFlowReportDetailResults() {
		//statementheading.append("Cash-Flow-Statement").append(heading);
		// populateDataSource();
		populateCashFlowReportResult();
		return "detailResults";
	}
//me
	private void populateCashFlowReportResult() {
		statementheading.append("Cash-Flow-Statement").append(heading);
		if (cashFlowService == null)
			System.out.println("cashflowservice null hai");
		reportMap= cashFlowService.fetchReportData();
		

	}

	
	  protected void populateDataSource() {
	  
	  setRelatedEntitesOn();
	  
	  statementheading.append("Cash-Flow-Statement").append(heading); if
	  (cashFlowStatement.getFund() != null && cashFlowStatement.getFund().getId()
	  != null && cashFlowStatement.getFund().getId() != 0) { final List<Fund>
	  fundlist = new ArrayList<Fund>(); fundlist.add(cashFlowStatement.getFund());
	  cashFlowStatement.setFunds(fundlist);
	  cashFlowService.populateCFStatement(cashFlowStatement); } else {
	  //cashFlowStatement.setFunds(cashFlowService.getFunds());
	  cashFlowService.populateCFStatement(cashFlowStatement); } 
	  }
	 
	  
	  
	  
	  
	  

	  
	  /***
	  @Action(value = "/report/cashFlowReport-generateCashFlowPdf") public String
	  generateCahFlowPdf() throws JRException, IOException {
	  //populateDataSource(); final String heading = ReportUtil.getCityName()
	  +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) +
	  "\\n" + statementheading.toString(); final String subtitle =
	  "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate()); final JasperPrint
	  jasper = reportHelper.generateCashFlowReportJasperPrint(cashFlowStatement,
	  heading, getPreviousYearToDate(), getCurrentYearToDate(), subtitle, true);
	  inputStream = reportHelper.exportPdf(inputStream, jasper); return
	  INCOME_EXPENSE_PDF; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateDetailCodePdf") public String
	  generateDetailCodePdf() throws JRException, IOException {
	  populateSchedulewiseDetailCodeReport(); final String heading =
	  ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? ""
	  :cityService.getCityGrade()) + "\\n" + statementheading.toString(); final
	  String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate());
	  final JasperPrint jasper =
	  reportHelper.generatecashFlowReportJasperPrint(cashFlowStatement, heading,
	  getPreviousYearToDate(), getCurrentYearToDate(), subtitle, true); inputStream
	  = reportHelper.exportPdf(inputStream, jasper); return INCOME_EXPENSE_PDF; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateDetailCodeXls") public String
	  generateDetailCodeXls() throws JRException, IOException {
	  populateSchedulewiseDetailCodeReport(); final String heading =
	  ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? ""
	  :cityService.getCityGrade()) + "\\n" + statementheading.toString(); final
	  String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
	  + "                                               "; final JasperPrint jasper
	  = reportHelper.generateCashFlowReportJasperPrint(cashFlowStatement, heading,
	  getPreviousYearToDate(), getCurrentYearToDate(), subtitle, true); inputStream
	  = reportHelper.exportXls(inputStream, jasper); return INCOME_EXPENSE_XLS; }
	  
	  public String getUlbName() { final Query query =
	  persistenceService.getSession().createSQLQuery(
	  "select name from companydetail"); final List<String> result = query.list();
	  if (result != null) return result.get(0); return ""; }
	  
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateCashFlowXls") public String
	  generateCashFlowXls() throws JRException, IOException {
	  //populateDataSource(); final String heading = ReportUtil.getCityName()
	  +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) +
	  "\\n" + statementheading.toString(); final String subtitle =
	  "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate()) +
	  "                                               "; final JasperPrint jasper =
	  reportHelper.generateCashFlowReportJasperPrint(cashFlowStatement, heading,
	  getPreviousYearToDate(), getCurrentYearToDate(), subtitle, true); inputStream
	  = reportHelper.exportXls(inputStream, jasper); return INCOME_EXPENSE_XLS; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateSchedulePdf") public String
	  generateSchedulePdf() throws JRException, IOException {
	  populateDataSourceForAllSchedules(); final JasperPrint jasper =
	  reportHelper.generateFinancialStatementReportJasperPrint(cashFlowStatement,
	  getText("report.ie.heading"), heading.toString(), getPreviousYearToDate(),
	  getCurrentYearToDate(), false); inputStream =
	  reportHelper.exportPdf(inputStream, jasper); return INCOME_EXPENSE_PDF; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateScheduleXls") public String
	  generateScheduleXls() throws JRException, IOException {
	  populateDataSourceForAllSchedules(); final JasperPrint jasper =
	  reportHelper.generateFinancialStatementReportJasperPrint(cashFlowStatement,
	  getText("report.ie.heading"), heading.toString(), getPreviousYearToDate(),
	  getCurrentYearToDate(), false); inputStream =
	  reportHelper.exportXls(inputStream, jasper); return INCOME_EXPENSE_XLS; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateCashFlowSchedulePdf") public
	  String generateCashFlowSchedulePdf() throws JRException, IOException{
	  populateDataSourceForSchedule(); final String heading =
	  ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? ""
	  :cityService.getCityGrade()) + "\\n" + scheduleheading.toString(); final
	  String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
	  + "                                             "; final JasperPrint jasper =
	  reportHelper.generateCashFlowReportJasperPrint(cashFlowStatement, heading,
	  getPreviousYearToDate(), getCurrentYearToDate(), subtitle, false);
	  inputStream = reportHelper.exportPdf(inputStream, jasper); return
	  INCOME_EXPENSE_PDF; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateCashFlowScheduleXls") public
	  String generateCashFlowScheduleXls() throws JRException, IOException {
	  populateDataSourceForSchedule(); final String heading =
	  ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? ""
	  :cityService.getCityGrade()) + "\\n" + scheduleheading.toString(); // Blank
	  space for space didvidion between left and right corner final String subtitle
	  = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate()) +
	  "					  						 "; final JasperPrint jasper =
	  reportHelper.generateCashFlowReportJasperPrint(cashFlowStatement, heading,
	  getPreviousYearToDate(), getCurrentYearToDate(), subtitle, false);
	  inputStream = reportHelper.exportXls(inputStream, jasper); return
	  INCOME_EXPENSE_XLS; }
	  
	  public String getCurrentYearToDate() { return
	  cashFlowService.getFormattedDate(cashFlowService.getToDate(cashFlowStatement)
	  ); }
	  
	  public String getPreviousYearToDate() { return
	  cashFlowService.getFormattedDate(cashFlowService.getPreviousYearFor(
	  cashFlowService .getToDate(cashFlowStatement))); }
	  
	  public String getCurrentYearFromDate() { return
	  cashFlowService.getFormattedDate(cashFlowService.getFromDate(
	  cashFlowStatement)); }
	  
	  public String getPreviousYearFromDate() { return
	  cashFlowService.getFormattedDate(cashFlowService.getPreviousYearFor(
	  cashFlowService .getFromDate(cashFlowStatement))); }
	 */
	  
	public Date getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(final Date todayDate) {
		this.todayDate = todayDate;
	}

	public String getMajorCode() {
		return majorCode;
	}

	public void setMajorCode(final String majorCode) {
		this.majorCode = majorCode;
	}

	public String getMinorCode() {
		return minorCode;
	}

	public void setMinorCode(final String minorCode) {
		this.minorCode = minorCode;
	}

	public String getScheduleNo() {
		return scheduleNo;
	}

	public void setScheduleNo(final String scheduleNo) {
		this.scheduleNo = scheduleNo;
	}

	public List<CChartOfAccounts> getListChartOfAccounts() {
		return listChartOfAccounts;
	}

	public void setListChartOfAccounts(final List<CChartOfAccounts> listChartOfAccounts) {
		this.listChartOfAccounts = listChartOfAccounts;
	}

	public String getFinancialYearId() {
		return financialYearId;
	}

	public void setFinancialYearId(final String financialYearId) {
		this.financialYearId = financialYearId;
	}

	public Integer getFundId() {
		return fundId;
	}

	public void setFundId(final Integer fundId) {
		this.fundId = fundId;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(final String period) {
		this.period = period;
	}

	public String getAsOnDateRange() {
		return asOnDateRange;
	}

	public void setAsOnDateRange(final String asOnDateRange) {
		this.asOnDateRange = asOnDateRange;
	}

	public StringBuffer getScheduleheading() {
		return scheduleheading;
	}

	public void setScheduleheading(final StringBuffer scheduleheading) {
		this.scheduleheading = scheduleheading;
	}

	public StringBuffer getStatementheading() {
		return statementheading;
	}

	public void setStatementheading(final StringBuffer statementheading) {
		this.statementheading = statementheading;
	}

	public boolean isDetailReport() {
		return detailReport;
	}

	public void setDetailReport(final boolean detailReport) {
		this.detailReport = detailReport;
	}

	// added by deepak for Actioncontroller
	/*
	 * @Autowired private ChartOfAccountDetailService chartOfAccountDetailService;
	 * 
	 * @Autowired private CChartOfAccountsRepository chartOfAccountsRepository;
	 * 
	 * @Autowired private CGeneralLedgerRepository cGeneralLedgerRepository;
	 * 
	 * //1
	 * 
	 * @GetMapping(value = "/generalLedgerDataBetweenGLCodes")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerDataBetweenGLCodes(final
	 * Model model) { model.addAttribute("taxationCurrentYear",
	 * chartOfAccountDetailService.findGeneralLedgerDataBetweenGLCodes());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerDataBetweenGLCodes();
	 * 
	 * }
	 * 
	 * //2
	 * 
	 * @GetMapping(value = "/generalLedgerDataBetweenGLCode")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerDataBetweenGLCode(final Model
	 * model) { model.addAttribute("salesofgoodsandservicesCurrentYear",
	 * chartOfAccountDetailService.findGeneralLedgerDataBetweenGLCode());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerDataBetweenGLCode(); }
	 * 
	 * // 3
	 * 
	 * @GetMapping(value = "/generalLedgerDataBetweenGLCod")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerDataBetweenGLCod(final Model
	 * model) {
	 * model.addAttribute("grantsrelatedtoRevenue/generalgrantscurrentyear",
	 * chartOfAccountDetailService.findGeneralLedgerDataBetweenGLCode()); return
	 * chartOfAccountDetailService.findGeneralLedgerDataBetweenGLCod(); }
	 * 
	 * //4
	 * 
	 * @GetMapping(value = "/generalLedgerDataBetweenGL")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerDataBetweenGL(final Model
	 * model) { model.addAttribute("InterestReceivedcurrentyear",
	 * chartOfAccountDetailService.findGeneralLedgerDataBetweenGLCode());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerDataBetweenGL(); }
	 * 
	 * // 5
	 * 
	 * @GetMapping(value = "/generalLedgerDataBetween")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerDataBetween(final Model
	 * model) { model.addAttribute("otherreceiptcurrentyear",
	 * chartOfAccountDetailService.findGeneralLedgerDataBetween());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerDataBetween(); }
	 * 
	 * // 6
	 * 
	 * @GetMapping(value = "/generalLedgerData")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerData(final Model model) {
	 * model.addAttribute("employeecostcurrentyear",
	 * chartOfAccountDetailService.findGeneralLedgerDataBetweenGLCode());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerData(); }
	 * 
	 * // 7
	 * 
	 * @GetMapping(value = "/generalLedger")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedger(final Model model) {
	 * model.addAttribute("suppliersCurrentYear",
	 * chartOfAccountDetailService.findGeneralLedger());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedger(); }
	 * 
	 * // 8
	 * 
	 * @GetMapping(value = "/generalLedger1")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerD(final Model model) {
	 * model.addAttribute("interestPaidCurrentyear",
	 * chartOfAccountDetailService.findGeneralLedgerD());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerD(); }
	 * 
	 * // 9
	 * 
	 * @GetMapping(value = "/generalLedger2")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerDa(final Model model) {
	 * model.addAttribute("otherpaymenturrentyear",
	 * chartOfAccountDetailService.findGeneralLedgerDa());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerDa(); }
	 * 
	 * // 11
	 * 
	 * @GetMapping(value = "/generalLedger3")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerData1(final Model model) {
	 * model.addAttribute("purchaseofassetscurrentyear",
	 * chartOfAccountDetailService.findGeneralLedgerData1());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerData1(); }
	 * 
	 * // 12
	 * 
	 * @GetMapping(value = "/generalLedger4")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerData2(final Model model) {
	 * model.addAttribute("Specialfundscurrentyear",
	 * chartOfAccountDetailService.findGeneralLedgerData2());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerData2(); }
	 * 
	 * // 14
	 * 
	 * @GetMapping(value = "/generalLedger5")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerData3(final Model model) {
	 * model.addAttribute("PurchaseofInvestmentscurrentyear",
	 * chartOfAccountDetailService.findGeneralLedgerData3());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerData3(); }
	 * 
	 * // 20
	 * 
	 * @GetMapping(value = "/generalLedger20")
	 * 
	 * @ResponseBody public BigDecimal getGeneralLedgerDat(final Model model) {
	 * model.addAttribute("financeExpenseCurrentYear",
	 * chartOfAccountDetailService.findGeneralLedgerDat());
	 * 
	 * return chartOfAccountDetailService.findGeneralLedgerDat(); }
	 * 
	 * 
	 */
}