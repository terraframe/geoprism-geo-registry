/**
 * Copyright (c) 2019 TerraFrame, Inc. All rights reserved.
 *
 * This file is part of Geoprism Registry(tm).
 *
 * Geoprism Registry(tm) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Geoprism Registry(tm) is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Geoprism Registry(tm).  If not, see <http://www.gnu.org/licenses/>.
 */
package net.geoprism.registry.account;

@com.runwaysdk.business.ClassSignature(hash = -338050549)
/**
 * This class is generated automatically.
 * DO NOT MAKE CHANGES TO IT - THEY WILL BE OVERWRITTEN
 * Custom business logic should be added to RegistryAccountUtil.java
 *
 * @author Autogenerated by RunwaySDK
 */
public abstract class RegistryAccountUtilBase extends com.runwaysdk.business.Util
{
  public final static String CLASS = "net.geoprism.registry.account.RegistryAccountUtil";
  public static java.lang.String OID = "oid";
  private static final long serialVersionUID = -338050549;
  
  public RegistryAccountUtilBase()
  {
    super();
  }
  
  public String getOid()
  {
    return getValue(OID);
  }
  
  public void validateOid()
  {
    this.validateAttribute(OID);
  }
  
  public static com.runwaysdk.dataaccess.MdAttributeUUIDDAOIF getOidMd()
  {
    com.runwaysdk.dataaccess.MdClassDAOIF mdClassIF = com.runwaysdk.dataaccess.metadata.MdClassDAO.getMdClassDAO(net.geoprism.registry.account.RegistryAccountUtil.CLASS);
    return (com.runwaysdk.dataaccess.MdAttributeUUIDDAOIF)mdClassIF.definesAttribute(OID);
  }
  
  protected String getDeclaredType()
  {
    return CLASS;
  }
  
  public static RegistryAccountUtil get(String oid)
  {
    return (RegistryAccountUtil) com.runwaysdk.business.Util.get(oid);
  }
  
  public static void initiate(java.lang.String invite, java.lang.String serverUrl, java.lang.String roleIds)
  {
    String msg = "This method should never be invoked.  It should be overwritten in net.geoprism.registry.account.RegistryAccountUtil.java";
    throw new com.runwaysdk.dataaccess.metadata.ForbiddenMethodException(msg);
  }
  
  public static void inviteComplete(java.lang.String token, java.lang.String user)
  {
    String msg = "This method should never be invoked.  It should be overwritten in net.geoprism.registry.account.RegistryAccountUtil.java";
    throw new com.runwaysdk.dataaccess.metadata.ForbiddenMethodException(msg);
  }
  
  public static net.geoprism.GeoprismUser newUserInst()
  {
    String msg = "This method should never be invoked.  It should be overwritten in net.geoprism.registry.account.RegistryAccountUtil.java";
    throw new com.runwaysdk.dataaccess.metadata.ForbiddenMethodException(msg);
  }
  
  public String toString()
  {
    if (this.isNew())
    {
      return "New: "+ this.getClassDisplayLabel();
    }
    else
    {
      return super.toString();
    }
  }
}
