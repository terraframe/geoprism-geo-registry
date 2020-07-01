package net.geoprism.registry.etl.export;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.runwaysdk.session.Session;
import com.runwaysdk.system.scheduler.JobHistory;

import net.geoprism.dhis2.dhis2adapter.response.EntityImportResponse.ErrorReport;
import net.geoprism.registry.etl.ExportJobHasErrors;

public class ExportError extends ExportErrorBase
{
  private static final long serialVersionUID = -1831328547;
  
  public ExportError()
  {
    super();
  }
  
  public ErrorReport getErrorReport()
  {
    String json = this.getResponseJson();
    
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    
    return gson.fromJson(json, ErrorReport.class);
  }
  
  public JsonObject toJson()
  {
    JsonObject jo = new JsonObject();
    
    if (this.getCode() != null && this.getCode().length() > 0)
    {
      jo.addProperty("code", this.getCode());
    }
    
    if (this.getResponseJson() != null && this.getResponseJson().length() > 0)
    {
      jo.addProperty("message", this.getErrorReport().getMessage());
    }
    else if (this.getErrorJson() != null && this.getErrorJson().length() > 0)
    {
      jo.addProperty("message", JobHistory.readLocalizedException(new JSONObject(this.getErrorJson()), Session.getCurrentLocale()));
    }
    
    jo.addProperty("id", this.getOid());
    
    jo.addProperty("rowNum", this.getRowIndex());
    
    return jo;
  }
  
}