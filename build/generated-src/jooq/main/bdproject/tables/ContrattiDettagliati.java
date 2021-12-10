/*
 * This file is generated by jOOQ.
 */
package bdproject.tables;


import bdproject.Utenze;
import bdproject.tables.records.ContrattiDettagliatiRecord;

import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row10;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * VIEW
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ContrattiDettagliati extends TableImpl<ContrattiDettagliatiRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>utenze.contratti_dettagliati</code>
     */
    public static final ContrattiDettagliati CONTRATTI_DETTAGLIATI = new ContrattiDettagliati();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContrattiDettagliatiRecord> getRecordType() {
        return ContrattiDettagliatiRecord.class;
    }

    /**
     * The column <code>utenze.contratti_dettagliati.IdContratto</code>.
     */
    public final TableField<ContrattiDettagliatiRecord, Integer> IDCONTRATTO = createField(DSL.name("IdContratto"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti_dettagliati.DataInizio</code>.
     */
    public final TableField<ContrattiDettagliatiRecord, LocalDate> DATAINIZIO = createField(DSL.name("DataInizio"), SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti_dettagliati.DataCessazione</code>.
     */
    public final TableField<ContrattiDettagliatiRecord, LocalDate> DATACESSAZIONE = createField(DSL.name("DataCessazione"), SQLDataType.LOCALDATE, this, "");

    /**
     * The column <code>utenze.contratti_dettagliati.DataRichiesta</code>.
     */
    public final TableField<ContrattiDettagliatiRecord, LocalDate> DATARICHIESTA = createField(DSL.name("DataRichiesta"), SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti_dettagliati.Cliente</code>.
     */
    public final TableField<ContrattiDettagliatiRecord, Integer> CLIENTE = createField(DSL.name("Cliente"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti_dettagliati.Offerta</code>.
     */
    public final TableField<ContrattiDettagliatiRecord, Integer> OFFERTA = createField(DSL.name("Offerta"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti_dettagliati.Attivazione</code>.
     */
    public final TableField<ContrattiDettagliatiRecord, Integer> ATTIVAZIONE = createField(DSL.name("Attivazione"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti_dettagliati.Uso</code>.
     */
    public final TableField<ContrattiDettagliatiRecord, Integer> USO = createField(DSL.name("Uso"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti_dettagliati.Contatore</code>.
     */
    public final TableField<ContrattiDettagliatiRecord, Integer> CONTATORE = createField(DSL.name("Contatore"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti_dettagliati.NumeroComponenti</code>.
     */
    public final TableField<ContrattiDettagliatiRecord, Integer> NUMEROCOMPONENTI = createField(DSL.name("NumeroComponenti"), SQLDataType.INTEGER.nullable(false), this, "");

    private ContrattiDettagliati(Name alias, Table<ContrattiDettagliatiRecord> aliased) {
        this(alias, aliased, null);
    }

    private ContrattiDettagliati(Name alias, Table<ContrattiDettagliatiRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("VIEW"), TableOptions.view("create view `contratti_dettagliati` as select `C`.`IdContratto` AS `IdContratto`,`C`.`DataInizio` AS `DataInizio`,`C`.`DataCessazione` AS `DataCessazione`,`R`.`DataRichiesta` AS `DataRichiesta`,`R`.`Cliente` AS `Cliente`,`R`.`Offerta` AS `Offerta`,`R`.`Attivazione` AS `Attivazione`,`R`.`Uso` AS `Uso`,`R`.`Contatore` AS `Contatore`,`R`.`NumeroComponenti` AS `NumeroComponenti` from `utenze`.`contratti` `C` join `utenze`.`richieste_attivazione` `R` where (`C`.`IdContratto` = `R`.`Numero`)"));
    }

    /**
     * Create an aliased <code>utenze.contratti_dettagliati</code> table
     * reference
     */
    public ContrattiDettagliati(String alias) {
        this(DSL.name(alias), CONTRATTI_DETTAGLIATI);
    }

    /**
     * Create an aliased <code>utenze.contratti_dettagliati</code> table
     * reference
     */
    public ContrattiDettagliati(Name alias) {
        this(alias, CONTRATTI_DETTAGLIATI);
    }

    /**
     * Create a <code>utenze.contratti_dettagliati</code> table reference
     */
    public ContrattiDettagliati() {
        this(DSL.name("contratti_dettagliati"), null);
    }

    public <O extends Record> ContrattiDettagliati(Table<O> child, ForeignKey<O, ContrattiDettagliatiRecord> key) {
        super(child, key, CONTRATTI_DETTAGLIATI);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Utenze.UTENZE;
    }

    @Override
    public ContrattiDettagliati as(String alias) {
        return new ContrattiDettagliati(DSL.name(alias), this);
    }

    @Override
    public ContrattiDettagliati as(Name alias) {
        return new ContrattiDettagliati(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ContrattiDettagliati rename(String name) {
        return new ContrattiDettagliati(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContrattiDettagliati rename(Name name) {
        return new ContrattiDettagliati(name, null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<Integer, LocalDate, LocalDate, LocalDate, Integer, Integer, Integer, Integer, Integer, Integer> fieldsRow() {
        return (Row10) super.fieldsRow();
    }
}
