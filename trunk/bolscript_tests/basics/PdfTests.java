package basics;

import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JLabel;

import bolscript.config.Config;
import bolscript.config.UserConfig;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;


 class PdfTests {
	
	
	public static void main(String[] args) {
		Config.init();
		//Debug.temporary(, "compPanel createPDF started");
		// step 1: creation of a document-object
		Document document = new Document();
		try {
			// step 2:
			// we create a writer
			PdfWriter writer;
			
			writer = PdfWriter.getInstance(document, new FileOutputStream(UserConfig.libraryFolder +Config.fileSeperator  + "pdf_test.pdf"));
			Rectangle pageSize = writer.getPageSize();
			
			float pageWidth =  pageSize.getWidth();
			float pageHeight =  pageSize.getHeight();
			// step 3: we open the document
			document.open();
			// step 4: we add a table to the document
			/*
			PdfContentByte cb = writer.getDirectContent();
			Rectangle pageSize = writer.getPageSize();
			
			float pageWidth =  pageSize.getWidth();
			float pageHeight =  pageSize.getHeight();
			
			//Graphics2D g = cb.createGraphics(pageWidth, pageHeight);
			PdfTemplate template = cb.createTemplate(pageWidth, pageHeight);
			template.setColorStroke(Color.red);
			template.circle(0, 0, 10);
			template.circle(100,100, 20);
			template.circle(200,200, 30);
			template.setColorStroke(Color.blue);
			template.circle(-100,-100, 20);
			template.circle(-200,-200, 30);
			
			cb.addTemplate(template, 0, -100);*/
			document.add(new Paragraph("Hello World"));
			java.awt.Font font = new java.awt.Font("arial", 0, 18);
			PdfContentByte cb = writer.getDirectContent();
			
			
			java.awt.Graphics2D g2 =
			    cb.createGraphicsShapes(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			g2.setFont(font);
			g2.drawString("bla bla", 100, 100);
			g2.dispose();
			
			JLabel label;
		    label = new JLabel("other");
		    Dimension labelDim = GUI.getPrefferedSize(label, 1000);
		    label.setBounds(0,0, GUI.getPrefferedSize(label, 100).width, GUI.getPrefferedSize(label, 100).height);
			PdfTemplate tp = cb.createTemplate(labelDim.width*2, labelDim.height*2);
		    g2 = tp.createGraphics(labelDim.width*2, labelDim.height*2, new DefaultFontMapper());
		    g2.scale(2,2);
		    label.print(g2);
			
			g2.dispose();
			cb.addTemplate(tp, 0, 0);//pageSize.getHeight()-labelDim.height);
			document.close();
			
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		System.exit(0);
		// step 5: we close the document
		
	}
}
