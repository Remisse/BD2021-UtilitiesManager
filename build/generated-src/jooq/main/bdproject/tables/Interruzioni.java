/*
 * This file is generated by jOOQ.
 */
package bdproject.tables;


import bdproject.Keys;
import bdproject.Utenze;
import bdproject.tables.records.InterruzioniRecord;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.jooq.Check;
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
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Interruzioni extends TableImpl<InterruzioniRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>utenze.interruzioni</code>
     */
    public static final Interruzioni INTERRUZIONI = new Interruzioni();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<InterruzioniRecord> getRecordType() {
        return InterruzioniRecord.class;
    }

    /**
     * The column <code>utenze.interruzioni.IdContratto</code>.
     */
    public final TableField<InterruzioniRecord, Integer> IDCONTRATTO = createField(DSL.name("IdContratto"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.interruzioni.DataInterruzione</code>.
     */
    public final TableField<InterruzioniRecord, LocalDate> DATAINTERRUZIONE = createField(DSL.name("DataInterruzione"), SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>utenze.interruzioni.DataRiattivazione</code>.
     */
    public final TableField<InterruzioniRecord, LocalDate> DATARIATTIVAZIONE = createField(DSL.name("DataRiattivazione"), SQLDataType.LOCALDATE, this, "");

    /**
     * The column <code>utenze.interruzioni.Motivazione</code>.
     */
    public final TableField<InterruzioniRecord, String> MOTIVAZIONE = createField(DSL.name("Motivazione"), SQLDataType.VARCHAR(1000).nullable(false), this, "");

    private Interruzioni(Name alias, Table<InterruzioniRecord> aliased) {
        this(alias, aliased, null);
    }

    private Interruzioni(Name alias, Table<InterruzioniRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>utenze.interruzioni</code> table reference
     */
    public Interruzioni(String alias) {
        this(DSL.name(alias), INTERRUZIONI);
    }

    /**
     * Create an aliased <code>utenze.interruzioni</code> table reference
     */
    public Interruzioni(Name alias) {
        this(alias, INTERRUZIONI);
    }

    /**
     * Create a <code>utenze.interruzioni</code> table reference
     */
    public Interruzioni() {
        this(DSL.name("interruzioni"), null);
    }

    public <O extends Record> Interruzioni(Table<O> child, ForeignKey<O, InterruzioniRecord> key) {
        super(child, key, INTERRUZIONI);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Utenze.UTENZE;
    }

    @Override
    public UniqueKey<InterruzioniRecord> getPrimaryKey() {
        return Keys.KEY_INTERRUZIONI_PRIMARY;
    }

    @Override
    public List<Check<InterruzioniRecord>> getChecks() {
        return Arrays.asList(
            Internal.createCheck(this, DSL.name("interruzioni_chk_1"), "((`DataRiattivazione` is null) or (`DataRiattivazione` >= `DataInterruzione`))", true)
        );
    }

    @Override
    public Interruzioni as(String alias) {
        return new Interruzioni(DSL.name(alias), this);
    }

    @Override
    public Interruzioni as(Name alias) {
        return new Interruzioni(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Interruzioni rename(String name) {
        return new Interruzioni(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Interruzioni rename(Name name) {
        return new Interruzioni(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, LocalDate, LocalDate, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
