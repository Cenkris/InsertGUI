package Application;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class StartPage extends JFrame {
    private JPanel contentPanel;
    private JButton chooseTemplateButton;
    private JTextField chooseTF;
    private JButton nextButton;
    private File currentDirectory;
    private JFileChooser fileChooser;
    private final Path settingsPath = Paths.get("./settings.ini");

    public StartPage() {
        readFromSettings();
        initChooseTemplateButton();
        initNextButton();
        initDefaultValues();

//        chooseTF.setText("E:\\Programming\\Telacad Java 2\\testFolder\\Document.docx");
    }

    private void initNextButton() {
        nextButton.addActionListener(event -> {

            if (chooseTF.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a template file", "No template file selected", JOptionPane.ERROR_MESSAGE);
            } else {
                Path path = Paths.get(chooseTF.getText());
                String fileName = String.valueOf(path.getFileName());
                if (fileName.endsWith(".docx")) {
                    String[] split = fileName.split("\\.");
                    new FieldsPage(chooseTF.getText(), fileName, (split[split.length - 1]));
                    setVisible(false);
                    saveSettings();
                }
            }
        });
    }

    private void saveSettings() {
        fileChooser.setCurrentDirectory(new File(chooseTF.getText()));
        try {
            if (!Files.exists(settingsPath)) {
                Files.createFile(settingsPath);
            }

            BufferedWriter writer = Files.newBufferedWriter(settingsPath);
            writer.write(chooseTF.getText());

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initChooseTemplateButton() {
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setCurrentDirectory(currentDirectory);
        try {
            chooseTF.setText(currentDirectory.getAbsolutePath());
        } catch (NullPointerException ignored) {

        }

        chooseTemplateButton.addActionListener(event -> {
            if (fileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().toString();
                chooseTF.setText(path);
            }
        });
    }

    private void readFromSettings() {
        if (Files.exists(settingsPath)) {
            try {
                Scanner scanner = new Scanner(settingsPath);

                if (scanner.hasNext()) {
                    currentDirectory = new File(scanner.nextLine());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initDefaultValues() {
        setTitle("Insert GUI");
        add(contentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(500, 150));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
