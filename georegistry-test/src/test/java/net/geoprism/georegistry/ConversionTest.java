package net.geoprism.georegistry;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

import org.commongeoregistry.adapter.Term;
import org.commongeoregistry.adapter.constants.GeometryType;
import org.commongeoregistry.adapter.dataaccess.GeoObject;
import org.commongeoregistry.adapter.metadata.AttributeBooleanType;
import org.commongeoregistry.adapter.metadata.AttributeCharacterType;
import org.commongeoregistry.adapter.metadata.AttributeDateType;
import org.commongeoregistry.adapter.metadata.AttributeFloatType;
import org.commongeoregistry.adapter.metadata.AttributeIntegerType;
import org.commongeoregistry.adapter.metadata.AttributeTermType;
import org.commongeoregistry.adapter.metadata.AttributeType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.runwaysdk.constants.ClientRequestIF;

import junit.framework.Assert;
import net.geoprism.georegistry.service.ServiceFactory;
import net.geoprism.georegistry.testframework.USATestData;

public class ConversionTest
{
  protected USATestData     testData;

  protected ClientRequestIF adminCR;

  @Before
  public void setUp()
  {
    this.testData = USATestData.newTestData(GeometryType.MULTIPOLYGON, false);

    this.adminCR = testData.adminClientRequest;
  }

  @After
  public void tearDown() throws IOException
  {
    testData.cleanUp();
  }

  @Test
  public void testAttributeTypeDateTree()
  {
    String sessionId = this.adminCR.getSessionId();

    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(2019, Calendar.JANUARY, 12, 20, 21, 32);

    // Add a new custom attribute
    AttributeType testDate = AttributeType.factory("testDate", "testDateLocalName", "testDateLocalDescrip", AttributeDateType.TYPE);
    testDate = ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.STATE.getCode(), testDate.toJSON().toString());

    // Create a new GeoObject with the custom attribute
    GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.STATE.getCode());
    geoObj.setCode("000");
    geoObj.setLocalizedDisplayLabel("Test Label");
    geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
    geoObj.setValue(testDate.getName(), calendar.getTime());

    ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

    // Get the object with the custom attribute
    GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.STATE.getCode());

    Assert.assertNotNull(result);
    Assert.assertEquals(geoObj.getValue(testDate.getName()), result.getValue(testDate.getName()));
  }

  @Test
  public void testAttributeTypeDateLeaf()
  {
    String sessionId = this.adminCR.getSessionId();

    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(2019, Calendar.JANUARY, 12, 20, 21, 32);

    // Add a new custom attribute
    AttributeType testDate = AttributeType.factory("testDate", "testDateLocalName", "testDateLocalDescrip", AttributeDateType.TYPE);
    testDate = ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.DISTRICT.getCode(), testDate.toJSON().toString());

    // Create a new GeoObject with the custom attribute
    GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.DISTRICT.getCode());
    geoObj.setCode("000");
    geoObj.setLocalizedDisplayLabel("Test Label");
    geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
    geoObj.setValue(testDate.getName(), calendar.getTime());

    ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

    // Get the object with the custom attribute
    GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.DISTRICT.getCode());

    Assert.assertNotNull(result);
    Assert.assertEquals(geoObj.getValue(testDate.getName()), result.getValue(testDate.getName()));
  }

  @Test
  public void testAttributeTypeBooleanTree()
  {
    String sessionId = this.adminCR.getSessionId();

    // Add a new custom attribute
    AttributeType testBoolean = AttributeType.factory("testBoolean", "testBooleanLocalName", "testBooleanLocalDescrip", AttributeBooleanType.TYPE);
    testBoolean = ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.STATE.getCode(), testBoolean.toJSON().toString());

    // Create a new GeoObject with the custom attribute
    GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.STATE.getCode());
    geoObj.setCode("000");
    geoObj.setLocalizedDisplayLabel("Test Label");
    geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
    geoObj.setValue(testBoolean.getName(), new Boolean(true));

    ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

    // Get the object with the custom attribute
    GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.STATE.getCode());

    Assert.assertNotNull(result);
    Assert.assertEquals(geoObj.getValue(testBoolean.getName()), result.getValue(testBoolean.getName()));
  }

  @Test
  public void testAttributeTypeBooleanLeaf()
  {
    String sessionId = this.adminCR.getSessionId();

    // Add a new custom attribute
    AttributeType testBoolean = AttributeType.factory("testBoolean", "testBooleanLocalName", "testBooleanLocalDescrip", AttributeBooleanType.TYPE);
    testBoolean = ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.DISTRICT.getCode(), testBoolean.toJSON().toString());

    // Create a new GeoObject with the custom attribute
    GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.DISTRICT.getCode());
    geoObj.setCode("000");
    geoObj.setLocalizedDisplayLabel("Test Label");
    geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
    geoObj.setValue(testBoolean.getName(), new Boolean(true));

    ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

    // Get the object with the custom attribute
    GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.DISTRICT.getCode());

    Assert.assertNotNull(result);
    Assert.assertEquals(geoObj.getValue(testBoolean.getName()), result.getValue(testBoolean.getName()));
  }

  @Test
  public void testAttributeTypeFloatTree()
  {
    String sessionId = this.adminCR.getSessionId();

    // Add a new custom attribute
    AttributeType testFloat = AttributeType.factory("testFloat", "testFloatLocalName", "testFloatLocalDescrip", AttributeFloatType.TYPE);
    testFloat = ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.STATE.getCode(), testFloat.toJSON().toString());

    // Create a new GeoObject with the custom attribute
    GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.STATE.getCode());
    geoObj.setCode("000");
    geoObj.setLocalizedDisplayLabel("Test Label");
    geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
    geoObj.setValue(testFloat.getName(), new Double(234.2));

    ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

    // Get the object with the custom attribute
    GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.STATE.getCode());

    Assert.assertNotNull(result);
    Assert.assertEquals(geoObj.getValue(testFloat.getName()), result.getValue(testFloat.getName()));
  }

  @Test
  public void testAttributeTypeFloatLeaf()
  {
    String sessionId = this.adminCR.getSessionId();

    // Add a new custom attribute
    AttributeType testFloat = AttributeType.factory("testFloat", "testFloatLocalName", "testFloatLocalDescrip", AttributeFloatType.TYPE);
    testFloat = ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.DISTRICT.getCode(), testFloat.toJSON().toString());

    // Create a new GeoObject with the custom attribute
    GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.DISTRICT.getCode());
    geoObj.setCode("000");
    geoObj.setLocalizedDisplayLabel("Test Label");
    geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
    geoObj.setValue(testFloat.getName(), new Double(234.2));

    ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

    // Get the object with the custom attribute
    GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.DISTRICT.getCode());

    Assert.assertNotNull(result);
    Assert.assertEquals(geoObj.getValue(testFloat.getName()), result.getValue(testFloat.getName()));
  }

  @Test
  public void testAttributeTypeIntegerTree()
  {
    String sessionId = this.adminCR.getSessionId();

    // Add a new custom attribute
    AttributeType testInteger = AttributeType.factory("testInteger", "testIntegerLocalName", "testIntegerLocalDescrip", AttributeIntegerType.TYPE);
    testInteger = ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.STATE.getCode(), testInteger.toJSON().toString());

    // Create a new GeoObject with the custom attribute
    GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.STATE.getCode());
    geoObj.setCode("000");
    geoObj.setLocalizedDisplayLabel("Test Label");
    geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
    geoObj.setValue(testInteger.getName(), new Long(123));

    ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

    // Get the object with the custom attribute
    GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.STATE.getCode());

    Assert.assertNotNull(result);
    Assert.assertEquals(geoObj.getValue(testInteger.getName()), result.getValue(testInteger.getName()));
  }

  @Test
  public void testAttributeTypeIntegerLeaf()
  {
    String sessionId = this.adminCR.getSessionId();

    // Add a new custom attribute
    AttributeType testInteger = AttributeType.factory("testInteger", "testIntegerLocalName", "testIntegerLocalDescrip", AttributeIntegerType.TYPE);
    testInteger = ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.DISTRICT.getCode(), testInteger.toJSON().toString());

    // Create a new GeoObject with the custom attribute
    GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.DISTRICT.getCode());
    geoObj.setCode("000");
    geoObj.setLocalizedDisplayLabel("Test Label");
    geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
    geoObj.setValue(testInteger.getName(), new Long(234));

    ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

    // Get the object with the custom attribute
    GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.DISTRICT.getCode());

    Assert.assertNotNull(result);
    Assert.assertEquals(geoObj.getValue(testInteger.getName()), result.getValue(testInteger.getName()));
  }

  @Test
  public void testAttributeTypeCharacterTree()
  {
    String sessionId = this.adminCR.getSessionId();

    // Add a new custom attribute
    AttributeType testCharacter = AttributeType.factory("testCharacter", "testCharacterLocalName", "testCharacterLocalDescrip", AttributeCharacterType.TYPE);
    testCharacter = ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.STATE.getCode(), testCharacter.toJSON().toString());

    // Create a new GeoObject with the custom attribute
    GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.STATE.getCode());
    geoObj.setCode("000");
    geoObj.setLocalizedDisplayLabel("Test Label");
    geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
    geoObj.setValue(testCharacter.getName(), "ABC");

    ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

    // Get the object with the custom attribute
    GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.STATE.getCode());

    Assert.assertNotNull(result);
    Assert.assertEquals(geoObj.getValue(testCharacter.getName()), result.getValue(testCharacter.getName()));
  }

  @Test
  public void testAttributeTypeCharacterLeaf()
  {
    String sessionId = this.adminCR.getSessionId();

    // Add a new custom attribute
    AttributeType testCharacter = AttributeType.factory("testCharacter", "testCharacterLocalName", "testCharacterLocalDescrip", AttributeCharacterType.TYPE);
    testCharacter = ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.DISTRICT.getCode(), testCharacter.toJSON().toString());

    // Create a new GeoObject with the custom attribute
    GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.DISTRICT.getCode());
    geoObj.setCode("000");
    geoObj.setLocalizedDisplayLabel("Test Label");
    geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
    geoObj.setValue(testCharacter.getName(), "ABCZ");

    ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

    // Get the object with the custom attribute
    GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.DISTRICT.getCode());

    Assert.assertNotNull(result);
    Assert.assertEquals(geoObj.getValue(testCharacter.getName()), result.getValue(testCharacter.getName()));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testAttributeTypeTermTree()
  {
    String sessionId = this.adminCR.getSessionId();

    // Add a new custom attribute
    AttributeTermType testTerm = (AttributeTermType) AttributeType.factory("testTerm", "testTermLocalName", "testTermLocalDescrip", AttributeTermType.TYPE);
    testTerm = (AttributeTermType) ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.STATE.getCode(), testTerm.toJSON().toString());
    Term rootTerm = testTerm.getRootTerm();

    Term term = ServiceFactory.getRegistryService().createTerm(sessionId, rootTerm.getCode(), new Term("termValue2", "Term Value 2", "").toJSON().toString());

    this.testData.refreshTerms(testTerm);

    try
    {
      // Create a new GeoObject with the custom attribute
      GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.STATE.getCode());
      geoObj.setCode("000");
      geoObj.setLocalizedDisplayLabel("Test Label");
      geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
      geoObj.setValue(testTerm.getName(), term.getCode());

      ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

      // Get the object with the custom attribute
      GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.STATE.getCode());

      Assert.assertNotNull(result);
      Iterator<String> expected = (Iterator<String>) geoObj.getValue(testTerm.getName());
      Iterator<String> test = (Iterator<String>) result.getValue(testTerm.getName());

      Assert.assertTrue(expected.hasNext());
      Assert.assertTrue(test.hasNext());

      Assert.assertEquals(expected.next(), test.next());
    }
    finally
    {
      ServiceFactory.getRegistryService().deleteTerm(sessionId, term.getCode());
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testAttributeTypeTermLeaf()
  {
    String sessionId = this.adminCR.getSessionId();

    // Add a new custom attribute
    AttributeTermType testTerm = (AttributeTermType) AttributeType.factory("testTerm", "testTermLocalName", "testTermLocalDescrip", AttributeTermType.TYPE);
    testTerm = (AttributeTermType) ServiceFactory.getRegistryService().createAttributeType(sessionId, this.testData.DISTRICT.getCode(), testTerm.toJSON().toString());
    Term rootTerm = testTerm.getRootTerm();

    Term term = ServiceFactory.getRegistryService().createTerm(sessionId, rootTerm.getCode(), new Term("termValue2", "Term Value 2", "").toJSON().toString());
    
    this.testData.refreshTerms(testTerm);

    try
    {
      // Create a new GeoObject with the custom attribute
      GeoObject geoObj = ServiceFactory.getRegistryService().newGeoObjectInstance(sessionId, this.testData.DISTRICT.getCode());
      geoObj.setCode("000");
      geoObj.setLocalizedDisplayLabel("Test Label");
      geoObj.setUid(ServiceFactory.getRegistryService().getUIDS(sessionId, 1)[0]);
      geoObj.setValue(testTerm.getName(), term.getCode());

      ServiceFactory.getRegistryService().createGeoObject(sessionId, geoObj.toJSON().toString());

      // Get the object with the custom attribute
      GeoObject result = ServiceFactory.getRegistryService().getGeoObjectByCode(sessionId, "000", this.testData.DISTRICT.getCode());

      Assert.assertNotNull(result);
      Iterator<String> expected = (Iterator<String>) geoObj.getValue(testTerm.getName());
      Iterator<String> test = (Iterator<String>) result.getValue(testTerm.getName());

      Assert.assertTrue(expected.hasNext());
      Assert.assertTrue(test.hasNext());

      Assert.assertEquals(expected.next(), test.next());
    }
    finally
    {
      ServiceFactory.getRegistryService().deleteTerm(sessionId, term.getCode());
    }
  }
}