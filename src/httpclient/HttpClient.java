package httpclient;

import httpclient.gui.GuiOutputHandler;
import httpclient.gui.HttpClientGui;
import httpclient.repository.OptionsRepository;
import jurl.Jutl;
import jurl.OutputHandler;

import javax.swing.*;
import java.awt.*;

/**
 * Http client main class, that initializes GUI and runs it.
 */
public class HttpClient {
    /**
     * repository instance for options that saves and loads options n file
     */
    private final OptionsRepository optionsRepository;

    /**
     * jurl instance to execute commands
     */
    private Jutl jutl;

    /**
     * Constructor of Http client that initializes the options repository.
     */
    private HttpClient() {
        optionsRepository = new OptionsRepository();
    }

    /**
     * Main method of the program.
     *
     * @param args main arguments
     */
    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient();
        httpClient.run();
    }

    /**
     * Primary running method of http client that initializes GUI and runs it in system event queue.
     */
    private void run() {
        setLookAndFeel();
        //invoke code segment that initialize and runs GUI in system event queue
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                HttpClientGui gui = null;
                try {
                    gui = new HttpClientGui(optionsRepository);
                    OutputHandler outputHandler = new GuiOutputHandler(gui);
                    jutl = new Jutl(outputHandler);
                    gui.setJurl(jutl);
                    gui.setVisible(true);
                } catch (Exception e) {
                    //terminate program if instantiation of GUI causes any error
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        });
    }

    /**
     * Sets GUI look and feel as selected theme in options by user.
     */
    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(optionsRepository.load().getTheme().getLookAndFeel());
        } catch (Exception e) {
            System.out.println("Can't set to system look and fell");
        }
    }

}
