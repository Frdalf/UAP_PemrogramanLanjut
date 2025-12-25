package Util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Kelas utilitas untuk memformat dan mem-parse nominal uang Rupiah.
 *
 * Format menggunakan pemisah ribuan titik (.) setiap 3 digit.
 * Proses parsing hanya mengambil angka, sehingga aman untuk input seperti
 * "1.000.000", "Rp 1.000.000", dan sejenisnya.
 */

public final class MoneyUtil {

    private static final DecimalFormat GROUP_FORMAT;

    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.of("in", "ID"));
        sym.setGroupingSeparator('.');
        sym.setDecimalSeparator(',');

        DecimalFormat df = new DecimalFormat("#,###", sym);
        df.setGroupingUsed(true);
        df.setMaximumFractionDigits(0);
        df.setMinimumFractionDigits(0);
        GROUP_FORMAT = df;
    }

    private MoneyUtil() {}

    // Format angka ke bentuk 1.000.000 (tanpa Rp)
    public static String format(long value) {
        return GROUP_FORMAT.format(value);
    }

    // Format nominal double (dibulatkan) ke bentuk 1.000.000 (tanpa Rp)
    public static String format(double value) {
        return format(Math.round(value));
    }

    // Membaca input nominal yang bisa berisi titik, spasi, atau tulisan “Rp”
    public static long parseToLong(String text) {
        if (text == null) return 0L;
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0L;
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException ex) {
            // angka terlalu besar / invalid
            return 0L;
        }
    }
}
