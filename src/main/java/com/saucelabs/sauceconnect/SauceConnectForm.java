package com.saucelabs.sauceconnect;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ross Rowe
 */

public class SauceConnectForm extends JPanel implements ActionListener {

    private ByteArrayOutputStream out;
    private ExecutorService executor;
    private static final String LAUNCH_SAUCE_CONNECT = "Launch Sauce Connect";
    private JButton button;
    private static final String SHUTDOWN_SAUCE_CONNECT = "Shutdown Sauce Connect";
    private JTextField userNameField;
    private JTextField accessKeyField;
    private String jythonJar;

    public SauceConnectForm(BorderLayout borderLayout, String jythonJar) throws HeadlessException {
        super(borderLayout);
        this.jythonJar = jythonJar;
        this.userNameField = new JTextField();
        this.accessKeyField = new JTextField();
        setLayout(new FlowLayout());
        createFieldPanel();
        createButton();
        createLogField();
        setVisible(true);
    }

    private void createFieldPanel() {
        JPanel labelPanel = new JPanel(new GridLayout(2, 1));
        JPanel fieldPanel = new JPanel(new GridLayout(2, 1));
        add(labelPanel, BorderLayout.WEST);
        add(fieldPanel, BorderLayout.CENTER);
        addField("User Name", labelPanel, fieldPanel, userNameField);
        addField("Access Key", labelPanel, fieldPanel, accessKeyField);

    }

    private void addField(String labelText, JPanel labelPanel, JPanel fieldPanel, JTextField textField) {
        textField.setColumns(20);
        fieldPanel.add(textField);
        JLabel label = new JLabel(labelText, JLabel.RIGHT);
        label.setLabelFor(textField);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        labelPanel.add(panel);
    }

    private void createLogField() {
        final JTextArea textArea = new JTextArea(30, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                TextAreaOutputStream taos = new TextAreaOutputStream(textArea, 60);
                PrintStream ps = new PrintStream(taos);
                System.setOut(ps);
                System.setErr(ps);
                return null;
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createButton() {
        this.button = new JButton(LAUNCH_SAUCE_CONNECT);
        button.addActionListener(this);
        add(button);
    }


    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals(LAUNCH_SAUCE_CONNECT)) {
            this.executor = Executors.newFixedThreadPool(1);
            executor.execute(new Runnable() {
                public void run() {
                    AccessController.doPrivileged(new PrivilegedAction() {
                        public Object run() {
                            System.out.println("About to start Sauce Connect");
                            System.out.println("Downloading Jython from " + jythonJar);
                            File tempJythonJar = new File(System.getProperty("java.io.tmpdir"), "jython.jar");
                            try {
                                FileUtils.copyURLToFile(new URL(jythonJar), tempJythonJar);
                                System.out.println("Download finished" + tempJythonJar.getAbsolutePath());
                                File logFileLocation = new File(System.getProperty("java.io.tmpdir"), "sauce-connect.log");
                                System.setProperty("python.cachedir.skip", "true");
                                System.setProperty("python.cachedir", System.getProperty("java.io.tmpdir"));
                                System.setProperty("python.path", tempJythonJar.getAbsolutePath());
                                System.setProperty("python.home", tempJythonJar.getAbsolutePath());
                                SauceConnect.main(new String[]{userNameField.getText(), accessKeyField.getText(), "-l", logFileLocation.getAbsolutePath()});

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    });
                }
            });
            button.setText(SHUTDOWN_SAUCE_CONNECT);
        } else {
            System.out.println("About to stop Sauce Connect");
            executor.shutdownNow();
            System.out.println("Sauce Connect stopped");
            button.setText(LAUNCH_SAUCE_CONNECT);
        }
    }
}
