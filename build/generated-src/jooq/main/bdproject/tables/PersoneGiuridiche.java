/*
 * This file is generated by jOOQ.
 */
package bdproject.tables;


import bdproject.Keys;
import bdproject.Utenze;
import bdproject.tables.records.PersoneGiuridicheRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
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
public class PersoneGiuridiche extends TableImpl<PersoneGiuridicheRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>utenze.persone_giuridiche</code>
     */
    public static final PersoneGiuridiche PERSONE_GIURIDICHE = new PersoneGiuridiche();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PersoneGiuridicheRecord> getRecordType() {
        return PersoneGiuridicheRecord.class;
    }

    /**
     * The column <code>utenze.persone_giuridiche.CodiceAzienda</code>.
     */
    public final TableField<PersoneGiuridicheRecord, Integer> CODICEAZIENDA = createField(DSL.name("CodiceAzienda"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>utenze.persone_giuridiche.PartitaIVA</code>.
     */
    public final TableField<PersoneGiuridicheRecord, String> PARTITAIVA = createField(DSL.name("PartitaIVA"), SQLDataType.VARCHAR(11).nullable(false), this, "");

    /**
     * The column <code>utenze.persone_giuridiche.Via</code>.
     */
    public final TableField<PersoneGiuridicheRecord, String> VIA = createField(DSL.name("Via"), SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * The column <code>utenze.persone_giuridiche.NumCivico</code>.
     */
    public final TableField<PersoneGiuridicheRecord, String> NUMCIVICO = createField(DSL.name("NumCivico"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * The column <code>utenze.persone_giuridiche.CAP</code>.
     */
    public final TableField<PersoneGiuridicheRecord, Integer> CAP = createField(DSL.name("CAP"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>utenze.persone_giuridiche.Comune</code>.
     */
    public final TableField<PersoneGiuridicheRecord, String> COMUNE = createField(DSL.name("Comune"), SQLDataType.VARCHAR(30).nullable(false), this, "");

    /**
     * The column <code>utenze.persone_giuridiche.Provincia</code>.
     */
    public final TableField<PersoneGiuridicheRecord, String> PROVINCIA = createField(DSL.name("Provincia"), SQLDataType.VARCHAR(2).nullable(false), this, "");

    /**
     * The column <code>utenze.persone_giuridiche.FormaGiuridica</code>.
     */
    public final TableField<PersoneGiuridicheRecord, String> FORMAGIURIDICA = createField(DSL.name("FormaGiuridica"), SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * The column <code>utenze.persone_giuridiche.RagioneSociale</code>.
     */
    public final TableField<PersoneGiuridicheRecord, String> RAGIONESOCIALE = createField(DSL.name("RagioneSociale"), SQLDataType.VARCHAR(30).nullable(false), this, "");

    private PersoneGiuridiche(Name alias, Table<PersoneGiuridicheRecord> aliased) {
        this(alias, aliased, null);
    }

    private PersoneGiuridiche(Name alias, Table<PersoneGiuridicheRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>utenze.persone_giuridiche</code> table reference
     */
    public PersoneGiuridiche(String alias) {
        this(DSL.name(alias), PERSONE_GIURIDICHE);
    }

    /**
     * Create an aliased <code>utenze.persone_giuridiche</code> table reference
     */
    public PersoneGiuridiche(Name alias) {
        this(alias, PERSONE_GIURIDICHE);
    }

    /**
     * Create a <code>utenze.persone_giuridiche</code> table reference
     */
    public PersoneGiuridiche() {
        this(DSL.name("persone_giuridiche"), null);
    }

    public <O extends Record> PersoneGiuridiche(Table<O> child, ForeignKey<O, PersoneGiuridicheRecord> key) {
        super(child, key, PERSONE_GIURIDICHE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Utenze.UTENZE;
    }

    @Override
    public Identity<PersoneGiuridicheRecord, Integer> getIdentity() {
        return (Identity<PersoneGiuridicheRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<PersoneGiuridicheRecord> getPrimaryKey() {
        return Keys.KEY_PERSONE_GIURIDICHE_PRIMARY;
    }

    @Override
    public PersoneGiuridiche as(String alias) {
        return new PersoneGiuridiche(DSL.name(alias), this);
    }

    @Override
    public PersoneGiuridiche as(Name alias) {
        return new PersoneGiuridiche(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PersoneGiuridiche rename(String name) {
        return new PersoneGiuridiche(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PersoneGiuridiche rename(Name name) {
        return new PersoneGiuridiche(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<Integer, String, String, String, Integer, String, String, String, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
