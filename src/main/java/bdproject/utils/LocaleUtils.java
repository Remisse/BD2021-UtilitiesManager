package bdproject.utils;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class LocaleUtils {

    private static final Map<String, String> mUnit = Map.of(
            "Luce", "€/kWh",
            "Gas", "€/Smc",
            "Acqua", "€/mc"
    );

    private LocaleUtils () {
    }

    public static DateTimeFormatter getItDateFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.ITALIAN);
    }

    public static DateTimeFormatter getItLongMonthFormatter() {
        return DateTimeFormatter.ofPattern("MMMM").withLocale(Locale.ITALIAN);
    }

    public static Map<String, String> getItUtilitiesUnits() {
        return mUnit;
    }
}
