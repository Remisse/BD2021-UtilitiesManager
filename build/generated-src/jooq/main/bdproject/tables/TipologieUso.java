/*
 * This file is generated by jOOQ.
 */
package bdproject.tables;


import bdproject.Keys;
import bdproject.Utenze;
import bdproject.tables.records.TipologieUsoRecord;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.jooq.Check;
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
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TipologieUso extends TableImpl<TipologieUsoRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>utenze.tipologie uso</code>
     */
    public static final TipologieUso TIPOLOGIE_USO = new TipologieUso();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TipologieUsoRecord> getRecordType() {
        return TipologieUsoRecord.class;
    }

    /**
     * The column <code>utenze.tipologie uso.CodUso</code>.
     */
    public final TableField<TipologieUsoRecord, Integer> CODUSO = createField(DSL.name("CodUso"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>utenze.tipologie uso.Nome</code>.
     */
    public final TableField<TipologieUsoRecord, String> NOME = createField(DSL.name("Nome"), SQLDataType.VARCHAR(30).nullable(false), this, "");

    /**
     * The column <code>utenze.tipologie uso.StimaPerPersona</code>.
     */
    public final TableField<TipologieUsoRecord, BigDecimal> STIMAPERPERSONA = createField(DSL.name("StimaPerPersona"), SQLDataType.DECIMAL(20, 2).nullable(false), this, "");

    /**
     * The column <code>utenze.tipologie uso.ScontoReddito</code>.
     */
    public final TableField<TipologieUsoRecord, Byte> SCONTOREDDITO = createField(DSL.name("ScontoReddito"), SQLDataType.TINYINT.nullable(false), this, "");

    private TipologieUso(Name alias, Table<TipologieUsoRecord> aliased) {
        this(alias, aliased, null);
    }

    private TipologieUso(Name alias, Table<TipologieUsoRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>utenze.tipologie uso</code> table reference
     */
    public TipologieUso(String alias) {
        this(DSL.name(alias), TIPOLOGIE_USO);
    }

    /**
     * Create an aliased <code>utenze.tipologie uso</code> table reference
     */
    public TipologieUso(Name alias) {
        this(alias, TIPOLOGIE_USO);
    }

    /**
     * Create a <code>utenze.tipologie uso</code> table reference
     */
    public TipologieUso() {
        this(DSL.name("tipologie uso"), null);
    }

    public <O extends Record> TipologieUso(Table<O> child, ForeignKey<O, TipologieUsoRecord> key) {
        super(child, key, TIPOLOGIE_USO);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Utenze.UTENZE;
    }

    @Override
    public Identity<TipologieUsoRecord, Integer> getIdentity() {
        return (Identity<TipologieUsoRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<TipologieUsoRecord> getPrimaryKey() {
        return Keys.KEY_TIPOLOGIE_USO_PRIMARY;
    }

    @Override
    public List<Check<TipologieUsoRecord>> getChecks() {
        return Arrays.asList(
            Internal.createCheck(this, DSL.name("tipologie uso_chk_1"), "(`StimaPerPersona` >= 0.0)", true)
        );
    }

    @Override
    public TipologieUso as(String alias) {
        return new TipologieUso(DSL.name(alias), this);
    }

    @Override
    public TipologieUso as(Name alias) {
        return new TipologieUso(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TipologieUso rename(String name) {
        return new TipologieUso(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TipologieUso rename(Name name) {
        return new TipologieUso(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, BigDecimal, Byte> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
