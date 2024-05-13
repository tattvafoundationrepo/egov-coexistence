


<div class="form-group">
    <label class="col-sm-2 control-label text-right" for="date">
        <spring:message code="grantAmountTransfer.date" text="Date"/>
    </label>
    <div class="col-sm-3 add-margin">
        <form:input path="date" class="form-control" id="date"  data-date-end-date="0d" readonly="true"/>
    </div>
</div>


 <div class="form-group">
    <label class="col-sm-2 control-label text-right" for="ulbName">
        <spring:message code="grantAmountTransfer.ulbName" text="ULB Name"/>
    </label>
    <div class="col-sm-3 add-margin">
        <form:input path="ulbName" id="ulbName" size="40" maxlength="100" cssClass="form-control patternvalidation" data-pattern="alphabetWithSpecialCharForContraWOAndGrantAmountTransfer" readonly="true"/>
    </div>

    <label class="col-sm-2 control-label text-right" for="ulbCode">
        <spring:message code="grantAmountTransfer.ulbCode" text="ULB Code"/>
    </label>
    <div class="col-sm-3 add-margin">
        <form:input path="ulbCode" id="ulbCode" size="40" maxlength="100" cssClass="form-control patternvalidation" data-pattern="alphabetWithSpecialCharForContraWOAndGrantAmountTransfer" readonly="true"/>
    </div>
</div>




<div class="form-group">
    <label class="col-sm-2 control-label text-right" for="grantType">
        <spring:message code="grantAmountTransfer.grantType"  text="Grant Type"/>
    </label>
    <div class="col-sm-3 add-margin">
        <form:input path="code" id="code" cssClass="form-control patternvalidation" readonly="true"/>
    </div>
    
    <label class="col-sm-2 control-label text-right" for="grant">
        <spring:message code="grantAmountTransfer.grant" text="Grant" />
    </label>
    <div class="col-sm-3 add-margin">
       <form:input path="name" id="name" cssClass="form-control patternvalidation" readonly="true"/>
    </div>
</div>










    <div class="form-group">
        <label class="col-sm-2 control-label text-right" for="amount">
            <spring:message code="grantAmountTransfer.amount" text="Amount "/>
        </label>
        <div class="col-sm-3 add-margin amount"> 
            <form:input path="amount" id="amount" size="40" maxlength="100" cssClass="form-control patternvalidation" data-pattern="numeric" readonly="true"/>
        </div>
 
        <label class="col-sm-2 control-label text-right" for="bankAccountType">
            <spring:message code="grantAmountTransfer.bankAccountType" text="Bank Account Type"/>
        </label>
        <div class="col-sm-3 add-margin">
            <form:input path="bankAccountType" id="bankAccountType" cssClass="form-control patternvalidation" readonly="true"/>
        </div>
    </div>




<div class="form-group">
    <label class="col-sm-2 control-label text-right" for="bankId">
        <spring:message code="grantAmountTransfer.bankId" text="Bank & Branch"/>
    </label>
    <div class="col-sm-3 add-margin">
        <form:input path="bankId" id="bankId" cssClass="form-control patternvalidation" readonly="true"/>
    </div>
    
    
    <label class="col-sm-2 control-label text-right" for="bankAccountNumber">
        <spring:message code="grantAmountTransfer.bankAccountNumber" text="Bank Account Number"/>
    </label>
    <div class="col-sm-3 add-margin">
        <form:input path="bankAccountNumber" id="bankAccountNumber" maxlength="25" cssClass="form-control patternvalidation" data-pattern="numericslashhyphen" readonly="true"/>
    </div>
</div>




<div class="form-group">
    <label class="col-sm-2 control-label text-right" for="bankIFSC">
        <spring:message code="grantAmountTransfer.bankIFSC" text="IFSC Code"/>
    </label>
    <div class="col-sm-3 add-margin">
        <form:input path="bankIFSC" id="bankIFSC" minlength="11" maxlength="11" cssClass="form-control patternvalidation" data-pattern="alphanumericwithspace" readonly="true"/>
    </div>
    
</div>


