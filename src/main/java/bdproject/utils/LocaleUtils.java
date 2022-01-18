package bdproject.utils;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class LocaleUtils {

    private static final Map<String, String> priceUnit = Map.of(
            "Luce", "€/kWh",
            "Gas", "€/Smc",
            "Acqua", "€/mc"
    );

    private static final Map<String, String> utilityUnit = Map.of(
            "Luce", "kWh",
            "Gas", "Smc",
            "Acqua", "mc"
    );

    private LocaleUtils () {
    }

    public static DateTimeFormatter getItDateFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.ITALIAN);
    }

    public static DateTimeFormatter getItLongMonthFormatter() {
        return DateTimeFormatter.ofPattern("MMMM").withLocale(Locale.ITALIAN);
    }

    public static Map<String, String> getItPriceUnits() {
        return priceUnit;
    }

    public static Map<String, String> getItUtilityUnits() {
        return utilityUnit;
    }

    public static DecimalFormat getItDecimalFormat() {
        return new DecimalFormat("#,###.00");
    }
}
