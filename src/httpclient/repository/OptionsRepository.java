package httpclient.repository;

import httpclient.gui.theme.DarkTheme;
import httpclient.gui.theme.LightTheme;
import httpclient.gui.theme.Theme;

import java.io.*;

/**
 * A repository for loading and saving options into file. It serializes the options model object and saves the byte
 * stream into an specified file.
 */
public class OptionsRepository {
    /**
     * name of file that options saved into
     */
    private static final String FILE_NAME = "options";

    /**
     * Loads options model from file.
     *
     * @return options model object
     * @throws Exception If reading and obtaining options model object fails
     */
    public OptionsModel load() throws Exception {
        File optionsFile = new File(FILE_NAME);
        //checking file existence
        if (optionsFile.exists()) {
            Object readObject;
            try {
                //reading the file
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(optionsFile));
                readObject = inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new Exception("Failed to load options, " + e.getMessage(), e);
            }
            //if the file contains an object of type options model, its the result
            if (readObject instanceof OptionsModel) {
                return (OptionsModel) readObject;
            } else {
                throw new Exception("Invalid options file!");
            }
        }
        return new OptionsModel();
    }

    /**
     * Saves new options to file and returns new options model
     *
     * @param followRedirect follow redirect option
     * @param systemTray     system tray enabling option
     * @param theme          theme type option
     * @return new options model
     * @throws Exception If can't save to file
     */
    public OptionsModel save(boolean followRedirect, boolean systemTray, int theme) throws Exception {
        OptionsModel optionsModel = new OptionsModel();
        optionsModel.followRedirect = followRedirect;
        optionsModel.systemTray = systemTray;
        optionsModel.theme = theme;
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            outputStream.writeObject(optionsModel);
            return optionsModel;
        } catch (IOException e) {
            throw new Exception("Failed to save options, " + e.getMessage(), e);
        }
    }

    /**
     * Options model class that belongs to repository
     */
    public static class OptionsModel implements Serializable {
        /**
         * light theme code
         */
        private static final int LIGHT = 1;
        /**
         * dark theme code
         */
        private static final int DARK = 2;
        /**
         * system tray option
         */
        private boolean systemTray = false;
        /**
         * follow redirect option
         */
        private boolean followRedirect = false;
        /**
         * theme option
         */
        private int theme = LIGHT;

        /**
         * Gets system tray option.
         *
         * @return system tray option
         */
        public boolean isSystemTray() {
            return systemTray;
        }

        /**
         * Gets follow redirect option.
         *
         * @return follow redirect option
         */
        public boolean isFollowRedirect() {
            return followRedirect;
        }

        /**
         * Gets theme object that user configs.
         *
         * @return theme object
         */
        public Theme getTheme() {
            switch (theme) {
                case DARK:
                    return new DarkTheme();
                case LIGHT:
                default:
                    return new LightTheme();
            }
        }

        /**
         * Gets theme option value.
         *
         * @return theme option value
         */
        public int getThemeValue() {
            return theme;
        }
    }
}
