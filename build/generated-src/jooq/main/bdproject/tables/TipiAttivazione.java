/*
 * This file is generated by jOOQ.
 */
package bdproject.tables;


import bdproject.Keys;
import bdproject.Utenze;
import bdproject.tables.records.TipiAttivazioneRecord;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.jooq.Check;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TipiAttivazione extends TableImpl<TipiAttivazioneRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>utenze.tipi_attivazione</code>
     */
    public static final TipiAttivazione TIPI_ATTIVAZIONE = new TipiAttivazione();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TipiAttivazioneRecord> getRecordType() {
        return TipiAttivazioneRecord.class;
    }

    /**
     * The column <code>utenze.tipi_attivazione.CodAttivazione</code>.
     */
    public final TableField<TipiAttivazioneRecord, Integer> CODATTIVAZIONE = createField(DSL.name("CodAttivazione"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.tipi_attivazione.Nome</code>.
     */
    public final TableField<TipiAttivazioneRecord, String> NOME = createField(DSL.name("Nome"), SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>utenze.tipi_attivazione.Costo</code>.
     */
    public final TableField<TipiAttivazioneRecord, BigDecimal> COSTO = createField(DSL.name("Costo"), SQLDataType.DECIMAL(20, 2).nullable(false), this, "");

    private TipiAttivazione(Name alias, Table<TipiAttivazioneRecord> aliased) {
        this(alias, aliased, null);
    }

    private TipiAttivazione(Name alias, Table<TipiAttivazioneRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>utenze.tipi_attivazione</code> table reference
     */
    public TipiAttivazione(String alias) {
        this(DSL.name(alias), TIPI_ATTIVAZIONE);
    }

    /**
     * Create an aliased <code>utenze.tipi_attivazione</code> table reference
     */
    public TipiAttivazione(Name alias) {
        this(alias, TIPI_ATTIVAZIONE);
    }

    /**
     * Create a <code>utenze.tipi_attivazione</code> table reference
     */
    public TipiAttivazione() {
        this(DSL.name("tipi_attivazione"), null);
    }

    public <O extends Record> TipiAttivazione(Table<O> child, ForeignKey<O, TipiAttivazioneRecord> key) {
        super(child, key, TIPI_ATTIVAZIONE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Utenze.UTENZE;
    }

    @Override
    public UniqueKey<TipiAttivazioneRecord> getPrimaryKey() {
        return Keys.KEY_TIPI_ATTIVAZIONE_PRIMARY;
    }

    @Override
    public List<Check<TipiAttivazioneRecord>> getChecks() {
        return Arrays.asList(
            Internal.createCheck(this, DSL.name("tipi_attivazione_chk_1"), "(`Costo` >= 0)", true)
        );
    }

    @Override
    public TipiAttivazione as(String alias) {
        return new TipiAttivazione(DSL.name(alias), this);
    }

    @Override
    public TipiAttivazione as(Name alias) {
        return new TipiAttivazione(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TipiAttivazione rename(String name) {
        return new TipiAttivazione(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TipiAttivazione rename(Name name) {
        return new TipiAttivazione(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, BigDecimal> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
