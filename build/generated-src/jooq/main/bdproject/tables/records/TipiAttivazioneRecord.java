/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.TipiAttivazione;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TipiAttivazioneRecord extends UpdatableRecordImpl<TipiAttivazioneRecord> implements Record2<String, BigDecimal> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.tipi_attivazione.Nome</code>.
     */
    public TipiAttivazioneRecord setNome(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.tipi_attivazione.Nome</code>.
     */
    public String getNome() {
        return (String) get(0);
    }

    /**
     * Setter for <code>utenze.tipi_attivazione.CostoUnaTantum</code>.
     */
    public TipiAttivazioneRecord setCostounatantum(BigDecimal value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.tipi_attivazione.CostoUnaTantum</code>.
     */
    public BigDecimal getCostounatantum() {
        return (BigDecimal) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, BigDecimal> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, BigDecimal> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return TipiAttivazione.TIPI_ATTIVAZIONE.NOME;
    }

    @Override
    public Field<BigDecimal> field2() {
        return TipiAttivazione.TIPI_ATTIVAZIONE.COSTOUNATANTUM;
    }

    @Override
    public String component1() {
        return getNome();
    }

    @Override
    public BigDecimal component2() {
        return getCostounatantum();
    }

    @Override
    public String value1() {
        return getNome();
    }

    @Override
    public BigDecimal value2() {
        return getCostounatantum();
    }

    @Override
    public TipiAttivazioneRecord value1(String value) {
        setNome(value);
        return this;
    }

    @Override
    public TipiAttivazioneRecord value2(BigDecimal value) {
        setCostounatantum(value);
        return this;
    }

    @Override
    public TipiAttivazioneRecord values(String value1, BigDecimal value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TipiAttivazioneRecord
     */
    public TipiAttivazioneRecord() {
        super(TipiAttivazione.TIPI_ATTIVAZIONE);
    }

    /**
     * Create a detached, initialised TipiAttivazioneRecord
     */
    public TipiAttivazioneRecord(String nome, BigDecimal costounatantum) {
        super(TipiAttivazione.TIPI_ATTIVAZIONE);

        setNome(nome);
        setCostounatantum(costounatantum);
    }

    /**
     * Create a detached, initialised TipiAttivazioneRecord
     */
    public TipiAttivazioneRecord(bdproject.tables.pojos.TipiAttivazione value) {
        super(TipiAttivazione.TIPI_ATTIVAZIONE);

        if (value != null) {
            setNome(value.getNome());
            setCostounatantum(value.getCostounatantum());
        }
    }
}