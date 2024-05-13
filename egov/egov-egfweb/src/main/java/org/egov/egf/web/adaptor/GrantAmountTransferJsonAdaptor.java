package org.egov.egf.web.adaptor;

import java.math.BigDecimal;

import java.lang.reflect.Type;


import org.egov.egf.masters.model.GrantAmountTransfer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class GrantAmountTransferJsonAdaptor implements  JsonSerializer<GrantAmountTransfer>{
	
		@Override
		public JsonElement serialize(final GrantAmountTransfer grantAmountTransfer, final Type type, final JsonSerializationContext jsc) {
			final JsonObject jsonObject = new JsonObject();
			
			
			

			
			
			
			if (grantAmountTransfer != null) {
				if (grantAmountTransfer.getName() != null)
					jsonObject.addProperty("name", grantAmountTransfer.getName());
				else
					jsonObject.addProperty("name", "");
				if (grantAmountTransfer.getCode() != null)
					jsonObject.addProperty("code", grantAmountTransfer.getCode());
				else
					jsonObject.addProperty("code", "");
				
				/*
				 * if (grantAmountTransfer.getDate() != null) jsonObject.addProperty("date",
				 * grantAmountTransfer.getDate()); else jsonObject.addProperty("date", "");
				 */
				
				if (grantAmountTransfer.getUlbName() != null)
					jsonObject.addProperty("ulbName", grantAmountTransfer.getUlbName());
				else
					jsonObject.addProperty("ulbName", "");
				if (grantAmountTransfer.getUlbCode() != null)
					jsonObject.addProperty("ulbCode", grantAmountTransfer.getUlbCode());
				else
					jsonObject.addProperty("ulbCode", "");
				if (grantAmountTransfer.getAmount() != null)
					jsonObject.addProperty("amount", grantAmountTransfer.getAmount());
				else
					jsonObject.addProperty("amount", "");
				
				if (grantAmountTransfer.getBankId() != null )
					jsonObject.addProperty("bankId", grantAmountTransfer.getBankId());
				else
					jsonObject.addProperty("bankId", "");
				if (grantAmountTransfer.getBankIFSC() != null)
					jsonObject.addProperty("bankIFSC", grantAmountTransfer.getBankIFSC());
				else
					jsonObject.addProperty("bankIFSC", "");
				jsonObject.addProperty("id", grantAmountTransfer.getId());
			}
			return jsonObject;
		}

}
