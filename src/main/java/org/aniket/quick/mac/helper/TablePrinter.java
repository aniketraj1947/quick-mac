package org.aniket.quick.mac.helper;

import java.util.List;
import org.fusesource.jansi.Ansi;

public class TablePrinter {

    private TablePrinter() {
        throw new IllegalArgumentException("No instance for util classes");
    }

    public static String[][] convertListTo2DArray(List<List<String>> dataList) {
        int numRows = dataList.size();
        int numColumns = numRows > 0 ? dataList.get(0).size() : 0;
        String[][] resultArray = new String[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            List<String> rowList = dataList.get(i);
            for (int j = 0; j < numColumns; j++) {
                resultArray[i][j] = rowList.get(j);
            }
        }
        return resultArray;
    }

    public static String getColouredString(final String text, final Ansi.Color color) {
        return Ansi.ansi().fgBrightCyan().bold().a(text).reset().toString();
    }

    public static String printTable(final String[] header, final String[][] data) {
        final StringBuilder result = new StringBuilder();
        final int numColumns = data[0].length;
        final int[] columnWidths = calculateColumnWidths(data);
        appendHeader(result, header, columnWidths);
        for (int i = 0; i < data.length; i++) {
            appendRow(result, data[i], columnWidths);
            if (i == data.length - 1) {
                appendBorder(result, numColumns, BorderType.BOTTOM, columnWidths);
            } else {
                appendBorder(result, numColumns, BorderType.MIDDLE, columnWidths);
            }
        }
        return result.toString();
    }

    private static void appendHeader(StringBuilder result, String[] header, int[] columnWidths) {
        appendBorder(result, header.length, BorderType.TOP, columnWidths);
        appendRow(result, header, columnWidths);
        appendBorder(result, header.length, BorderType.MIDDLE, columnWidths);
    }

    private static void appendBorder(StringBuilder result, int numColumns, BorderType type, int[] columnWidths) {
        char startChar, middleChar, endChar;
        switch (type) {
            case TOP:
                startChar = '╔';
                middleChar = '╦';
                endChar = '╗';
                break;
            case MIDDLE:
                startChar = '╠';
                middleChar = '╬';
                endChar = '╣';
                break;
            case BOTTOM:
                startChar = '╚';
                middleChar = '╩';
                endChar = '╝';
                break;
            default:
                throw new IllegalArgumentException("Invalid border type");
        }

        result.append(startChar);
        for (int i = 0; i < numColumns; i++) {
            int width = columnWidths[i] + 2; // Adding 2 for padding
            if (i == numColumns - 1) {
                result.append("═".repeat(width)).append(endChar);
            } else {
                result.append("═".repeat(width)).append(middleChar);
            }
        }
        result.append("\n");
    }

    private static void appendRow(StringBuilder result, String[] values, int[] columnWidths) {
        result.append("║");
        for (int i = 0; i < values.length; i++) {
            result.append(String.format("%-" + (columnWidths[i] + 2 + getOffsetWidth(values[i])) + "s║", Ansi.ansi().render(values[i]).toString())); // Adding 2 for padding
        }
        result.append("\n");
    }

    private static int[] calculateColumnWidths(String[][] data) {
        int numColumns = data[0].length;
        int[] columnWidths = new int[numColumns];
        for (String[] row : data) {
            for (int i = 0; i < numColumns; i++) {
                int strippedLength = stripAnsiCodes(row[i]).length();
                columnWidths[i] = Math.max(columnWidths[i], strippedLength);
            }
        }
        return columnWidths;
    }

    private static int getOffsetWidth(final String value) {
        return value.length() - stripAnsiCodes(value).length();
    }

    public static String stripAnsiCodes(String input) {
        // Remove ANSI escape codes
        return input.replaceAll("\u001B\\[[;\\d]*m", "");
    }


    private enum BorderType {
        TOP, MIDDLE, BOTTOM
    }
}
