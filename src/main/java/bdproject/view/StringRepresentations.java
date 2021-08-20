package bdproject.view;

import bdproject.model.Queries;
import bdproject.tables.pojos.Immobili;
import bdproject.tables.pojos.Persone;
import bdproject.tables.pojos.Redditi;
import bdproject.tables.pojos.Zone;
import bdproject.utils.LocaleUtils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.Optional;

import static bdproject.Tables.PERSONE;
import static bdproject.Tables.REDDITI;
import static bdproject.tables.Zone.ZONE;

public class StringRepresentations {

    private StringRepresentations() {}

    public static String getGenericError() {
        return "Impossibile soddisfare la richiesta.";
    }

    public static String clientToString(final int clientId, final Connection conn) {
        final Optional<Persone> client = Queries.fetchClientById(clientId, conn);
        final Redditi income = Queries.getClientIncomeBracket(clientId, conn);

        StringBuilder builder = new StringBuilder();
        client.ifPresent(c -> builder.append(c.getNome())
                .append(" ")
                .append(c.getCognome())
                .append("\nData di nascita: ")
                .append(LocaleUtils.getItDateFormatter().format(c.getDatanascita()))
                .append("\n")
                .append(c.getCodicefiscale())
                .append("\nCodice cliente: ")
                .append(c.getCodicecliente())
                .append("\n")
                .append(c.getEmail())
                .append("\nReddito: ")
                .append(income.getFascia())
        );
        return builder.toString();
    }

    public static String premisesToString(final Immobili premises, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final Zone zone = query.select()
                .from(ZONE)
                .where(ZONE.IDZONA.eq(premises.getIdzona()))
                .fetchOneInto(Zone.class);
        StringBuilder builder = new StringBuilder();
        assert zone != null;
        builder.append(premises.getTipo().equals("F") ? "Fabbricato"
                                                      : premises.getTipo().equals("T") ? "Terreno"
                       : "Non definito")
                .append("\n\n")
                .append(premises.getVia())
                .append(" ")
                .append(premises.getNumcivico())
                .append(premises.getInterno() != null ? "\nInterno: " + premises.getInterno() : "")
                .append("\n")
                .append(zone.getComune())
                .append("\n")
                .append(zone.getCap())
                .append("\n")
                .append(zone.getProvincia());
        return builder.toString();
    }
}
