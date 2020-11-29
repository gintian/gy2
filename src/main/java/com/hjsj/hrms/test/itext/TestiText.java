/**
 * 
 */
package com.hjsj.hrms.test.itext;


import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.utils.PubFunc;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 11, 20068:49:17 AM
 * @author chenmengqing
 * @version 4.0
 */
public class TestiText {

	public TestiText() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        System.out.println("Text at absolute positions");
        
        // step 1: creation of a document-object
        Document document = new Document();
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("c://text.pdf");
            // step 2: creation of the writer
            PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);
            
            // step 3: we open the document
            document.open();
            
            // step 4: we grab the ContentByte and do some stuff with it
            PdfContentByte cb = writer.getDirectContent();
            
            // first we draw some lines to be able to visualize the text alignment functions
            cb.setLineWidth(0f);
            cb.moveTo(250, 500);
            cb.lineTo(250, 800);
            cb.moveTo(50, 700);
            cb.lineTo(400, 700);
            cb.moveTo(50, 650);
            cb.lineTo(400, 650);
            cb.moveTo(50, 600);
            cb.lineTo(400, 600);
            cb.stroke();
            
            // we tell the ContentByte we're ready to draw text
            cb.beginText();
            
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            cb.setFontAndSize(bf, 12);
            String text = "Sample text for alignment";
            // we show some text starting on some absolute position with a given alignment
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text + " Center", 250, 700, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, text + " Right", 250, 650, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text + " Left", 250, 600, 0);
            
            // we draw some text on a certain position
            cb.setTextMatrix(100, 400);
            cb.showText("Text at position 100,400.");
            
            // we draw some rotated text on a certain position
            cb.setTextMatrix(0, 1, -1, 0, 100, 300);
            cb.showText("Text at position 100,300, rotated 90 degrees.");
            
            // we draw some mirrored, rotated text on a certain position
            cb.setTextMatrix(0, 1, 1, 0, 200, 200);
            cb.showText("Text at position 200,200, mirrored and rotated 90 degrees.");
            
            // we tell the contentByte, we've finished drawing text
            cb.endText();
            

			Paragraph para=new Paragraph("人世ss\nfasdfasd",FontFamilyType.getFont("宋体",1,12));
			para.setAlignment(Element.ALIGN_TOP);
			Paragraph para1=new Paragraph("人世",FontFamilyType.getFont("宋体",1,12));
			para.setAlignment(Element.ALIGN_TOP);
			para1.add(para);
			//cell.addElement(para);
			PdfPCell cell=new PdfPCell(para1);
	        cell.setFixedHeight(50);

            
            
            
            
            PdfPTable table = new PdfPTable(1);
    		table.setTotalWidth(100); // 设置表的总体宽度
    		table.setLockedWidth(true); // 宽度锁定
            
			//Paragraph para1=new Paragraph("木器厂 要林",FontFamilyType.getFont("宋体",1,12));
			//cell.addElement(para1);
	        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

	        //			cell.setTop(1);
//			cell.setBottom(1);
//			cell.setLeft(1);
//			cell.setRight(1);
//			cell.setBorderColor(Color.RED);
//	        cell.setNoWrap(false);//
	      
	       // table.setLockedWidth(true);
	        table.addCell(cell);
	        table.writeSelectedRows(0, 1,0,100,writer.getDirectContent());    //固定坐标
			
        }
        catch(DocumentException de) {
            System.err.println(de.getMessage());
        }
        catch(IOException ioe) {
            System.err.println(ioe.getMessage());
        }finally{
        	PubFunc.closeIoResource(fileOutputStream);
        }
        
        // step 5: we close the document
        document.close();

	}

}
