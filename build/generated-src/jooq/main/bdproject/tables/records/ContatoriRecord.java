/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.Contatori;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ContatoriRecord extends UpdatableRecordImpl<ContatoriRecord> implements Record4<Integer, String, String, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.contatori.Progressivo</code>.
     */
    public ContatoriRecord setProgressivo(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.contatori.Progressivo</code>.
     */
    public Integer getProgressivo() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>utenze.contatori.Matricola</code>.
     */
    public ContatoriRecord setMatricola(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.contatori.Matricola</code>.
     */
    public String getMatricola() {
        return (String) get(1);
    }

    /**
     * Setter for <code>utenze.contatori.MateriaPrima</code>.
     */
    public ContatoriRecord setMateriaprima(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>utenze.contatori.MateriaPrima</code>.
     */
    public String getMateriaprima() {
        return (String) get(2);
    }

    /**
     * Setter for <code>utenze.contatori.IdImmobile</code>.
     */
    public ContatoriRecord setIdimmobile(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>utenze.contatori.IdImmobile</code>.
     */
    public Integer getIdimmobile() {
        return (Integer) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, String, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Integer, String, String, Integer> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Contatori.CONTATORI.PROGRESSIVO;
    }

    @Override
    public Field<String> field2() {
        return Contatori.CONTATORI.MATRICOLA;
    }

    @Override
    public Field<String> field3() {
        return Contatori.CONTATORI.MATERIAPRIMA;
    }

    @Override
    public Field<Integer> field4() {
        return Contatori.CONTATORI.IDIMMOBILE;
    }

    @Override
    public Integer component1() {
        return getProgressivo();
    }

    @Override
    public String component2() {
        return getMatricola();
    }

    @Override
    public String component3() {
        return getMateriaprima();
    }

    @Override
    public Integer component4() {
        return getIdimmobile();
    }

    @Override
    public Integer value1() {
        return getProgressivo();
    }

    @Override
    public String value2() {
        return getMatricola();
    }

    @Override
    public String value3() {
        return getMateriaprima();
    }

    @Override
    public Integer value4() {
        return getIdimmobile();
    }

    @Override
    public ContatoriRecord value1(Integer value) {
        setProgressivo(value);
        return this;
    }

    @Override
    public ContatoriRecord value2(String value) {
        setMatricola(value);
        return this;
    }

    @Override
    public ContatoriRecord value3(String value) {
        setMateriaprima(value);
        return this;
    }

    @Override
    public ContatoriRecord value4(Integer value) {
        setIdimmobile(value);
        return this;
    }

    @Override
    public ContatoriRecord values(Integer value1, String value2, String value3, Integer value4) {
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
     * Create a detached ContatoriRecord
     */
    public ContatoriRecord() {
        super(Contatori.CONTATORI);
    }

    /**
     * Create a detached, initialised ContatoriRecord
     */
    public ContatoriRecord(Integer progressivo, String matricola, String materiaprima, Integer idimmobile) {
        super(Contatori.CONTATORI);

        setProgressivo(progressivo);
        setMatricola(matricola);
        setMateriaprima(materiaprima);
        setIdimmobile(idimmobile);
    }

    /**
     * Create a detached, initialised ContatoriRecord
     */
    public ContatoriRecord(bdproject.tables.pojos.Contatori value) {
        super(Contatori.CONTATORI);

        if (value != null) {
            setProgressivo(value.getProgressivo());
            setMatricola(value.getMatricola());
            setMateriaprima(value.getMateriaprima());
            setIdimmobile(value.getIdimmobile());
        }
    }
}
