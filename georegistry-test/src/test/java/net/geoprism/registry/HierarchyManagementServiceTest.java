package net.geoprism.registry;

import java.util.Locale;

import net.geoprism.georegistry.service.ConversionService;
import net.geoprism.georegistry.service.RegistryService;

import org.commongeoregistry.adapter.RegistryAdapter;
import org.commongeoregistry.adapter.RegistryAdapterServer;
import org.commongeoregistry.adapter.constants.DefaultAttribute;
import org.commongeoregistry.adapter.constants.GeometryType;
import org.commongeoregistry.adapter.metadata.GeoObjectType;
import org.commongeoregistry.adapter.metadata.HierarchyType;
import org.commongeoregistry.adapter.metadata.MetadataFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.runwaysdk.business.BusinessFacade;
import com.runwaysdk.business.rbac.RoleDAO;
import com.runwaysdk.business.rbac.UserDAO;
import com.runwaysdk.constants.CommonProperties;
import com.runwaysdk.constants.LocalProperties;
import com.runwaysdk.constants.MdBusinessInfo;
import com.runwaysdk.constants.UserInfo;
import com.runwaysdk.dataaccess.DuplicateDataException;
import com.runwaysdk.dataaccess.MdBusinessDAOIF;
import com.runwaysdk.dataaccess.cache.DataNotFoundException;
import com.runwaysdk.dataaccess.transaction.Transaction;
import com.runwaysdk.session.Request;
import com.runwaysdk.session.SessionFacade;
import com.runwaysdk.system.gis.geo.Universal;
import com.runwaysdk.system.metadata.MdBusiness;
import com.runwaysdk.system.metadata.MdTermRelationship;

public class HierarchyManagementServiceTest
{
  
  public static RegistryAdapter adapter = null;
  
  public static RegistryService service = null;
  
  /**
   * The test user object
   */
  private static UserDAO                  newUser;
  
  /**
   * The username for the user
   */
  private final static String             USERNAME                 = "btables";

  /**
   * The password for the user
   */
  private final static String             PASSWORD                 = "1234";

  private final static int                sessionLimit             = 2;
  
  private final static String             COUNTRY_CODE             = "CountryTest";
  
  private final static String             PROVINCE_CODE            = "ProvinceTest";
  
  private final static String             DISTRICT_CODE            = "DistrictTest";
  
  private final static String             VILLAGE_CODE             = "VillageTest";
  
  private final static String             REPORTING_DIVISION_CODE  = "ReportingDivision";
  
  @BeforeClass
  @Request
  public static void setUp()
  {
    LocalProperties.setSkipCodeGenAndCompile(true);
    
    adapter = RegistryService.getRegistryAdapter();
    
    service = new RegistryService();
    
    setUpTransaction();
  }
  
  @Transaction
  private static void setUpTransaction()
  {    
    try
    {
      // Create a new user
      newUser = UserDAO.newInstance();
      newUser.setValue(UserInfo.USERNAME, USERNAME);
      newUser.setValue(UserInfo.PASSWORD, PASSWORD);
      newUser.setValue(UserInfo.SESSION_LIMIT, Integer.toString(sessionLimit));
      newUser.apply();
      
      // Make the user an admin
      RoleDAO adminRole = RoleDAO.findRole(RoleDAO.ADMIN_ROLE).getBusinessDAO();
      adminRole.assignMember(newUser);
    }
    catch (DuplicateDataException e) {}
  }
  
  @AfterClass
  @Request
  public static void tearDown()
  {
    tearDownTransaction();
    
    LocalProperties.setSkipCodeGenAndCompile(false);
  }
  @Transaction
  private static void tearDownTransaction()
  {
    // Just in case a previous test did not clean up properly.   
    try
    {
      Universal universal = Universal.getByKey(VILLAGE_CODE);
      universal.delete();
    } catch (DataNotFoundException e) {} 
    
    try
    {
      Universal universal = Universal.getByKey(DISTRICT_CODE);
      universal.delete();
    } catch (DataNotFoundException e) {} 
    
    try
    {
      Universal provinceTestUniversal = Universal.getByKey(PROVINCE_CODE);
      provinceTestUniversal.delete();
    } catch (DataNotFoundException e) {} 
    
    try
    {
      Universal provinceTestUniversal = Universal.getByKey(COUNTRY_CODE);
      provinceTestUniversal.delete();
    } catch (DataNotFoundException e) {} 
    
    try
    {
      MdTermRelationship mdTermRelationship = MdTermRelationship.getByKey(ConversionService.buildMdTermRelationshipKey(REPORTING_DIVISION_CODE));
      mdTermRelationship.delete();
    } catch (DataNotFoundException e) {} 
    
    if (newUser.isAppliedToDB())
    {
      newUser.getBusinessDAO().delete();
    }  
  }
  
  @Test
  public void testCreateGeoObjectType()
  {  
    String sessionId = this.logInAdmin();
   
    
    RegistryAdapterServer registry = new RegistryAdapterServer();
    
    GeoObjectType province = MetadataFactory.newGeoObjectType(PROVINCE_CODE, GeometryType.POLYGON, "Province", "", false, registry);
    String gtJSON = province.toJSON().toString();
    
    try
    {
      service.createGeoObjectType(sessionId, gtJSON);
    }
    finally
    {
      logOutAdmin(sessionId);
    }
    
    checkAttributes(PROVINCE_CODE);
    
    sessionId = this.logInAdmin();
    try
    {
      service.deleteGeoObjectType(sessionId, PROVINCE_CODE);
    }
    finally
    {
      logOutAdmin(sessionId);
    }
  }  
  @Request
  private void checkAttributes(String code)
  {
    Universal universal = Universal.getByKey(code);
    MdBusiness mdBusiness = universal.getMdBusiness();
    
    MdBusinessDAOIF mdBusinessDAOIF = (MdBusinessDAOIF)BusinessFacade.getEntityDAO(mdBusiness);
// For debugging    
//    mdBusinessDAOIF.getAllDefinedMdAttributes().forEach(a -> System.out.println(a.definesAttribute() +" "+a.getType()));

    // DefaultAttribute.UID - Defined on the MdBusiness and the values are from the {@code GeoObject#OID};
    try
    {
      mdBusinessDAOIF.definesAttribute(DefaultAttribute.UID.getName());
    }
    catch (DataNotFoundException e)
    {
      Assert.fail("Attribute that implements GeoObject.UID does not exist. It should be defined on the business class");
    }
    
    // DefaultAttribute.CODE - defined by GeoEntity geoId
    try
    {
      mdBusinessDAOIF.definesAttribute(DefaultAttribute.CODE.getName());
    }
    catch (DataNotFoundException e)
    {
      Assert.fail("Attribute that implements GeoObjectType.CODE does not exist.It should be defined on the business class");
    }
    
    // DefaultAttribute.CREATED_DATE - The create data on the GeoObject?
    try
    {
      mdBusinessDAOIF.definesAttribute(MdBusinessInfo.CREATE_DATE);
    }
    catch (DataNotFoundException e)
    {
      Assert.fail("Attribute that implements GeoObjectType.CREATED_DATE does not exist.It should be defined on the business class");
    }
    
    // DefaultAttribute.UPDATED_DATE - The update data on the GeoObject?
    try
    {
      mdBusinessDAOIF.definesAttribute(MdBusinessInfo.LAST_UPDATE_DATE);
    }
    catch (DataNotFoundException e)
    {
      Assert.fail("Attribute that implements GeoObjectType.LAST_UPDATE_DATE does not exist.It should be defined on the business class");
    }

    // DefaultAttribute.STATUS 
    try
    {
      mdBusinessDAOIF.definesAttribute(MdBusinessInfo.LAST_UPDATE_DATE);
    }
    catch (DataNotFoundException e)
    {
      Assert.fail("Attribute that implements GeoObjectType.LAST_UPDATE_DATE does not exist.It should be defined on the business class");
    }
  }

  
  @Test
  public void testUpdateGeoObjectType()
  {      
    
    RegistryAdapterServer registry = new RegistryAdapterServer();
    
    GeoObjectType province = MetadataFactory.newGeoObjectType(PROVINCE_CODE, GeometryType.POLYGON, "Province Test", "Some Description", false, registry);
    String gtJSON = province.toJSON().toString();
    
    String sessionId = this.logInAdmin();
    try
    {
      service.createGeoObjectType(sessionId, gtJSON);
    }
    finally
    {
      logOutAdmin(sessionId);
    }

    sessionId = this.logInAdmin();
    try
    {
      province = service.getGeoObjectTypes(sessionId, new String[]{PROVINCE_CODE})[0]; 
    
      province.setLocalizedLabel("Province Test 2");
      province.setLocalizedDescription("Some Description 2");

      gtJSON = province.toJSON().toString();
      service.updateGeoObjectType(sessionId, gtJSON);
      
      province = service.getGeoObjectTypes(sessionId, new String[]{PROVINCE_CODE})[0]; 
      
      Assert.assertEquals("Display label was not updated on a GeoObjectType", "Province Test 2", province.getLocalizedLabel());
      Assert.assertEquals("Description  was not updated on a GeoObjectType", "Some Description 2", province.getLocalizedDescription());
    }
    finally
    {
      logOutAdmin(sessionId);
    }

    sessionId = this.logInAdmin();
    try
    {
      service.deleteGeoObjectType(sessionId, PROVINCE_CODE);
    }
    finally
    {
      logOutAdmin(sessionId);
    }
  }  
  
  @Test
  public void testCreateHierarchyType()
  {      
    RegistryAdapterServer registry = new RegistryAdapterServer();
    
    // newGeoObjectType(PROVINCE_CODE, GeometryType.POLYGON, "Province", "", false, registry);
    
    HierarchyType reportingDivision = MetadataFactory.newHierarchyType(REPORTING_DIVISION_CODE, "Reporting Division", "The rporting division hieracy...", registry);
    String gtJSON = reportingDivision.toJSON().toString();
    
    String sessionId = this.logInAdmin();
    try
    {
      service.createHierarchyType(sessionId, gtJSON);
    }
    finally
    {
      logOutAdmin(sessionId);
    }
   
    sessionId = this.logInAdmin();
    try
    {
      HierarchyType[] hierarchies = service.getHierarchyTypes(sessionId, new String[]{REPORTING_DIVISION_CODE});

      Assert.assertNotNull("The created hierarchy was not returned", hierarchies);
      
      Assert.assertEquals("The wrong number of hierarchies were returned.", 1, hierarchies.length);

      HierarchyType hierarchy = hierarchies[0];
      
      Assert.assertEquals("", "Reporting Division", hierarchy.getLocalizedLabel());
    }
    finally
    {
      logOutAdmin(sessionId);
    }
    
    sessionId = this.logInAdmin();
    try
    {
      service.deleteHierarchyType(sessionId, REPORTING_DIVISION_CODE);
    }
    finally
    {
      logOutAdmin(sessionId);
    }
  }  
  
  @Test
  public void testUpdateHierarchyType()
  {      
    RegistryAdapterServer registry = new RegistryAdapterServer();
    
    // newGeoObjectType(PROVINCE_CODE, GeometryType.POLYGON, "Province", "", false, registry);
    
    HierarchyType reportingDivision = MetadataFactory.newHierarchyType(REPORTING_DIVISION_CODE, "Reporting Division", "The rporting division hieracy...", registry);
    String gtJSON = reportingDivision.toJSON().toString();
    
    String sessionId = this.logInAdmin();
    try
    {
      reportingDivision = service.createHierarchyType(sessionId, gtJSON);
      
      reportingDivision.setLocalizedLabel("Reporting Division 2");
      
      reportingDivision.setLocalizedDescription("The rporting division hieracy 2");
      
      gtJSON = reportingDivision.toJSON().toString();
     
      reportingDivision = service.updateHierarchyType(sessionId, gtJSON);
      
      Assert.assertNotNull("The created hierarchy was not returned", reportingDivision);
      Assert.assertEquals("", "Reporting Division 2", reportingDivision.getLocalizedLabel());
      Assert.assertEquals("", "The rporting division hieracy 2", reportingDivision.getLocalizedDescription());
    }
    finally
    {
      logOutAdmin(sessionId);
    }
 
    sessionId = this.logInAdmin();
    try
    {
      service.deleteHierarchyType(sessionId, REPORTING_DIVISION_CODE);
    }
    finally
    {
      logOutAdmin(sessionId);
    }
  }  
  
  
  @Test
  public void testAddToHierarchy()
  { 
    RegistryAdapterServer registry = new RegistryAdapterServer();
    
    GeoObjectType country = MetadataFactory.newGeoObjectType(COUNTRY_CODE, GeometryType.POLYGON, "Country Test", "Some Description", false, registry);
    
    GeoObjectType province = MetadataFactory.newGeoObjectType(PROVINCE_CODE, GeometryType.POLYGON, "Province Test", "Some Description", false, registry);
    
    GeoObjectType district = MetadataFactory.newGeoObjectType(DISTRICT_CODE, GeometryType.POLYGON, "District Test", "Some Description", false, registry);

    GeoObjectType village = MetadataFactory.newGeoObjectType(VILLAGE_CODE, GeometryType.POLYGON, "Village Test", "Some Description", false, registry);
    
    HierarchyType reportingDivision = MetadataFactory.newHierarchyType(REPORTING_DIVISION_CODE, "Reporting Division", "The rporting division hieracy...", registry);
    
    // Create the GeoObjectTypes
    String sessionId = this.logInAdmin();
    try
    {
      String gtJSON = country.toJSON().toString();
      country = service.createGeoObjectType(sessionId, gtJSON);
      
      gtJSON = province.toJSON().toString();
      province = service.createGeoObjectType(sessionId, gtJSON);
      
      gtJSON = district.toJSON().toString();
      district = service.createGeoObjectType(sessionId, gtJSON);
      
      gtJSON = village.toJSON().toString();
      village = service.createGeoObjectType(sessionId, gtJSON);
      
      String htJSON = reportingDivision.toJSON().toString();
      reportingDivision = service.createHierarchyType(sessionId, htJSON);
      
      
      Assert.assertEquals("HierarchyType \""+REPORTING_DIVISION_CODE+"\" should not have any GeoObjectTypes in the hierarchy", 0, reportingDivision.getRootGeoObjectTypes().size());
      
      reportingDivision = service.addToHierarchy(sessionId, reportingDivision.getCode(), Universal.ROOT, country.getCode());
      
      Assert.assertEquals("HierarchyType \""+REPORTING_DIVISION_CODE+"\" should have one root type", 1, reportingDivision.getRootGeoObjectTypes().size());
      
      HierarchyType.HierarchyNode countryNode = reportingDivision.getRootGeoObjectTypes().get(0);
      
      Assert.assertEquals("HierarchyType \""+REPORTING_DIVISION_CODE+"\" should have root of type", COUNTRY_CODE, countryNode.getGeoObjectType().getCode());
      
      Assert.assertEquals("GeoObjectType \""+COUNTRY_CODE+"\" should have no child", 0, countryNode.getChildren().size());
      
      reportingDivision = service.addToHierarchy(sessionId, reportingDivision.getCode(), country.getCode(), province.getCode());

      countryNode = reportingDivision.getRootGeoObjectTypes().get(0);
      
      Assert.assertEquals("GeoObjectType \""+COUNTRY_CODE+"\" should have one child", 1, countryNode.getChildren().size());
      
      HierarchyType.HierarchyNode provinceNode = countryNode.getChildren().get(0);
      
      Assert.assertEquals("GeoObjectType \""+COUNTRY_CODE+"\" should have a child of type", PROVINCE_CODE, provinceNode.getGeoObjectType().getCode());
      
      reportingDivision = service.addToHierarchy(sessionId, reportingDivision.getCode(), province.getCode(), district.getCode());
      
      countryNode = reportingDivision.getRootGeoObjectTypes().get(0);
      provinceNode = countryNode.getChildren().get(0);
      HierarchyType.HierarchyNode districtNode = provinceNode.getChildren().get(0);
      
      Assert.assertEquals("GeoObjectType \""+PROVINCE_CODE+"\" should have a child of type", DISTRICT_CODE, districtNode.getGeoObjectType().getCode()); 
      
      reportingDivision = service.addToHierarchy(sessionId, reportingDivision.getCode(), district.getCode(), village.getCode());
      
      countryNode = reportingDivision.getRootGeoObjectTypes().get(0);
      provinceNode = countryNode.getChildren().get(0);
      districtNode = provinceNode.getChildren().get(0);
      HierarchyType.HierarchyNode villageNode = districtNode.getChildren().get(0);
      
      Assert.assertEquals("GeoObjectType \""+DISTRICT_CODE+"\" should have a child of type", VILLAGE_CODE, villageNode.getGeoObjectType().getCode()); 
      
      
      reportingDivision = service.removeFromHierarchy(sessionId, reportingDivision.getCode(), district.getCode(), village.getCode());
      
      countryNode = reportingDivision.getRootGeoObjectTypes().get(0);
      provinceNode = countryNode.getChildren().get(0);
      districtNode = provinceNode.getChildren().get(0);
      
      Assert.assertEquals("GeoObjectType \""+DISTRICT_CODE+"\" should have no child", 0, districtNode.getChildren().size());
      
      reportingDivision = service.removeFromHierarchy(sessionId, reportingDivision.getCode(), province.getCode(), district.getCode());
      
      countryNode = reportingDivision.getRootGeoObjectTypes().get(0);
      provinceNode = countryNode.getChildren().get(0);

      Assert.assertEquals("GeoObjectType \""+PROVINCE_CODE+"\" should have no child", 0, provinceNode.getChildren().size());
      
      reportingDivision = service.removeFromHierarchy(sessionId, reportingDivision.getCode(), country.getCode(), province.getCode());
      
      countryNode = reportingDivision.getRootGeoObjectTypes().get(0);
      
      Assert.assertEquals("GeoObjectType \""+COUNTRY_CODE+"\" should have no child", 0, countryNode.getChildren().size());
      
      reportingDivision = service.removeFromHierarchy(sessionId, reportingDivision.getCode(), Universal.ROOT, country.getCode());
      
      Assert.assertEquals("HierarchyType \""+REPORTING_DIVISION_CODE+"\" should not have any GeoObjectTypes in the hierarchy", 0, reportingDivision.getRootGeoObjectTypes().size());
      
    }
    finally
    {
      logOutAdmin(sessionId);
    }

    

    sessionId = this.logInAdmin();
    try
    {
      service.deleteGeoObjectType(sessionId, VILLAGE_CODE);
      
      service.deleteGeoObjectType(sessionId, DISTRICT_CODE);
      
      service.deleteGeoObjectType(sessionId, PROVINCE_CODE);
      
      service.deleteGeoObjectType(sessionId, COUNTRY_CODE);
      
      service.deleteHierarchyType(sessionId, REPORTING_DIVISION_CODE);
    }
    finally
    {
      logOutAdmin(sessionId);
    }
  }
  
  @Test
  public void testHierarchyType()
  {  
    String sessionId = this.logInAdmin();

    try
    {
      HierarchyType[] hierarchyTypes = service.getHierarchyTypes(sessionId, null);

      for (HierarchyType hierarchyType : hierarchyTypes)
      {
System.out.println(hierarchyType.toJSON());
      }
    }
    finally
    {
      logOutAdmin(sessionId);
    }

  }  
  
  /**
   * Logs in admin user and returns session id of the user.
   * 
   * @return
   */
  @Request
  private String logInAdmin()
  {
    return SessionFacade.logIn(USERNAME, PASSWORD, new Locale[] { CommonProperties.getDefaultLocale() });
  }
  
  /**
   * Log out the admin user with the given session id.
   * 
   * @param sessionId
   */
  @Request
  private void logOutAdmin(String sessionId)
  {
    SessionFacade.closeSession(sessionId);
  }

  
}