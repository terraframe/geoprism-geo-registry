package net.geoprism.registry.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.runwaysdk.dataaccess.cache.DataNotFoundException;
import com.runwaysdk.query.OIterator;
import com.runwaysdk.query.QueryFactory;
import com.runwaysdk.session.Request;
import com.runwaysdk.session.RequestType;
import com.runwaysdk.session.Session;

import net.geoprism.registry.GeoRegistryUtil;
import net.geoprism.registry.MasterList;
import net.geoprism.registry.MasterListQuery;
import net.geoprism.registry.progress.ProgressService;

public class MasterListService
{

  @Request(RequestType.SESSION)
  public JsonArray listAll(String sessionId)
  {
    return list();
  }

  public JsonArray list()
  {
    JsonArray response = new JsonArray();

    MasterListQuery query = new MasterListQuery(new QueryFactory());
    query.ORDER_BY_DESC(query.getDisplayLabel().localize());

    OIterator<? extends MasterList> it = query.getIterator();

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    try
    {
      while (it.hasNext())
      {
        MasterList list = it.next();

        JsonObject object = new JsonObject();
        object.addProperty("label", list.getDisplayLabel().getValue());
        object.addProperty("oid", list.getOid());

        object.addProperty("createDate", format.format(list.getCreateDate()));
        object.addProperty("lasteUpdateDate", format.format(list.getLastUpdateDate()));

        response.add(object);
      }
    }
    finally
    {
      it.close();
    }

    return response;
  }

  @Request(RequestType.SESSION)
  public JsonObject create(String sessionId, JsonObject list)
  {
    MasterList mList = MasterList.create(list);

    ( (Session) Session.getCurrentSession() ).reloadPermissions();

    return mList.toJSON();
  }

  @Request(RequestType.SESSION)
  public void remove(String sessionId, String oid)
  {
    try
    {
      MasterList.get(oid).delete();

      ( (Session) Session.getCurrentSession() ).reloadPermissions();
    }
    catch (DataNotFoundException e)
    {
      // Do nothing
    }
  }

  @Request(RequestType.SESSION)
  public JsonObject publish(String sessionId, String oid)
  {
    return MasterList.get(oid).publish();
  }

  @Request(RequestType.SESSION)
  public JsonObject get(String sessionId, String oid)
  {
    return MasterList.get(oid).toJSON();
  }

  @Request(RequestType.SESSION)
  public JsonObject data(String sessionId, String oid, Integer pageNumber, Integer pageSize, String filter, String sort)
  {
    return MasterList.get(oid).data(pageNumber, pageSize, filter, sort);
  }

  @Request(RequestType.SESSION)
  public JsonArray values(String sessionId, String oid, String value, String attributeName, String valueAttribute, String filterJson)
  {
    return MasterList.get(oid).values(value, attributeName, valueAttribute, filterJson);
  }

  @Request(RequestType.SESSION)
  public InputStream exportShapefile(String sessionId, String oid, String filterJson)
  {
    return GeoRegistryUtil.exportMasterListShapefile(oid, filterJson);
  }

  @Request(RequestType.SESSION)
  public InputStream exportSpreadsheet(String sessionId, String oid, String filterJson)
  {
    return GeoRegistryUtil.exportMasterListExcel(oid, filterJson);
  }

  @Request(RequestType.SESSION)
  public JsonObject progress(String sessionId, String oid)
  {
    return ProgressService.progress(oid).toJson();
  }
}
