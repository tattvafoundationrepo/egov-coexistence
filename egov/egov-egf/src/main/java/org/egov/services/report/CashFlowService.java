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


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CFinancialYear;
import org.egov.commons.Fund;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.commons.repository.CChartOfAccountsRepository;
import org.egov.egf.model.Statement;
import org.egov.egf.model.StatementEntry;
import org.egov.egf.model.StatementResultObject;
import org.egov.egf.utils.FinancialUtils;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infstr.services.PersistenceService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CashFlowService extends PersistenceService<CChartOfAccountDetail, Long> {
    public CashFlowService(Class<CChartOfAccountDetail> type) {
		super(type);
	}
    
    public CashFlowService() {
    	super(CChartOfAccountDetail.class);
    }

	private static final String BS = "BS";
    private static final String L = "L";
    private static final BigDecimal NEGATIVE = new BigDecimal(-1);
    private String removeEntrysWithZeroAmount = "";
   
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	
	@Autowired
	private  FinancialYearHibernateDAO financialYearDAO;
	
	@Autowired
	private FinancialUtils financialUtils;

	
	@Autowired
	private CChartOfAccountsRepository chartOfAccountsRepository;
	
	/*
    @Override
    protected void addRowsToStatement(final Statement cashFlow, final Statement assets, final Statement liabilities) {
        if (liabilities.size() > 0) {
        	cashFlow.add(new StatementEntry(null, Constants.LIABILITIES, "", null, null, true));
        	cashFlow.addAll(liabilities);
        	cashFlow.add(new StatementEntry(null, Constants.TOTAL_LIABILITIES, "", null, null, true));
        }
        if (assets.size() > 0) {
        	cashFlow.add(new StatementEntry(null, Constants.ASSETS, "", null, null, true));
        	cashFlow.addAll(assets);
        	cashFlow.add(new StatementEntry(null, Constants.TOTAL_ASSETS, "", null, null, true));
        }
    }

    public void addCurrentOpeningBalancePerFund(final Statement cashFlow, final List<Fund> fundList,
            final String transactionQuery, Map<String, Object> params) {
        final BigDecimal divisor = cashFlow.getDivisor();
		final Query query = persistenceService.getSession().createSQLQuery(
				new StringBuilder("select sum(openingdebitbalance)- sum(openingcreditbalance),ts.fundid,coa.majorcode,")
						.append("coa.type FROM transactionsummary ts,chartofaccounts coa  WHERE ts.glcodeid = coa.ID ")
						.append(" AND ts.financialyearid = :financialyearid").append(transactionQuery)
						.append(" GROUP BY ts.fundid,coa.majorcode,coa.type").toString());
		persistenceService.populateQueryWithParams(query, params);
		final List<Object[]> openingBalanceAmountList = query
				.setParameter("financialyearid", cashFlow.getFinancialYear().getId()).list();
        for (final Object[] obj : openingBalanceAmountList)
            if (obj[0] != null && obj[1] != null) {
                BigDecimal total = (BigDecimal)obj[0];
                if (L.equals(obj[3].toString()))
                    total = total.multiply(NEGATIVE);
                for (final StatementEntry entry : cashFlow.getEntries())
                    if (obj[2].toString().equals(entry.getGlCode()))
                        if (entry.getFundWiseAmount().isEmpty())
                            entry.getFundWiseAmount().put(getFundNameForId(fundList, new Long(obj[1].toString())),
                                    divideAndRound(total, divisor));
                        else {
                            boolean shouldAddNewFund = true;
                            for (final Entry<String, BigDecimal> object : entry.getFundWiseAmount().entrySet())
                                if (object.getKey().equalsIgnoreCase(getFundNameForId(fundList, new Long(obj[1].toString())))) {
                                    entry.getFundWiseAmount().put(object.getKey(),
                                            object.getValue().add(divideAndRound(total, divisor)));
                                    shouldAddNewFund = false;
                                }
                            if (shouldAddNewFund)
                                entry.getFundWiseAmount().put(getFundNameForId(fundList, new Long(obj[1].toString())),
                                        divideAndRound(total, divisor));
                        }
            }
    }*/

    /*public void addOpeningBalancePrevYear(final Statement cashFlow, final String transactionQuery, final Date fromDate, Map<String, Object> params) {
        try {
            final BigDecimal divisor = cashFlow.getDivisor();
           final CFinancialYear prevFinancialYr = financialYearDAO.getPreviousFinancialYearByDate(fromDate);
            final String prevFinancialYearId = prevFinancialYr.getId().toString();
			final Query query = persistenceService.getSession()
					.createSQLQuery(new StringBuilder("select sum(openingdebitbalance)- sum(openingcreditbalance),")
							.append("coa.majorcode,coa.type FROM transactionsummary ts,chartofaccounts coa ")
							.append(" WHERE ts.glcodeid = coa.ID  AND ts.financialyearid=:financialyearid")
							.append(transactionQuery).append(" GROUP BY coa.majorcode,coa.type").toString());
			persistenceService.populateQueryWithParams(query, params);
			final List<Object[]> openingBalanceAmountList = query
					.setParameter("financialyearid", Integer.valueOf(prevFinancialYearId)).list();
            for (final Object[] obj : openingBalanceAmountList)
                if (obj[0] != null && obj[1] != null) {
                  BigDecimal total =(BigDecimal) obj[0];
                    if (L.equals(obj[2].toString()))
                        total = total.multiply(NEGATIVE);
                    for (final StatementEntry entry : cashFlow.getEntries())
                        if (obj[1].toString().equals(entry.getGlCode())) {
                            BigDecimal prevYrTotal = entry.getPreviousYearTotal();
                            prevYrTotal = prevYrTotal == null ? BigDecimal.ZERO : prevYrTotal;
                            entry.setPreviousYearTotal(prevYrTotal.add(divideAndRound(total, divisor)));
                        }
                }
        } catch (final HibernateException exp)
        {

        }
    } */

	/*public void addExcessIEForCurrentYear(final Statement cashFlow, final List<Fund> fundList,
			final String glCodeForExcessIE, final String filterQuery, final Date toDate, final Date fromDate,
			Map<String, Object> params) {
		final BigDecimal divisor = cashFlow.getDivisor();
		String voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");
		final StringBuilder qry = new StringBuilder();
		final Map<String, Object> sqlParams = new HashMap<>();
		// TODO- We are only grouping by fund here. Instead here grouping should happen
		// based on the filter like -department and Function also
		qry.append("select sum(g.creditamount)-sum(g.debitamount),v.fundid from voucherheader v,");
		if (cashFlow.getDepartment() != null && !"null".equals(cashFlow.getDepartment().getCode()))
			qry.append("VoucherMis mis ,");
		qry.append("generalledger g, chartofaccounts coa where  v.ID=g.VOUCHERHEADERID")
				.append(" and v.status not in (:voucherStatusToExclude)")
				.append(" and  v.voucherdate>=:vFromDate and v.voucherdate<=:vToDate");
		if (cashFlow.getDepartment() != null && !"null".equals(cashFlow.getDepartment().getCode())) {
			qry.append(" and v.id= mis.voucherheaderid  and mis.departmentcode= :departmentcode");
			sqlParams.put("departmentcode", cashFlow.getDepartment().getCode());
		}
		qry.append(" and coa.ID=g.glcodeid and coa.type in ('I','E') ").append(filterQuery)
				.append(" group by v.fundid");
		final Query query = persistenceService.getSession().createSQLQuery(qry.toString());
		query.setParameterList("voucherStatusToExclude", financialUtils.getStatuses(voucherStatusToExclude))
				.setParameter("vFromDate", fromDate).setParameter("vToDate", toDate);
		sqlParams.putAll(params);
		persistenceService.populateQueryWithParams(query, sqlParams);

		final List<Object[]> excessieAmountList = query.list();

		for (final StatementEntry entry : cashFlow.getEntries())
			if (entry.getGlCode() != null && glCodeForExcessIE.equals(entry.getGlCode()))
				for (final Object[] obj : excessieAmountList) {

					if (obj[0] != null && obj[1] != null) {
						final String fundNameForId = getFundNameForId(fundList, Long.valueOf(obj[1].toString()));
						if (entry.getFundWiseAmount().containsKey(fundNameForId))
							entry.getFundWiseAmount().put(fundNameForId, entry.getFundWiseAmount().get(fundNameForId)
									.add(divideAndRound((BigDecimal) obj[0], divisor)));
						else
							entry.getFundWiseAmount().put(fundNameForId, divideAndRound((BigDecimal) obj[0], divisor));
					}
				}
	}*/

    /*public void addExcessIEForPreviousYear(final Statement cashFLow, final List<Fund> fundList,
            final String glCodeForExcessIE,
            final String filterQuery, final Date toDate, final Date fromDate, Map<String, Object> params) {
		final BigDecimal divisor = cashFLow.getDivisor();
		BigDecimal sum = BigDecimal.ZERO;
		Date formattedToDate = null;
		String voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");
        if ("Yearly".equalsIgnoreCase(cashFLow.getPeriod()))
        {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(fromDate);
            cal.add(Calendar.DATE, -1);
            formattedToDate = cal.getTime();
        }
        else 
            formattedToDate = getPreviousYearFor(toDate);
        final StringBuilder qry = new StringBuilder();
        qry.append("select sum(g.creditamount)-sum(g.debitamount),v.fundid  from voucherheader v,generalledger g, ");
		if (cashFLow.getDepartment() != null && !"null".equals(cashFLow.getDepartment().getCode()))
			qry.append("  VoucherMis mis ,");
		qry.append(" chartofaccounts coa   where  v.ID=g.VOUCHERHEADERID and v.status not in (:voucherStatusToExclude)")
				.append(" and v.voucherdate>=:vFromDate and v.voucherdate<=:vToDate and coa.ID=g.glcodeid ");
		if (cashFLow.getDepartment() != null && !"null".equals(cashFLow.getDepartment().getCode()))
			qry.append(" and v.id= mis.voucherheaderid");

		qry.append(" and coa.type in ('I','E') ").append(filterQuery).append(" group by v.fundid,g.functionid");
		final Query query = persistenceService.getSession().createSQLQuery(qry.toString());
		query.setParameterList("voucherStatusToExclude", financialUtils.getStatuses(voucherStatusToExclude))
				.setParameter("vFromDate", getPreviousYearFor(fromDate))
				.setParameter("vToDate", formattedToDate);

		persistenceService.populateQueryWithParams(query, params);
		
        final List<Object[]> excessieAmountList = query.list();
        for (final Object[] obj : excessieAmountList)
            sum = sum.add((BigDecimal) obj[0]);
        for (int index = 0; index < cashFLow.size(); index++)
            if (cashFLow.get(index).getGlCode() != null && glCodeForExcessIE.equals(cashFLow.get(index).getGlCode())) {
                BigDecimal prevYrTotal = cashFLow.get(index).getPreviousYearTotal();
                prevYrTotal = prevYrTotal == null ? BigDecimal.ZERO : prevYrTotal;
                cashFLow.get(index).setPreviousYearTotal(prevYrTotal.add(divideAndRound(sum, divisor)));
            }
    } */

    /*public void populateCashFlow(final Statement cashFlow) {
        try {
            final List<AppConfigValues> configValues = appConfigValuesService.
                    getConfigValuesByModuleAndKey(FinancialConstants.MODULE_NAME_APPCONFIG,
                            FinancialConstants.REMOVE_ENTRIES_WITH_ZERO_AMOUNT_IN_REPORT);

            for (final AppConfigValues appConfigVal : configValues)
                removeEntrysWithZeroAmount = appConfigVal.getValue();
        } catch (final ApplicationRuntimeException e) {
            throw new ApplicationRuntimeException(
                    "Appconfig value for remove entries with zero amount in report is not defined in the system");
        }
        minorCodeLength = Integer.valueOf(getAppConfigValueFor(Constants.EGF, "coa_minorcode_length"));
        coaType.add('A');
        coaType.add('L');
        final Date fromDate = getFromDate(cashFlow);
        final Date toDate = getToDate(cashFlow);
        String   voucherStatusToExclude = getAppConfigValueFor("EGF", "statusexcludeReport");
        final List<Fund> fundList = cashFlow.getFunds();
        Map<String, Object> filterQueryParams = new HashMap<>();
        final String filterQuery = getFilterQuery(cashFlow, filterQueryParams);
        populateCurrentYearAmountPerFund(cashFlow, fundList, filterQuery, toDate, fromDate, BS, filterQueryParams);
        populatePreviousYearTotals(cashFlow, filterQuery, toDate, fromDate, BS, "'L','A'", filterQueryParams);
        Map<String, Object> params = new HashMap<>();
        addCurrentOpeningBalancePerFund(cashFlow, fundList, getTransactionQuery(cashFlow, params), params);
        params = new HashMap<>();
        addOpeningBalancePrevYear(cashFlow, getTransactionQuery(cashFlow, params), fromDate, params);
        final String glCodeForExcessIE = getGlcodeForPurposeCode(7);//purpose is ExcessIE
        addExcessIEForCurrentYear(cashFlow, fundList, glCodeForExcessIE, filterQuery, toDate, fromDate, filterQueryParams);
        addExcessIEForPreviousYear(cashFlow, fundList, glCodeForExcessIE, filterQuery, toDate, fromDate, filterQueryParams);
        computeCurrentYearTotals(cashFlow, Constants.LIABILITIES, Constants.ASSETS);
        populateSchedule(cashFlow, BS);
        removeFundsWithNoData(cashFlow);
        groupBySubSchedule(cashFlow);
        computeTotalAssetsAndLiabilities(cashFlow);
        if (removeEntrysWithZeroAmount.equalsIgnoreCase("Yes"))
            removeEntrysWithZeroAmount(cashFlow);
    }*/

  /*  private void computeTotalAssetsAndLiabilities(final Statement cashFlow) {
        BigDecimal currentYearTotal = BigDecimal.ZERO;
        BigDecimal previousYearTotal = BigDecimal.ZERO;
        for (int index = 0; index < cashFlow.size(); index++) {
            if (Constants.TOTAL.equalsIgnoreCase(cashFlow.get(index).getAccountName())
                    || Constants.LIABILITIES.equals(cashFlow.get(index).getAccountName())
                    || Constants.ASSETS.equals(cashFlow.get(index).getAccountName()))
                continue;
            if (Constants.TOTAL_LIABILITIES.equalsIgnoreCase(cashFlow.get(index).getAccountName())
                    || Constants.TOTAL_ASSETS.equalsIgnoreCase(cashFlow.get(index).getAccountName())) {
            	cashFlow.get(index).setCurrentYearTotal(currentYearTotal);
                currentYearTotal = BigDecimal.ZERO;
                cashFlow.get(index).setPreviousYearTotal(previousYearTotal);
                previousYearTotal = BigDecimal.ZERO;
            } else {
                if (cashFlow.get(index).getCurrentYearTotal() != null)
                    currentYearTotal = currentYearTotal.add(cashFlow.get(index).getCurrentYearTotal());
                if (cashFlow.get(index).getPreviousYearTotal() != null)
                    previousYearTotal = previousYearTotal.add(cashFlow.get(index).getPreviousYearTotal());
            }
        }
    }

    private void groupBySubSchedule(final Statement cashFlow) {
        final List<StatementEntry> list = new LinkedList<StatementEntry>();
        final Map<String, String> schedueNumberToNameMap = getSubSchedule(BS);
        final Set<String> grouped = new HashSet<String>();
        BigDecimal previousTotal = BigDecimal.ZERO;
        BigDecimal currentTotal = BigDecimal.ZERO;
        Map<String, BigDecimal> fundTotals = new HashMap<String, BigDecimal>();
        boolean isLastEntryAHeader = true;
        // this loop assumes entries are ordered by major codes and have implicit grouping
        for (final StatementEntry entry : cashFlow.getEntries()) {
            if (!grouped.contains(schedueNumberToNameMap.get(entry.getScheduleNo()))) {
                // hack to take care of liabilities and asset rows
                if (!isLastEntryAHeader) {
                    final StatementEntry cashFlowEntry = new StatementEntry(null, Constants.TOTAL, "", previousTotal,
                            currentTotal,
                            true);
                    cashFlowEntry.setFundWiseAmount(fundTotals);
                    fundTotals = new HashMap<String, BigDecimal>();
                    list.add(cashFlowEntry);
                }
                // the current schedule number is not grouped yet, we'll start grouping it now.
                // Before starting the group we have to add total row for the last group
                addTotalRowToPreviousGroup(list, schedueNumberToNameMap, entry);
                previousTotal = BigDecimal.ZERO;
                currentTotal = BigDecimal.ZERO;
                // now this is grouped, so add it to to grouped set
                grouped.add(schedueNumberToNameMap.get(entry.getScheduleNo()));
            }
            if (Constants.TOTAL_LIABILITIES.equalsIgnoreCase(entry.getAccountName())) {
                final StatementEntry cashFLowEntry = new StatementEntry(null, Constants.TOTAL, "", previousTotal,
                        currentTotal,
                        true);
                cashFLowEntry.setFundWiseAmount(fundTotals);
                fundTotals = new HashMap<String, BigDecimal>();
                list.add(cashFLowEntry);
            }
            list.add(entry);
            addFundAmount(entry, fundTotals);
            previousTotal = previousTotal.add(zeroOrValue(entry.getPreviousYearTotal()));
            currentTotal = currentTotal.add(zeroOrValue(entry.getCurrentYearTotal()));
            isLastEntryAHeader = entry.getGlCode() == null;
            if (Constants.TOTAL_LIABILITIES.equalsIgnoreCase(entry.getAccountName())) {
                previousTotal = BigDecimal.ZERO;
                currentTotal = BigDecimal.ZERO;
            }
        }
        // add the total row for the last grouping
        final StatementEntry sheetEntry = new StatementEntry(null, Constants.TOTAL, "", previousTotal, currentTotal, true);
        sheetEntry.setFundWiseAmount(fundTotals);
        list.add(list.size() - 1, sheetEntry);
        cashFlow.setEntries(list);
    }

    private void removeEntrysWithZeroAmount(final Statement cashFlow) {
        final List<StatementEntry> list = new LinkedList<StatementEntry>();
        Boolean check;
        Map<String, BigDecimal> FundWiseAmount = new HashMap<String, BigDecimal>();
        for (final StatementEntry entry : cashFlow.getEntries())
            if (entry.getGlCode() != null && !entry.getGlCode().equalsIgnoreCase("")) {
                FundWiseAmount = entry.getFundWiseAmount();
                if (FundWiseAmount != null) {
                    check = false;
                    for (final String keyGroup : FundWiseAmount.keySet())
                        if (!(entry.getPreviousYearTotal() != null
                        && FundWiseAmount.get(keyGroup).compareTo(BigDecimal.ZERO) == 0 && entry.getPreviousYearTotal()
                        .compareTo(BigDecimal.ZERO) == 0)) {
                            check = true;
                            break;
                        }
                    if (check.equals(true))
                        list.add(entry);
                } else
                    list.add(entry);
            } else
                list.add(entry);
        cashFlow.setEntries(new LinkedList<StatementEntry>());
        cashFlow.setEntries(list);
    }

    public void removeScheduleEntrysWithZeroAmount(final Statement cashFLow) {
        final List<StatementEntry> list = new ArrayList<StatementEntry>();
        for (final StatementEntry entry : cashFLow.getEntries())
            if (entry.getGlCode() != null && !entry.getGlCode().equalsIgnoreCase("")) {
                if (!(entry.getCurrentYearTotal() != null && entry.getPreviousYearTotal() != null
                        && entry.getCurrentYearTotal().compareTo(BigDecimal.ZERO) == 0 && entry.getPreviousYearTotal().compareTo(
                                BigDecimal.ZERO) == 0))
                    list.add(entry);
            } else
                list.add(entry);
        cashFLow.setEntries(new LinkedList<StatementEntry>());
        cashFLow.setEntries(list);
    }

    public void populateCurrentYearAmountPerFund(final Statement statement, final List<Fund> fundList, final String filterQuery,
            final Date toDate,
            final Date fromDate, final String scheduleReportType, Map<String, Object> params) {
        final Statement assets = new Statement();
        final Statement liabilities = new Statement();
        final BigDecimal divisor = statement.getDivisor();
        final List<StatementResultObject> allGlCodes = getAllGlCodesFor(scheduleReportType);
        final List<StatementResultObject> results = getTransactionAmount(filterQuery, toDate, fromDate, "'L','A'", "BS", params);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("row.getGlCode()--row.getFundId()--row.getAmount()--row.getBudgetAmount()");
        for (final StatementResultObject queryObject : allGlCodes) {
            if (queryObject.getGlCode() == null)
                queryObject.setGlCode("");
            final List<StatementResultObject> rows = getRowWithGlCode(results, queryObject.getGlCode());

            if (rows.isEmpty()) {
                if (queryObject.isLiability())
                    liabilities.add(new StatementEntry(queryObject.getGlCode(), queryObject.getScheduleName(), queryObject
                            .getScheduleNumber(), BigDecimal.ZERO, BigDecimal.ZERO, false));
                else
                    assets.add(new StatementEntry(queryObject.getGlCode(), queryObject.getScheduleName(), queryObject
                            .getScheduleNumber(), BigDecimal.ZERO, BigDecimal.ZERO, false));
            } else
                for (final StatementResultObject row : rows) {
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug(row.getGlCode() + "--" + row.getFundId() + "--" + row.getAmount() + "--"
                                + row.getBudgetAmount());
                    if (row.isLiability())
                        row.negateAmount();
                    if (liabilities.containsCashFlowEntry(row.getGlCode())
                            || assets.containsCashFlowEntry(row.getGlCode())) {
                        if (row.isLiability())
                            addFundAmount(fundList, liabilities, divisor, row);
                        else
                            addFundAmount(fundList, assets, divisor, row);
                    } else {
                        final StatementEntry cashFlowEntry = new StatementEntry();
                        if (row.getAmount() != null && row.getFundId() != null)
                        	cashFlowEntry.getFundWiseAmount().put(
                                    getFundNameForId(fundList, row.getFundId()),
                                    divideAndRound(row.getAmount(), divisor));
                        if (queryObject.getGlCode() != null) {
                        	cashFlowEntry.setGlCode(queryObject.getGlCode());
                        	cashFlowEntry.setAccountName(queryObject.getScheduleName());
                        	cashFlowEntry.setScheduleNo(queryObject.getScheduleNumber());
                        }

                        if (row.isLiability())
                            liabilities.add(cashFlowEntry);
                        else
                            assets.add(cashFlowEntry);
                    }
                }
        }
        addRowsToStatement(statement, assets, liabilities);
    }

    public void populatePreviousYearTotals(final Statement cashFlow, final String filterQuery, final Date toDate,
            final Date fromDate,
            final String reportSubType, final String coaType, Map<String, Object> params) {
        final boolean newcashFLow = cashFlow.size() > 2 ? false : true;
        final BigDecimal divisor = cashFlow.getDivisor();
        final Statement assets = new Statement();
        final Statement liabilities = new Statement();
        Date formattedToDate;
        final Calendar cal = Calendar.getInstance();

        if ("Yearly".equalsIgnoreCase(cashFlow.getPeriod()))
        {
            cal.setTime(fromDate);
            cal.add(Calendar.DATE, -1);
            formattedToDate = cal.getTime();
        }
        else
            formattedToDate = getPreviousYearFor(toDate);
        final List<StatementResultObject> results = getTransactionAmount(filterQuery, formattedToDate,
                getPreviousYearFor(fromDate),
                coaType, reportSubType, params);
        for (final StatementResultObject row : results)
            if (cashFlow.containsCashFlowEntry(row.getGlCode())) {
                for (int index = 0; index < cashFlow.size(); index++)
                    if (cashFlow.get(index).getGlCode() != null
                    && row.getGlCode().equals(cashFlow.get(index).getGlCode())) {
                        if (row.isLiability())
                            row.negateAmount();
                        BigDecimal prevYrTotal = cashFlow.get(index).getPreviousYearTotal();
                        prevYrTotal = prevYrTotal == null ? BigDecimal.ZERO : prevYrTotal;
                        cashFlow.get(index).setPreviousYearTotal(prevYrTotal.add(divideAndRound(row.getAmount(), divisor)));
                    }
            } else {
                if (row.isLiability())
                    row.negateAmount();
                final StatementEntry cashFlowEntry = new StatementEntry();
                if (row.getAmount() != null && row.getFundId() != null) {
                	cashFlowEntry.setPreviousYearTotal(divideAndRound(row.getAmount(), divisor));
                	cashFlowEntry.setCurrentYearTotal(BigDecimal.ZERO);
                }
                if (row.getGlCode() != null)
                	cashFlowEntry.setGlCode(row.getGlCode());
                if (row.isLiability())
                    liabilities.add(cashFlowEntry);
                else
                    assets.add(cashFlowEntry);
            }
        if (newcashFLow)
            addRowsToStatement(cashFlow, assets, liabilities);
    }
*/
    public String getRemoveEntrysWithZeroAmount() {
        return removeEntrysWithZeroAmount;
    }

    public void setRemoveEntrysWithZeroAmount(final String removeEntrysWithZeroAmount) {
        this.removeEntrysWithZeroAmount = removeEntrysWithZeroAmount;
    }


	public void populateCFStatement(Statement cashFlowStatement) {
		// TODO Auto-generated method stub
		
	}

	public Map<String,String> fetchReportData() {
		System.out.println("=========================================>");
		if(chartOfAccountsRepository==null) System.out.println("HiHiHiHiHiHi");
		
		Map<String, String> reportMap = new HashMap<>();
		
		List<Object[]> generalLedgerData = chartOfAccountsRepository.Glcode("1100102", "1203001");
				
				//chartOfAccountsRepository.findGeneralLedgerDataBetweenGLCodes( );
		
		//1
		BigDecimal totalDebitAmt_1100102_1203001= BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[6]; // Assuming index 6 contains debit amount
			totalDebitAmt_1100102_1203001 = totalDebitAmt_1100102_1203001.add(debitAmount);
			
		}
		reportMap.put("taxationCurrentYear", totalDebitAmt_1100102_1203001.toString());
		//2
		generalLedgerData = chartOfAccountsRepository.Glcode("1301001", "1504103");
		BigDecimal totalDebitAmt_1301001_1504103=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[6]; // Assuming index 6 contains debit amount
			totalDebitAmt_1301001_1504103 = totalDebitAmt_1301001_1504103.add(debitAmount);
			
		}
		reportMap.put("salesofgoodsandservicesCurrentYear",totalDebitAmt_1301001_1504103.toString());
		
		
		//3
		generalLedgerData = chartOfAccountsRepository.Glcode("1601001", "1601099");
		BigDecimal totalDebitAmt_1601001_1601099=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[6];                                                   // Assuming index 6 contains debit amount
			totalDebitAmt_1601001_1601099 = totalDebitAmt_1601001_1601099.add(debitAmount);
			
		}
		reportMap.put("grantgeneralgrantscurrentyear",totalDebitAmt_1601001_1601099.toString());
		
		
		//4
		generalLedgerData = chartOfAccountsRepository.Glcode("1701001", "1701099");
		BigDecimal totalDebitAmt_1701001_1701099=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[6]; // Assuming index 6 contains debit amount
			totalDebitAmt_1701001_1701099 = totalDebitAmt_1701001_1701099.add(debitAmount);
			
		}
		reportMap.put("InterestReceivedcurrentyear",totalDebitAmt_1701001_1701099.toString());
		
		//5
		generalLedgerData = chartOfAccountsRepository.Glcode("1806001", "1901002");
		BigDecimal totalDebitAmt_1806001_1901002=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[6]; // Assuming index 6 contains debit amount
			totalDebitAmt_1806001_1901002 = totalDebitAmt_1806001_1901002.add(debitAmount);
			
		}
		reportMap.put("otherreceiptcurrentyear",totalDebitAmt_1806001_1901002.toString());

		
		
		//6
		generalLedgerData = chartOfAccountsRepository.Glcode("2101001", "2104099");
		BigDecimal totalDebitAmt_2101001_2104099=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[7]; // Assuming index 6 contains debit amount
			totalDebitAmt_2101001_2104099 = totalDebitAmt_2101001_2104099.add(debitAmount);
			
		}
		reportMap.put("employeecostcurrentyear",totalDebitAmt_2101001_2104099.toString());

		
		
	

		//7
		generalLedgerData = chartOfAccountsRepository.Glcode("2303001", "2303099");
		BigDecimal totalDebitAmt_2303001_2303099=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[7];                                    // Assuming index 6 contains debit amount
			totalDebitAmt_2303001_2303099 = totalDebitAmt_2303001_2303099.add(debitAmount);
			
		}
		reportMap.put("suppliersCurrentYear",totalDebitAmt_2303001_2303099.toString());

		
		
		
		//8
		generalLedgerData = chartOfAccountsRepository.Glcode("2406000", "2406099");
		BigDecimal totalDebitAmt_2406000_2406099=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[7];                                    // Assuming index 6 contains debit amount
			totalDebitAmt_2406000_2406099 = totalDebitAmt_2406000_2406099.add(debitAmount);
			
		}
		reportMap.put("interestPaidCurrentyear",totalDebitAmt_2406000_2406099.toString());

		
		
		//2201000' AND '2901001
		//9
		generalLedgerData = chartOfAccountsRepository.Glcode("2201000", "2901001");
		BigDecimal totalDebitAmt_2201000_2901001=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[7];                                    // Assuming index 6 contains debit amount
			totalDebitAmt_2201000_2901001 = totalDebitAmt_2201000_2901001.add(debitAmount);
			
		}
		reportMap.put("otherpaymentcurrentyear",totalDebitAmt_2201000_2901001.toString());

		
		
		
		
		
		//'4101000' AND '  4108003
		
		// BETWEEN '4121001' AND '4123008'"

		//11
		generalLedgerData = chartOfAccountsRepository.Glcode("4101000", "4108099");
		BigDecimal totalDebitAmt_4101000_4108099=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[7];                                    // Assuming index 6 contains debit amount
			totalDebitAmt_4101000_4108099 = totalDebitAmt_4101000_4108099.add(debitAmount);
			
		}
		reportMap.put("purchaseofassetscurrentyear",totalDebitAmt_4101000_4108099.toString());

		
		
		
		//12
		// BETWEEN '4121001' AND '4123008
		generalLedgerData = chartOfAccountsRepository.Glcode("4121001", "4123008");
		BigDecimal totalDebitAmt_4121001_4123008=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[7];                                    // Assuming index 6 contains debit amount
			totalDebitAmt_4121001_4123008 = totalDebitAmt_4121001_4123008.add(debitAmount);
			
		}
		reportMap.put("Specialfundscurrentyear",totalDebitAmt_4121001_4123008.toString());

		
		generalLedgerData = chartOfAccountsRepository.Glcode("4201000", "4208099");
		BigDecimal totalDebitAmt_4201000_4208099=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[7];                                    // Assuming index 6 contains debit amount
			totalDebitAmt_4201000_4208099 = totalDebitAmt_4201000_4208099.add(debitAmount);
			
		}
		reportMap.put("PurchaseofInvestmentscurrentyear",totalDebitAmt_4201000_4208099.toString());

		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//20
		
		
		generalLedgerData = chartOfAccountsRepository.Glcode("2400000", "2408099");
		BigDecimal totalDebitAmt_2400000_2408099=BigDecimal.ZERO;
		for (Object[] row : generalLedgerData) {
			BigDecimal debitAmount = (BigDecimal) row[7];                                    // Assuming index 6 contains debit amount
			totalDebitAmt_2400000_2408099 = totalDebitAmt_2400000_2408099.add(debitAmount);
			
		}
		reportMap.put("currentyearfinance",totalDebitAmt_2400000_2408099.toString());

		
		
		
		
		BigDecimal netCash1=totalDebitAmt_1100102_1203001.add(totalDebitAmt_1301001_1504103).add(totalDebitAmt_1601001_1601099).add(totalDebitAmt_1701001_1701099).add(totalDebitAmt_1806001_1901002).add(totalDebitAmt_2101001_2104099).add(totalDebitAmt_2303001_2303099).add(totalDebitAmt_2406000_2406099).add(totalDebitAmt_2201000_2901001);
		reportMap.put("netCash1", netCash1.toString());
		
		
		
		
		BigDecimal netCash2=totalDebitAmt_4101000_4108099.add(totalDebitAmt_4121001_4123008).add(totalDebitAmt_4201000_4208099);
		reportMap.put("netCash2", netCash2.toString());
		
		
		
		BigDecimal netCash3 = totalDebitAmt_2400000_2408099;
		reportMap.put("netCash3", netCash3.toString());

		
		
		//
		BigDecimal netCashAll = netCash1.add(netCash2).add(netCash3);
		reportMap.put("netCashAll", netCashAll.toString());
		
		
		
		
		
	
		System.out.println("=========================================>");
		System.out.println(totalDebitAmt_1100102_1203001 + "<= total debit amt");
		System.out.println("=========================================>");
		return reportMap;
	}

	
	
}