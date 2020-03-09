package net.geoprism.registry.etl;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import com.runwaysdk.dataaccess.ProgrammingErrorException;
import com.runwaysdk.dataaccess.transaction.Transaction;
import com.runwaysdk.query.OIterator;
import com.runwaysdk.query.OrderBy.SortOrder;
import com.runwaysdk.query.QueryFactory;
import com.runwaysdk.session.Request;
import com.runwaysdk.session.RequestType;
import com.runwaysdk.session.Session;
import com.runwaysdk.system.VaultFile;
import com.runwaysdk.system.scheduler.AllJobStatus;
import com.runwaysdk.system.scheduler.ExecutableJob;
import com.runwaysdk.system.scheduler.JobHistory;
import com.runwaysdk.system.scheduler.JobHistoryRecord;

import net.geoprism.DefaultConfiguration;
import net.geoprism.GeoprismUser;
import net.geoprism.registry.RegistryConstants;
import net.geoprism.registry.controller.GeoObjectEditorController;
import net.geoprism.registry.etl.ImportError.Resolution;
import net.geoprism.registry.io.GeoObjectImportConfiguration;

public class ETLService
{
  @Request(RequestType.SESSION)
  public void cancelImport(String sessionId, String json)
  {
    ImportConfiguration config = ImportConfiguration.build(json);

    String id = config.getVaultFileId();
    
    VaultFile.get(id).delete();
  }
  
  @Request(RequestType.SESSION)
  public JSONObject doImport(String sessionId, String json)
  {
    ImportConfiguration config = ImportConfiguration.build(json);

    ImportHistory hist;
    
    if (config.getHistoryId() != null && config.getHistoryId().length() > 0)
    {
      String historyId = config.getHistoryId();
      hist = ImportHistory.get(historyId);
      
      JobHistoryRecord record = hist.getAllJobRel().getAll().get(0);
      ExecutableJob execJob = record.getParent();

      execJob.resume(record);
    }
    else
    {
      DataImportJob job = new DataImportJob();
      job.setRunAsUserId(Session.getCurrentSession().getUser().getOid());
      job.apply();
     
      hist = job.start(config);
    }
    
    return new JSONObject(hist.getConfigJson());
  }
  
  @Request(RequestType.SESSION)
  public JSONObject getActiveImports(String sessionId, int pageSize, int pageNumber, String sortAttr, boolean isAscending)
  {
    JSONArray ja = new JSONArray();
    
    QueryFactory qf = new QueryFactory();
    ImportHistoryQuery ihq = new ImportHistoryQuery(qf);
    ihq.WHERE(ihq.getStatus().containsExactly(AllJobStatus.RUNNING).OR(ihq.getStatus().containsExactly(AllJobStatus.NEW)).OR(ihq.getStatus().containsExactly(AllJobStatus.QUEUED)).OR(ihq.getStatus().containsExactly(AllJobStatus.FEEDBACK)));
    ihq.restrictRows(pageSize, pageNumber);
    ihq.ORDER_BY(ihq.get(sortAttr), isAscending ? SortOrder.ASC : SortOrder.DESC);
    
    JSONObject page = new JSONObject();
    page.put("count", ihq.getCount());
    page.put("pageNumber", ihq.getPageNumber());
    page.put("pageSize", ihq.getPageSize());
    
    OIterator<? extends ImportHistory> it = ihq.getIterator();
    
    while (it.hasNext())
    {
      ImportHistory hist = it.next();
      DataImportJob job = (DataImportJob) hist.getAllJob().getAll().get(0);
      
      GeoprismUser user = GeoprismUser.get(job.getRunAsUser().getOid());
      
      ja.put(serializeHistory(hist, user));
    }
    
    page.put("results", ja);
    
    return page;
  }
  
  @Request(RequestType.SESSION)
  public JSONObject getCompletedImports(String sessionId, int pageSize, int pageNumber, String sortAttr, boolean isAscending)
  {
    JSONArray ja = new JSONArray();
    
    QueryFactory qf = new QueryFactory();
    ImportHistoryQuery ihq = new ImportHistoryQuery(qf);
    ihq.WHERE(ihq.getStatus().containsExactly(AllJobStatus.SUCCESS).OR(ihq.getStatus().containsExactly(AllJobStatus.FAILURE)));
    ihq.restrictRows(pageSize, pageNumber);
    ihq.ORDER_BY(ihq.get(sortAttr), isAscending ? SortOrder.ASC : SortOrder.DESC);
    
    JSONObject page = new JSONObject();
    page.put("count", ihq.getCount());
    page.put("pageNumber", ihq.getPageNumber());
    page.put("pageSize", ihq.getPageSize());
    
    OIterator<? extends ImportHistory> it = ihq.getIterator();
    
    while (it.hasNext())
    {
      ImportHistory hist = it.next();
      DataImportJob job = (DataImportJob) hist.getAllJob().getAll().get(0);
      
      GeoprismUser user = GeoprismUser.get(job.getRunAsUser().getOid());
      
      ja.put(serializeHistory(hist, user));
    }
    
    page.put("results", ja);
    
    return page;
  }

  protected JSONObject serializeHistory(ImportHistory hist, GeoprismUser user)
  {
    JSONObject jo = new JSONObject();
    
    jo.put("fileName", hist.getImportFile().getFileName());
    jo.put("stage", hist.getStage().get(0).name());
    jo.put("status", hist.getStatus().get(0).name());
    jo.put("author", user.getUsername());
    jo.put("createDate", hist.getCreateDate());
    jo.put("lastUpdateDate", hist.getLastUpdateDate());
    jo.put("importedRecords", hist.getImportedRecords());
    jo.put("workProgress", hist.getWorkProgress());
    jo.put("workTotal", hist.getWorkTotal());
    jo.put("historyId", hist.getOid());
    
    ImportConfiguration config = ImportConfiguration.build(hist.getConfigJson());
    if (config instanceof GeoObjectImportConfiguration)
    {
      SimpleDateFormat format = new SimpleDateFormat(GeoObjectImportConfiguration.DATE_FORMAT);
      format.setTimeZone(TimeZone.getTimeZone("GMT"));
      
      jo.put("configStartDate", format.format(((GeoObjectImportConfiguration)config).getStartDate()));
      jo.put("configEndDate", format.format(((GeoObjectImportConfiguration)config).getEndDate()));
    }
    
    jo.put("formatType", config.getFormatType());
    jo.put("importStrategy", config.getImportStrategy().name());
    jo.put("objectType", config.getObjectType());
    
    if (hist.getStatus().get(0).equals(AllJobStatus.FAILURE) && hist.getErrorJson().length() > 0)
    {
      JSONObject exception = new JSONObject();
      
      exception.put("type", new JSONObject(hist.getErrorJson()).get("type"));
      exception.put("message", hist.getLocalizedError(Session.getCurrentLocale()));
      
      jo.put("exception", exception);
    }
    
    return jo;
  }
  
  @Request(RequestType.SESSION)
  public JSONObject getImportErrors(String sessionId, String historyId, boolean onlyUnresolved, int pageSize, int pageNumber)
  {
    JSONArray ja = new JSONArray();
    
    ImportErrorQuery query = new ImportErrorQuery(new QueryFactory());
    
    query.WHERE(query.getHistory().EQ(historyId));
    
    if (onlyUnresolved)
    {
      query.WHERE(query.getResolution().EQ(Resolution.UNRESOLVED.name()));
    }
    
    query.ORDER_BY(query.getRowIndex(), SortOrder.ASC);

    query.restrictRows(pageSize, pageNumber);
    
    JSONObject page = new JSONObject();
    page.put("count", query.getCount());
    page.put("pageNumber", query.getPageNumber());
    page.put("pageSize", query.getPageSize());
    
    OIterator<? extends ImportError> it = query.getIterator();
    while (it.hasNext())
    {
      ImportError err = it.next();
      
      ja.put(serializeImportError(err));
    }
    
    page.put("result", ja);
    
    return page;
  }
  
  @Request(RequestType.SESSION)
  public JSONObject getReferenceValidationProblems(String sessionId, String historyId, int pageSize, int pageNumber)
  {
    ImportHistory hist = ImportHistory.get(historyId);
    
    ValidationProblemQuery vpq = new ValidationProblemQuery(new QueryFactory());
    vpq.WHERE(vpq.getHistory().EQ(hist).AND(vpq.getType().EQ(ParentReferenceProblem.CLASS).OR(vpq.getType().EQ(TermReferenceProblem.CLASS))));
    vpq.restrictRows(pageSize, pageNumber);
    vpq.ORDER_BY(vpq.getCreateDate(), SortOrder.ASC);
    
    JSONObject page = new JSONObject();
    page.put("count", vpq.getCount());
    page.put("pageNumber", vpq.getPageNumber());
    page.put("pageSize", vpq.getPageSize());
    
    JSONArray jaVP = new JSONArray();
    
    OIterator<? extends ValidationProblem> it = vpq.getIterator();
    try
    {
      while (it.hasNext())
      {
        ValidationProblem vp = it.next();
        
        jaVP.put(vp.toJSON());
      }
    }
    finally
    {
      it.close();
    }
    
    page.put("results", jaVP);
    
    return page;
  }
  
  @Request(RequestType.SESSION)
  public JSONObject getRowValidationProblems(String sessionId, String historyId, int pageSize, int pageNumber)
  {
    ImportHistory hist = ImportHistory.get(historyId);
    
    RowValidationProblemQuery vpq = new RowValidationProblemQuery(new QueryFactory());
    vpq.WHERE(vpq.getHistory().EQ(hist));
    vpq.restrictRows(pageSize, pageNumber);
    vpq.ORDER_BY(vpq.getRowNum(), SortOrder.ASC);
    
    JSONObject page = new JSONObject();
    page.put("count", vpq.getCount());
    page.put("pageNumber", vpq.getPageNumber());
    page.put("pageSize", vpq.getPageSize());
    
    JSONArray jaVP = new JSONArray();
    
    OIterator<? extends ValidationProblem> it = vpq.getIterator();
    try
    {
      while (it.hasNext())
      {
        ValidationProblem vp = it.next();
        
        jaVP.put(vp.toJSON());
      }
    }
    finally
    {
      it.close();
    }
    
    page.put("results", jaVP);
    
    return page;
  }
  
  @Request(RequestType.SESSION)
  public JSONObject getImportDetails(String sessionId, String historyId, boolean onlyUnresolved, int pageSize, int pageNumber)
  {
    ImportHistory hist = ImportHistory.get(historyId);
    DataImportJob job = (DataImportJob) hist.getAllJob().getAll().get(0);
    GeoprismUser user = GeoprismUser.get(job.getRunAsUser().getOid());
    
    JSONObject jo = this.serializeHistory(hist, user);
    
    if (hist.getStage().get(0).equals(ImportStage.IMPORT_RESOLVE) && hist.hasImportErrors())
    {
      jo.put("importErrors", this.getImportErrors(sessionId, historyId, onlyUnresolved, pageSize, pageNumber));
    }
    else if (hist.getStage().get(0).equals(ImportStage.VALIDATION_RESOLVE))
    {
      jo.put("referenceProblems", this.getReferenceValidationProblems(sessionId, historyId, pageSize, pageNumber));
      jo.put("rowValidationProblems", this.getRowValidationProblems(sessionId, historyId, pageSize, pageNumber));
    }
    
    return jo;
  }
  
  protected JSONObject serializeImportError(ImportError err)
  {
    JSONObject jo = new JSONObject();
    
    JSONObject exception = new JSONObject();
    exception.put("type", new JSONObject(err.getErrorJson()).get("type"));
    exception.put("message", JobHistory.readLocalizedException(new JSONObject(err.getErrorJson()), Session.getCurrentLocale()));
    jo.put("exception", exception);
    
    if (err.getObjectJson() != null && err.getObjectJson().length() > 0)
    {
      jo.put("object", new JSONObject(err.getObjectJson()));
    }
    
    jo.put("objectType", err.getObjectType());
    
    jo.put("importErrorId", err.getOid());
    
    jo.put("resolution", err.getResolution());
    
    return jo;
  }
  
  private void checkPermissions()
  {
    Map<String, String> roles = Session.getCurrentSession().getUserRoles();
    if (! (roles.keySet().contains(DefaultConfiguration.ADMIN)
        || roles.keySet().contains(RegistryConstants.REGISTRY_MAINTAINER_ROLE)
        || roles.keySet().contains(RegistryConstants.REGISTRY_ADMIN_ROLE)
      ))
    {
      throw new ProgrammingErrorException("You don't have permissions to access this endpoint.");
    }
  }

  @Request(RequestType.SESSION)
  public void submitImportErrorResolution(String sessionId, String json)
  {
    submitImportErrorResolutionInTrans(sessionId, json);
  }

  @Transaction
  private void submitImportErrorResolutionInTrans(String sessionId, String json)
  {
    checkPermissions();
    
    JSONObject config = new JSONObject(json);
    
    ImportHistory hist = ImportHistory.get(config.getString("historyId"));
    
    ImportError err = ImportError.get(config.getString("importErrorId"));
    
    String resolution = config.getString("resolution");
    
    if (resolution.equals(Resolution.APPLY_GEO_OBJECT.name()))
    {
      String parentTreeNode = config.getString("parentTreeNode");
      String geoObject = config.getString("geoObject");
      Boolean isNew = config.getBoolean("isNew");
      
      new GeoObjectEditorController().applyInReq(sessionId, parentTreeNode, geoObject, isNew, null, null);
      
      err.appLock();
      err.setResolution(resolution);
      err.apply();
      
      hist.appLock();
      hist.setErrorResolvedCount(hist.getErrorResolvedCount() + 1);
      hist.apply();
    }
    else if (resolution.equals(Resolution.IGNORE.name()))
    {
      err.appLock();
      err.setResolution(resolution);
      err.apply();
    }
    else
    {
      throw new UnsupportedOperationException("Invalid import resolution [" + resolution + "].");
    }
  }
  
  @Request(RequestType.SESSION)
  public void resolveImport(String sessionId, String historyId)
  {
    resolveImportInTrans(historyId);
  }
  
  @Transaction
  private void resolveImportInTrans(String historyId)
  {
    checkPermissions();
    
    ImportHistory hist = ImportHistory.get(historyId);
    hist.appLock();
    
    ImportErrorQuery ieq = new ImportErrorQuery(new QueryFactory());
    ieq.WHERE(ieq.getHistory().EQ(historyId));
    OIterator<? extends ImportError> it = ieq.getIterator();
    try
    {
      ImportError err = it.next();
      
      err.delete();
    }
    finally
    {
      it.close();
    }
    
    hist.clearStatus();
    hist.addStatus(AllJobStatus.SUCCESS);
    
    hist.clearStage();
    hist.addStage(ImportStage.COMPLETE);
    
    hist.apply();
    
    VaultFile file = hist.getImportFile();
    file.delete();
  }
}
