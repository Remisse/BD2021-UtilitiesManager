/*
 * This file is generated by jOOQ.
 */
package bdproject;


import bdproject.tables.Bollette;
import bdproject.tables.Compatibilità;
import bdproject.tables.Contatori;
import bdproject.tables.Contratti;
import bdproject.tables.Distributori;
import bdproject.tables.Distribuzioni;
import bdproject.tables.Immobili;
import bdproject.tables.Interruzioni;
import bdproject.tables.Letture;
import bdproject.tables.MateriePrime;
import bdproject.tables.Offerte;
import bdproject.tables.PersoneFisiche;
import bdproject.tables.PersoneGiuridiche;
import bdproject.tables.Redditi;
import bdproject.tables.TipiAttivazione;
import bdproject.tables.TipologieUso;
import bdproject.tables.Zone;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Utenze extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>utenze</code>
     */
    public static final Utenze UTENZE = new Utenze();

    /**
     * The table <code>utenze.bollette</code>.
     */
    public final Bollette BOLLETTE = Bollette.BOLLETTE;

    /**
     * The table <code>utenze.compatibilità</code>.
     */
    public final Compatibilità COMPATIBILITÀ = Compatibilità.COMPATIBILITÀ;

    /**
     * The table <code>utenze.contatori</code>.
     */
    public final Contatori CONTATORI = Contatori.CONTATORI;

    /**
     * The table <code>utenze.contratti</code>.
     */
    public final Contratti CONTRATTI = Contratti.CONTRATTI;

    /**
     * The table <code>utenze.distributori</code>.
     */
    public final Distributori DISTRIBUTORI = Distributori.DISTRIBUTORI;

    /**
     * The table <code>utenze.distribuzioni</code>.
     */
    public final Distribuzioni DISTRIBUZIONI = Distribuzioni.DISTRIBUZIONI;

    /**
     * The table <code>utenze.immobili</code>.
     */
    public final Immobili IMMOBILI = Immobili.IMMOBILI;

    /**
     * The table <code>utenze.interruzioni</code>.
     */
    public final Interruzioni INTERRUZIONI = Interruzioni.INTERRUZIONI;

    /**
     * The table <code>utenze.letture</code>.
     */
    public final Letture LETTURE = Letture.LETTURE;

    /**
     * The table <code>utenze.materie_prime</code>.
     */
    public final MateriePrime MATERIE_PRIME = MateriePrime.MATERIE_PRIME;

    /**
     * The table <code>utenze.offerte</code>.
     */
    public final Offerte OFFERTE = Offerte.OFFERTE;

    /**
     * The table <code>utenze.persone_fisiche</code>.
     */
    public final PersoneFisiche PERSONE_FISICHE = PersoneFisiche.PERSONE_FISICHE;

    /**
     * The table <code>utenze.persone_giuridiche</code>.
     */
    public final PersoneGiuridiche PERSONE_GIURIDICHE = PersoneGiuridiche.PERSONE_GIURIDICHE;

    /**
     * The table <code>utenze.redditi</code>.
     */
    public final Redditi REDDITI = Redditi.REDDITI;

    /**
     * The table <code>utenze.tipi_attivazione</code>.
     */
    public final TipiAttivazione TIPI_ATTIVAZIONE = TipiAttivazione.TIPI_ATTIVAZIONE;

    /**
     * The table <code>utenze.tipologie_uso</code>.
     */
    public final TipologieUso TIPOLOGIE_USO = TipologieUso.TIPOLOGIE_USO;

    /**
     * The table <code>utenze.zone</code>.
     */
    public final Zone ZONE = Zone.ZONE;

    /**
     * No further instances allowed
     */
    private Utenze() {
        super("utenze", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Bollette.BOLLETTE,
            Compatibilità.COMPATIBILITÀ,
            Contatori.CONTATORI,
            Contratti.CONTRATTI,
            Distributori.DISTRIBUTORI,
            Distribuzioni.DISTRIBUZIONI,
            Immobili.IMMOBILI,
            Interruzioni.INTERRUZIONI,
            Letture.LETTURE,
            MateriePrime.MATERIE_PRIME,
            Offerte.OFFERTE,
            PersoneFisiche.PERSONE_FISICHE,
            PersoneGiuridiche.PERSONE_GIURIDICHE,
            Redditi.REDDITI,
            TipiAttivazione.TIPI_ATTIVAZIONE,
            TipologieUso.TIPOLOGIE_USO,
            Zone.ZONE
        );
    }
}
