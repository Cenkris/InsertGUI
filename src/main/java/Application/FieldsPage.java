package Application;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.joda.time.LocalDateTime;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class FieldsPage extends JFrame {

    private final String filePath, ext, fileName;
    private JPanel contentPanel, mainPanel;
    private JButton genButton, backButton;
    private final List<String> fieldsList = new ArrayList<>();
    private final Map<String, JTextField> fieldStorage = new HashMap<>();
    private final Path outputPath = Paths.get("output/");

    FieldsPage(String filePath, String fileName, String ext) {
        this.filePath = filePath;
        this.ext = ext;
        this.fileName = fileName;

        readFile();
        initDefaultValues();
        initBackButton();
        initGenerateButton();
        populateFields();
    }

    private void initBackButton() {
        backButton.addActionListener(event -> {
            new StartPage();
            setVisible(false);
        });
    }

    private void initGenerateButton() {
        genButton.addActionListener(event -> outputFile());
    }

    private void outputFile() {
        try (XWPFDocument document = new XWPFDocument(new FileInputStream(filePath))) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null) {
                    for (XWPFRun run : runs) {
                        String runString = run.getText(0);
                        if (runString.contains("<#")) {
                            String fieldName = getFieldName(runString);
                            run.setText(runString.replace("<#" + fieldName + "#>", fieldStorage.get(fieldName).getText()), 0);
                        }
                    }
                }
            }

            String timeStamp = LocalDateTime.now().toString("ddMMyy_hhmmSS");
            String outString = outputPath + "\\" + timeStamp + "_" + fileName;

            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            document.write(new FileOutputStream(outString));
            File file = new File(outString);

            document.close();

            // force cleanup, is random anyway
            System.gc();
            
            JOptionPane.showMessageDialog(null, file.getAbsolutePath() + " was saved!", "Done!", JOptionPane.INFORMATION_MESSAGE);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateFields() {
        JPanel fieldPanel = new JPanel(new GridLayout(fieldsList.size() / 2 + 1, 2));
        for (String field : fieldsList) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JLabel label = new JLabel(field.toLowerCase(Locale.ROOT));
            JTextField textField = new JTextField();

            textField.setPreferredSize(new Dimension(150, 30));

            panel.add(label);
            panel.add(textField);
            fieldPanel.add(panel);
            fieldStorage.put(field, textField);
        }
        mainPanel.add(fieldPanel);
        pack();
    }

    private void readFile() {
        if (ext.equals("docx")) {
            try {
                XWPFDocument document = new XWPFDocument(new FileInputStream(filePath));
                List<XWPFParagraph> paragraphs = document.getParagraphs();
                for (XWPFParagraph paragraph : paragraphs) {
                    if (!paragraph.getText().isEmpty() && paragraph.getText().contains("<#")) {
                        String fieldName = getFieldName(paragraph.getText());
                        fieldsList.add(fieldName);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //TODO add functionality for .doc
        }
    }

    private String getFieldName(String text) {
        return text.substring(text.indexOf("<#") + 2, text.indexOf("#>"));
    }

    private void initDefaultValues() {
        setTitle(fileName);
        add(contentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(300, 100));
        setLocationRelativeTo(null);
        setVisible(true);
        pack();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        mainPanel = new JPanel();
    }
}
