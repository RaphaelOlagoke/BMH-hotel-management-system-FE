package com.bmh.hotelmanagementsystem;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

public class PDFPrinter {
    public static boolean printPDF(File pdfFile) throws Exception {
        // Find available printers
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        if (printServices.length == 0) {
//            throw new Exception("No printer found!");
            return false;
        }

        if (!Desktop.isDesktopSupported()) {
            throw new UnsupportedOperationException("Desktop printing not supported");
        }

        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.PRINT)) {
            desktop.print(pdfFile);
            return true;
        } else {
            throw new UnsupportedOperationException("Printing not supported on this system");
        }

//        // Select the first available printer
//        PrintService printService = printServices[0];
//
//        // Create a print job
//        DocPrintJob job = printService.createPrintJob();
//
//        // Set up document format
//        FileInputStream fis = new FileInputStream(pdfFile);
//        Doc doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.PDF, null);
//
//        // Print request attributes
//        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
//        attributes.add(new Copies(1));  // Set number of copies
//
//        // Print document
//        job.print(doc, attributes);
//
//        fis.close();
//
//        return true;
    }
}

