/*
 * This file is generated by jOOQ.
 */
package bdproject.tables;


import bdproject.Keys;
import bdproject.Utenze;
import bdproject.tables.records.PersoneRecord;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row13;
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
public class Persone extends TableImpl<PersoneRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>utenze.persone</code>
     */
    public static final Persone PERSONE = new Persone();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PersoneRecord> getRecordType() {
        return PersoneRecord.class;
    }

    /**
     * The column <code>utenze.persone.IdPersona</code>.
     */
    public final TableField<PersoneRecord, Integer> IDPERSONA = createField(DSL.name("IdPersona"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>utenze.persone.Nome</code>.
     */
    public final TableField<PersoneRecord, String> NOME = createField(DSL.name("Nome"), SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * The column <code>utenze.persone.Cognome</code>.
     */
    public final TableField<PersoneRecord, String> COGNOME = createField(DSL.name("Cognome"), SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * The column <code>utenze.persone.CodiceFiscale</code>.
     */
    public final TableField<PersoneRecord, String> CODICEFISCALE = createField(DSL.name("CodiceFiscale"), SQLDataType.VARCHAR(16).nullable(false), this, "");

    /**
     * The column <code>utenze.persone.Via</code>.
     */
    public final TableField<PersoneRecord, String> VIA = createField(DSL.name("Via"), SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * The column <code>utenze.persone.NumCivico</code>.
     */
    public final TableField<PersoneRecord, String> NUMCIVICO = createField(DSL.name("NumCivico"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * The column <code>utenze.persone.Comune</code>.
     */
    public final TableField<PersoneRecord, String> COMUNE = createField(DSL.name("Comune"), SQLDataType.VARCHAR(30).nullable(false), this, "");

    /**
     * The column <code>utenze.persone.CAP</code>.
     */
    public final TableField<PersoneRecord, String> CAP = createField(DSL.name("CAP"), SQLDataType.VARCHAR(5).nullable(false), this, "");

    /**
     * The column <code>utenze.persone.Provincia</code>.
     */
    public final TableField<PersoneRecord, String> PROVINCIA = createField(DSL.name("Provincia"), SQLDataType.VARCHAR(2).nullable(false), this, "");

    /**
     * The column <code>utenze.persone.DataNascita</code>.
     */
    public final TableField<PersoneRecord, LocalDate> DATANASCITA = createField(DSL.name("DataNascita"), SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>utenze.persone.NumeroTelefono</code>.
     */
    public final TableField<PersoneRecord, String> NUMEROTELEFONO = createField(DSL.name("NumeroTelefono"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * The column <code>utenze.persone.Email</code>.
     */
    public final TableField<PersoneRecord, String> EMAIL = createField(DSL.name("Email"), SQLDataType.VARCHAR(40).nullable(false), this, "");

    /**
     * The column <code>utenze.persone.Password</code>.
     */
    public final TableField<PersoneRecord, String> PASSWORD = createField(DSL.name("Password"), SQLDataType.VARCHAR(32).nullable(false), this, "");

    private Persone(Name alias, Table<PersoneRecord> aliased) {
        this(alias, aliased, null);
    }

    private Persone(Name alias, Table<PersoneRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>utenze.persone</code> table reference
     */
    public Persone(String alias) {
        this(DSL.name(alias), PERSONE);
    }

    /**
     * Create an aliased <code>utenze.persone</code> table reference
     */
    public Persone(Name alias) {
        this(alias, PERSONE);
    }

    /**
     * Create a <code>utenze.persone</code> table reference
     */
    public Persone() {
        this(DSL.name("persone"), null);
    }

    public <O extends Record> Persone(Table<O> child, ForeignKey<O, PersoneRecord> key) {
        super(child, key, PERSONE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Utenze.UTENZE;
    }

    @Override
    public Identity<PersoneRecord, Integer> getIdentity() {
        return (Identity<PersoneRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<PersoneRecord> getPrimaryKey() {
        return Keys.KEY_PERSONE_PRIMARY;
    }

    @Override
    public List<UniqueKey<PersoneRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.KEY_PERSONE_AK_PERSONA);
    }

    @Override
    public Persone as(String alias) {
        return new Persone(DSL.name(alias), this);
    }

    @Override
    public Persone as(Name alias) {
        return new Persone(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Persone rename(String name) {
        return new Persone(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Persone rename(Name name) {
        return new Persone(name, null);
    }

    // -------------------------------------------------------------------------
    // Row13 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row13<Integer, String, String, String, String, String, String, String, String, LocalDate, String, String, String> fieldsRow() {
        return (Row13) super.fieldsRow();
    }
}
