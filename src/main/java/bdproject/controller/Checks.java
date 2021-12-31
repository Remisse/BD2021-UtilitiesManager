package bdproject.controller;

import bdproject.tables.pojos.Contratti;
import bdproject.tables.pojos.ContrattiApprovati;
import bdproject.tables.pojos.RichiesteContratto;
import bdproject.tables.pojos.TipologieUso;

import java.math.BigDecimal;

public class Checks {

    private Checks() {}

    public static boolean isIntegerNumber(String s) {
        return s.codePoints().allMatch(Character::isDigit);
    }

    /**
     * Costly, but whatever
     *
     * @param str
     * @return
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

    public static boolean isSubscriptionActive(final ContrattiApprovati sub) {
        return sub.getDatacessazione() == null;
    }

    public static boolean isSubscriptionRequestBeingReviewed(final RichiesteContratto sub) {
        return sub.getDatachiusurarichiesta() == null;
    }
}
