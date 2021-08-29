package bdproject.controller;

import bdproject.model.Queries;
import bdproject.tables.pojos.ContrattiDettagliati;
import bdproject.tables.pojos.TipologieUso;

import java.sql.Connection;

public class Checks {

    private Checks() {}

    public static boolean isNumber(String s) {
        return s.codePoints().allMatch(Character::isDigit);
    }

    public static boolean isValidConsumption(final String consumption) {
        return consumption.length() != 0 && isNumber(consumption);
    }

    public static boolean requiresPeopleNumber(final TipologieUso use) {
        return use.getNome().equals("Abitativo residenziale") || use.getNome().equals("Commerciale");
    }

    public static boolean isSubscriptionActive(final ContrattiDettagliati sub, final Connection conn) {
        final boolean isInterrupted = Queries.hasOngoingInterruption(sub, conn);
        return sub.getDatainizio() != null && sub.getDatacessazione() == null && !isInterrupted;
    }
}
