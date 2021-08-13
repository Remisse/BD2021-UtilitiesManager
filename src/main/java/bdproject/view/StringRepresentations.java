package bdproject.view;

import bdproject.tables.pojos.Immobili;
import bdproject.tables.pojos.Persone;
import bdproject.tables.pojos.Zone;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;

import static bdproject.tables.Zone.ZONE;

public class StringRepresentations {

    private StringRepresentations() {}

    public static String clientToString(final Persone client) {
        StringBuilder builder = new StringBuilder();
        builder.append(client.getNome())
                .append(" ")
                .append(client.getCognome())
                .append("\nCodice cliente: ")
                .append(client.getCodicecliente())
                .append("\n")
                .append(client.getCodicefiscale())
                .append("\n")
                .append(client.getEmail());
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
                .append(premises.getCap())
                .append("\n")
                .append(zone.getProvincia());
        return builder.toString();
    }
}
