/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.OperatoriLetture;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OperatoriLettureRecord extends UpdatableRecordImpl<OperatoriLettureRecord> implements Record2<Integer, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.operatori letture.Lettura</code>.
     */
    public OperatoriLettureRecord setLettura(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.operatori letture.Lettura</code>.
     */
    public Integer getLettura() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>utenze.operatori letture.IdOperatore</code>.
     */
    public OperatoriLettureRecord setIdoperatore(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.operatori letture.IdOperatore</code>.
     */
    public Integer getIdoperatore() {
        return (Integer) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer, Integer> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Integer, Integer> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return OperatoriLetture.OPERATORI_LETTURE.LETTURA;
    }

    @Override
    public Field<Integer> field2() {
        return OperatoriLetture.OPERATORI_LETTURE.IDOPERATORE;
    }

    @Override
    public Integer component1() {
        return getLettura();
    }

    @Override
    public Integer component2() {
        return getIdoperatore();
    }

    @Override
    public Integer value1() {
        return getLettura();
    }

    @Override
    public Integer value2() {
        return getIdoperatore();
    }

    @Override
    public OperatoriLettureRecord value1(Integer value) {
        setLettura(value);
        return this;
    }

    @Override
    public OperatoriLettureRecord value2(Integer value) {
        setIdoperatore(value);
        return this;
    }

    @Override
    public OperatoriLettureRecord values(Integer value1, Integer value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OperatoriLettureRecord
     */
    public OperatoriLettureRecord() {
        super(OperatoriLetture.OPERATORI_LETTURE);
    }

    /**
     * Create a detached, initialised OperatoriLettureRecord
     */
    public OperatoriLettureRecord(Integer lettura, Integer idoperatore) {
        super(OperatoriLetture.OPERATORI_LETTURE);

        setLettura(lettura);
        setIdoperatore(idoperatore);
    }

    /**
     * Create a detached, initialised OperatoriLettureRecord
     */
    public OperatoriLettureRecord(bdproject.tables.pojos.OperatoriLetture value) {
        super(OperatoriLetture.OPERATORI_LETTURE);

        if (value != null) {
            setLettura(value.getLettura());
            setIdoperatore(value.getIdoperatore());
        }
    }
}
