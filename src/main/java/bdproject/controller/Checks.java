package bdproject.controller;

import bdproject.model.Queries;
import bdproject.model.types.StatusType;
import bdproject.tables.pojos.Contratti;
import bdproject.tables.pojos.ContrattiAttivi;
import bdproject.tables.pojos.RichiesteContratto;
import bdproject.tables.pojos.TipologieUso;

import java.math.BigDecimal;
import java.sql.Connection;

public class Checks {

    private Checks() {}

    public static boolean isIntegerNumber(String s) {
        return s.codePoints().allMatch(Character::isDigit);
    }

    /*
     * Costly, but whatever
     */
    public static boolean isBigDecimal(String str) {
        try {
            new BigDecimal(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static boolean isValidConsumption(final String consumption) {
        return consumption.length() > 0 && isIntegerNumber(consumption);
    }

    public static boolean requiresPeopleNumber(final TipologieUso use) {
        return use.getNome().equals("Abitativo residenziale") || use.getNome().equals("Commerciale");
    }

    public static boolean isSubscriptionRequestBeingReviewed(final RichiesteContratto sub) {
        return sub.getStatorichiesta().equals(StatusType.REVIEWING.toString()) && sub.getDatachiusurarichiesta() == null;
    }

    public static boolean isSubscriptionActive(final Contratti sub, final Connection conn) {
        return sub.getDatachiusurarichiesta() != null &&
                sub.getStatorichiesta().equals(StatusType.APPROVED.toString()) &&
                Queries.fetchApprovedEndRequestBySubscription(sub.getIdcontratto(), conn).isEmpty();
    }
}
