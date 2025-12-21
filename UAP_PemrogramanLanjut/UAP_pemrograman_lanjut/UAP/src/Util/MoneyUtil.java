package Util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility untuk format & parse nominal Rupiah.
 *
 * <p>Format grouping menggunakan titik (.) tiap 3 digit.
 * Parsing akan mengambil digit saja (aman untuk "1.000.000", "Rp 1.000.000", dll).</p>
 */
public final class MoneyUtil {

    private static final DecimalFormat GROUP_FORMAT;

    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("in", "ID"));
        sym.setGroupingSeparator('.');
        sym.setDecimalSeparator(',');

        DecimalFormat df = new DecimalFormat("#,###", sym);
        df.setGroupingUsed(true);
        df.setMaximumFractionDigits(0);
        df.setMinimumFractionDigits(0);
        GROUP_FORMAT = df;
    }

    private MoneyUtil() {}

    /** Format angka ke bentuk 1.000.000 (tanpa Rp). */
    public static String format(long value) {
        return GROUP_FORMAT.format(value);
    }

    /** Format nominal double (dibulatkan) ke bentuk 1.000.000 (tanpa Rp). */
    public static String format(double value) {
        return format(Math.round(value));
    }

    /**
     * Parse input nominal yang mungkin mengandung titik, spasi, atau "Rp".
     * Hanya digit yang dihitung.
     */
    public static long parseToLong(String text) {
        if (text == null) return 0L;
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0L;
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException ex) {
            // angka terlalu besar / invalid, fallback 0 agar tidak crash UI
            return 0L;
        }
    }
}
