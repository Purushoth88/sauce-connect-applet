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
        final String jythonJar = getParameter("jythonJar");
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI(jythonJar);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void createGUI(String jythonJar) {
        setContentPane(new SauceConnectForm(new BorderLayout(), jythonJar));
    }

    public static final void main(String[] args) {
        JApplet applet = new SauceConnectApplet();
        applet.init();
        applet.start();
        JFrame window = new JFrame("Sauce Connect");
        window.setContentPane(new SauceConnectForm(new BorderLayout(), ""));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }
}
