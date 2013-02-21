package com.saucelabs.sauceconnect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
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
    private String username;
    private String accessKey;

    public SauceConnectForm(BorderLayout borderLayout, String username, String accessKey) throws HeadlessException {
        super(borderLayout);
        this.username = username;
        this.accessKey = accessKey;
        setLayout(new FlowLayout());
        createButton();
        createLogField();
        setVisible(true);
    }

    private void createLogField() {
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        TextAreaOutputStream taos = new TextAreaOutputStream(textArea, 60);
        PrintStream ps = new PrintStream(taos);
        System.setOut(ps);
        System.setErr(ps);

        JScrollPane scrollPane = new JScrollPane(textArea);
//        scrollPane.setPreferredSize(new Dimension(175, 150));
        add(scrollPane, BorderLayout.CENTER);
        //setBorder(BorderFactory.createEmptyBorder(35, 10, 35, 10));
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
                            File logFileLocation = new File(System.getProperty("java.io.tmpdir"), "sauce-connect.log");
                            SauceConnect.main(new String[]{"rossco_9_9", "44f0744c-1689-4418-af63-560303cbb37b", "-l", logFileLocation.getAbsolutePath()});
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
