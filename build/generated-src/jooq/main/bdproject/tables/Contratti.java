/*
 * This file is generated by jOOQ.
 */
package bdproject.tables;


import bdproject.Keys;
import bdproject.Utenze;
import bdproject.tables.records.ContrattiRecord;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Contratti extends TableImpl<ContrattiRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>utenze.contratti</code>
     */
    public static final Contratti CONTRATTI = new Contratti();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContrattiRecord> getRecordType() {
        return ContrattiRecord.class;
    }

    /**
     * The column <code>utenze.contratti.IdContratto</code>.
     */
    public final TableField<ContrattiRecord, Integer> IDCONTRATTO = createField(DSL.name("IdContratto"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti.DataInizio</code>.
     */
    public final TableField<ContrattiRecord, LocalDate> DATAINIZIO = createField(DSL.name("DataInizio"), SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>utenze.contratti.DataCessazione</code>.
     */
    public final TableField<ContrattiRecord, LocalDate> DATACESSAZIONE = createField(DSL.name("DataCessazione"), SQLDataType.LOCALDATE, this, "");

    /**
     * The column <code>utenze.contratti.DataUltimaBolletta</code>.
     */
    public final TableField<ContrattiRecord, LocalDate> DATAULTIMABOLLETTA = createField(DSL.name("DataUltimaBolletta"), SQLDataType.LOCALDATE, this, "");

    private Contratti(Name alias, Table<ContrattiRecord> aliased) {
        this(alias, aliased, null);
    }

    private Contratti(Name alias, Table<ContrattiRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>utenze.contratti</code> table reference
     */
    public Contratti(String alias) {
        this(DSL.name(alias), CONTRATTI);
    }

    /**
     * Create an aliased <code>utenze.contratti</code> table reference
     */
    public Contratti(Name alias) {
        this(alias, CONTRATTI);
    }

    /**
     * Create a <code>utenze.contratti</code> table reference
     */
    public Contratti() {
        this(DSL.name("contratti"), null);
    }

    public <O extends Record> Contratti(Table<O> child, ForeignKey<O, ContrattiRecord> key) {
        super(child, key, CONTRATTI);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Utenze.UTENZE;
    }

    @Override
    public UniqueKey<ContrattiRecord> getPrimaryKey() {
        return Keys.KEY_CONTRATTI_PRIMARY;
    }

    @Override
    public List<ForeignKey<ContrattiRecord, ?>> getReferences() {
        return Arrays.asList(Keys.FK_DEFINIZIONE);
    }

    private transient RichiesteAttivazione _richiesteAttivazione;

    public RichiesteAttivazione richiesteAttivazione() {
        if (_richiesteAttivazione == null)
            _richiesteAttivazione = new RichiesteAttivazione(this, Keys.FK_DEFINIZIONE);

        return _richiesteAttivazione;
    }

    @Override
    public Contratti as(String alias) {
        return new Contratti(DSL.name(alias), this);
    }

    @Override
    public Contratti as(Name alias) {
        return new Contratti(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Contratti rename(String name) {
        return new Contratti(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Contratti rename(Name name) {
        return new Contratti(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, LocalDate, LocalDate, LocalDate> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
