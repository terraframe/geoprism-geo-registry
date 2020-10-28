/**
 * Copyright (c) 2019 TerraFrame, Inc. All rights reserved.
 *
 * This file is part of Geoprism Registry(tm).
 *
 * Geoprism Registry(tm) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Geoprism Registry(tm) is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Geoprism Registry(tm). If not, see <http://www.gnu.org/licenses/>.
 */
package net.geoprism.registry.service;

import java.util.List;

import org.json.JSONException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.runwaysdk.session.Request;
import com.runwaysdk.session.RequestType;

import net.geoprism.registry.Organization;
import net.geoprism.registry.graph.ExternalSystem;
import net.geoprism.registry.view.Page;

public class ExternalSystemService
{
  @Request(RequestType.SESSION)
  public JsonObject page(String sessionId, Integer pageNumber, Integer pageSize) throws JSONException
  {
    long count = ExternalSystem.getCount();
    List<ExternalSystem> results = ExternalSystem.getExternalSystemsForOrg(pageNumber, pageSize);

    return new Page<ExternalSystem>(count, pageNumber, pageSize, results).toJSON();
  }

  @Request(RequestType.SESSION)
  public JsonObject apply(String sessionId, String json) throws JSONException
  {
    JsonElement element = JsonParser.parseString(json);

    ExternalSystem system = ExternalSystem.desieralize(element.getAsJsonObject());
    system.apply();

    return system.toJSON();
  }

  @Request(RequestType.SESSION)
  public JsonObject get(String sessionId, String oid) throws JSONException
  {
    ExternalSystem system = ExternalSystem.get(oid);

    return system.toJSON();
  }

  @Request(RequestType.SESSION)
  public void remove(String sessionId, String oid)
  {
    ExternalSystem system = ExternalSystem.get(oid);
    Organization organization = system.getOrganization();

    ServiceFactory.getRolePermissionService().enforceRA(organization.getCode());

    system.delete();
  }
}
