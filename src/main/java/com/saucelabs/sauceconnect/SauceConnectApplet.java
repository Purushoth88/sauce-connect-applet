package com.saucelabs.sauceconnect;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Ross Rowe
 */
public class SauceConnectApplet extends JApplet {

    @Override
    public void init() {
        super.init();
        final String username = getParameter("sauceUser");
        final String accessKey = getParameter("sauceAccessKey");

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI(username, accessKey);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void createGUI(String username, String accessKey) {
        setContentPane(new SauceConnectForm(new BorderLayout(), username, accessKey));
    }

    public static final void main(String[] args) {
        JApplet applet = new SauceConnectApplet();
        applet.init();
        applet.start();
        JFrame window = new JFrame("Sauce Connect");
        window.setContentPane(applet);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }
}
