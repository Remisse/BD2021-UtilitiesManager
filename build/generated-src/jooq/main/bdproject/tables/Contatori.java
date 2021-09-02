/*
 * This file is generated by jOOQ.
 */
package bdproject.tables;


import bdproject.Keys;
import bdproject.Utenze;
import bdproject.tables.records.ContatoriRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
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
public class Contatori extends TableImpl<ContatoriRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>utenze.contatori</code>
     */
    public static final Contatori CONTATORI = new Contatori();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContatoriRecord> getRecordType() {
        return ContatoriRecord.class;
    }

    /**
     * The column <code>utenze.contatori.Progressivo</code>.
     */
    public final TableField<ContatoriRecord, Integer> PROGRESSIVO = createField(DSL.name("Progressivo"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>utenze.contatori.Matricola</code>.
     */
    public final TableField<ContatoriRecord, String> MATRICOLA = createField(DSL.name("Matricola"), SQLDataType.VARCHAR(20), this, "");

    /**
     * The column <code>utenze.contatori.MateriaPrima</code>.
     */
    public final TableField<ContatoriRecord, String> MATERIAPRIMA = createField(DSL.name("MateriaPrima"), SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>utenze.contatori.IdImmobile</code>.
     */
    public final TableField<ContatoriRecord, Integer> IDIMMOBILE = createField(DSL.name("IdImmobile"), SQLDataType.INTEGER.nullable(false), this, "");

    private Contatori(Name alias, Table<ContatoriRecord> aliased) {
        this(alias, aliased, null);
    }

    private Contatori(Name alias, Table<ContatoriRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>utenze.contatori</code> table reference
     */
    public Contatori(String alias) {
        this(DSL.name(alias), CONTATORI);
    }

    /**
     * Create an aliased <code>utenze.contatori</code> table reference
     */
    public Contatori(Name alias) {
        this(alias, CONTATORI);
    }

    /**
     * Create a <code>utenze.contatori</code> table reference
     */
    public Contatori() {
        this(DSL.name("contatori"), null);
    }

    public <O extends Record> Contatori(Table<O> child, ForeignKey<O, ContatoriRecord> key) {
        super(child, key, CONTATORI);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Utenze.UTENZE;
    }

    @Override
    public Identity<ContatoriRecord, Integer> getIdentity() {
        return (Identity<ContatoriRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<ContatoriRecord> getPrimaryKey() {
        return Keys.KEY_CONTATORI_PRIMARY;
    }

    @Override
    public List<UniqueKey<ContatoriRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.KEY_CONTATORI_AK1_CONTATORI, Keys.KEY_CONTATORI_AK2_CONTATORI);
    }

    @Override
    public List<ForeignKey<ContatoriRecord, ?>> getReferences() {
        return Arrays.asList(Keys.FK_MISURAZIONE, Keys.FK_INSTALLAZIONE);
    }

    private transient MateriePrime _materiePrime;
    private transient Immobili _immobili;

    public MateriePrime materiePrime() {
        if (_materiePrime == null)
            _materiePrime = new MateriePrime(this, Keys.FK_MISURAZIONE);

        return _materiePrime;
    }

    public Immobili immobili() {
        if (_immobili == null)
            _immobili = new Immobili(this, Keys.FK_INSTALLAZIONE);

        return _immobili;
    }

    @Override
    public Contatori as(String alias) {
        return new Contatori(DSL.name(alias), this);
    }

    @Override
    public Contatori as(Name alias) {
        return new Contatori(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Contatori rename(String name) {
        return new Contatori(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Contatori rename(Name name) {
        return new Contatori(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, String, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
