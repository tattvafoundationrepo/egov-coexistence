 

<div class="form-group">
    <label class="col-sm-2 control-label text-right" for="date">
        <spring:message code="grantAmountTransfer.date" text="Date"/>
        <span class="mandatory"></span>                                                                                                 <!-- Added asterisk for mandatory field -->
    </label>
    <div class="col-sm-3 add-margin">
        <form:input path="date" class="form-control datepicker" id="date"  data-date-end-date="0d" data-inputmask="'mask': 'd/m/y'" required="required"/>
        <form:errors path="date" cssClass="add-margin error-msg" />
    </div>
</div>



<div class="form-group">
    <label class="col-sm-2 control-label text-right" for="ulbName">
    <spring:message code="grantAmountTransfer.ulbName" text="ULB Name"/>
    <span class="mandatory"></span> 
</label>
 <div class="col-sm-3 add-margin">
  <select id="ulbName" data-field-type="ulbName" name="ulbName" class="form-control" required="required" onchange="fillUlbCode()">
    <option value="">Select</option>
    <c:forEach var="entry" items="${ulbMap}">
        <option value="${entry.value}">${entry.key}</option>
        
    </c:forEach>
</select>
    <form:errors path="ulbName" cssClass="add-margin error-msg" />
</div>  
    <label class="col-sm-2 control-label text-right" for="ulbCode">
        <spring:message code="grantAmountTransfer.ulbCode" text="ULB Code"/>
        <span class="mandatory"></span>
    </label>
    <div class="col-sm-3 add-margin">
        <form:input path="ulbCode"  id="ulbCode" name="ulbCode" size="40" maxlength="100" cssClass="form-control patternvalidation" data-pattern="alphabetWithSpecialCharForContraWOAndGrantAmountTransfer" required="required" />
        <form:errors path="ulbCode" cssClass="add-margin error-msg" />
    </div>
</div>


<div class="form-group">
    <label class="col-sm-2 control-label text-right" for="grantType">
        <spring:message code="grantAmountTransfer.grantType"  text="Grant Type"/>
        <span class="mandatory"></span> <!-- Added asterisk for mandatory field -->
    </label>
    <div class="col-sm-3 add-margin">
        <form:select path="code" data-field-type="code" data-first-option="false" id="code" class="form-control" required="required">
            <form:option value=""><spring:message code="lbl.select" text="Select"/></form:option>                                      <!-- Added option for section selection -->  
           		<c:forEach var="grantTypes" items="${grantTypes}">
					<option value="${grantTypes.name}">${grantTypes.glcode}-${grantTypes.name}</option>
				</c:forEach>
        </form:select>
        <form:errors path="code" cssClass="add-margin error-msg" />
    </div>
    
    <label class="col-sm-2 control-label text-right" for="grant">
        <spring:message code="grantAmountTransfer.grant" text="Grant" />
        <span class="mandatory"></span> <!-- Added asterisk for mandatory field -->
    </label>
    <div class="col-sm-3 add-margin">
        <form:select path="name" data-first-option="false" id="name" class="form-control" required="required">
            <form:option value=""><spring:message code="lbl.select" text="Select"/></form:option> <!-- Added option for section selection -->
        </form:select>
        <form:errors path="name" cssClass="add-margin error-msg" />
    </div>
</div>






    <div class="form-group">
       <!--  <label class="col-sm-2 control-label text-right" for="amount">
            <spring:message code="grantAmountTransfer.amount" text="Amount "/>
            <span class="mandatory"> </span> Added asterisk for mandatory field
        </label>
        <div class="col-sm-3 add-margin amount"> 
            <form:input path="amount" id="amount" size="40" maxlength="100" cssClass="form-control patternvalidation" data-pattern="numeric" required="required" />
            <form:errors path="amount" cssClass="add-margin error-msg" />
        </div> -->
        <label class="col-sm-2 control-label text-right" for="amount">
          <spring:message code="grantAmountTransfer.amount" text="Amount "/>
          <span class="mandatory"> </span> <!-- Added asterisk for mandatory field -->
       </label>
        <div class="col-sm-3 add-margin amount"> 
           <form:input path="amount" id="amount" size="40" maxlength="100" 
            cssClass="form-control patternvalidation" data-pattern="numeric" 
            required="required" onblur="validateAmount(this)" />
          <form:errors path="amount" cssClass="add-margin error-msg" />
</div>
        
        <label class="col-sm-2 control-label text-right" for="bankAccountType">
            <spring:message code="grantAmountTransfer.bankAccountType" text="Bank Account Type"/>
          <!-- Added asterisk for mandatory field -->
        </label>
        <div class="col-sm-3 add-margin">
            <form:select path="bankAccountType" data-field-type="bankAccountType" name="bankAccountType" data-first-option="false" id="bankAccountType" class="form-control" >
                <form:option value=""><spring:message code="lbl.select" text="Select"/></form:option> <!-- Added option for section selection -->
              <%--  <form:options items="${bankAccountTypes}" itemValue="name"
									itemLabel="name" />  --%>
            </form:select>
            <form:errors path="bankAccountType" cssClass="add-margin error-msg" />
        </div>
    </div>



    
  <div class="form-group">
    <label class="col-sm-2 control-label text-right" for="bankId">
        <spring:message code="grantAmountTransfer.bankId" text="Bank & Branch"/>
    </label>
    <div class="col-sm-3 add-margin">
        <form:select path="bankId" data-field-type="bankId" name="bankId" data-first-option="false" id="bankId" class="form-control"  >
            <form:option value="0"><spring:message code="lbl.select" text="Select"/></form:option>
            
           <%--  <!-- Iterate over the list of banks -->
            <c:forEach items="${banks}" var="bank">
                <form:option value="${bank.id}">${bank.name}</form:option>
            </c:forEach>
            <!-- Iterate over the list of bank branches -->
            <c:forEach items="${bankBranch}" var="branch">
                <form:option value="${branch.id}">${branch.branchname}</form:option>
            </c:forEach> --%>
        </form:select>
        <form:errors path="bankId" cssClass="add-margin error-msg" />
    </div>
    
    
    <label class="col-sm-2 control-label text-right" for="bankAccountNumber">
        <spring:message code="grantAmountTransfer.bankAccountNumber" text="Bank Account Number"/>
    </label>
    <div class="col-sm-3 add-margin">
         <form:select path="bankAccountNumber" data-first-option="false" id="bankAccountNumber" class="form-control"  >
            <form:option value="0"><spring:message code="lbl.select" text="Select"/></form:option>
           
          <%--  <form:options items="${bankAccounts}" itemValue="accountnumber"
									itemLabel="accountnumber" /> --%>
        </form:select>
        <form:errors path="bankAccountNumber" cssClass="add-margin error-msg" />
    </div>
</div>
<div class="form-group">
    <label class="col-sm-2 control-label text-right" for="bankIFSC">
        <spring:message code="grantAmountTransfer.bankIFSC" text="IFSC Code"/>
    </label>
    <div class="col-sm-3 add-margin">
        <form:input path="bankIFSC" id="bankIFSC" minlength="11" maxlength="11" cssClass="form-control patternvalidation" data-pattern="alphanumericwithspace" />
        <form:errors path="bankIFSC" cssClass="add-margin error-msg" />
    </div>
</div>



