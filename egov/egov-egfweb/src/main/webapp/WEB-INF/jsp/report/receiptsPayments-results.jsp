<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>


<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<link href="/services/EGF/resources/css/budget.css?rnd=${app_release_no}" rel="stylesheet"
  type="text/css" />
<style type="text/css">
@media print {
  div#ieReport {
    display: none;
  }
}
</style>

<div id="budgetSearchGrid"
  style="width: 1250px; overflow-x: auto; overflow-y: hidden;">
  <br />
  <div style="overflow-x: scroll; overflow-y: scroll;">
    <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
        <td>
          <div align="center">
            <br />
            
            <table border="0" cellspacing="0" cellpadding="0"
              class="tablebottom" width="100%">
              <tr>
                <td colspan="12">
                  <div class="subheadsmallnew">
                    <strong><s:property value="statementheading" /></strong>
                  </div>
                </td>
              </tr>
              <tr>
                <th class="bluebgheadtd">Receipts</th>
                <th class="bluebgheadtd">Amount(CurrentYear)</th>
                <th class="bluebgheadtd">Amount(PreviousYear)</th>
                <th class="bluebgheadtd">Payments</th>
                <th class="bluebgheadtd">Amount(CurrentYear)</th>
                <th class="bluebgheadtd">Amount(PreviousYear)</th>
              </tr>
              
             
             
             
             
             
             
             
             
             
             
             
             
             
             
          
    <tr style="color: #ff9933;">
        <td><b>Opening Balance (Cash/Bank including Imprest Balances and Designated Bank Accounts)</b></td>
        <td><s:property value="%{reportMap.openingbalanceCurrentYear}"/></td>
        <td><s:property value="%{reportMap.openingbalancePreviousYear}"/></td>
        <td><b>Oprating Payments</b></td>
        <td><s:property value="%{reportMap.operatingpaymentsCurrentYear}"/></td>
        <td><s:property value="%{reportMap.operatingpaymentsPreviousYear}"/></td>
    </tr>
    <tr>
        <td>Bank</td>
        <td><s:property value="%{reportMap.bankCurrentYear}"/></td>
        <td><s:property value="%{reportMap.bankPreviousYear}"/></td>
        <td>Establishment Expenses</td>
        <td><s:property value="%{reportMap.establishmentexpensesCurrentYear}"/></td>
        <td><s:property value="%{reportMap.establishmentexpensesPreviousYear}"/></td>
    </tr>
    <tr>
        <td>Cash</td>
        <td><s:property value="%{reportMap.cashCurrentYear}" /></td>
        <td><s:property value="%{reportMap.cashPreviousYear}" /></td>
        <td>Administrative Expenses</td>
        <td><s:property value="%{reportMap.administrativeexpensesCurrentYear}" /></td>
        <td><s:property value="%{reportMap.administrativeexpensesPreviousYear}" /></td>
    </tr>
    <tr>
        <td>Imprest Amount</td>
        <td><s:property value="%{reportMap.imprestcurrentyear}"/></td>
        <td><s:property value="%{reportMap.imprestpreviousyear}"/></td>
        <td>Operations and Maintenance</td>
        <td><s:property value="%{reportMap.operationsandmaintenancecurrentyear}"/></td>
        <td><s:property value="%{reportMap.operationsandmaintenancepreviousyear}"/></td>
    </tr>
    <tr>
        <td><b>Operating Receipts</b></td>
        <td><s:property value="%{reportMap.operatingreceiptscurrentyear}"/></td>
        <td><s:property value="%{reportMap.operatingreceiptspreviousyear}"/></td>
        <td><b>Interest & Finance Charges</b></td>
        <td><s:property value="%{reportMap.interestfinancecurrentyear}"/></td>
        <td><s:property value="%{reportMap.interestfinancepreviousyear}"/></td>
    </tr>
    <tr>
        <td>Tax Revenue</td>
        <td><s:property value="%{reportMap.taxrevenuecurrentyear}" /></td>
        <td><s:property value="%{reportMap.taxrevenuepreviousyear}" /></td>
        <td>Program Expenses</td>
        <td><s:property value="%{reportMap.programexpensescurrentyear}" /></td>
        <td><s:property value="%{reportMap.programexpensespreviousyear}" /></td>
    </tr> 
    <tr>
        <td>Assigned Revenues & Compensations</td>
        <td><s:property value="%{reportMap.assignedrevenuescurrentyear}" /></td>
        <td><s:property value="%{reportMap.assignedrevenuespreviousyear}" /></td>
        <td>Revenue Grants, Contributions and Subsidies</td>
        <td><s:property value="%{reportMap.revenuegrantscurrentyear}" /></td>
        <td><s:property value="%{reportMap.revenuegrantspreviousyear}" /></td>
    </tr>
    <tr>
        <td>Rental income from Municipal properties</td>
        <td><s:property value="%{reportMap.renalincomecurrentyear}" /></td>
        <td><s:property value="%{reportMap.renalincomepreviousyear}" /></td>
        <td>Purchase of Stores</td>
        <td><s:property value="%{reportMap.purchasestorescurrentyear}" /></td>
        <td><s:property value="%{reportMap.purchasestorespreviousyear}" /></td>
    </tr>
    <tr>
        <td>Fees and User Charges</td>
        <td><s:property value="%{reportMap.feeuserCurrentYear}"/></td>
        <td><s:property value="%{reportMap.feeuserPreviousYear}"/></td>
        <td>Other Collections on behalf of State and Central Government</td>
        <td><s:property value="%{reportMap.othercollectionCurrentYear}"/></td>
        <td><s:property value="%{reportMap.othercollectionPreviousYear}"/></td>
    </tr>
    <tr>
        <td>Sale & Hire Charges</td>
        <td><s:property value="%{reportMap.saleandhireCurrentyear}"/></td>
        <td><s:property value="%{reportMap.saleandhirePreviousyear}"/></td>
        <td><b>Non Operating Payments</b></td>
        <td><s:property value="%{reportMap.nonoperatingpaymentsCurrentyear}"/></td>
        <td><s:property value="%{reportMap.nonoperatingpaymentsPreviousyear}"/></td>
    </tr>
    <tr>
        <td>Revenue Grants, Contributions and Subsidies</td>
        <td><s:property value="%{reportMap.revenuegrantsubsidycurrentyear}"/></td>
        <td><s:property value="%{reportMap.revenuegrantsubsidypreviousyear}"/></td>
        <td>Other Payables</td>
        <td><s:property value="%{reportMap.otherpayablescurrentyear}"/></td>
        <td><s:property value="%{reportMap.otherpayablespreviousyear}"/></td>
    </tr>
    <tr>
        <td>Income from Investments</td>
        <td><s:property value="%{reportMap.incomeinvestmentscurrentyear}"/></td>
        <td><s:property value="%{reportMap.incomeinvestmentspreviousyear}"/></td>
        <td>Refunds Payables</td>
        <td><s:property value="%{reportMap.refundpayablescurrentyear}"/></td>
        <td><s:property value="%{reportMap.refundpayablespreviousyear}"/></td>
    </tr>
    <tr>
        <td>Interest earned</td>
        <td><s:property value="%{reportMap.interestearnedcurrentyear}"/></td>
        <td><s:property value="%{reportMap.interestearnedpreviousyear}"/></td>
        <td>Repayment of Loans</td>
        <td><s:property value="%{reportMap.repaymentloanscurrentyear}" /></td>
        <td><s:property value="%{reportMap.repaymentloanspreviousyear}" /></td>
    </tr>
    <tr>
        <td>Other Income</td>
        <td><s:property value="%{reportMap.otherincomescurrentyear}" /></td>
        <td><s:property value="%{reportMap.otherincomespreviousyear}" /></td>  
        <td>Refund of Deposits</td>
        <td><s:property value="%{reportMap.refunddedpositscurrentyear}" /></td>
        <td><s:property value="%{reportMap.refunddedpositspreviousyear}" /></td>
    </tr>  
    <tr>
        <td><b>Non Operating Receipts</b></td>
        <td><s:property value="%{reportMap.nonopreceiptscurrentyear}" /></td>
        <td><s:property value="%{reportMap.nonopreceiptspreviousyear}" /></td>
        <td>Acquisition / Purchase of fixed assets</td>
        <td><s:property value="%{reportMap.acquisitionpurchasecurrentyear}" /></td>
        <td><s:property value="%{reportMap.acquisitionpurchasepreviousyear}" /></td>
    </tr>  
    <tr>
        <td>Loans received</td>
        <td><s:property value="%{reportMap.loansreceivedcurrentyear}" /></td>
        <td><s:property value="%{reportMap.loansreceivedpreviousyear}" /></td>
        <td>Capital work in progress</td>
        <td><s:property value="%{reportMap.capitalworkcurentyear}" /></td>
        <td><s:property value="%{reportMap.capitalworkpreviousyear}" /></td>
    </tr> 
    <tr>
        <td>Deposits received</td>
        <td><s:property value="%{reportMap.depositsreceivedcurrentyear}" /></td>
        <td><s:property value="%{reportMap.depositsreceivedpreviousyear}" /></td>
        <td>Deposit work</td>
        <td><s:property value="%{reportMap.depositworkcurrentyear}" /></td>
        <td><s:property value="%{reportMap.depositworkpreviousyear}" /></td>
    </tr>
    <tr>
        <td>Grants and Contributions for specific purpose</td>
        <td><s:property value="%{reportMap.grantscontricurrentyear}" /></td>
        <td><s:property value="%{reportMap.grantscontripreviousyear}" /></td>
        <td>Investment General Funds</td>
        <td><s:property value="%{reportMap.investmentgeneralcurrentyear}" /></td>
        <td><s:property value="%{reportMap.investmentgeneralpreviousyear}" /></td>
    </tr>
    <tr>
        <td>Sale proceeds from Assets</td> 
        <td><s:property value="%{reportMap.saleproceedcurrentyear}" /></td>
        <td><s:property value="%{reportMap.saleproceedpreviousyear}" /></td>
        <td>Investments Other Funds</td>
        <td><s:property value="%{reportMap.investmentotherscurrentyear}" /></td>
        <td><s:property value="%{reportMap.investmentotherspreviousyear}" /></td>
    </tr>
    <tr>
        <td>Realisation of Investment - General Fund</td> 
        <td><s:property value="%{reportMap.realisationgeneralcurrentyear}" /></td>
        <td><s:property value="%{reportMap.realisationgeneralpreviousyear}" /></td>
        <td>Loans & Advances to Employees</td> 
        <td><s:property value="%{reportMap.loansandadvancescurrentyear}" /></td>
        <td><s:property value="%{reportMap.loansandadvancespreviousyear}" /></td>      
    </tr>
    <tr>
        <td>Realisation of Investments - Other Funds</td> 
        <td><s:property value="%{reportMap.realisationotherscurrentyear}" /></td>
        <td><s:property value="%{reportMap.realisationotherspreviousyear}" /></td>      
        <td>Prepaid Expenses</td> 
        <td><s:property value="%{reportMap.prepaidexpensescurrentyear}" /></td>
        <td><s:property value="%{reportMap.prepaidexpensespreviousyear}" /></td>      
    </tr>    
    <tr>
        <td>Deposit Work</td> 
        <td><s:property value="%{reportMap.depositworkcurrentyear}" /></td>
        <td><s:property value="%{reportMap.depositworkpreviousyear}" /></td>      
        <td>Other Loans and Advances</td> 
        <td><s:property value="%{reportMap.loansandadvancescurrentyear}" /></td>
        <td><s:property value="%{reportMap.loansandadvancespreviousyear}" /></td>      
    </tr>
    <tr>
        <td>Revenue Collected in Advance</td> 
        <td><s:property value="%{reportMap.revenueadvancecurrentyear}" /></td>
        <td><s:property value="%{reportMap.revenueadvancepreviousyear}" /></td>      
        <td>Deposits with External Agencies</td> 
        <td><s:property value="%{reportMap.depositexternalcurrentyear}" /></td>
        <td><s:property value="%{reportMap.depositexternalpreviousyear}" /></td>      
    </tr>
    <tr>
        <td>Loans & Advances to Employees (Recovery)</td> 
        <td><s:property value="%{reportMap.loansrecoverycurrentyear}" /></td>
        <td><s:property value="%{reportMap.loansrecoverypreviousyear}" /></td>      
        <td>Other Payments</td> 
        <td><s:property value="%{reportMap.otherpaymentscurrentyear}" /></td>
        <td><s:property value="%{reportMap.otherpaymentspreviousyear}" /></td>      
    </tr>
    <tr>
        <td>Other Loans & Advances (recovery)</td> 
        <td><s:property value="%{reportMap.otherloansrecoverycurrentyear}" /></td>
        <td><s:property value="%{reportMap.otherloansrecoverypreviousyear}" /></td>      
        <td>Closing Balance (Cash/Bank including Imprest Balances and Designated Bank Accounts)</td> 
        <td><s:property value="%{reportMap.closingbalancecurrentyear}" /></td>
        <td><s:property value="%{reportMap.closingbalancepreviousyear}" /></td>      
    </tr>
    <tr>
        <td>Deposits with External Agencies (recovery)</td> 
        <td><s:property value="%{reportMap.depositsexternalrecoverycurrentyear}" /></td>
        <td><s:property value="%{reportMap.depositsexternalrecoverypreviousyear}" /></td>      
        <td>Bank</td> 
        <td><s:property value="%{reportMap.bankscurrentyear}" /></td>
        <td><s:property value="%{reportMap.bankspreviousyear}" /></td>      
    </tr>
    <tr>
        <td>Other Receipts</td> 
        <td><s:property value="%{reportMap.otherreceiptscurrentyear}" /></td>
        <td><s:property value="%{reportMap.otherreceiptspreviousyear}" /></td>      
        <td>Cash</td> 
        <td><s:property value="%{reportMap.cashescurrentyear}" /></td>
        <td><s:property value="%{reportMap.cashespreviousyear}" /></td>      
    </tr>
    <tr style="color: #ff9933;">
        <td><b>Cash and cash equivalents at end of period</b></td>
        <td><s:property value="%{reportMap.netCash3}" /></td>
    </tr>    

             
             
             
             
             
             
             
             
             
             
                <s:iterator value="cashFlowStatement.ieEntries"
                status="stat">
                <tr>
                  <td class="blueborderfortd">
                    <div align="center">
                      <s:if test='%{glCode != ""}'>
                        <s:if test='%{displayBold == true}'>

                          <strong><s:property value="glCode" /></strong>

                        </s:if>
                        <s:else>
                          <s:property value="glCode" />
                        </s:else>
                      </s:if>
                      &nbsp;
                    </div>
                  </td>
                  <td class="blueborderfortd">
                    <div align="left">
                      <s:if test='%{scheduleNo == ""}'>
                        <strong><s:property value="accountName" /></strong>
                      </s:if>
                      <s:else>
                        <s:property value="accountName" />
                      </s:else>
                      &nbsp;
                    </div>
                  </td>

                  <td class="blueborderfortd">
                    <div align="center">
                      <a href="javascript:void(0);"
                        onclick="return showSchedule('<s:property value="glCode"/>','<s:property value="scheduleNo"/>')"><s:property
                          value="scheduleNo" /></a>&nbsp;
                    </div>
                  </td>
                  <%--<td class="blueborderfortd">
                    <div align="right">
                      <s:if test='%{displayBold == true}'>
                        <strong><s:property value="budgetAmount" /></strong>
                      </s:if>
                      <s:else>
                        <s:property value="budgetAmount" />
                      </s:else>
                      &nbsp;
                    </div>
                  </td> --%>
                  <s:iterator value="cashFlowStatement.funds"
                    status="stat">
                    <td class="blueborderfortd">
                      <div align="right">

                        <s:if test='%{displayBold == true}'>
                          <strong><s:property value="netAmount[name]" />&nbsp;</strong>
                        </s:if>
                        <s:else>
                          <s:property value="netAmount[name]" />&nbsp;</s:else>
                      </div>
                    </td>
                    <td class="blueborderfortd">
                      <div align="right">
                        <s:if test='%{displayBold == true}'>
                          <strong><s:property
                              value="previousYearAmount[name]" />&nbsp;</strong>
                        </s:if>
                        <s:else>
                          <s:property value="previousYearAmount[name]" />&nbsp;</s:else>
                      </div>
                    </td>

                    </td>
                  </s:iterator>

                </tr>
              </s:iterator>
            </table>
            
            
            
            
            <!-- Upar delete -->
          </div>
        </td>
      </tr>
    </table>
    <div class="buttonbottom" id="ieReport">
      <s:text name="report.export.options" />
      : <a
        href='/services/EGF/report/receiptsandPaymentsReport-generateReceiptsPaymentsXls.action?showDropDown=false&model.period=<s:property value="model.period"/>&model.currency=<s:property value="model.currency"/>&model.financialYear.id=<s:property value="model.financialYear.id"/>&model.department.id=<s:property value="model.department.id"/>&model.asOndate=<s:property value="model.asOndate"/>&model.fund.id=<s:property value="model.fund.id"/>&model.function.id=<s:property value="model.function.id"/>&model.functionary.id=<s:property value="model.functionary.id"/>&model.field.id=<s:property value="model.field.id"/>'>Excel</a>
      | <a
        href='/services/EGF/report/receiptsandPaymentsReport-generateReceiptsPaymentsPdf.action?showDropDown=false&model.period=<s:property value="model.period"/>&model.currency=<s:property value="model.currency"/>&model.financialYear.id=<s:property value="model.financialYear.id"/>&model.department.id=<s:property value="model.department.id"/>&model.asOndate=<s:property value="model.asOndate"/>&model.fund.id=<s:property value="model.fund.id"/>&model.function.id=<s:property value="model.function.id"/>&model.functionary.id=<s:property value="model.functionary.id"/>&model.field.id=<s:property value="model.field.id"/>'>PDF</a>
    </div>
  </div>