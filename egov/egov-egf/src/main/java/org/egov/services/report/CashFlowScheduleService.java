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
package org.egov.services.report;

import org.apache.log4j.Logger;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.Fund;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.egf.model.Statement;
import org.egov.egf.model.StatementEntry;
import org.egov.egf.utils.FinancialUtils;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infstr.services.PersistenceService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CashFlowScheduleService extends ScheduleService {
	private static final String BS = "BS";
	private static final String L = "L";
	private CashFlowService cashFlowService;
	private String removeEntrysWithZeroAmount = "";
	private static final Logger LOGGER = Logger.getLogger(CashFlowScheduleService.class);

	@Autowired
	private FinancialUtils financialUtils;

	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	@Autowired
	private FinancialYearHibernateDAO financialYearDAO;

	public void setcashFlowService(final CashFlowService cashFlowService) {
		this.cashFlowService = cashFlowService;
	}

	/*
	 * public void populateDataForSchedule(final Statement cashFlow, final String
	 * majorCode) { getAppConfigValueForRemoveEntrysWithZeroAmount();
	 * voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");
	 * minorCodeLength =
	 * Integer.valueOf(cashFlowService.getAppConfigValueFor(Constants.EGF,
	 * "coa_minorcode_length")); majorCodeLength =
	 * Integer.valueOf(cashFlowService.getAppConfigValueFor(Constants.EGF,
	 * "coa_majorcode_length")); final Date fromDate =
	 * cashFlowService.getFromDate(cashFlow); final Date toDate =
	 * cashFlowService.getToDate(cashFlow); final CChartOfAccounts coa =
	 * (CChartOfAccounts) find("from CChartOfAccounts where glcode=?", majorCode);
	 * final List<Fund> fundList = cashFlow.getFunds(); Map<String, Object> params =
	 * new HashMap<>(); populateCurrentYearAmountForSchedule(cashFlow, fundList,
	 * cashFlowService.getFilterQuery(cashFlow, params), toDate, fromDate,
	 * majorCode, coa.getType(), params); params = new HashMap<>();
	 * addCurrentOpeningBalancePerFund(cashFlow, fundList,
	 * cashFlowService.getTransactionQuery(cashFlow, params), params); params = new
	 * HashMap<>(); populatePreviousYearTotalsForSchedule(cashFlow,
	 * cashFlowService.getFilterQuery(cashFlow, params), toDate, fromDate,
	 * majorCode, coa.getType(), params); params = new HashMap<>();
	 * addOpeningBalanceForPreviousYear(cashFlow,
	 * cashFlowService.getTransactionQuery(cashFlow, params), fromDate, params);
	 * params = new HashMap<>(); cashFlowService.addExcessIEForCurrentYear(cashFlow,
	 * fundList, getGlcodeForPurposeCode7(),
	 * cashFlowService.getFilterQuery(cashFlow, params), toDate, fromDate, params);
	 * params = new HashMap<>();
	 * cashFlowService.addExcessIEForPreviousYear(cashFlow, fundList,
	 * getGlcodeForPurposeCode7(), cashFlowService.getFilterQuery(cashFlow, params),
	 * toDate, fromDate, params); cashFlowService.removeFundsWithNoData(cashFlow);
	 * cashFlowService.computeCurrentYearTotals(cashFlow, Constants.LIABILITIES,
	 * Constants.ASSETS); computeAndAddTotals(cashFlow); if
	 * (removeEntrysWithZeroAmount.equalsIgnoreCase("Yes"))
	 * cashFlowService.removeScheduleEntrysWithZeroAmount(cashFlow); }
	 */
	/*
	 * public void addCurrentOpeningBalancePerFund(final Statement cashFlow, final
	 * List<Fund> fundList, final String transactionQuery, Map<String, Object>
	 * params) { final BigDecimal divisor = cashFlow.getDivisor(); if
	 * (LOGGER.isDebugEnabled()) LOGGER.debug("addCurrentOpeningBalancePerFund");
	 * final Query query = persistenceService.getSession() .createSQLQuery(new
	 * StringBuilder("select sum(openingdebitbalance)- sum(openingcreditbalance),")
	 * .append("ts.fundid,coa.glcode,coa.type FROM transactionsummary ts,chartofaccounts coa "
	 * )
	 * .append(" WHERE ts.glcodeid = coa.ID  AND ts.financialyearid=:financialyearid"
	 * )
	 * .append(transactionQuery).append(" GROUP BY ts.fundid,coa.glcode,coa.type").
	 * toString()); params.entrySet().forEach(entry ->
	 * query.setParameter(entry.getKey(), entry.getValue())); final List<Object[]>
	 * openingBalanceAmountList = query .setParameter("financialyearid",
	 * cashFlow.getFinancialYear().getId()).list(); for (final Object[] obj :
	 * openingBalanceAmountList) if (obj[0] != null && obj[1] != null) { BigDecimal
	 * total = (BigDecimal)obj[0]; if (L.equals(obj[3].toString())) total =
	 * total.multiply(NEGATIVE); for (final StatementEntry entry :
	 * cashFlow.getEntries()) if (obj[2].toString().equals(entry.getGlCode())) { if
	 * (LOGGER.isDebugEnabled()) LOGGER.debug(entry.getGlCode() +
	 * "==================" + total); if (entry.getFundWiseAmount().isEmpty())
	 * entry.getFundWiseAmount().put( cashFlowService.getFundNameForId(fundList, new
	 * Long(obj[1].toString())), cashFlowService.divideAndRound(total, divisor));
	 * else { boolean shouldAddNewFund = true; for (final Entry<String, BigDecimal>
	 * object : entry.getFundWiseAmount().entrySet()) if
	 * (object.getKey().equalsIgnoreCase( cashFlowService.getFundNameForId(fundList,
	 * new Long(obj[1].toString())))) {
	 * entry.getFundWiseAmount().put(object.getKey(),
	 * object.getValue().add(cashFlowService.divideAndRound(total, divisor)));
	 * shouldAddNewFund = false; } if (shouldAddNewFund)
	 * entry.getFundWiseAmount().put( cashFlowService.getFundNameForId(fundList, new
	 * Long(obj[1].toString())), cashFlowService.divideAndRound(total, divisor)); }
	 * } } }
	 */
	/*
	 * public void addOpeningBalanceForPreviousYear(final Statement cashFlow, final
	 * String transactionQuery, final Date fromDate, Map<String, Object> params) {
	 * if (LOGGER.isDebugEnabled())
	 * LOGGER.debug("addOpeningBalanceForPreviousYear"); final BigDecimal divisor =
	 * cashFlow.getDivisor(); final CFinancialYear prevFinanciaYr =
	 * financialYearDAO.getPreviousFinancialYearByDate(fromDate); final String
	 * prevFinancialYrId = prevFinanciaYr.getId().toString(); final Query query =
	 * persistenceService.getSession().createSQLQuery( new
	 * StringBuilder("select sum(openingdebitbalance)- sum(openingcreditbalance),coa.glcode,coa.type"
	 * )
	 * .append(" FROM transactionsummary ts,chartofaccounts coa  WHERE ts.glcodeid = coa.ID "
	 * )
	 * .append(" AND ts.financialyearid=:financialyearid").append(transactionQuery)
	 * .append(" GROUP BY coa.glcode,coa.type").toString());
	 * params.entrySet().forEach(entry -> query.setParameter(entry.getKey(),
	 * entry.getValue())); final List<Object[]> openingBalanceAmountList = query
	 * .setParameter("financialyearid", Integer.valueOf(prevFinancialYrId)).list();
	 * for (final Object[] obj : openingBalanceAmountList) if (obj[0] != null &&
	 * obj[1] != null) {
	 * 
	 * BigDecimal total = (BigDecimal)obj[0];
	 * 
	 * if (L.equals(obj[2].toString())) total = total.multiply(NEGATIVE); for (final
	 * StatementEntry entry : cashFlow.getEntries()) if
	 * (obj[1].toString().equals(entry.getGlCode())) { if (LOGGER.isDebugEnabled())
	 * LOGGER.debug(entry.getGlCode() + "==================" + total); BigDecimal
	 * prevYrTotal = entry.getPreviousYearTotal(); prevYrTotal = prevYrTotal == null
	 * ? BigDecimal.ZERO : prevYrTotal;
	 * entry.setPreviousYearTotal(prevYrTotal.add(cashFlowService.divideAndRound(
	 * total, divisor))); } } }
	 */
	/*
	 * private String getGlcodeForPurposeCode7() { final Query query =
	 * persistenceService.getSession().createSQLQuery(
	 * "select glcode from chartofaccounts where purposeid=7"); final List list =
	 * query.list(); String glCode = ""; if (list.get(0) != null) glCode =
	 * list.get(0).toString(); return glCode; }
	 * 
	 * private String getGlcodeForPurposeCode7MinorCode() { final Query query =
	 * persistenceService.getSession() .createSQLQuery(new
	 * StringBuilder(String.format("select substr(glcode,1,%d)", minorCodeLength))
	 * .append(" from chartofaccounts where purposeid=7").toString()); final List
	 * list = query.list(); String glCode = ""; if (list.get(0) != null) glCode =
	 * list.get(0).toString(); return glCode; }
	 * 
	 * For Detailed private String getGlcodeForPurposeCode7DetailedCode() { final
	 * Query query = persistenceService.getSession() .createSQLQuery(new
	 * StringBuilder(String.format("select substr(glcode,1,%d)", detailCodeLength))
	 * .append(" from chartofaccounts where purposeid=7").toString()); final List
	 * list = query.list(); String glCode = ""; if (list.get(0) != null) glCode =
	 * list.get(0).toString(); return glCode; }
	 * 
	 * private void populatePreviousYearTotalsForSchedule(final Statement cashFlow,
	 * final String filterQuery, final Date toDate, final Date fromDate, final
	 * String majorCode, final Character type, Map<String, Object> filterParams) {
	 * Date formattedToDate = null; if
	 * ("Yearly".equalsIgnoreCase(cashFlow.getPeriod())) { final Calendar cal =
	 * Calendar.getInstance(); cal.setTime(fromDate); cal.add(Calendar.DATE, -1);
	 * formattedToDate = cal.getTime(); } else formattedToDate =
	 * cashFlowService.getPreviousYearFor(toDate); final StringBuilder qry = new
	 * StringBuilder(); final Map<String, Object> params = new HashMap<>();
	 * qry.append("select sum(debitamount)-sum(creditamount),c.glcode")
	 * .append(" from generalledger g,chartofaccounts c,voucherheader v   "); if
	 * (cashFlow.getDepartment() != null && cashFlow.getDepartment().getCode() !=
	 * null) qry.append(", VoucherMis mis "); qry.
	 * append(" where  v.id=g.voucherheaderid and c.id=g.glcodeid and v.status not in (:voucherStatusToExclude) "
	 * ); params.put("voucherStatusToExclude",
	 * financialUtils.getStatuses(voucherStatusToExclude)); if
	 * (cashFlow.getDepartment() != null && cashFlow.getDepartment().getCode() !=
	 * null) qry.append(" and v.id= mis.voucherheaderid "); qry.
	 * append(" AND v.voucherdate <= :formattedToDate and v.voucherdate >=:formattedFromDate"
	 * )
	 * .append(" and c.glcode in (select distinct coad.glcode from chartofaccounts coa2, schedulemapping s "
	 * )
	 * .append(",chartofaccounts coad where s.id=coa2.scheduleid and coa2.classification=2 and s.reporttype = 'BS'"
	 * )
	 * .append(" and coa2.glcode=SUBSTR(coad.glcode,1,:minorCodeLength) and coad.classification=4"
	 * )
	 * .append(" and coad.majorcode=:majorCode) and c.majorcode=:majorCode and c.classification=4 "
	 * ) .append(filterQuery).append(" group by c.glcode");
	 * 
	 * params.put("formattedToDate", formattedToDate);
	 * params.put("formattedFromDate",
	 * cashFlowService.getPreviousYearFor(fromDate)); params.put("minorCodeLength",
	 * minorCodeLength); params.put("majorCode", majorCode);
	 * params.putAll(filterParams);
	 * 
	 * final Query query =
	 * persistenceService.getSession().createSQLQuery(qry.toString());
	 * persistenceService.populateQueryWithParams(query, params);
	 * 
	 * final List<Object[]> result = query.list(); for (final Object[] row : result)
	 * for (int index = 0; index < cashFlow.size(); index++) if
	 * (cashFlow.get(index).getGlCode() != null &&
	 * row[1].toString().equalsIgnoreCase(cashFlow.get(index).getGlCode())) {
	 * 
	 * BigDecimal previousYearTotal = new BigDecimal(row[0].toString()); if
	 * (LOGGER.isDebugEnabled()) LOGGER.debug(row[1] +
	 * "-----------------------------------" + previousYearTotal); if
	 * (L.equalsIgnoreCase(type.toString())) previousYearTotal =
	 * previousYearTotal.multiply(NEGATIVE); previousYearTotal =
	 * cashFlowService.divideAndRound(previousYearTotal, cashFlow.getDivisor());
	 * cashFlow.get(index).setPreviousYearTotal(previousYearTotal); } }
	 */

	/*
	 * private void populateCurrentYearAmountForSchedule(final Statement cashFlow,
	 * final List<Fund> fundList, final String filterQuery, final Date toDate, final
	 * Date fromDate, final String majorCode, final Character type, Map<String,
	 * Object> params) { final BigDecimal divisor = cashFlow.getDivisor(); final
	 * List<Object[]> allGlCodes = getAllDetailGlCodesForSubSchedule(majorCode,
	 * type, BS); // addRowForSchedule(cashFlow, allGlCodes); final List<Object[]>
	 * resultMap = currentYearAmountQuery(filterQuery, toDate, fromDate, majorCode,
	 * BS, params); for (final Object[] obj : allGlCodes) if (!contains(resultMap,
	 * obj[0].toString())) cashFlow.add(new StatementEntry(obj[0].toString(),
	 * obj[1].toString(), "", BigDecimal.ZERO, BigDecimal.ZERO, false)); else {
	 * final List<Object[]> rowsForGlcode = getRowsForGlcode(resultMap,
	 * obj[0].toString()); for (final Object[] row : rowsForGlcode) if
	 * (!cashFlow.containsCashFlowEntry(row[2].toString())) { final StatementEntry
	 * cashFlowEntry = new StatementEntry(); if (row[0] != null && row[1] != null) {
	 * BigDecimal total = (BigDecimal) row[0]; if (LOGGER.isDebugEnabled())
	 * LOGGER.debug(row[0] + "-----" + row[1] + "------------------------------" +
	 * total); if (L.equalsIgnoreCase(type.toString())) total =
	 * total.multiply(NEGATIVE); cashFlowEntry.getFundWiseAmount().put(
	 * cashFlowService.getFundNameForId(fundList, new Long(row[1].toString())),
	 * cashFlowService.divideAndRound(total, divisor)); } if (row[2] != null)
	 * cashFlowEntry.setGlCode(row[2].toString());
	 * cashFlowEntry.setAccountName(obj[1].toString()); cashFlow.add(cashFlowEntry);
	 * } else for (int index = 0; index < cashFlow.size(); index++) { BigDecimal
	 * amount = cashFlowService.divideAndRound((BigDecimal) row[0], divisor); if
	 * (LOGGER.isDebugEnabled()) LOGGER.debug(row[0] + "-----" + row[1] +
	 * "------------------------------" + amount); if
	 * (L.equalsIgnoreCase(type.toString())) amount = amount.multiply(NEGATIVE); if
	 * (cashFlow.get(index).getGlCode() != null &&
	 * row[2].toString().equals(cashFlow.get(index).getGlCode())) { final String
	 * fundNameForId = cashFlowService.getFundNameForId(fundList, new
	 * Long(row[1].toString())); if
	 * (cashFlow.get(index).getFundWiseAmount().get(fundNameForId) == null)
	 * cashFlow.get(index).getFundWiseAmount().put(
	 * cashFlowService.getFundNameForId(fundList, new Long(row[1].toString())),
	 * amount); else cashFlow.get(index).getFundWiseAmount().put(
	 * cashFlowService.getFundNameForId(fundList, new Long(row[1].toString())),
	 * cashFlow.get(index).getFundWiseAmount().get(cashFlowService
	 * .getFundNameForId(fundList, new Long(row[1].toString()))) .add(amount)); } }
	 * } }
	 */
	/*
	 * For detailed public void populateDataForAllSchedulesDetailed(final Statement
	 * cashFlow) { getAppConfigValueForRemoveEntrysWithZeroAmount();
	 * voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");
	 * minorCodeLength =
	 * Integer.valueOf(cashFlowService.getAppConfigValueFor(Constants.EGF,
	 * "coa_minorcode_length")); majorCodeLength =
	 * Integer.valueOf(cashFlowService.getAppConfigValueFor(Constants.EGF,
	 * "coa_majorcode_length")); detailCodeLength = Integer
	 * .valueOf(cashFlowService.getAppConfigValueFor(Constants.EGF,
	 * "coa_detailcode_length")); final Date fromDate =
	 * cashFlowService.getFromDate(cashFlow); final Date toDate =
	 * cashFlowService.getToDate(cashFlow); final List<Fund> fundList =
	 * cashFlow.getFunds(); Map<String, Object> params = new HashMap<>();
	 * populateCurrentYearAmountForAllSchedulesDetailed(cashFlow, fundList,
	 * amountPerFundQueryForAllSchedulesDetailed(
	 * cashFlowService.getFilterQuery(cashFlow, params), toDate, fromDate, BS,
	 * params)); params = new HashMap<>(); addCurrentOpeningBalancePerFund(cashFlow,
	 * fundList, cashFlowService.getTransactionQuery(cashFlow, params), params);
	 * params = new HashMap<>();
	 * populatePreviousYearTotalsForScheduleForAllSchedulesDetailed(cashFlow,
	 * cashFlowService.getFilterQuery(cashFlow, params), toDate, fromDate, params);
	 * params = new HashMap<>(); addOpeningBalanceForPreviousYear(cashFlow,
	 * cashFlowService.getTransactionQuery(cashFlow, params), fromDate, params);
	 * params = new HashMap<>(); cashFlowService.addExcessIEForCurrentYear(cashFlow,
	 * fundList, getGlcodeForPurposeCode7DetailedCode(),
	 * cashFlowService.getFilterQuery(cashFlow, params), toDate, fromDate, params);
	 * params = new HashMap<>();
	 * cashFlowService.addExcessIEForPreviousYear(cashFlow, fundList,
	 * getGlcodeForPurposeCode7DetailedCode(),
	 * cashFlowService.getFilterQuery(cashFlow, params), toDate, fromDate, params);
	 * cashFlowService.removeFundsWithNoData(cashFlow);
	 * cashFlowService.computeCurrentYearTotals(cashFlow, Constants.LIABILITIES,
	 * Constants.ASSETS); computeAndAddTotals(cashFlow);
	 * computeAndAddTotalsForSchedules(cashFlow); if
	 * (removeEntrysWithZeroAmount.equalsIgnoreCase("Yes"))
	 * cashFlowService.removeScheduleEntrysWithZeroAmount(cashFlow); }
	 * 
	 * public void populateDataForAllSchedules(final Statement cashFlow) {
	 * getAppConfigValueForRemoveEntrysWithZeroAmount(); voucherStatusToExclude =
	 * getAppConfigValueFor("EGF", "statusexcludeReport"); minorCodeLength =
	 * Integer.valueOf(cashFlowService.getAppConfigValueFor(Constants.EGF,
	 * "coa_minorcode_length")); majorCodeLength =
	 * Integer.valueOf(cashFlowService.getAppConfigValueFor(Constants.EGF,
	 * "coa_majorcode_length")); detailCodeLength = Integer
	 * .valueOf(cashFlowService.getAppConfigValueFor(Constants.EGF,
	 * "coa_detailcode_length")); final Date fromDate =
	 * cashFlowService.getFromDate(cashFlow); final Date toDate =
	 * cashFlowService.getToDate(cashFlow); final List<Fund> fundList =
	 * cashFlow.getFunds(); Map<String, Object> params = new HashMap<>();
	 * populateCurrentYearAmountForAllSchedules(cashFlow, fundList,
	 * amountPerFundQueryForAllSchedules( cashFlowService.getFilterQuery(cashFlow,
	 * params), toDate, fromDate, BS, params)); params = new HashMap<>();
	 * addCurrentOpeningBalancePerFund(cashFlow, fundList,
	 * cashFlowService.getTransactionQuery(cashFlow, params), params); params = new
	 * HashMap<>(); populatePreviousYearTotalsForScheduleForAllSchedules(cashFlow,
	 * cashFlowService.getFilterQuery(cashFlow, params), toDate, fromDate, params);
	 * params = new HashMap<>(); addOpeningBalanceForPreviousYear(cashFlow,
	 * cashFlowService.getTransactionQuery(cashFlow, params), fromDate, params);
	 * params = new HashMap<>(); cashFlowService.addExcessIEForCurrentYear(cashFlow,
	 * fundList, getGlcodeForPurposeCode7MinorCode(),
	 * cashFlowService.getFilterQuery(cashFlow, params), toDate, fromDate, params);
	 * params = new HashMap<>();
	 * cashFlowService.addExcessIEForPreviousYear(cashFlow, fundList,
	 * getGlcodeForPurposeCode7MinorCode(), cashFlowService.getFilterQuery(cashFlow,
	 * params), toDate, fromDate, params);
	 * cashFlowService.removeFundsWithNoData(cashFlow);
	 * cashFlowService.computeCurrentYearTotals(cashFlow, Constants.LIABILITIES,
	 * Constants.ASSETS); computeAndAddTotals(cashFlow); if
	 * (removeEntrysWithZeroAmount.equalsIgnoreCase("Yes"))
	 * cashFlowService.removeScheduleEntrysWithZeroAmount(cashFlow); }
	 */
	/*
	 * 
	 * 
	 * 
	 * private void populatePreviousYearTotalsForScheduleForAllSchedules(final
	 * Statement cashFlow, final String filterQuery, final Date toDate, final Date
	 * fromDate, Map<String, Object> params) { Date formattedToDate = null; final
	 * BigDecimal divisor = cashFlow.getDivisor(); if
	 * ("Yearly".equalsIgnoreCase(cashFlow.getPeriod())) { final Calendar cal =
	 * Calendar.getInstance(); cal.setTime(fromDate); cal.add(Calendar.DATE, -1);
	 * formattedToDate = cal.getTime(); } else formattedToDate =
	 * cashFlowService.getPreviousYearFor(toDate); final List<Object[]> resultMap =
	 * amountPerFundQueryForAllSchedules(filterQuery, formattedToDate,
	 * cashFlowService.getPreviousYearFor(fromDate), BS, params); final
	 * List<Object[]> allGlCodes = getAllGlCodesForAllSchedule(BS, "('A','L')"); for
	 * (final Object[] obj : allGlCodes) for (final Object[] row : resultMap) {
	 * final String glCode = row[2].toString(); if (glCode.substring(1,
	 * majorCodeLength).equals(obj[0].toString())) { final String type =
	 * obj[3].toString(); if (!cashFlow.containsCashFlowEntry(row[2].toString()))
	 * addRowToStatement(cashFlow, row, glCode); else for (int index = 0; index <
	 * cashFlow.size(); index++) { BigDecimal amount =
	 * cashFlowService.divideAndRound((BigDecimal) row[0], divisor); if
	 * (L.equalsIgnoreCase(type)) amount = amount.multiply(NEGATIVE); if
	 * (cashFlow.get(index).getGlCode() != null &&
	 * row[2].toString().equals(cashFlow.get(index).getGlCode())) { BigDecimal
	 * prevYrTotal = cashFlow.get(index).getPreviousYearTotal(); prevYrTotal =
	 * prevYrTotal == null ? BigDecimal.ZERO : prevYrTotal;
	 * cashFlow.get(index).setPreviousYearTotal(prevYrTotal.add(amount)); } } } } }
	 */

	/*
	 * for detailed private void
	 * populatePreviousYearTotalsForScheduleForAllSchedulesDetailed(final Statement
	 * cashFlow, final String filterQuery, final Date toDate, final Date fromDate,
	 * Map<String, Object> params) { Date formattedToDate = null; final BigDecimal
	 * divisor = cashFlow.getDivisor(); if
	 * ("Yearly".equalsIgnoreCase(cashFlow.getPeriod())) { final Calendar cal =
	 * Calendar.getInstance(); cal.setTime(fromDate); cal.add(Calendar.DATE, -1);
	 * formattedToDate = cal.getTime(); } else formattedToDate =
	 * cashFlowService.getPreviousYearFor(toDate); final List<Object[]> resultMap =
	 * amountPerFundQueryForAllSchedulesDetailed(filterQuery, formattedToDate,
	 * cashFlowService.getPreviousYearFor(fromDate), BS, params); final
	 * List<Object[]> allGlCodes = getAllGlCodesForAllSchedule(BS, "('A','L')"); for
	 * (final Object[] obj : allGlCodes) for (final Object[] row : resultMap) {
	 * final String glCode = row[2].toString(); if (glCode.substring(1,
	 * majorCodeLength).equals(obj[0].toString())) { final String type =
	 * obj[3].toString(); if (!cashFlow.containsCashFlowEntry(row[2].toString()))
	 * addRowToStatement(cashFlow, row, glCode); else for (int index = 0; index <
	 * cashFlow.size(); index++) { BigDecimal amount =
	 * cashFlowService.divideAndRound((BigDecimal) row[0], divisor); if
	 * (L.equalsIgnoreCase(type)) amount = amount.multiply(NEGATIVE); if
	 * (cashFlow.get(index).getGlCode() != null &&
	 * row[2].toString().equals(cashFlow.get(index).getGlCode())) { BigDecimal
	 * prevYrTotal = cashFlow.get(index).getPreviousYearTotal(); prevYrTotal =
	 * prevYrTotal == null ? BigDecimal.ZERO : prevYrTotal;
	 * cashFlow.get(index).setPreviousYearTotal(prevYrTotal.add(amount)); } } } } }
	 */
	/*
	 * private void populateCurrentYearAmountForAllSchedules(final Statement
	 * cashFlow, final List<Fund> fundList, final List<Object[]> currentYearAmounts)
	 * { final BigDecimal divisor = cashFlow.getDivisor(); final Map<String,
	 * Schedules> scheduleToGlCodeMap = getScheduleToGlCodeMap(BS, "'A','L'"); for
	 * (final Entry<String, Schedules> entry : scheduleToGlCodeMap.entrySet()) {
	 * final String scheduleNumber = entry.getValue().scheduleNumber; final String
	 * scheduleName = entry.getValue().scheduleName; final String type =
	 * entry.getValue().chartOfAccount.size() > 0 ?
	 * entry.getValue().chartOfAccount.iterator().next().type : ""; cashFlow.add(new
	 * StatementEntry(scheduleNumber, scheduleName, "", null, null, true)); for
	 * (final Object[] row : currentYearAmounts) { final String glCode =
	 * row[2].toString(); if (entry.getValue().contains(glCode)) if
	 * (!cashFlow.containsCashFlowEntry(glCode)) { final StatementEntry
	 * cashFlowEntry = new StatementEntry(); if (row[0] != null && row[1] != null) {
	 * BigDecimal total = (BigDecimal) row[0]; if (L.equalsIgnoreCase(type)) total =
	 * total.multiply(NEGATIVE); cashFlowEntry.getFundWiseAmount().put(
	 * cashFlowService.getFundNameForId(fundList, new Long(row[1].toString())),
	 * cashFlowService.divideAndRound(total, divisor)); }
	 * cashFlowEntry.setGlCode(glCode);
	 * cashFlowEntry.setAccountName(entry.getValue().getCoaName(glCode));
	 * cashFlow.add(cashFlowEntry); } else for (int index = 0; index <
	 * cashFlow.size(); index++) { BigDecimal amount =
	 * cashFlowService.divideAndRound((BigDecimal) row[0], divisor); if
	 * (L.equalsIgnoreCase(type)) amount = amount.multiply(NEGATIVE); if
	 * (cashFlow.get(index).getGlCode() != null &&
	 * row[2].toString().equals(cashFlow.get(index).getGlCode())) { final String
	 * fundNameForId = cashFlowService.getFundNameForId(fundList, new
	 * Long(row[1].toString())); if
	 * (cashFlow.get(index).getFundWiseAmount().get(fundNameForId) == null)
	 * cashFlow.get(index).getFundWiseAmount().put(fundNameForId, amount); else
	 * cashFlow.get(index).getFundWiseAmount().put(fundNameForId,
	 * cashFlow.get(index).getFundWiseAmount().get(fundNameForId).add(amount)); } }
	 * } for (final ChartOfAccount s : entry.getValue().chartOfAccount) if
	 * (!cashFlow.containsCashFlowEntry(s.glCode)) { final StatementEntry
	 * cashFlowEntry = new StatementEntry(); cashFlowEntry.setGlCode(s.glCode);
	 * cashFlowEntry.setAccountName(s.name); cashFlow.add(cashFlowEntry); } } }
	 */

	/* for detailed */
	/*
	 * private void populateCurrentYearAmountForAllSchedulesDetailed(final Statement
	 * cashFlow, final List<Fund> fundList, final List<Object[]> currentYearAmounts)
	 * { final BigDecimal divisor = cashFlow.getDivisor(); final Map<String,
	 * Schedules> scheduleToGlCodeMap = getScheduleToGlCodeMapDetailed(BS,
	 * "'A','L'"); for (final Entry<String, Schedules> entry :
	 * scheduleToGlCodeMap.entrySet()) { final String scheduleNumber =
	 * entry.getValue().scheduleNumber; final String scheduleName =
	 * entry.getValue().scheduleName; final String type =
	 * entry.getValue().chartOfAccount.size() > 0 ?
	 * entry.getValue().chartOfAccount.iterator().next().type : ""; cashFlow.add(new
	 * StatementEntry(scheduleNumber, scheduleName, "", null, null, true)); for
	 * (final Object[] row : currentYearAmounts) { final String glCode =
	 * row[2].toString(); if (entry.getValue().contains(glCode)) if
	 * (!cashFlow.containsCashFlowEntry(glCode)) { final StatementEntry
	 * cashFlowEntry = new StatementEntry(); if (row[0] != null && row[1] != null) {
	 * BigDecimal total = (BigDecimal) row[0]; if (L.equalsIgnoreCase(type)) total =
	 * total.multiply(NEGATIVE); cashFlowEntry.getFundWiseAmount().put(
	 * cashFlowService.getFundNameForId(fundList, new Long(row[1].toString())),
	 * cashFlowService.divideAndRound(total, divisor)); }
	 * cashFlowEntry.setGlCode(glCode);
	 * cashFlowEntry.setAccountName(entry.getValue().getCoaName(glCode));
	 * cashFlow.add(cashFlowEntry); } else for (int index = 0; index <
	 * cashFlow.size(); index++) { BigDecimal amount =
	 * cashFlowService.divideAndRound((BigDecimal) row[0], divisor); if
	 * (L.equalsIgnoreCase(type)) amount = amount.multiply(NEGATIVE); if
	 * (cashFlow.get(index).getGlCode() != null &&
	 * row[2].toString().equals(cashFlow.get(index).getGlCode())) { final String
	 * fundNameForId = cashFlowService.getFundNameForId(fundList, new
	 * Long(row[1].toString())); if
	 * (cashFlow.get(index).getFundWiseAmount().get(fundNameForId) == null)
	 * cashFlow.get(index).getFundWiseAmount().put(fundNameForId, amount); else
	 * cashFlow.get(index).getFundWiseAmount().put(fundNameForId,
	 * cashFlow.get(index).getFundWiseAmount().get(fundNameForId).add(amount)); } }
	 * } for (final ChartOfAccount s : entry.getValue().chartOfAccount) if
	 * (!cashFlow.containsCashFlowEntry(s.glCode)) { final StatementEntry
	 * cashFlowEntry = new StatementEntry(); cashFlowEntry.setGlCode(s.glCode);
	 * cashFlowEntry.setAccountName(s.name); cashFlow.add(cashFlowEntry); }
	 * cashFlow.add(new StatementEntry("", "Schedule Total", "", null, null, true));
	 * } }
	 */

	private void getAppConfigValueForRemoveEntrysWithZeroAmount() {
		try {
			final List<AppConfigValues> configValues = appConfigValuesService.getConfigValuesByModuleAndKey(
					FinancialConstants.MODULE_NAME_APPCONFIG,
					FinancialConstants.REMOVE_ENTRIES_WITH_ZERO_AMOUNT_IN_REPORT);

			for (final AppConfigValues appConfigVal : configValues)
				removeEntrysWithZeroAmount = appConfigVal.getValue();
		} catch (final ApplicationRuntimeException e) {
			throw new ApplicationRuntimeException(
					"Appconfig value for remove entries with zero amount in report is not defined in the system");
		}
	}

	public String getRemoveEntrysWithZeroAmount() {
		return removeEntrysWithZeroAmount;
	}

	public void setRemoveEntrysWithZeroAmount(final String removeEntrysWithZeroAmount) {
		this.removeEntrysWithZeroAmount = removeEntrysWithZeroAmount;
	}

	public void populateDetailcode(Statement cashFlowStatement) {
		// TODO Auto-generated method stub

	}

	public void populateDataForLedgerSchedule(Statement cashFlowStatement, String string) {
		// TODO Auto-generated method stub

	}

}