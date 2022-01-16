/*
 * This file is generated by jOOQ.
 */
package bdproject.tables;


import bdproject.Utenze;
import bdproject.tables.records.ContrattiApprovatiRecord;

import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row11;
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
public class ContrattiApprovati extends TableImpl<ContrattiApprovatiRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>utenze.contratti approvati</code>
     */
    public static final ContrattiApprovati CONTRATTI_APPROVATI = new ContrattiApprovati();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContrattiApprovatiRecord> getRecordType() {
        return ContrattiApprovatiRecord.class;
    }

    /**
     * The column <code>utenze.contratti approvati.IdContratto</code>.
     */
    public final TableField<ContrattiApprovatiRecord, Integer> IDCONTRATTO = createField(DSL.name("IdContratto"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>utenze.contratti approvati.DataAperturaRichiesta</code>.
     */
    public final TableField<ContrattiApprovatiRecord, LocalDate> DATAAPERTURARICHIESTA = createField(DSL.name("DataAperturaRichiesta"), SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti approvati.DataChiusuraRichiesta</code>.
     */
    public final TableField<ContrattiApprovatiRecord, LocalDate> DATACHIUSURARICHIESTA = createField(DSL.name("DataChiusuraRichiesta"), SQLDataType.LOCALDATE, this, "");

    /**
     * The column <code>utenze.contratti approvati.StatoRichiesta</code>.
     */
    public final TableField<ContrattiApprovatiRecord, String> STATORICHIESTA = createField(DSL.name("StatoRichiesta"), SQLDataType.VARCHAR(30).nullable(false), this, "");

    /**
     * The column <code>utenze.contratti approvati.NoteRichiesta</code>.
     */
    public final TableField<ContrattiApprovatiRecord, String> NOTERICHIESTA = createField(DSL.name("NoteRichiesta"), SQLDataType.VARCHAR(500).nullable(false), this, "");

    /**
     * The column <code>utenze.contratti approvati.NumeroComponenti</code>.
     */
    public final TableField<ContrattiApprovatiRecord, Integer> NUMEROCOMPONENTI = createField(DSL.name("NumeroComponenti"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti approvati.Uso</code>.
     */
    public final TableField<ContrattiApprovatiRecord, Integer> USO = createField(DSL.name("Uso"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti approvati.Offerta</code>.
     */
    public final TableField<ContrattiApprovatiRecord, Integer> OFFERTA = createField(DSL.name("Offerta"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti approvati.TipoAttivazione</code>.
     */
    public final TableField<ContrattiApprovatiRecord, Integer> TIPOATTIVAZIONE = createField(DSL.name("TipoAttivazione"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti approvati.IdImmobile</code>.
     */
    public final TableField<ContrattiApprovatiRecord, Integer> IDIMMOBILE = createField(DSL.name("IdImmobile"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti approvati.IdCliente</code>.
     */
    public final TableField<ContrattiApprovatiRecord, Integer> IDCLIENTE = createField(DSL.name("IdCliente"), SQLDataType.INTEGER.nullable(false), this, "");

    private ContrattiApprovati(Name alias, Table<ContrattiApprovatiRecord> aliased) {
        this(alias, aliased, null);
    }

    private ContrattiApprovati(Name alias, Table<ContrattiApprovatiRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("VIEW"), TableOptions.view("create view `contratti approvati` as select `C`.`IdContratto` AS `IdContratto`,`C`.`DataAperturaRichiesta` AS `DataAperturaRichiesta`,`C`.`DataChiusuraRichiesta` AS `DataChiusuraRichiesta`,`C`.`StatoRichiesta` AS `StatoRichiesta`,`C`.`NoteRichiesta` AS `NoteRichiesta`,`C`.`NumeroComponenti` AS `NumeroComponenti`,`C`.`Uso` AS `Uso`,`C`.`Offerta` AS `Offerta`,`C`.`TipoAttivazione` AS `TipoAttivazione`,`C`.`IdImmobile` AS `IdImmobile`,`C`.`IdCliente` AS `IdCliente` from `utenze`.`contratti` `C` where ((`C`.`DataChiusuraRichiesta` is not null) and (`C`.`StatoRichiesta` = 'Approvata'))"));
    }

    /**
     * Create an aliased <code>utenze.contratti approvati</code> table reference
     */
    public ContrattiApprovati(String alias) {
        this(DSL.name(alias), CONTRATTI_APPROVATI);
    }

    /**
     * Create an aliased <code>utenze.contratti approvati</code> table reference
     */
    public ContrattiApprovati(Name alias) {
        this(alias, CONTRATTI_APPROVATI);
    }

    /**
     * Create a <code>utenze.contratti approvati</code> table reference
     */
    public ContrattiApprovati() {
        this(DSL.name("contratti approvati"), null);
    }

    public <O extends Record> ContrattiApprovati(Table<O> child, ForeignKey<O, ContrattiApprovatiRecord> key) {
        super(child, key, CONTRATTI_APPROVATI);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Utenze.UTENZE;
    }

    @Override
    public ContrattiApprovati as(String alias) {
        return new ContrattiApprovati(DSL.name(alias), this);
    }

    @Override
    public ContrattiApprovati as(Name alias) {
        return new ContrattiApprovati(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ContrattiApprovati rename(String name) {
        return new ContrattiApprovati(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContrattiApprovati rename(Name name) {
        return new ContrattiApprovati(name, null);
    }

    // -------------------------------------------------------------------------
    // Row11 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row11<Integer, LocalDate, LocalDate, String, String, Integer, Integer, Integer, Integer, Integer, Integer> fieldsRow() {
        return (Row11) super.fieldsRow();
    }
}
