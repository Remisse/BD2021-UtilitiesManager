/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.Interruzioni;

import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class InterruzioniRecord extends UpdatableRecordImpl<InterruzioniRecord> implements Record4<Integer, LocalDate, LocalDate, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.interruzioni.IdContratto</code>.
     */
    public InterruzioniRecord setIdcontratto(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.interruzioni.IdContratto</code>.
     */
    public Integer getIdcontratto() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>utenze.interruzioni.DataInterruzione</code>.
     */
    public InterruzioniRecord setDatainterruzione(LocalDate value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.interruzioni.DataInterruzione</code>.
     */
    public LocalDate getDatainterruzione() {
        return (LocalDate) get(1);
    }

    /**
     * Setter for <code>utenze.interruzioni.DataRiattivazione</code>.
     */
    public InterruzioniRecord setDatariattivazione(LocalDate value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>utenze.interruzioni.DataRiattivazione</code>.
     */
    public LocalDate getDatariattivazione() {
        return (LocalDate) get(2);
    }

    /**
     * Setter for <code>utenze.interruzioni.Descrizione</code>.
     */
    public InterruzioniRecord setDescrizione(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>utenze.interruzioni.Descrizione</code>.
     */
    public String getDescrizione() {
        return (String) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<LocalDate, Integer> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, LocalDate, LocalDate, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Integer, LocalDate, LocalDate, String> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Interruzioni.INTERRUZIONI.IDCONTRATTO;
    }

    @Override
    public Field<LocalDate> field2() {
        return Interruzioni.INTERRUZIONI.DATAINTERRUZIONE;
    }

    @Override
    public Field<LocalDate> field3() {
        return Interruzioni.INTERRUZIONI.DATARIATTIVAZIONE;
    }

    @Override
    public Field<String> field4() {
        return Interruzioni.INTERRUZIONI.DESCRIZIONE;
    }

    @Override
    public Integer component1() {
        return getIdcontratto();
    }

    @Override
    public LocalDate component2() {
        return getDatainterruzione();
    }

    @Override
    public LocalDate component3() {
        return getDatariattivazione();
    }

    @Override
    public String component4() {
        return getDescrizione();
    }

    @Override
    public Integer value1() {
        return getIdcontratto();
    }

    @Override
    public LocalDate value2() {
        return getDatainterruzione();
    }

    @Override
    public LocalDate value3() {
        return getDatariattivazione();
    }

    @Override
    public String value4() {
        return getDescrizione();
    }

    @Override
    public InterruzioniRecord value1(Integer value) {
        setIdcontratto(value);
        return this;
    }

    @Override
    public InterruzioniRecord value2(LocalDate value) {
        setDatainterruzione(value);
        return this;
    }

    @Override
    public InterruzioniRecord value3(LocalDate value) {
        setDatariattivazione(value);
        return this;
    }

    @Override
    public InterruzioniRecord value4(String value) {
        setDescrizione(value);
        return this;
    }

    @Override
    public InterruzioniRecord values(Integer value1, LocalDate value2, LocalDate value3, String value4) {
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
     * Create a detached InterruzioniRecord
     */
    public InterruzioniRecord() {
        super(Interruzioni.INTERRUZIONI);
    }

    /**
     * Create a detached, initialised InterruzioniRecord
     */
    public InterruzioniRecord(Integer idcontratto, LocalDate datainterruzione, LocalDate datariattivazione, String descrizione) {
        super(Interruzioni.INTERRUZIONI);

        setIdcontratto(idcontratto);
        setDatainterruzione(datainterruzione);
        setDatariattivazione(datariattivazione);
        setDescrizione(descrizione);
    }

    /**
     * Create a detached, initialised InterruzioniRecord
     */
    public InterruzioniRecord(bdproject.tables.pojos.Interruzioni value) {
        super(Interruzioni.INTERRUZIONI);

        if (value != null) {
            setIdcontratto(value.getIdcontratto());
            setDatainterruzione(value.getDatainterruzione());
            setDatariattivazione(value.getDatariattivazione());
            setDescrizione(value.getDescrizione());
        }
    }
}
