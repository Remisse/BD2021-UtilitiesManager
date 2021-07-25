/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.TipologieUso;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TipologieUsoRecord extends UpdatableRecordImpl<TipologieUsoRecord> implements Record4<String, BigDecimal, BigDecimal, Byte> {

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
     * Setter for <code>utenze.tipologie_uso.CanoneRAI</code>.
     */
    public TipologieUsoRecord setCanonerai(BigDecimal value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.tipologie_uso.CanoneRAI</code>.
     */
    public BigDecimal getCanonerai() {
        return (BigDecimal) get(1);
    }

    /**
     * Setter for <code>utenze.tipologie_uso.StimaPerPersona</code>.
     */
    public TipologieUsoRecord setStimaperpersona(BigDecimal value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>utenze.tipologie_uso.StimaPerPersona</code>.
     */
    public BigDecimal getStimaperpersona() {
        return (BigDecimal) get(2);
    }

    /**
     * Setter for <code>utenze.tipologie_uso.ScontoReddito</code>.
     */
    public TipologieUsoRecord setScontoreddito(Byte value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>utenze.tipologie_uso.ScontoReddito</code>.
     */
    public Byte getScontoreddito() {
        return (Byte) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, BigDecimal, BigDecimal, Byte> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<String, BigDecimal, BigDecimal, Byte> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return TipologieUso.TIPOLOGIE_USO.NOME;
    }

    @Override
    public Field<BigDecimal> field2() {
        return TipologieUso.TIPOLOGIE_USO.CANONERAI;
    }

    @Override
    public Field<BigDecimal> field3() {
        return TipologieUso.TIPOLOGIE_USO.STIMAPERPERSONA;
    }

    @Override
    public Field<Byte> field4() {
        return TipologieUso.TIPOLOGIE_USO.SCONTOREDDITO;
    }

    @Override
    public String component1() {
        return getNome();
    }

    @Override
    public BigDecimal component2() {
        return getCanonerai();
    }

    @Override
    public BigDecimal component3() {
        return getStimaperpersona();
    }

    @Override
    public Byte component4() {
        return getScontoreddito();
    }

    @Override
    public String value1() {
        return getNome();
    }

    @Override
    public BigDecimal value2() {
        return getCanonerai();
    }

    @Override
    public BigDecimal value3() {
        return getStimaperpersona();
    }

    @Override
    public Byte value4() {
        return getScontoreddito();
    }

    @Override
    public TipologieUsoRecord value1(String value) {
        setNome(value);
        return this;
    }

    @Override
    public TipologieUsoRecord value2(BigDecimal value) {
        setCanonerai(value);
        return this;
    }

    @Override
    public TipologieUsoRecord value3(BigDecimal value) {
        setStimaperpersona(value);
        return this;
    }

    @Override
    public TipologieUsoRecord value4(Byte value) {
        setScontoreddito(value);
        return this;
    }

    @Override
    public TipologieUsoRecord values(String value1, BigDecimal value2, BigDecimal value3, Byte value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
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
    public TipologieUsoRecord(String nome, BigDecimal canonerai, BigDecimal stimaperpersona, Byte scontoreddito) {
        super(TipologieUso.TIPOLOGIE_USO);

        setNome(nome);
        setCanonerai(canonerai);
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
            setCanonerai(value.getCanonerai());
            setStimaperpersona(value.getStimaperpersona());
            setScontoreddito(value.getScontoreddito());
        }
    }
}
