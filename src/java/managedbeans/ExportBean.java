
package managedbeans;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.export.PDFOptions;


@Named(value = "exportb")
@RequestScoped
public class ExportBean {
  private PDFOptions pdfOpt;

  @PostConstruct
  public void init() {
    pdfOpt = new PDFOptions();
    pdfOpt.setFacetFontSize("8");
    pdfOpt.setCellFontSize("8");
  }
  
  public PDFOptions getPdfOpt() {
    return pdfOpt;
  }
  
  //Returns filename when table is exported
  public String getFileName(){
    return String.valueOf("HRSchemaEmployees_"+Instant.now().toEpochMilli());
  }
  
  public void preProcessPDF(Object document) {
      Document pdf = (Document) document;
      pdf.setPageSize(PageSize.A4.rotate());
      pdf.open();
    }
  
  public void postProcessXLSX(Object document) {

        XSSFWorkbook wb = (XSSFWorkbook) document;
        XSSFSheet sheet = wb.getSheetAt(0);
        int[] columnsType = {1,2,2,2,2,3,2,1,1,1,1};//1:integer, 2:string, 3:date
        LocalDate date;
        DateTimeFormatter dateform = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        CellStyle style = wb.createCellStyle();
        style.setDataFormat((short)0xe);

        Iterator<Row> rowIterator = sheet.iterator();

        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                    if (!cell.getStringCellValue().isEmpty()) {
                      switch (columnsType[cell.getColumnIndex()]){
                        case 1: 
                          cell.setCellValue(Integer.valueOf(cell.getStringCellValue().replace("'", "").replace(",", "")));
                          break;
                        case 3:
                          date=LocalDate.parse(cell.getStringCellValue().replace("'", ""),dateform);
                          cell.setCellValue(java.sql.Date.valueOf(date));
                          
                          cell.setCellStyle(style);
                          break;
                      }
                    }
            }
        }
  }
  
}
