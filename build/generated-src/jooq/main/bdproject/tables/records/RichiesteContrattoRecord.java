/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.RichiesteContratto;

import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.TableRecordImpl;


/**
 * VIEW
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RichiesteContrattoRecord extends TableRecordImpl<RichiesteContrattoRecord> implements Record11<Integer, LocalDate, LocalDate, String, String, Integer, Integer, Integer, Integer, Integer, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.richieste contratto.IdContratto</code>.
     */
    public RichiesteContrattoRecord setIdcontratto(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.IdContratto</code>.
     */
    public Integer getIdcontratto() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>utenze.richieste contratto.DataAperturaRichiesta</code>.
     */
    public RichiesteContrattoRecord setDataaperturarichiesta(LocalDate value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.DataAperturaRichiesta</code>.
     */
    public LocalDate getDataaperturarichiesta() {
        return (LocalDate) get(1);
    }

    /**
     * Setter for <code>utenze.richieste contratto.DataChiusuraRichiesta</code>.
     */
    public RichiesteContrattoRecord setDatachiusurarichiesta(LocalDate value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.DataChiusuraRichiesta</code>.
     */
    public LocalDate getDatachiusurarichiesta() {
        return (LocalDate) get(2);
    }

    /**
     * Setter for <code>utenze.richieste contratto.StatoRichiesta</code>.
     */
    public RichiesteContrattoRecord setStatorichiesta(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.StatoRichiesta</code>.
     */
    public String getStatorichiesta() {
        return (String) get(3);
    }

    /**
     * Setter for <code>utenze.richieste contratto.NoteRichiesta</code>.
     */
    public RichiesteContrattoRecord setNoterichiesta(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.NoteRichiesta</code>.
     */
    public String getNoterichiesta() {
        return (String) get(4);
    }

    /**
     * Setter for <code>utenze.richieste contratto.NumeroComponenti</code>.
     */
    public RichiesteContrattoRecord setNumerocomponenti(Integer value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.NumeroComponenti</code>.
     */
    public Integer getNumerocomponenti() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>utenze.richieste contratto.Uso</code>.
     */
    public RichiesteContrattoRecord setUso(Integer value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.Uso</code>.
     */
    public Integer getUso() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>utenze.richieste contratto.Offerta</code>.
     */
    public RichiesteContrattoRecord setOfferta(Integer value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.Offerta</code>.
     */
    public Integer getOfferta() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>utenze.richieste contratto.TipoAttivazione</code>.
     */
    public RichiesteContrattoRecord setTipoattivazione(Integer value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.TipoAttivazione</code>.
     */
    public Integer getTipoattivazione() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>utenze.richieste contratto.IdImmobile</code>.
     */
    public RichiesteContrattoRecord setIdimmobile(Integer value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.IdImmobile</code>.
     */
    public Integer getIdimmobile() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>utenze.richieste contratto.IdCliente</code>.
     */
    public RichiesteContrattoRecord setIdcliente(Integer value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>utenze.richieste contratto.IdCliente</code>.
     */
    public Integer getIdcliente() {
        return (Integer) get(10);
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<Integer, LocalDate, LocalDate, String, String, Integer, Integer, Integer, Integer, Integer, Integer> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<Integer, LocalDate, LocalDate, String, String, Integer, Integer, Integer, Integer, Integer, Integer> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.IDCONTRATTO;
    }

    @Override
    public Field<LocalDate> field2() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.DATAAPERTURARICHIESTA;
    }

    @Override
    public Field<LocalDate> field3() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.DATACHIUSURARICHIESTA;
    }

    @Override
    public Field<String> field4() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.STATORICHIESTA;
    }

    @Override
    public Field<String> field5() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.NOTERICHIESTA;
    }

    @Override
    public Field<Integer> field6() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.NUMEROCOMPONENTI;
    }

    @Override
    public Field<Integer> field7() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.USO;
    }

    @Override
    public Field<Integer> field8() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.OFFERTA;
    }

    @Override
    public Field<Integer> field9() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.TIPOATTIVAZIONE;
    }

    @Override
    public Field<Integer> field10() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.IDIMMOBILE;
    }

    @Override
    public Field<Integer> field11() {
        return RichiesteContratto.RICHIESTE_CONTRATTO.IDCLIENTE;
    }

    @Override
    public Integer component1() {
        return getIdcontratto();
    }

    @Override
    public LocalDate component2() {
        return getDataaperturarichiesta();
    }

    @Override
    public LocalDate component3() {
        return getDatachiusurarichiesta();
    }

    @Override
    public String component4() {
        return getStatorichiesta();
    }

    @Override
    public String component5() {
        return getNoterichiesta();
    }

    @Override
    public Integer component6() {
        return getNumerocomponenti();
    }

    @Override
    public Integer component7() {
        return getUso();
    }

    @Override
    public Integer component8() {
        return getOfferta();
    }

    @Override
    public Integer component9() {
        return getTipoattivazione();
    }

    @Override
    public Integer component10() {
        return getIdimmobile();
    }

    @Override
    public Integer component11() {
        return getIdcliente();
    }

    @Override
    public Integer value1() {
        return getIdcontratto();
    }

    @Override
    public LocalDate value2() {
        return getDataaperturarichiesta();
    }

    @Override
    public LocalDate value3() {
        return getDatachiusurarichiesta();
    }

    @Override
    public String value4() {
        return getStatorichiesta();
    }

    @Override
    public String value5() {
        return getNoterichiesta();
    }

    @Override
    public Integer value6() {
        return getNumerocomponenti();
    }

    @Override
    public Integer value7() {
        return getUso();
    }

    @Override
    public Integer value8() {
        return getOfferta();
    }

    @Override
    public Integer value9() {
        return getTipoattivazione();
    }

    @Override
    public Integer value10() {
        return getIdimmobile();
    }

    @Override
    public Integer value11() {
        return getIdcliente();
    }

    @Override
    public RichiesteContrattoRecord value1(Integer value) {
        setIdcontratto(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord value2(LocalDate value) {
        setDataaperturarichiesta(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord value3(LocalDate value) {
        setDatachiusurarichiesta(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord value4(String value) {
        setStatorichiesta(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord value5(String value) {
        setNoterichiesta(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord value6(Integer value) {
        setNumerocomponenti(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord value7(Integer value) {
        setUso(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord value8(Integer value) {
        setOfferta(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord value9(Integer value) {
        setTipoattivazione(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord value10(Integer value) {
        setIdimmobile(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord value11(Integer value) {
        setIdcliente(value);
        return this;
    }

    @Override
    public RichiesteContrattoRecord values(Integer value1, LocalDate value2, LocalDate value3, String value4, String value5, Integer value6, Integer value7, Integer value8, Integer value9, Integer value10, Integer value11) {
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
        value11(value11);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RichiesteContrattoRecord
     */
    public RichiesteContrattoRecord() {
        super(RichiesteContratto.RICHIESTE_CONTRATTO);
    }

    /**
     * Create a detached, initialised RichiesteContrattoRecord
     */
    public RichiesteContrattoRecord(Integer idcontratto, LocalDate dataaperturarichiesta, LocalDate datachiusurarichiesta, String statorichiesta, String noterichiesta, Integer numerocomponenti, Integer uso, Integer offerta, Integer tipoattivazione, Integer idimmobile, Integer idcliente) {
        super(RichiesteContratto.RICHIESTE_CONTRATTO);

        setIdcontratto(idcontratto);
        setDataaperturarichiesta(dataaperturarichiesta);
        setDatachiusurarichiesta(datachiusurarichiesta);
        setStatorichiesta(statorichiesta);
        setNoterichiesta(noterichiesta);
        setNumerocomponenti(numerocomponenti);
        setUso(uso);
        setOfferta(offerta);
        setTipoattivazione(tipoattivazione);
        setIdimmobile(idimmobile);
        setIdcliente(idcliente);
    }

    /**
     * Create a detached, initialised RichiesteContrattoRecord
     */
    public RichiesteContrattoRecord(bdproject.tables.pojos.RichiesteContratto value) {
        super(RichiesteContratto.RICHIESTE_CONTRATTO);

        if (value != null) {
            setIdcontratto(value.getIdcontratto());
            setDataaperturarichiesta(value.getDataaperturarichiesta());
            setDatachiusurarichiesta(value.getDatachiusurarichiesta());
            setStatorichiesta(value.getStatorichiesta());
            setNoterichiesta(value.getNoterichiesta());
            setNumerocomponenti(value.getNumerocomponenti());
            setUso(value.getUso());
            setOfferta(value.getOfferta());
            setTipoattivazione(value.getTipoattivazione());
            setIdimmobile(value.getIdimmobile());
            setIdcliente(value.getIdcliente());
        }
    }
}