package bdproject.view;

import bdproject.model.Queries;
import bdproject.tables.pojos.ClientiDettagliati;
import bdproject.tables.pojos.Immobili;
import bdproject.tables.pojos.Offerte;
import bdproject.tables.pojos.TipiImmobile;
import bdproject.utils.LocaleUtils;

import java.sql.Connection;
import java.util.Optional;

public class StringUtils {

    private StringUtils() {}

    public static String getGenericError() {
        return "Impossibile soddisfare la richiesta.";
    }

    public static String clientToString(final int personId, final Connection conn) {
        final Optional<ClientiDettagliati> client = Queries.fetchClientById(personId, conn);

        final StringBuilder builder = new StringBuilder();
        client.ifPresent(c -> builder.append(c.getNome())
                .append(" ")
                .append(c.getCognome())
                .append("\nData di nascita: ")
                .append(LocaleUtils.getItDateFormatter().format(c.getDatanascita()))
                .append("\n")
                .append(c.getCodicefiscale())
                .append("\nCodice cliente: ")
                .append(c.getIdentificativo())
                .append("\n")
                .append(c.getEmail())
                .append("\nReddito: ")
                .append(c.getFasciareddito())
        );
        return builder.toString();
    }

    public static String premisesToString(final Immobili premises, final TipiImmobile type) {
        return new StringBuilder().append(type.getNome())
                .append("\n\n")
                .append(premises.getVia())
                .append(" ")
                .append(premises.getNumcivico())
                .append(type.getHainterno() == 1 ? "\nInterno " + premises.getInterno() : "")
                .append("\n")
                .append(premises.getComune())
                .append("\n")
                .append(premises.getCap())
                .append("\n")
                .append(premises.getProvincia())
                .toString();
    }

    public static String requestStatusToString(final String result) {
        String outVal = "UNDEFINED";
        switch (result) {
            case "N":
                outVal = "Non esaminata";
                break;
            case "E":
                outVal = "Presa in carico";
                break;
            case "A":
                outVal = "Approvata";
                break;
            case "R":
                outVal = "Respinta";
        }
        return outVal;
    }

    public static String planToString(final Offerte plan) {
        return new StringBuilder().append(plan.getNome())
                .append("\nMateria prima: ")
                .append(plan.getMateriaprima())
                .append("\nCosto materia prima: ")
                .append(plan.getCostomateriaprima())
                .append(LocaleUtils.getItUtilitiesUnits().get(plan.getMateriaprima()))
                .toString();
    }

    public static String byteToYesNo(final byte value) {
        return value == 1 ? "Sì" : value == 0 ? "No" : "UNDEFINED";
    }
}
