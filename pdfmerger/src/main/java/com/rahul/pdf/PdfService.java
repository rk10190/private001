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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

/**
 * The Class PdfService to merge n number of pdf into a single output.
 *
 * @author rahul09988@gmail.com
 */
public class PdfService {

  /** The src folder. */
  private static File srcFolder;

  /** The target folder. */
  private static File targetFolder;

  /** The config file. */
  private static File configFile = new File(System.getProperty("user.home") + "/pdfmerge.config");

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

    JList<String> listBox = new JList<>(srcFolder.list());
    listBox.setPreferredSize(new Dimension(550, 230));
    listBox.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    listBox.setLayoutOrientation(JList.VERTICAL);
    listBox.setVisibleRowCount(10);

    JLabel label2 = new JLabel("Source Path : ");
    label2.setPreferredSize(new Dimension(80, 25));
    JTextField srcPath = new JTextField(srcFolder.getAbsolutePath());
    srcPath.setPreferredSize(new Dimension(300, 25));
    JButton pathSelecterBtn2 = new JButton("Select");
    pathSelecterBtn2.setPreferredSize(new Dimension(100, 25));
    pathSelecterBtn2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        srcFolder = selectFolder(srcFolder);
        if (srcFolder != null) {
          srcPath.setText(srcFolder.getAbsolutePath());
          updateConfig();
          listBox.setListData(srcFolder.list());
        }
      }
    });

    JLabel label3 = new JLabel("Process Status : Application started...");
    label3.setPreferredSize(new Dimension(385, 25));
    JButton processBtn = new JButton("Process");
    processBtn.setPreferredSize(new Dimension(100, 30));
    processBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        createPdf(listBox.getSelectedValuesList(), label3);
      }
    });

    JPanel panel1 = new JPanel();
    panel1.add(label1);
    panel1.add(outputPath);
    panel1.add(pathSelecterBtn);
    JPanel panel3 = new JPanel();
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(listBox);
    panel3.add(scrollPane);
    JPanel panel2 = new JPanel();
    panel2.add(label2);
    panel2.add(srcPath);
    panel2.add(pathSelecterBtn2);
    JPanel panel4 = new JPanel();
    panel4.add(label3);
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
        srcFolder = new File(props.getProperty("srcfolder"));
        targetFolder = new File(props.getProperty("targetfolder"));
      } else {
        srcFolder = new File(".");
        targetFolder = new File(".");
      }
    } catch (IOException e) {
      e.printStackTrace();
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
  private static void createPdf(List<String> fileNames, JLabel label) {
    PDFMergerUtility pdfMerger = new PDFMergerUtility();
    String outputFileName = "/Merged" + System.currentTimeMillis() + ".pdf";
    pdfMerger.setDestinationFileName(targetFolder.getAbsolutePath() + outputFileName);
    try {
      label.setText("Process Status : Initial...");
      fileNames.stream().filter(a -> isPdf(a)).forEach(a -> {
        try {
          File resource = new File(srcFolder.getAbsolutePath() + File.separator + a);
          if (resource != null && resource.exists()) {
            pdfMerger.addSource(resource);
          }
          label.setText("Process Status : Adding resource " + resource.getName());
        } catch (FileNotFoundException e) {
          System.out.println(e.getMessage());
        }
      });
      label.setText("Process Status : Resources added");
      pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    label.setText("Process Status : Merge completed");
  }
}
