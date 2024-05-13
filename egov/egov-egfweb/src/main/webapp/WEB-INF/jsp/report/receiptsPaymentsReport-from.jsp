<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>

<br />
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td>
			<div align="center">
				<br />
				<table border="0" cellspacing="0" cellpadding="0"
					class="tablebottom" width="100%">
					<tr>
						<td class="bluebgheadtd" width="100%"  colspan="12">
							<s:property value="scheduleheading" /> </strong>
						</td>
					</tr>
					<tr>
						<td class="bluebox" colspan="2"><strong><s:text
									name="report.run.date" />:<s:date name="todayDate"
									format="dd/MM/yyyy" /></strong></td>
						<td colspan="12">
							<div class="blueborderfortd" align="right">
								<strong> <s:text name="report.amount.in" /> <s:property
										value="model.currency" />&nbsp;&nbsp;&nbsp;&nbsp;
								</strong>
							</div>
						</td>
					</tr>

					<tr>
						<!--<th class="bluebgheadtd"><s:text name="report.accountCode" /></th>-->
						<th class="bluebgheadtd" width="20%"><s:text
								name="report.headOfAccount" /></th>
						<s:iterator value="receiptsPaymentsStatement.funds" status="stat">
							<th class="bluebgheadtd" colspan="2"><s:property
									value="name" />(Rs)</th>
						</s:iterator>
					</tr>
					<tr>
						<th class="bluebgheadtd"><s:text name="" /></th>
						<th class="bluebgheadtd"><s:text name="" /></th>
						<s:iterator value="receiptsPaymentsStatement.funds" status="stat">
							<th class="bluebgheadtd" width="15%" align="center" colspan="1"><s:text
									name="report.currentTotals" /> <s:property
									value="currentYearToDate" /></th>
							<th class="bluebgheadtd" width="15%" align="center" colspan="1"><s:text
									name="report.previousTotals" /> <s:property
									value="previousYearToDate" /></th>
						</s:iterator>
					</tr>
					</tr>
					<s:iterator value="receiptsPaymentsStatement.ieEntries"
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
									<s:if test='%{displayBold == true}'>
										<strong><s:property value="accountName" /></strong>
									</s:if>
									<s:else>
										<s:property value="accountName" />
									</s:else>
									&nbsp;
								</div>
							</td>
							<s:iterator value="receiptsPaymentsStatement.funds"
								status="stat">
								<s:if
									test='%{accountName == "Total" || accountName == "Schedule Total"}'>
									<td class="blueborderfortd">
										<div align="right">
											<strong><s:property value="netAmount[name]" /></strong>
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">
											<strong><s:property value="previousYearAmount[name]" /></strong>
										</div>
									</td>
								</s:if>
								<s:else>
									<td class="blueborderfortd">
										<div align="right">
											<a href="javascript:void(0);"
												onclick='return showDetail(<s:property value="glCode"/>,"<s:property value="id"/>","<s:property value="currentYearToDate"/>","<s:property value="currentYearFromDate"/>")'><s:property
													value="netAmount[name]" /></a>&nbsp;
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">
											<a href="javascript:void(0);"
												onclick='return showDetail(<s:property value="glCode"/>,"<s:property value="id"/>","<s:property value="previousYearToDate"/>","<s:property value="previousYearFromDate"/>")'><s:property
													value="previousYearAmount[name]" /></a>&nbsp;
										</div>
									</td>
								</s:else>
							</s:iterator>

						</tr>
					</s:iterator>
				</table>
			</div>
		</td>
	</tr>
</table>

