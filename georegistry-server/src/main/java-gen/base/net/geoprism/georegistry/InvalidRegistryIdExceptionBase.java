package net.geoprism.georegistry;

@com.runwaysdk.business.ClassSignature(hash = 2135017651)
/**
 * This class is generated automatically.
 * DO NOT MAKE CHANGES TO IT - THEY WILL BE OVERWRITTEN
 * Custom business logic should be added to InvalidRegistryIdException.java
 *
 * @author Autogenerated by RunwaySDK
 */
public abstract class InvalidRegistryIdExceptionBase extends com.runwaysdk.business.SmartException
{
  public final static String CLASS = "net.geoprism.georegistry.InvalidRegistryIdException";
  public static java.lang.String OID = "oid";
  public static java.lang.String REGISTRYID = "registryId";
  private static final long serialVersionUID = 2135017651;
  
  public InvalidRegistryIdExceptionBase()
  {
    super();
  }
  
  public InvalidRegistryIdExceptionBase(java.lang.String developerMessage)
  {
    super(developerMessage);
  }
  
  public InvalidRegistryIdExceptionBase(java.lang.String developerMessage, java.lang.Throwable cause)
  {
    super(developerMessage, cause);
  }
  
  public InvalidRegistryIdExceptionBase(java.lang.Throwable cause)
  {
    super(cause);
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
    com.runwaysdk.dataaccess.MdClassDAOIF mdClassIF = com.runwaysdk.dataaccess.metadata.MdClassDAO.getMdClassDAO(net.geoprism.georegistry.InvalidRegistryIdException.CLASS);
    return (com.runwaysdk.dataaccess.MdAttributeUUIDDAOIF)mdClassIF.definesAttribute(OID);
  }
  
  public String getRegistryId()
  {
    return getValue(REGISTRYID);
  }
  
  public void validateRegistryId()
  {
    this.validateAttribute(REGISTRYID);
  }
  
  public static com.runwaysdk.dataaccess.MdAttributeCharacterDAOIF getRegistryIdMd()
  {
    com.runwaysdk.dataaccess.MdClassDAOIF mdClassIF = com.runwaysdk.dataaccess.metadata.MdClassDAO.getMdClassDAO(net.geoprism.georegistry.InvalidRegistryIdException.CLASS);
    return (com.runwaysdk.dataaccess.MdAttributeCharacterDAOIF)mdClassIF.definesAttribute(REGISTRYID);
  }
  
  public void setRegistryId(String value)
  {
    if(value == null)
    {
      setValue(REGISTRYID, "");
    }
    else
    {
      setValue(REGISTRYID, value);
    }
  }
  
  protected String getDeclaredType()
  {
    return CLASS;
  }
  
  public java.lang.String localize(java.util.Locale locale)
  {
    java.lang.String message = super.localize(locale);
    message = replace(message, "{oid}", this.getOid());
    message = replace(message, "{registryId}", this.getRegistryId());
    return message;
  }
  
}
