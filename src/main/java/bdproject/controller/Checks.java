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

    public static boolean isValidMeasurement(final TextField f1, final TextField f2, final TextField f3) {
        final boolean f1Pass = f1.getText().length() != 0 && isNumber(f1.getText());
        final boolean f2Pass = !f2.isVisible()
                || (f2.isVisible() && f2.getText().length() != 0 && isNumber(f2.getText()));
        final boolean f3Pass = !f3.isVisible()
                || (f3.isVisible() && f3.getText().length() != 0 && isNumber(f3.getText()));
        return f1Pass && f2Pass && f3Pass;
    }

    public static boolean requiresPeopleNumber(final Contratti sub) {
        return sub.getTipouso().equals("Abitativo residenziale") || sub.getTipouso().equals("Commerciale");
    }

    public static boolean isSubscriptionActive(final Contratti sub, final Connection conn) {
        final boolean isInterrupted = Queries.hasOngoingInterruption(sub, conn);
        return sub.getDatainizio() != null && sub.getDatacessazione() == null && !isInterrupted;
    }
}
