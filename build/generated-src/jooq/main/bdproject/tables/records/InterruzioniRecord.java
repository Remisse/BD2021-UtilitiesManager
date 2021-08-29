/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.Interruzioni;

import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class InterruzioniRecord extends UpdatableRecordImpl<InterruzioniRecord> implements Record6<Integer, LocalDate, LocalDate, String, Integer, Integer> {

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
     * Setter for <code>utenze.interruzioni.Motivazione</code>.
     */
    public InterruzioniRecord setMotivazione(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>utenze.interruzioni.Motivazione</code>.
     */
    public String getMotivazione() {
        return (String) get(3);
    }

    /**
     * Setter for <code>utenze.interruzioni.IndettaDa</code>.
     */
    public InterruzioniRecord setIndettada(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>utenze.interruzioni.IndettaDa</code>.
     */
    public Integer getIndettada() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>utenze.interruzioni.AnnullataDa</code>.
     */
    public InterruzioniRecord setAnnullatada(Integer value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>utenze.interruzioni.AnnullataDa</code>.
     */
    public Integer getAnnullatada() {
        return (Integer) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<LocalDate, Integer> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Integer, LocalDate, LocalDate, String, Integer, Integer> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Integer, LocalDate, LocalDate, String, Integer, Integer> valuesRow() {
        return (Row6) super.valuesRow();
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
        return Interruzioni.INTERRUZIONI.MOTIVAZIONE;
    }

    @Override
    public Field<Integer> field5() {
        return Interruzioni.INTERRUZIONI.INDETTADA;
    }

    @Override
    public Field<Integer> field6() {
        return Interruzioni.INTERRUZIONI.ANNULLATADA;
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
        return getMotivazione();
    }

    @Override
    public Integer component5() {
        return getIndettada();
    }

    @Override
    public Integer component6() {
        return getAnnullatada();
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
        return getMotivazione();
    }

    @Override
    public Integer value5() {
        return getIndettada();
    }

    @Override
    public Integer value6() {
        return getAnnullatada();
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
        setMotivazione(value);
        return this;
    }

    @Override
    public InterruzioniRecord value5(Integer value) {
        setIndettada(value);
        return this;
    }

    @Override
    public InterruzioniRecord value6(Integer value) {
        setAnnullatada(value);
        return this;
    }

    @Override
    public InterruzioniRecord values(Integer value1, LocalDate value2, LocalDate value3, String value4, Integer value5, Integer value6) {
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
     * Create a detached InterruzioniRecord
     */
    public InterruzioniRecord() {
        super(Interruzioni.INTERRUZIONI);
    }

    /**
     * Create a detached, initialised InterruzioniRecord
     */
    public InterruzioniRecord(Integer idcontratto, LocalDate datainterruzione, LocalDate datariattivazione, String motivazione, Integer indettada, Integer annullatada) {
        super(Interruzioni.INTERRUZIONI);

        setIdcontratto(idcontratto);
        setDatainterruzione(datainterruzione);
        setDatariattivazione(datariattivazione);
        setMotivazione(motivazione);
        setIndettada(indettada);
        setAnnullatada(annullatada);
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
            setMotivazione(value.getMotivazione());
            setIndettada(value.getIndettada());
            setAnnullatada(value.getAnnullatada());
        }
    }
}
