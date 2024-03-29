/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.Offerte;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OfferteRecord extends UpdatableRecordImpl<OfferteRecord> implements Record6<Integer, String, String, BigDecimal, Byte, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.offerte.CodOfferta</code>.
     */
    public OfferteRecord setCodofferta(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.offerte.CodOfferta</code>.
     */
    public Integer getCodofferta() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>utenze.offerte.Nome</code>.
     */
    public OfferteRecord setNome(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.offerte.Nome</code>.
     */
    public String getNome() {
        return (String) get(1);
    }

    /**
     * Setter for <code>utenze.offerte.Descrizione</code>.
     */
    public OfferteRecord setDescrizione(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>utenze.offerte.Descrizione</code>.
     */
    public String getDescrizione() {
        return (String) get(2);
    }

    /**
     * Setter for <code>utenze.offerte.CostoMateriaPrima</code>.
     */
    public OfferteRecord setCostomateriaprima(BigDecimal value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>utenze.offerte.CostoMateriaPrima</code>.
     */
    public BigDecimal getCostomateriaprima() {
        return (BigDecimal) get(3);
    }

    /**
     * Setter for <code>utenze.offerte.Attiva</code>.
     */
    public OfferteRecord setAttiva(Byte value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>utenze.offerte.Attiva</code>.
     */
    public Byte getAttiva() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>utenze.offerte.MateriaPrima</code>.
     */
    public OfferteRecord setMateriaprima(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>utenze.offerte.MateriaPrima</code>.
     */
    public String getMateriaprima() {
        return (String) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Integer, String, String, BigDecimal, Byte, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Integer, String, String, BigDecimal, Byte, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Offerte.OFFERTE.CODOFFERTA;
    }

    @Override
    public Field<String> field2() {
        return Offerte.OFFERTE.NOME;
    }

    @Override
    public Field<String> field3() {
        return Offerte.OFFERTE.DESCRIZIONE;
    }

    @Override
    public Field<BigDecimal> field4() {
        return Offerte.OFFERTE.COSTOMATERIAPRIMA;
    }

    @Override
    public Field<Byte> field5() {
        return Offerte.OFFERTE.ATTIVA;
    }

    @Override
    public Field<String> field6() {
        return Offerte.OFFERTE.MATERIAPRIMA;
    }

    @Override
    public Integer component1() {
        return getCodofferta();
    }

    @Override
    public String component2() {
        return getNome();
    }

    @Override
    public String component3() {
        return getDescrizione();
    }

    @Override
    public BigDecimal component4() {
        return getCostomateriaprima();
    }

    @Override
    public Byte component5() {
        return getAttiva();
    }

    @Override
    public String component6() {
        return getMateriaprima();
    }

    @Override
    public Integer value1() {
        return getCodofferta();
    }

    @Override
    public String value2() {
        return getNome();
    }

    @Override
    public String value3() {
        return getDescrizione();
    }

    @Override
    public BigDecimal value4() {
        return getCostomateriaprima();
    }

    @Override
    public Byte value5() {
        return getAttiva();
    }

    @Override
    public String value6() {
        return getMateriaprima();
    }

    @Override
    public OfferteRecord value1(Integer value) {
        setCodofferta(value);
        return this;
    }

    @Override
    public OfferteRecord value2(String value) {
        setNome(value);
        return this;
    }

    @Override
    public OfferteRecord value3(String value) {
        setDescrizione(value);
        return this;
    }

    @Override
    public OfferteRecord value4(BigDecimal value) {
        setCostomateriaprima(value);
        return this;
    }

    @Override
    public OfferteRecord value5(Byte value) {
        setAttiva(value);
        return this;
    }

    @Override
    public OfferteRecord value6(String value) {
        setMateriaprima(value);
        return this;
    }

    @Override
    public OfferteRecord values(Integer value1, String value2, String value3, BigDecimal value4, Byte value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OfferteRecord
     */
    public OfferteRecord() {
        super(Offerte.OFFERTE);
    }

    /**
     * Create a detached, initialised OfferteRecord
     */
    public OfferteRecord(Integer codofferta, String nome, String descrizione, BigDecimal costomateriaprima, Byte attiva, String materiaprima) {
        super(Offerte.OFFERTE);

        setCodofferta(codofferta);
        setNome(nome);
        setDescrizione(descrizione);
        setCostomateriaprima(costomateriaprima);
        setAttiva(attiva);
        setMateriaprima(materiaprima);
    }

    /**
     * Create a detached, initialised OfferteRecord
     */
    public OfferteRecord(bdproject.tables.pojos.Offerte value) {
        super(Offerte.OFFERTE);

        if (value != null) {
            setCodofferta(value.getCodofferta());
            setNome(value.getNome());
            setDescrizione(value.getDescrizione());
            setCostomateriaprima(value.getCostomateriaprima());
            setAttiva(value.getAttiva());
            setMateriaprima(value.getMateriaprima());
        }
    }
}
