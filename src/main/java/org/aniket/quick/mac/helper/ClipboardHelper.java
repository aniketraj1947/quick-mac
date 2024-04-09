package org.aniket.quick.mac.helper;

import lombok.Getter;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class ClipboardHelper {

    private static final String SUCCESSFUL_MESSAGE = "Successfully copied data on clipboard";
    private static final String EXCEPTION_MESSAGE = "Successfully copied data on clipboard - %s";

    public static Response copyToClipboard(final String data) {
        try {
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            final StringSelection stringSelection = new StringSelection(TablePrinter.stripAnsiCodes(data));
            clipboard.setContents(stringSelection, null);
            return new Response(true, null);
        } catch (final Exception e) {
            return new Response(false, e.getMessage());
        }

    }

    public static String getFooter(final Response response) {
        return response.isSuccessful() ?
                SUCCESSFUL_MESSAGE :
                String.format(EXCEPTION_MESSAGE, response.getMessage());
    }

    @Getter
    public static class Response {
        public boolean isSuccessful;
        public String message;

        public Response(final boolean isSuccessful, final String message) {
            this.isSuccessful = isSuccessful;
            this.message = message;
        }
    }
}
