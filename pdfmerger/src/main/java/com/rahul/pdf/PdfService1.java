/**
 * Copyright ©2020 Rahul_Kumar(rahul09988@gmail.com). All Rights Reserved.
 *
 * This work contains Rahul Kumar's unpublished proprietary information which may constitute a trade
 * secret and/or be confidential. This work may be used only for the purposes for which it was
 * provided, and may not be copied or disclosed to others. Copyright notice is precautionary only,
 * and does not imply publication.
 */
package com.rahul.pdf;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * The Class PdfService to merge n number of pdf into a single output.
 *
 * @author rahul09988@gmail.com
 */
public class PdfService1 {

  /** The src folder. */
  private static File srcFolder;

  /** The target folder. */
  private static File targetFolder;

  /** The config file. */
  private static File configFile = new File(System.getProperty("user.home") + "/pdfmerge.config");

  /** The image pixels. */
  private static String pixels;

  /**
   * Creates the and show GUI.
   */
  private static void createAndShowGUI() {
    loadConfig();
    JDialog dialog = new JDialog(new JFrame(), "Merge PDF");
    dialog.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent evt) {
        System.exit(0);
      }
    });
    dialog.setVisible(false);
    dialog.setLayout(new FlowLayout());
    JLabel label1 = new JLabel("Target Path : ");
    label1.setPreferredSize(new Dimension(80, 25));
    JTextField outputPath = new JTextField(targetFolder.getAbsolutePath());
    outputPath.setPreferredSize(new Dimension(300, 25));
    outputPath.setEditable(false);
    JButton pathSelecterBtn = new JButton("Select");
    pathSelecterBtn.setPreferredSize(new Dimension(100, 25));
    pathSelecterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        targetFolder = selectFolder(targetFolder);
        if (targetFolder != null) {
          outputPath.setText(targetFolder.getAbsolutePath());
          updateConfig();
        }
      }
    });
    DefaultListModel<String> fileList = new DefaultListModel<>();
    modifyFileList(fileList);
    JList<String> listBox = new JList<>(fileList);
    listBox.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    listBox.setLayoutOrientation(JList.VERTICAL);
    listBox.setVisibleRowCount(10);
    JLabel label4 = new JLabel("File count : " + listBox.getModel().getSize());
    label4.setPreferredSize(new Dimension(130, 25));


    JLabel label2 = new JLabel("Source Path : ");
    label2.setPreferredSize(new Dimension(80, 25));
    JTextField srcPath = new JTextField(srcFolder.getAbsolutePath());
    srcPath.setPreferredSize(new Dimension(300, 25));
    srcPath.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        if (validateFolder(srcPath.getText())) {
          updateSrcPath(srcPath.getText());
          updateConfig();
          modifyFileList(fileList);
        }
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        if (validateFolder(srcPath.getText())) {
          updateSrcPath(srcPath.getText());
          updateConfig();
          modifyFileList(fileList);
        }
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        if (validateFolder(srcPath.getText())) {
          updateSrcPath(srcPath.getText());
          updateConfig();
          modifyFileList(fileList);
        }
      }
    });

    JButton pathSelecterBtn2 = new JButton("Select");
    pathSelecterBtn2.setPreferredSize(new Dimension(100, 25));
    pathSelecterBtn2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        srcFolder = selectFolder(srcFolder);
        if (srcFolder != null) {
          srcPath.setText(srcFolder.getAbsolutePath());
          updateConfig();
          modifyFileList(fileList);
          label4.setText("File count : " + listBox.getModel().getSize());
        }
      }
    });

    JLabel label3 = new JLabel("Process Status : Application started...");
    label3.setPreferredSize(new Dimension(230, 30));
    JTextField compresRatio = new JTextField(pixels);
    compresRatio.setPreferredSize(new Dimension(30, 30));
    JButton processBtn = new JButton("Process");
    processBtn.setPreferredSize(new Dimension(100, 30));
    processBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          pixels = updateCompressRatio(compresRatio.getText());
          compresRatio.setText(pixels);
          updateConfig();
          shrinkPdf(createPdf(listBox.getSelectedValuesList(), label3), label3);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error in merging PDF files!",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JPanel panel1 = new JPanel();
    panel1.add(label1);
    panel1.add(outputPath);
    panel1.add(pathSelecterBtn);
    JPanel panel3 = new JPanel();
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setPreferredSize(new Dimension(550, 210));
    scrollPane.setViewportView(listBox);
    panel3.add(scrollPane);
    JPanel panel2 = new JPanel();
    panel2.add(label2);
    panel2.add(srcPath);
    panel2.add(pathSelecterBtn2);
    JPanel panel4 = new JPanel();
    panel4.add(label3);
    panel4.add(compresRatio);
    panel4.add(label4);
    panel4.add(processBtn);
    dialog.add(panel2);
    dialog.add(panel1);
    dialog.add(panel3);
    dialog.add(panel4);
    dialog.setLocation(300, 200);
    dialog.setSize(600, 400);
    dialog.setVisible(true);
  }

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        createAndShowGUI();
      }
    });
  }

  private static void updateSrcPath(String path) {
    srcFolder = new File(path);
  }

  private static String updateCompressRatio(String pixels) {
    try {
      Integer temp = Integer.parseInt(pixels);
      if (temp < 49 || temp > 300) {
        throw new Exception();
      } else {
        return String.valueOf(temp);
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
          "Only Valid number from 50-300 are allowed!\n\nSetting up default value!!",
          "Numuric Error !", JOptionPane.ERROR_MESSAGE);
      return "150";
    }
  }

  /**
   * Select folder.
   *
   * @param current the current
   * @return the file
   */
  private static File selectFolder(File current) {
    JFileChooser fc = new JFileChooser();
    if (current == null) {
      fc.setCurrentDirectory(new File(System.getProperty("user.home"))); // start at application
    } else {
      fc.setCurrentDirectory(current);
    }
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    JFrame frame = new JFrame("Choose Folder");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    int returnVal = fc.showSaveDialog(frame);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return fc.getSelectedFile();
    }
    return null;
  }

  /**
   * Update config.
   */
  private static void updateConfig() {
    try {
      Properties props = new Properties();
      if (!configFile.exists()) {
        configFile.createNewFile();
      }
      FileOutputStream out = new FileOutputStream(configFile);
      props.setProperty("srcfolder", srcFolder.getAbsolutePath());
      props.setProperty("targetfolder", targetFolder.getAbsolutePath());
      props.setProperty("pixels", pixels);
      props.store(out, null);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load config.
   */
  private static void loadConfig() {
    Properties props = new Properties();
    try {
      if (configFile.exists()) {
        FileInputStream in = new FileInputStream(configFile);
        props.load(in);
        in.close();
        updateSrcPath(props.getProperty("srcfolder"));
        targetFolder = new File(props.getProperty("targetfolder"));
        pixels = props.getProperty("pixels");
        updateCompressRatio(pixels);
      } else {
        updateSrcPath(".");
        targetFolder = new File(".");
        pixels = "150";
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null,
          "Error in loading setup parameters...\n\n Please remove the file :\n"
              + configFile.getAbsolutePath(),
          " Loading Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Checks if file is having .pdf extension.
   *
   * @param fileName the file name
   * @return true, if file is pdf
   */
  private static boolean isPdf(String fileName) {
    if (fileName.substring(fileName.lastIndexOf('.') + 1).equalsIgnoreCase("pdf")) {
      return true;
    }
    return false;
  }

  /**
   * Creates the pdf.
   *
   * @param fileNames the file names
   * @param label the label
   */
  private static String createPdf(List<String> fileNames, JLabel label) {
    if (fileNames == null || fileNames.isEmpty()) {
      JOptionPane.showMessageDialog(null, "No pdf file been selected for merging", "File Error!",
          JOptionPane.ERROR_MESSAGE);
      return null;
    }
    PDFMergerUtility pdfMerger = new PDFMergerUtility();
    String outputFileName =
        targetFolder.getAbsolutePath() + "/Merged" + System.currentTimeMillis() + ".pdf";
    pdfMerger.setDestinationFileName(outputFileName);
    try {
      label.setText("Process Status : Initial...");
      fileNames.stream().filter(a -> isPdf(a)).forEach(a -> {
        File resource = new File(srcFolder.getAbsolutePath() + File.separator + a);
        if (resource != null && resource.exists()) {
          try {
            pdfMerger.addSource(resource);
          } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                resource.getName() + " file is not available for merging", "File Not found !",
                JOptionPane.ERROR_MESSAGE);
          }
        }
        label.setText("Process Status : Adding resource " + resource.getName());
      });
      label.setText("Process Status : Resources added");
      pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    label.setText("Process Status : Resizing the pdf");
    return outputFileName;
  }

  private static void modifyFileList(DefaultListModel<String> fileList) {
    fileList.clear();
    Arrays.asList(srcFolder.list()).stream().filter(PdfService1::isPdf)
        .forEach(fileList::addElement);
  }

  private static boolean validateFolder(String folderPath) {
    File temp = new File(folderPath);
    if (temp.exists() && temp.isDirectory()) {
      return true;
    }
    return false;
  }

  /**
   * Shrink pdf.
   *
   * @param filePath the file path
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private static void shrinkPdf(String filePath, JLabel label) throws IOException {
    int pixelsVal = Integer.parseInt(pixels);
    PDDocument pdDocument = new PDDocument();
    File tempFile = new File(filePath);
    PDDocument oDocument = PDDocument.load(tempFile);
    PDFRenderer pdfRenderer = new PDFRenderer(oDocument);
    int numberOfPages = oDocument.getNumberOfPages();
    PDPage page = null;
    for (int i = 0; i < numberOfPages; i++) {
      page = new PDPage(PDRectangle.LETTER);
      BufferedImage bim = pdfRenderer.renderImageWithDPI(i, pixelsVal, ImageType.RGB);
      PDImageXObject pdImage = JPEGFactory.createFromImage(pdDocument, bim);
      PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page);
      float newHeight = PDRectangle.LETTER.getHeight();
      float newWidth = PDRectangle.LETTER.getWidth();
      contentStream.drawImage(pdImage, 0, 0, newWidth, newHeight);
      contentStream.close();
      pdDocument.addPage(page);
    }
    String outputFileName =
        targetFolder.getAbsolutePath() + "/Merged" + System.currentTimeMillis() + ".pdf";
    pdDocument.save(outputFileName);
    pdDocument.close();
    oDocument.close();
    if (tempFile.exists())
      tempFile.delete();
    label.setText("Process Status : Merging Completed");
  }
}
