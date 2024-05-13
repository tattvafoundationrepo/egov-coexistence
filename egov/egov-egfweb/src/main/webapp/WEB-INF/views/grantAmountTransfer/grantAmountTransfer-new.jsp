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



<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<form:form role="form" action="create" modelAttribute="grantAmountTransfer" id="grantAmountTransferform"
    cssClass="form-horizontal form-groups-bordered" enctype="multipart/form-data">
     
 <div class="main-content">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading control-label">
					 <div class="panel-title"><spring:message code="title.grantAmountTransfer.create"/></div>
				</div>
				
			    <!-- Include the form content here -->
			    <%-- <spring:url var="create" value="/create"></spring:url> --%>
			    <%@ include file="grantAmountTransfer-form.jsp"%>
			    
			    <div class="form-group">
			      <div class="text-center">
			        <button type='submit' class='btn btn-primary' id="buttonSubmit"> <spring:message code='lbl.create'/> </button>
			        <input type="button" class="btn btn-default" value='<spring:message code='lbl.clear' />' id="button" name="clear" onclick="this.form.reset();">&nbsp;
			        <a href='javascript:void(0)' class='btn btn-default' onclick="javascript:window.parent.postMessage('close','*');"><spring:message code='lbl.close' text="Close"/></a>
			      </div>
			    </div>
		    </div>
		</div>
	</div>
</div>
</form:form>

<script>
    $(document).ready(function() {

         $('select[data-field-type="ulbName"]').change(function() {
            var ulbName =  $("#ulbName option:selected").text();
            console.log("<== ulb code is " + ulbName);
            $.ajax({
                type: "POST",
                url: "/services/EGF/uddgrants/ulbcodeMapping/"+ ulbName,
                success: function(response) {
                    console.log(response);
                    var len = response.length;
                    $("#bankAccountType").empty();
                    for( var i = 0; i<len; i++){
                        var accountType = response[i];                    
                        $("#bankAccountType").append("<option value='" + accountType + "'>" + accountType + "</option>"); 
                    }
                },
                error: function(xhr, status, error) {
                    // Handle error
                    console.error(error);
                }
            });
        });
    });
 </script>
<script> 
    $(document).ready(function() {

         $('select[data-field-type="bankAccountType"]').change(function() {
            var bankAccountType =  $("#bankAccountType option:selected").text();
            console.log("<== bankAccountType is " + bankAccountType);
            $.ajax({
                type: "GET",
                url: "/services/EGF/uddgrants/bankMapping/"+ bankAccountType,
                success: function(response) {
                    console.log(response);
                    var len = response.length;
                    $("#bankId").empty();
                    for( var i = 0; i<len; i++){
                        var bank = response[i];                    
                        $("#bankId").append("<option value='" + bank + "'>" + bank + "</option>"); 
                    }
                },
                error: function(xhr, status, error) {
                    // Handle error
                    console.error(error);
                }
            });
        });
    });
</script>
<script> 
    $(document).ready(function() {

         $('select[data-field-type="bankId"]').change(function() {
            var bankId =  $("#bankId option:selected").text();
            console.log("<== bankA is " + bankId);
            $.ajax({
                type: "GET",
                url: "/services/EGF/uddgrants/bankAccountMapping/"+ bankId,
                success: function(response) {
                    console.log(response);
                    var len = response.length;
                    $("#bankAccountNumber").empty();
                    for( var i = 0; i<len; i++){
                        var accountNo = response[i];                    
                        $("#bankAccountNumber").append("<option value='" + accountNo + "'>" + accountNo + "</option>"); 
                    }
                },
                error: function(xhr, status, error) {
                    // Handle error
                    console.error(error);
                }
            });
        });
    });
</script>

<script> 
$(document).ready(function(){

   $('select[data-field-type="code"]').change(function(){
    var grantType = $("#code option:selected").text(); 
    console.log("<== grant type is " + grantType);
    grantType=grantType.replace(/(^\d+)(.+$)/i,'$1');
    console.log("<== updated grant type is " + grantType);
    $.ajax({
        url: "/services/EGF/uddgrants/grantTypeSubList/" + grantType,
        type: 'GET',
        success:function(response){
            var len = response.length;
            console.log("<========= response length ===> "+len);
            //$("#name").empty(); hame lekin first wale ko remove nhi karna hai 
            //$("#name").append("<option value='-1'>"+"Select"+"</option>");
            $('#name option:not(:first)').remove();
            for( var i = 0; i<len; i++){
                var grantVal = response[i].glcode;                    
                $("#name").append("<option value='"+response[i].name+"'>"+response[i].glcode+"-"+response[i].name+"</option>");
            }
          }
        });
     });
 });
</script>
<script>
function validateAmount(input) {
    var amountValue = input.value;
    if (parseFloat(amountValue) <= 0) {
        alert("Amount must be a positive number.");
        input.value = ''; // Clear the input field
        input.focus(); // Set focus back to the input field
    }
}
</script>

<script>
	$('#buttonSubmit').click(function(e) {
		if ($('form').valid()) {
		} else {
			e.preventDefault();
		}
	});
</script>
<script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/app/js/grantAmountTransferHelper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>

























