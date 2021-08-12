/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.TipologieUso;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TipologieUsoRecord extends UpdatableRecordImpl<TipologieUsoRecord> implements Record3<String, BigDecimal, Byte> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.tipologie_uso.Nome</code>.
     */
    public TipologieUsoRecord setNome(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.tipologie_uso.Nome</code>.
     */
    public String getNome() {
        return (String) get(0);
    }

    /**
     * Setter for <code>utenze.tipologie_uso.StimaPerPersona</code>.
     */
    public TipologieUsoRecord setStimaperpersona(BigDecimal value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.tipologie_uso.StimaPerPersona</code>.
     */
    public BigDecimal getStimaperpersona() {
        return (BigDecimal) get(1);
    }

    /**
     * Setter for <code>utenze.tipologie_uso.ScontoReddito</code>.
     */
    public TipologieUsoRecord setScontoreddito(Byte value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>utenze.tipologie_uso.ScontoReddito</code>.
     */
    public Byte getScontoreddito() {
        return (Byte) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, BigDecimal, Byte> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<String, BigDecimal, Byte> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return TipologieUso.TIPOLOGIE_USO.NOME;
    }

    @Override
    public Field<BigDecimal> field2() {
        return TipologieUso.TIPOLOGIE_USO.STIMAPERPERSONA;
    }

    @Override
    public Field<Byte> field3() {
        return TipologieUso.TIPOLOGIE_USO.SCONTOREDDITO;
    }

    @Override
    public String component1() {
        return getNome();
    }

    @Override
    public BigDecimal component2() {
        return getStimaperpersona();
    }

    @Override
    public Byte component3() {
        return getScontoreddito();
    }

    @Override
    public String value1() {
        return getNome();
    }

    @Override
    public BigDecimal value2() {
        return getStimaperpersona();
    }

    @Override
    public Byte value3() {
        return getScontoreddito();
    }

    @Override
    public TipologieUsoRecord value1(String value) {
        setNome(value);
        return this;
    }

    @Override
    public TipologieUsoRecord value2(BigDecimal value) {
        setStimaperpersona(value);
        return this;
    }

    @Override
    public TipologieUsoRecord value3(Byte value) {
        setScontoreddito(value);
        return this;
    }

    @Override
    public TipologieUsoRecord values(String value1, BigDecimal value2, Byte value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TipologieUsoRecord
     */
    public TipologieUsoRecord() {
        super(TipologieUso.TIPOLOGIE_USO);
    }

    /**
     * Create a detached, initialised TipologieUsoRecord
     */
    public TipologieUsoRecord(String nome, BigDecimal stimaperpersona, Byte scontoreddito) {
        super(TipologieUso.TIPOLOGIE_USO);

        setNome(nome);
        setStimaperpersona(stimaperpersona);
        setScontoreddito(scontoreddito);
    }

    /**
     * Create a detached, initialised TipologieUsoRecord
     */
    public TipologieUsoRecord(bdproject.tables.pojos.TipologieUso value) {
        super(TipologieUso.TIPOLOGIE_USO);

        if (value != null) {
            setNome(value.getNome());
            setStimaperpersona(value.getStimaperpersona());
            setScontoreddito(value.getScontoreddito());
        }
    }
}
