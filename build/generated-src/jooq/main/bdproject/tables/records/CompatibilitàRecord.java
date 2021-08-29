/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.Compatibilità;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CompatibilitàRecord extends UpdatableRecordImpl<CompatibilitàRecord> implements Record2<Integer, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.compatibilità.CodiceOfferta</code>.
     */
    public CompatibilitàRecord setCodiceofferta(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.compatibilità.CodiceOfferta</code>.
     */
    public Integer getCodiceofferta() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>utenze.compatibilità.Uso</code>.
     */
    public CompatibilitàRecord setUso(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.compatibilità.Uso</code>.
     */
    public Integer getUso() {
        return (Integer) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<Integer, Integer> key() {
        return (Record2) super.key();
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
        return Compatibilità.COMPATIBILITÀ.CODICEOFFERTA;
    }

    @Override
    public Field<Integer> field2() {
        return Compatibilità.COMPATIBILITÀ.USO;
    }

    @Override
    public Integer component1() {
        return getCodiceofferta();
    }

    @Override
    public Integer component2() {
        return getUso();
    }

    @Override
    public Integer value1() {
        return getCodiceofferta();
    }

    @Override
    public Integer value2() {
        return getUso();
    }

    @Override
    public CompatibilitàRecord value1(Integer value) {
        setCodiceofferta(value);
        return this;
    }

    @Override
    public CompatibilitàRecord value2(Integer value) {
        setUso(value);
        return this;
    }

    @Override
    public CompatibilitàRecord values(Integer value1, Integer value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CompatibilitàRecord
     */
    public CompatibilitàRecord() {
        super(Compatibilità.COMPATIBILITÀ);
    }

    /**
     * Create a detached, initialised CompatibilitàRecord
     */
    public CompatibilitàRecord(Integer codiceofferta, Integer uso) {
        super(Compatibilità.COMPATIBILITÀ);

        setCodiceofferta(codiceofferta);
        setUso(uso);
    }

    /**
     * Create a detached, initialised CompatibilitàRecord
     */
    public CompatibilitàRecord(bdproject.tables.pojos.Compatibilità value) {
        super(Compatibilità.COMPATIBILITÀ);

        if (value != null) {
            setCodiceofferta(value.getCodiceofferta());
            setUso(value.getUso());
        }
    }
}
