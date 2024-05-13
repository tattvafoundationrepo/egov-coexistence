package org.egov.egf.ptscheduler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
public class ReceiptHeader {
	@JsonProperty("localisationRequired")
	private boolean localisationRequired;
	@JsonProperty("name")
	private String name;
	@JsonProperty("label")
    private String label;
	@JsonProperty("type")
    private String type;
	@JsonProperty("defaultValue")
    private Object defaultValue;
	@JsonProperty("isMandatory")
    private boolean isMandatory;
	@JsonProperty("isLocalisationRequired")
    private boolean isLocalisationRequired;
	@JsonProperty("localisationPrefix")
    private String localisationPrefix;
	@JsonProperty("showColumn")
    private boolean showColumn;
	@JsonProperty("total")
    private boolean total;
	@JsonProperty("rowTotal")
    private Object rowTotal;
	@JsonProperty("columnTotal")
    private Object columnTotal;
	@JsonProperty("initialValue")
    private Object initialValue;
	@JsonProperty("minValue")
    private Object minValue;
	@JsonProperty("maxValue")
    private Object maxValue;
	
    
}   
   


