package com.yourname.myapp.ui.util;

import javax.swing.JOptionPane;
import java.util.Optional;

/**
 * Utility class for displaying Swing dialogs and alerts.
 */
public class DialogUtil {

    /**
     * Show information alert
     */
    public static void showInfo(String title, String header, String content) {
        JOptionPane.showMessageDialog(null, header + "\n" + content, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show error alert
     */
    public static void showError(String title, String header, String content) {
        JOptionPane.showMessageDialog(null, header + "\n" + content, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show warning alert
     */
    public static void showWarning(String title, String header, String content) {
        JOptionPane.showMessageDialog(null, header + "\n" + content, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show confirmation dialog
     */
    public static boolean showConfirmation(String title, String header, String content) {
        int result = JOptionPane.showConfirmDialog(null, header + "\n" + content, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Show text input dialog
     */
    public static Optional<String> showTextInput(String title, String header, String content) {
        String result = JOptionPane.showInputDialog(null, header + "\n" + content, title, JOptionPane.QUESTION_MESSAGE);
        return Optional.ofNullable(result);
    }
}
