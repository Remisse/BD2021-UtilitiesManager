/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.Redditi;

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
public class RedditiRecord extends UpdatableRecordImpl<RedditiRecord> implements Record3<Integer, String, BigDecimal> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.redditi.CodReddito</code>.
     */
    public RedditiRecord setCodreddito(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.redditi.CodReddito</code>.
     */
    public Integer getCodreddito() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>utenze.redditi.Fascia</code>.
     */
    public RedditiRecord setFascia(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.redditi.Fascia</code>.
     */
    public String getFascia() {
        return (String) get(1);
    }

    /**
     * Setter for <code>utenze.redditi.Sconto</code>.
     */
    public RedditiRecord setSconto(BigDecimal value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>utenze.redditi.Sconto</code>.
     */
    public BigDecimal getSconto() {
        return (BigDecimal) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, BigDecimal> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, String, BigDecimal> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Redditi.REDDITI.CODREDDITO;
    }

    @Override
    public Field<String> field2() {
        return Redditi.REDDITI.FASCIA;
    }

    @Override
    public Field<BigDecimal> field3() {
        return Redditi.REDDITI.SCONTO;
    }

    @Override
    public Integer component1() {
        return getCodreddito();
    }

    @Override
    public String component2() {
        return getFascia();
    }

    @Override
    public BigDecimal component3() {
        return getSconto();
    }

    @Override
    public Integer value1() {
        return getCodreddito();
    }

    @Override
    public String value2() {
        return getFascia();
    }

    @Override
    public BigDecimal value3() {
        return getSconto();
    }

    @Override
    public RedditiRecord value1(Integer value) {
        setCodreddito(value);
        return this;
    }

    @Override
    public RedditiRecord value2(String value) {
        setFascia(value);
        return this;
    }

    @Override
    public RedditiRecord value3(BigDecimal value) {
        setSconto(value);
        return this;
    }

    @Override
    public RedditiRecord values(Integer value1, String value2, BigDecimal value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RedditiRecord
     */
    public RedditiRecord() {
        super(Redditi.REDDITI);
    }

    /**
     * Create a detached, initialised RedditiRecord
     */
    public RedditiRecord(Integer codreddito, String fascia, BigDecimal sconto) {
        super(Redditi.REDDITI);

        setCodreddito(codreddito);
        setFascia(fascia);
        setSconto(sconto);
    }

    /**
     * Create a detached, initialised RedditiRecord
     */
    public RedditiRecord(bdproject.tables.pojos.Redditi value) {
        super(Redditi.REDDITI);

        if (value != null) {
            setCodreddito(value.getCodreddito());
            setFascia(value.getFascia());
            setSconto(value.getSconto());
        }
    }
}
