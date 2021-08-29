package bdproject.view;

import bdproject.model.Queries;
import bdproject.tables.pojos.ClientiDettagliati;
import bdproject.tables.pojos.Immobili;
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

        StringBuilder builder = new StringBuilder();
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

    public static String premisesToString(final Immobili premises) {
        StringBuilder builder = new StringBuilder();
        builder.append(premises.getTipo().equals("F") ? "Fabbricato"
                       : premises.getTipo().equals("T") ? "Terreno"
                       : "Non definito")
                .append("\n\n")
                .append(premises.getVia())
                .append(" ")
                .append(premises.getNumcivico())
                .append(premises.getInterno() != null ? "\nInterno: " + premises.getInterno() : "")
                .append("\n")
                .append(premises.getComune())
                .append("\n")
                .append(premises.getCap())
                .append("\n")
                .append(premises.getProvincia());
        return builder.toString();
    }

    public static String byteToYesNo(final byte value) {
        return value == 1 ? "Sì" : value == 0 ? "No" : "UNDEFINED";
    }
}
