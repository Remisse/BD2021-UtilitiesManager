package bdproject.controller;

import bdproject.model.Queries;
import bdproject.tables.pojos.Contratti;
import javafx.scene.control.TextField;

import java.sql.Connection;

public class Checks {

    private Checks() {}

    public static boolean isNumber(String s) {
        return s.codePoints().allMatch(Character::isDigit);
    }

    public static boolean isValidMeasurement(final String consumption) {
        return consumption.length() != 0 && isNumber(consumption);
    }

    public static boolean requiresPeopleNumber(final Contratti sub) {
        return sub.getTipouso().equals("Abitativo residenziale") || sub.getTipouso().equals("Commerciale");
    }

    public static boolean isSubscriptionActive(final Contratti sub, final Connection conn) {
        final boolean isInterrupted = Queries.hasOngoingInterruption(sub, conn);
        return sub.getDatainizio() != null && sub.getDatacessazione() == null && !isInterrupted;
    }
}
