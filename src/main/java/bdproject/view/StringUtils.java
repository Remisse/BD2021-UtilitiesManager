package bdproject.view;

import bdproject.tables.pojos.ClientiDettagliati;
import bdproject.tables.pojos.Immobili;
import bdproject.tables.pojos.Offerte;
import bdproject.utils.LocaleUtils;
public class StringUtils {

    private StringUtils() {}

    public static String getGenericError() {
        return "Impossibile soddisfare la richiesta.";
    }

    public static String clientToString(final ClientiDettagliati client) {
        final StringBuilder builder = new StringBuilder();
        return builder.append(client.getNome())
                .append(" ")
                .append(client.getCognome())
                .append("\nData di nascita: ")
                .append(LocaleUtils.getItDateFormatter().format(client.getDatanascita()))
                .append("\n")
                .append(client.getCodicefiscale())
                .append("\nCodice cliente: ")
                .append(client.getIdpersona())
                .append("\n")
                .append(client.getEmail())
                .append("\nReddito: ")
                .append(client.getFasciareddito())
                .toString();
    }

    public static String premiseToString(final Immobili premise) {
        return new StringBuilder().append(premise.getTipo())
                .append("\n\n")
                .append(premise.getVia())
                .append(" ")
                .append(premise.getNumcivico())
                .append(!premise.getInterno().equals("") ? "\nInterno " + premise.getInterno() : "")
                .append("\n")
                .append(premise.getComune())
                .append("\n")
                .append(premise.getCap())
                .append("\n")
                .append(premise.getProvincia())
                .toString();
    }

    public static String planToString(final Offerte plan) {
        return new StringBuilder().append(plan.getNome())
                .append("\nMateria prima: ")
                .append(plan.getMateriaprima())
                .append("\nCosto materia prima: ")
                .append(plan.getCostomateriaprima())
                .append(LocaleUtils.getItPriceUnits().get(plan.getMateriaprima()))
                .toString();
    }

    public static String byteToYesNo(final byte value) {
        return value == 1 ? "Sì" : value == 0 ? "No" : "UNDEFINED";
    }
}
