/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.Bollette;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BolletteRecord extends UpdatableRecordImpl<BolletteRecord> implements Record10<Integer, LocalDate, LocalDate, LocalDate, LocalDate, BigDecimal, BigDecimal, byte[], Integer, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.bollette.NumeroBolletta</code>.
     */
    public BolletteRecord setNumerobolletta(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.bollette.NumeroBolletta</code>.
     */
    public Integer getNumerobolletta() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>utenze.bollette.DataEmissione</code>.
     */
    public BolletteRecord setDataemissione(LocalDate value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.bollette.DataEmissione</code>.
     */
    public LocalDate getDataemissione() {
        return (LocalDate) get(1);
    }

    /**
     * Setter for <code>utenze.bollette.DataInizioPeriodo</code>.
     */
    public BolletteRecord setDatainizioperiodo(LocalDate value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>utenze.bollette.DataInizioPeriodo</code>.
     */
    public LocalDate getDatainizioperiodo() {
        return (LocalDate) get(2);
    }

    /**
     * Setter for <code>utenze.bollette.DataFinePeriodo</code>.
     */
    public BolletteRecord setDatafineperiodo(LocalDate value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>utenze.bollette.DataFinePeriodo</code>.
     */
    public LocalDate getDatafineperiodo() {
        return (LocalDate) get(3);
    }

    /**
     * Setter for <code>utenze.bollette.DataScadenza</code>.
     */
    public BolletteRecord setDatascadenza(LocalDate value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>utenze.bollette.DataScadenza</code>.
     */
    public LocalDate getDatascadenza() {
        return (LocalDate) get(4);
    }

    /**
     * Setter for <code>utenze.bollette.Importo</code>.
     */
    public BolletteRecord setImporto(BigDecimal value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>utenze.bollette.Importo</code>.
     */
    public BigDecimal getImporto() {
        return (BigDecimal) get(5);
    }

    /**
     * Setter for <code>utenze.bollette.Consumi</code>.
     */
    public BolletteRecord setConsumi(BigDecimal value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>utenze.bollette.Consumi</code>.
     */
    public BigDecimal getConsumi() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>utenze.bollette.DocumentoDettagliato</code>.
     */
    public BolletteRecord setDocumentodettagliato(byte[] value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>utenze.bollette.DocumentoDettagliato</code>.
     */
    public byte[] getDocumentodettagliato() {
        return (byte[]) get(7);
    }

    /**
     * Setter for <code>utenze.bollette.IdOperatore</code>.
     */
    public BolletteRecord setIdoperatore(Integer value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>utenze.bollette.IdOperatore</code>.
     */
    public Integer getIdoperatore() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>utenze.bollette.IdContratto</code>.
     */
    public BolletteRecord setIdcontratto(Integer value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>utenze.bollette.IdContratto</code>.
     */
    public Integer getIdcontratto() {
        return (Integer) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row10<Integer, LocalDate, LocalDate, LocalDate, LocalDate, BigDecimal, BigDecimal, byte[], Integer, Integer> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    public Row10<Integer, LocalDate, LocalDate, LocalDate, LocalDate, BigDecimal, BigDecimal, byte[], Integer, Integer> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Bollette.BOLLETTE.NUMEROBOLLETTA;
    }

    @Override
    public Field<LocalDate> field2() {
        return Bollette.BOLLETTE.DATAEMISSIONE;
    }

    @Override
    public Field<LocalDate> field3() {
        return Bollette.BOLLETTE.DATAINIZIOPERIODO;
    }

    @Override
    public Field<LocalDate> field4() {
        return Bollette.BOLLETTE.DATAFINEPERIODO;
    }

    @Override
    public Field<LocalDate> field5() {
        return Bollette.BOLLETTE.DATASCADENZA;
    }

    @Override
    public Field<BigDecimal> field6() {
        return Bollette.BOLLETTE.IMPORTO;
    }

    @Override
    public Field<BigDecimal> field7() {
        return Bollette.BOLLETTE.CONSUMI;
    }

    @Override
    public Field<byte[]> field8() {
        return Bollette.BOLLETTE.DOCUMENTODETTAGLIATO;
    }

    @Override
    public Field<Integer> field9() {
        return Bollette.BOLLETTE.IDOPERATORE;
    }

    @Override
    public Field<Integer> field10() {
        return Bollette.BOLLETTE.IDCONTRATTO;
    }

    @Override
    public Integer component1() {
        return getNumerobolletta();
    }

    @Override
    public LocalDate component2() {
        return getDataemissione();
    }

    @Override
    public LocalDate component3() {
        return getDatainizioperiodo();
    }

    @Override
    public LocalDate component4() {
        return getDatafineperiodo();
    }

    @Override
    public LocalDate component5() {
        return getDatascadenza();
    }

    @Override
    public BigDecimal component6() {
        return getImporto();
    }

    @Override
    public BigDecimal component7() {
        return getConsumi();
    }

    @Override
    public byte[] component8() {
        return getDocumentodettagliato();
    }

    @Override
    public Integer component9() {
        return getIdoperatore();
    }

    @Override
    public Integer component10() {
        return getIdcontratto();
    }

    @Override
    public Integer value1() {
        return getNumerobolletta();
    }

    @Override
    public LocalDate value2() {
        return getDataemissione();
    }

    @Override
    public LocalDate value3() {
        return getDatainizioperiodo();
    }

    @Override
    public LocalDate value4() {
        return getDatafineperiodo();
    }

    @Override
    public LocalDate value5() {
        return getDatascadenza();
    }

    @Override
    public BigDecimal value6() {
        return getImporto();
    }

    @Override
    public BigDecimal value7() {
        return getConsumi();
    }

    @Override
    public byte[] value8() {
        return getDocumentodettagliato();
    }

    @Override
    public Integer value9() {
        return getIdoperatore();
    }

    @Override
    public Integer value10() {
        return getIdcontratto();
    }

    @Override
    public BolletteRecord value1(Integer value) {
        setNumerobolletta(value);
        return this;
    }

    @Override
    public BolletteRecord value2(LocalDate value) {
        setDataemissione(value);
        return this;
    }

    @Override
    public BolletteRecord value3(LocalDate value) {
        setDatainizioperiodo(value);
        return this;
    }

    @Override
    public BolletteRecord value4(LocalDate value) {
        setDatafineperiodo(value);
        return this;
    }

    @Override
    public BolletteRecord value5(LocalDate value) {
        setDatascadenza(value);
        return this;
    }

    @Override
    public BolletteRecord value6(BigDecimal value) {
        setImporto(value);
        return this;
    }

    @Override
    public BolletteRecord value7(BigDecimal value) {
        setConsumi(value);
        return this;
    }

    @Override
    public BolletteRecord value8(byte[] value) {
        setDocumentodettagliato(value);
        return this;
    }

    @Override
    public BolletteRecord value9(Integer value) {
        setIdoperatore(value);
        return this;
    }

    @Override
    public BolletteRecord value10(Integer value) {
        setIdcontratto(value);
        return this;
    }

    @Override
    public BolletteRecord values(Integer value1, LocalDate value2, LocalDate value3, LocalDate value4, LocalDate value5, BigDecimal value6, BigDecimal value7, byte[] value8, Integer value9, Integer value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BolletteRecord
     */
    public BolletteRecord() {
        super(Bollette.BOLLETTE);
    }

    /**
     * Create a detached, initialised BolletteRecord
     */
    public BolletteRecord(Integer numerobolletta, LocalDate dataemissione, LocalDate datainizioperiodo, LocalDate datafineperiodo, LocalDate datascadenza, BigDecimal importo, BigDecimal consumi, byte[] documentodettagliato, Integer idoperatore, Integer idcontratto) {
        super(Bollette.BOLLETTE);

        setNumerobolletta(numerobolletta);
        setDataemissione(dataemissione);
        setDatainizioperiodo(datainizioperiodo);
        setDatafineperiodo(datafineperiodo);
        setDatascadenza(datascadenza);
        setImporto(importo);
        setConsumi(consumi);
        setDocumentodettagliato(documentodettagliato);
        setIdoperatore(idoperatore);
        setIdcontratto(idcontratto);
    }

    /**
     * Create a detached, initialised BolletteRecord
     */
    public BolletteRecord(bdproject.tables.pojos.Bollette value) {
        super(Bollette.BOLLETTE);

        if (value != null) {
            setNumerobolletta(value.getNumerobolletta());
            setDataemissione(value.getDataemissione());
            setDatainizioperiodo(value.getDatainizioperiodo());
            setDatafineperiodo(value.getDatafineperiodo());
            setDatascadenza(value.getDatascadenza());
            setImporto(value.getImporto());
            setConsumi(value.getConsumi());
            setDocumentodettagliato(value.getDocumentodettagliato());
            setIdoperatore(value.getIdoperatore());
            setIdcontratto(value.getIdcontratto());
        }
    }
}
