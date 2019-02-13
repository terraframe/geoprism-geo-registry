package net.geoprism.georegistry.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collection;
import java.util.Date;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.commongeoregistry.adapter.constants.GeometryType;
import org.commongeoregistry.adapter.dataaccess.GeoObject;
import org.commongeoregistry.adapter.metadata.AttributeTermType;
import org.commongeoregistry.adapter.metadata.AttributeType;
import org.commongeoregistry.adapter.metadata.GeoObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.runwaysdk.query.OIterator;
import com.vividsolutions.jts.geom.Point;

import net.geoprism.georegistry.io.GeoObjectConfiguration;
import net.geoprism.georegistry.io.GeoObjectUtil;
import net.geoprism.georegistry.io.ImportAttributeSerializer;

public class GeoObjectExcelExporter
{
  private static Logger        logger = LoggerFactory.getLogger(GeoObjectExcelExporter.class);

  private GeoObjectType        type;

  private OIterator<GeoObject> objects;

  public GeoObjectExcelExporter(GeoObjectType type, OIterator<GeoObject> objects)
  {
    this.type = type;
    this.objects = objects;
  }

  public GeoObjectType getType()
  {
    return type;
  }

  public void setType(GeoObjectType type)
  {
    this.type = type;
  }

  public Iterable<GeoObject> getObjects()
  {
    return objects;
  }

  public void setObjects(OIterator<GeoObject> objects)
  {
    this.objects = objects;
  }

  public Workbook createWorkbook() throws IOException
  {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(this.type.getLocalizedLabel()));

    CreationHelper createHelper = workbook.getCreationHelper();
    Font font = workbook.createFont();
    font.setBold(true);

    CellStyle boldStyle = workbook.createCellStyle();
    boldStyle.setFont(font);

    CellStyle dateStyle = workbook.createCellStyle();
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(14)));

    Row header = sheet.createRow(0);

    boolean includeCoordinates = this.type.getGeometryType().equals(GeometryType.POINT);
    Collection<AttributeType> attributes = new ImportAttributeSerializer(includeCoordinates, true).attributes(this.type);

    int col = 0;

    for (AttributeType attribute : attributes)
    {
      Cell cell = header.createCell(col++);
      cell.setCellStyle(boldStyle);
      cell.setCellValue(attribute.getLocalizedLabel());
    }

    int rownum = 1;

    while (this.objects.hasNext())
    {
      GeoObject object = this.objects.next();

      col = 0;

      Row row = sheet.createRow(rownum++);

      for (AttributeType attribute : attributes)
      {
        String name = attribute.getName();
        Cell cell = row.createCell(col++);

        if (name.equals(GeoObjectConfiguration.LATITUDE))
        {
          Point point = (Point) object.getGeometry();

          if (point != null)
          {
            cell.setCellValue(point.getY());
          }
        }
        else if (name.equals(GeoObjectConfiguration.LONGITUDE))
        {
          Point point = (Point) object.getGeometry();
          if (point != null)
          {
            cell.setCellValue(point.getX());
          }
        }
        else
        {
          Object value = object.getValue(name);

          if (value != null)
          {
            if (attribute instanceof AttributeTermType)
            {
              cell.setCellValue(GeoObjectUtil.convertToTermString((AttributeTermType) attribute, value));
            }
            else
            {
              if (value instanceof String)
              {
                cell.setCellValue((String) value);
              }
              else if (value instanceof Date)
              {
                cell.setCellValue((Date) value);
                cell.setCellStyle(dateStyle);
              }
              else if (value instanceof Number)
              {
                cell.setCellValue( ( (Number) value ).doubleValue());
              }
              else if (value instanceof Boolean)
              {
                cell.setCellValue((Boolean) value);
              }
            }
          }
        }
      }
    }

    return workbook;
  }

  public InputStream export() throws IOException
  {
    final Workbook workbook = this.createWorkbook();

    PipedOutputStream pos = new PipedOutputStream();
    PipedInputStream pis = new PipedInputStream(pos);

    Thread t = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          try
          {
            workbook.write(pos);
          }
          finally
          {
            pos.close();
          }
        }
        catch (IOException e)
        {
          logger.error("Error while writing the workbook", e);
        }
      }
    });
    t.setDaemon(true);
    t.start();

    return pis;
  }

}