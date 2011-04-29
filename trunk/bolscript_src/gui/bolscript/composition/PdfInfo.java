package gui.bolscript.composition;

import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;

import basics.Debug;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

public class PdfInfo {

	static Insets defaultPdfPageMargins = new Insets(20,10,30,10);

	private double pdfPageWidth, pdfPageHeight;	
	private Insets pdfPageMargins;

	/**
	 * Multiply a pixel with scalingFactor, to get its size in pdf scale.
	 */
	private double scalingFactor;		

	public PdfInfo(Rectangle pdfPageSize, Insets pdfPageMargins, Dimension panelSize) {
		this.pdfPageWidth = pdfPageSize.getWidth();
		this.pdfPageHeight = pdfPageSize.getHeight();
		this.pdfPageMargins = pdfPageMargins;

		scalingFactor = (double) getInnerPdfPageWidth() /(double)panelSize.width;			
	}

	public double getScalingFactor() {
		return scalingFactor;			
	}

	public float realPixelsToPdfPixels(double pixels) {
		return (float) (pixels*scalingFactor);
	}
	public float pdfPixelsToRealPixels(double pixels) {
		return (float) (pixels/scalingFactor);
	}

	public float getInnerPdfPageWidth() {
		return (float) pdfPageWidth - defaultPdfPageMargins.left - defaultPdfPageMargins.right;
	}

	public float getInnerPdfPageHeight() {
		return (float) pdfPageHeight -defaultPdfPageMargins.top - defaultPdfPageMargins.bottom;
	}

	public float getLeftPdfMargin() {
		return pdfPageMargins.left;			
	}
	public float getBottomPdfMargin() {
		return pdfPageMargins.bottom;			
	}

	public float getPdfPageWidth() {
		return (float) pdfPageWidth;
	}
	public float getPdfPageHeight() {
		return (float) pdfPageHeight;
	}

	public float getPageWidthInRealPixels() {
		return (float) (pdfPageWidth /scalingFactor);
	}

	public float getPageHeightInRealPixels() {
		return (float) (pdfPageHeight /scalingFactor);
	}

	public double getInnerPageWidthInRealPixels() {
		return (float) (getInnerPdfPageWidth() /scalingFactor);
	}

	private static Rectangle pageSize = null;
	static Rectangle getPdfPageSize() {
		if (pageSize == null) {		
			Document document = new Document();
			String randomFileNameForPdfTests = "temporary pdf file - can be deleted.pdf";

			try {
				// step 2:
				// we create a writer
				PdfWriter writer;

				Debug.debug(PdfInfo.class, "trying to write sample pdf to " + randomFileNameForPdfTests);
				FileOutputStream stream = new FileOutputStream(randomFileNameForPdfTests);
				writer = PdfWriter.getInstance(document, stream);

				// step 3: we open the document
				document.open();

				// step 4: we add a table to the document
				PdfContentByte cb = writer.getDirectContent();

				pageSize = writer.getPageSize();
				stream.close();			
			} catch (Exception ex) {
				Debug.critical(PdfInfo.class, "Could not determine pdfPageSize by testing on sample pdf document!");
			}
			try {
				File file = new File(randomFileNameForPdfTests);
				if (file.exists()) {
					file.delete();
				}
			} catch (Exception ex) {
				Debug.critical(PdfInfo.class, "Could not remove sample pdf!");
			}
			/*if (pageSize != null) {
				float pageWidth =  pageSize.getWidth() -defaultPdfPageMargins.left - defaultPdfPageMargins.right;
			//	float pageHeight =  pageSize.getHeight() -defaultPdfPageMargins.top - defaultPdfPageMargins.bottom;
			}*/
		}
		if (pageSize == null) {			
			pageSize = new Rectangle(595, 842);
			Debug.debug(PdfInfo.class, "Falling back to default page size...");
		}
		Debug.debug(PdfInfo.class, "returning pagesize: " + pageSize.toString());
		return pageSize;
	}


}