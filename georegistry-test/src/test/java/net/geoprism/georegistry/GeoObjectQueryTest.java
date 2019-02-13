package net.geoprism.georegistry;

import java.util.List;

import org.commongeoregistry.adapter.Term;
import org.commongeoregistry.adapter.constants.GeometryType;
import org.commongeoregistry.adapter.dataaccess.GeoObject;
import org.commongeoregistry.adapter.metadata.GeoObjectType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.runwaysdk.constants.ClientRequestIF;
import com.runwaysdk.query.OIterator;
import com.runwaysdk.session.Request;
import com.runwaysdk.system.gis.geo.LocatedIn;
import com.runwaysdk.system.gis.geo.Synonym;
import com.runwaysdk.system.gis.geo.Universal;
import com.runwaysdk.system.metadata.MdTermRelationship;

import net.geoprism.georegistry.io.SynonymRestriction;
import net.geoprism.georegistry.query.CodeRestriction;
import net.geoprism.georegistry.query.GeoObjectQuery;
import net.geoprism.georegistry.query.LookupRestriction;
import net.geoprism.georegistry.query.OidRestrction;
import net.geoprism.georegistry.query.UidRestriction;
import net.geoprism.georegistry.service.ServiceFactory;
import net.geoprism.georegistry.testframework.USATestData;
import net.geoprism.registry.GeoObjectStatus;

public class GeoObjectQueryTest
{
  protected USATestData     tutil;

  protected ClientRequestIF adminCR;

  @Before
  public void setUp()
  {
    this.tutil = USATestData.newTestData();

    this.adminCR = tutil.adminClientRequest;
  }

  @After
  public void tearDown()
  {
    if (this.tutil != null)
    {
      tutil.cleanUp();
    }
  }

  @Test
  @Request
  public void testQueryTreeNodes()
  {
    GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.STATE.getUniversal();
    GeoObjectQuery query = new GeoObjectQuery(type, universal);

    OIterator<GeoObject> it = query.getIterator();

    try
    {
      List<GeoObject> results = it.getAll();

      Assert.assertEquals(2, results.size());

      GeoObject result = results.get(0);

      Assert.assertEquals(this.tutil.COLORADO.getCode(), result.getCode());
      Assert.assertNotNull(result.getGeometry());
      Assert.assertEquals(this.tutil.COLORADO.getDisplayLabel(), result.getLocalizedDisplayLabel());

      Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
      Assert.assertEquals(expected, result.getStatus());
    }
    finally
    {
      it.close();
    }

  }

  @Test
  @Request
  public void testQueryLeafNodes()
  {
    GeoObjectType type = this.tutil.DISTRICT.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.DISTRICT.getUniversal();
    GeoObjectQuery query = new GeoObjectQuery(type, universal);

    OIterator<GeoObject> it = query.getIterator();

    try
    {
      List<GeoObject> results = it.getAll();

      Assert.assertEquals(5, results.size());

      GeoObject result = results.get(0);

      Assert.assertEquals(this.tutil.CO_D_ONE.getCode(), result.getCode());
      Assert.assertNotNull(result.getGeometry());
      Assert.assertEquals(this.tutil.CO_D_ONE.getDisplayLabel(), result.getLocalizedDisplayLabel());

      Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
      Assert.assertEquals(expected, result.getStatus());
    }
    finally
    {
      it.close();
    }
  }

  @Test
  @Request
  public void testTreeUidRestriction()
  {
    GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.STATE.getUniversal();

    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(new UidRestriction(this.tutil.COLORADO.getRegistryId()));

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.COLORADO.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.COLORADO.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testLeafUidRestriction()
  {
    GeoObjectType type = this.tutil.DISTRICT.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.DISTRICT.getUniversal();
    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(new UidRestriction(this.tutil.CO_D_ONE.getRegistryId()));

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.CO_D_ONE.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.CO_D_ONE.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testTreeOidRestriction()
  {
    GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.STATE.getUniversal();

    String oid = ServiceFactory.getIdService().registryIdToRunwayId(this.tutil.COLORADO.getRegistryId(), type);

    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(new OidRestrction(oid));

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.COLORADO.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.COLORADO.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testLeafOidRestriction()
  {
    GeoObjectType type = this.tutil.DISTRICT.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.DISTRICT.getUniversal();

    String oid = ServiceFactory.getIdService().registryIdToRunwayId(this.tutil.CO_D_ONE.getRegistryId(), type);

    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(new OidRestrction(oid));

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.CO_D_ONE.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.CO_D_ONE.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testTreeCodeRestriction()
  {
    GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.STATE.getUniversal();

    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(new CodeRestriction(this.tutil.COLORADO.getCode()));

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.COLORADO.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.COLORADO.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testLeafCodeRestriction()
  {
    GeoObjectType type = this.tutil.DISTRICT.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.DISTRICT.getUniversal();
    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(new CodeRestriction(this.tutil.CO_D_ONE.getCode()));

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.CO_D_ONE.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.CO_D_ONE.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testTreeSynonymRestrictionBySynonym()
  {
    String label = "TEST";

    Synonym synonym = new Synonym();
    synonym.getDisplayLabel().setValue(label);

    Synonym.create(synonym, this.tutil.COLORADO.getGeoEntity().getOid());

    try
    {
      GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
      Universal universal = this.tutil.STATE.getUniversal();

      GeoObjectQuery query = new GeoObjectQuery(type, universal);
      query.setRestriction(new SynonymRestriction(label));

      GeoObject result = query.getSingleResult();

      Assert.assertEquals(this.tutil.COLORADO.getCode(), result.getCode());
      Assert.assertNotNull(result.getGeometry());
      Assert.assertEquals(this.tutil.COLORADO.getDisplayLabel(), result.getLocalizedDisplayLabel());

      Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
      Assert.assertEquals(expected, result.getStatus());
    }
    finally
    {
      synonym.delete();
    }
  }

  @Test
  @Request
  public void testTreeSynonymRestrictionByDisplayLabel()
  {
    GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.STATE.getUniversal();

    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(new SynonymRestriction(this.tutil.COLORADO.getDisplayLabel()));

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.COLORADO.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.COLORADO.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testTreeSynonymRestrictionByCode()
  {
    GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.STATE.getUniversal();

    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(new SynonymRestriction(this.tutil.COLORADO.getCode()));

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.COLORADO.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.COLORADO.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testTreeSynonymRestrictionByCodeWithParent()
  {
    GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.STATE.getUniversal();

    MdTermRelationship mdRelationship = MdTermRelationship.getByKey(LocatedIn.CLASS);
    SynonymRestriction restriction = new SynonymRestriction(this.tutil.COLORADO.getCode(), this.tutil.USA.asGeoObject(), mdRelationship);

    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(restriction);

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.COLORADO.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.COLORADO.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testFailTreeSynonymRestrictionByCodeWithParent()
  {
    GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.STATE.getUniversal();

    MdTermRelationship mdRelationship = MdTermRelationship.getByKey(LocatedIn.CLASS);
    SynonymRestriction restriction = new SynonymRestriction(this.tutil.COLORADO.getCode(), this.tutil.CANADA.asGeoObject(), mdRelationship);

    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(restriction);

    GeoObject result = query.getSingleResult();

    Assert.assertNull(result);
  }

  @Test
  @Request
  public void testLeafSynonymRestriction()
  {
    GeoObjectType type = this.tutil.DISTRICT.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.DISTRICT.getUniversal();
    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(new SynonymRestriction(this.tutil.CO_D_ONE.getDisplayLabel()));

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.CO_D_ONE.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.CO_D_ONE.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testLookupRestriction()
  {
    GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.STATE.getUniversal();

    LookupRestriction restriction = new LookupRestriction("Co", this.tutil.USA.getCode(), LocatedIn.class.getSimpleName());

    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(restriction);

    GeoObject result = query.getSingleResult();

    Assert.assertEquals(this.tutil.COLORADO.getCode(), result.getCode());
    Assert.assertNotNull(result.getGeometry());
    Assert.assertEquals(this.tutil.COLORADO.getDisplayLabel(), result.getLocalizedDisplayLabel());

    Term expected = ServiceFactory.getConversionService().geoObjectStatusToTerm(GeoObjectStatus.ACTIVE);
    Assert.assertEquals(expected, result.getStatus());
  }

  @Test
  @Request
  public void testFailLookupRestriction()
  {
    GeoObjectType type = this.tutil.STATE.getGeoObjectType(GeometryType.POLYGON);
    Universal universal = this.tutil.STATE.getUniversal();

    LookupRestriction restriction = new LookupRestriction("Co", this.tutil.CANADA.getCode(), LocatedIn.class.getSimpleName());

    GeoObjectQuery query = new GeoObjectQuery(type, universal);
    query.setRestriction(restriction);

    GeoObject result = query.getSingleResult();

    Assert.assertNull(result);
  }

}