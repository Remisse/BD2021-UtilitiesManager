/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.records;


import bdproject.tables.ClientiDettagliati;

import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.Record14;
import org.jooq.Row14;
import org.jooq.impl.TableRecordImpl;


/**
 * VIEW
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ClientiDettagliatiRecord extends TableRecordImpl<ClientiDettagliatiRecord> implements Record14<Integer, String, String, String, String, String, String, String, String, LocalDate, String, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>utenze.clienti_dettagliati.Identificativo</code>.
     */
    public ClientiDettagliatiRecord setIdentificativo(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Identificativo</code>.
     */
    public Integer getIdentificativo() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.CodiceFiscale</code>.
     */
    public ClientiDettagliatiRecord setCodicefiscale(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.CodiceFiscale</code>.
     */
    public String getCodicefiscale() {
        return (String) get(1);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.Nome</code>.
     */
    public ClientiDettagliatiRecord setNome(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Nome</code>.
     */
    public String getNome() {
        return (String) get(2);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.Cognome</code>.
     */
    public ClientiDettagliatiRecord setCognome(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Cognome</code>.
     */
    public String getCognome() {
        return (String) get(3);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.Via</code>.
     */
    public ClientiDettagliatiRecord setVia(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Via</code>.
     */
    public String getVia() {
        return (String) get(4);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.NumCivico</code>.
     */
    public ClientiDettagliatiRecord setNumcivico(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.NumCivico</code>.
     */
    public String getNumcivico() {
        return (String) get(5);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.CAP</code>.
     */
    public ClientiDettagliatiRecord setCap(String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.CAP</code>.
     */
    public String getCap() {
        return (String) get(6);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.Comune</code>.
     */
    public ClientiDettagliatiRecord setComune(String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Comune</code>.
     */
    public String getComune() {
        return (String) get(7);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.Provincia</code>.
     */
    public ClientiDettagliatiRecord setProvincia(String value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Provincia</code>.
     */
    public String getProvincia() {
        return (String) get(8);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.DataNascita</code>.
     */
    public ClientiDettagliatiRecord setDatanascita(LocalDate value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.DataNascita</code>.
     */
    public LocalDate getDatanascita() {
        return (LocalDate) get(9);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.NumeroTelefono</code>.
     */
    public ClientiDettagliatiRecord setNumerotelefono(String value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.NumeroTelefono</code>.
     */
    public String getNumerotelefono() {
        return (String) get(10);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.Email</code>.
     */
    public ClientiDettagliatiRecord setEmail(String value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Email</code>.
     */
    public String getEmail() {
        return (String) get(11);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.Password</code>.
     */
    public ClientiDettagliatiRecord setPassword(String value) {
        set(12, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Password</code>.
     */
    public String getPassword() {
        return (String) get(12);
    }

    /**
     * Setter for <code>utenze.clienti_dettagliati.FasciaReddito</code>.
     */
    public ClientiDettagliatiRecord setFasciareddito(String value) {
        set(13, value);
        return this;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.FasciaReddito</code>.
     */
    public String getFasciareddito() {
        return (String) get(13);
    }

    // -------------------------------------------------------------------------
    // Record14 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row14<Integer, String, String, String, String, String, String, String, String, LocalDate, String, String, String, String> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    @Override
    public Row14<Integer, String, String, String, String, String, String, String, String, LocalDate, String, String, String, String> valuesRow() {
        return (Row14) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.IDENTIFICATIVO;
    }

    @Override
    public Field<String> field2() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.CODICEFISCALE;
    }

    @Override
    public Field<String> field3() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.NOME;
    }

    @Override
    public Field<String> field4() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.COGNOME;
    }

    @Override
    public Field<String> field5() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.VIA;
    }

    @Override
    public Field<String> field6() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.NUMCIVICO;
    }

    @Override
    public Field<String> field7() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.CAP;
    }

    @Override
    public Field<String> field8() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.COMUNE;
    }

    @Override
    public Field<String> field9() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.PROVINCIA;
    }

    @Override
    public Field<LocalDate> field10() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.DATANASCITA;
    }

    @Override
    public Field<String> field11() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.NUMEROTELEFONO;
    }

    @Override
    public Field<String> field12() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.EMAIL;
    }

    @Override
    public Field<String> field13() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.PASSWORD;
    }

    @Override
    public Field<String> field14() {
        return ClientiDettagliati.CLIENTI_DETTAGLIATI.FASCIAREDDITO;
    }

    @Override
    public Integer component1() {
        return getIdentificativo();
    }

    @Override
    public String component2() {
        return getCodicefiscale();
    }

    @Override
    public String component3() {
        return getNome();
    }

    @Override
    public String component4() {
        return getCognome();
    }

    @Override
    public String component5() {
        return getVia();
    }

    @Override
    public String component6() {
        return getNumcivico();
    }

    @Override
    public String component7() {
        return getCap();
    }

    @Override
    public String component8() {
        return getComune();
    }

    @Override
    public String component9() {
        return getProvincia();
    }

    @Override
    public LocalDate component10() {
        return getDatanascita();
    }

    @Override
    public String component11() {
        return getNumerotelefono();
    }

    @Override
    public String component12() {
        return getEmail();
    }

    @Override
    public String component13() {
        return getPassword();
    }

    @Override
    public String component14() {
        return getFasciareddito();
    }

    @Override
    public Integer value1() {
        return getIdentificativo();
    }

    @Override
    public String value2() {
        return getCodicefiscale();
    }

    @Override
    public String value3() {
        return getNome();
    }

    @Override
    public String value4() {
        return getCognome();
    }

    @Override
    public String value5() {
        return getVia();
    }

    @Override
    public String value6() {
        return getNumcivico();
    }

    @Override
    public String value7() {
        return getCap();
    }

    @Override
    public String value8() {
        return getComune();
    }

    @Override
    public String value9() {
        return getProvincia();
    }

    @Override
    public LocalDate value10() {
        return getDatanascita();
    }

    @Override
    public String value11() {
        return getNumerotelefono();
    }

    @Override
    public String value12() {
        return getEmail();
    }

    @Override
    public String value13() {
        return getPassword();
    }

    @Override
    public String value14() {
        return getFasciareddito();
    }

    @Override
    public ClientiDettagliatiRecord value1(Integer value) {
        setIdentificativo(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value2(String value) {
        setCodicefiscale(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value3(String value) {
        setNome(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value4(String value) {
        setCognome(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value5(String value) {
        setVia(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value6(String value) {
        setNumcivico(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value7(String value) {
        setCap(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value8(String value) {
        setComune(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value9(String value) {
        setProvincia(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value10(LocalDate value) {
        setDatanascita(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value11(String value) {
        setNumerotelefono(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value12(String value) {
        setEmail(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value13(String value) {
        setPassword(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord value14(String value) {
        setFasciareddito(value);
        return this;
    }

    @Override
    public ClientiDettagliatiRecord values(Integer value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, LocalDate value10, String value11, String value12, String value13, String value14) {
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
        value12(value12);
        value13(value13);
        value14(value14);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ClientiDettagliatiRecord
     */
    public ClientiDettagliatiRecord() {
        super(ClientiDettagliati.CLIENTI_DETTAGLIATI);
    }

    /**
     * Create a detached, initialised ClientiDettagliatiRecord
     */
    public ClientiDettagliatiRecord(Integer identificativo, String codicefiscale, String nome, String cognome, String via, String numcivico, String cap, String comune, String provincia, LocalDate datanascita, String numerotelefono, String email, String password, String fasciareddito) {
        super(ClientiDettagliati.CLIENTI_DETTAGLIATI);

        setIdentificativo(identificativo);
        setCodicefiscale(codicefiscale);
        setNome(nome);
        setCognome(cognome);
        setVia(via);
        setNumcivico(numcivico);
        setCap(cap);
        setComune(comune);
        setProvincia(provincia);
        setDatanascita(datanascita);
        setNumerotelefono(numerotelefono);
        setEmail(email);
        setPassword(password);
        setFasciareddito(fasciareddito);
    }

    /**
     * Create a detached, initialised ClientiDettagliatiRecord
     */
    public ClientiDettagliatiRecord(bdproject.tables.pojos.ClientiDettagliati value) {
        super(ClientiDettagliati.CLIENTI_DETTAGLIATI);

        if (value != null) {
            setIdentificativo(value.getIdentificativo());
            setCodicefiscale(value.getCodicefiscale());
            setNome(value.getNome());
            setCognome(value.getCognome());
            setVia(value.getVia());
            setNumcivico(value.getNumcivico());
            setCap(value.getCap());
            setComune(value.getComune());
            setProvincia(value.getProvincia());
            setDatanascita(value.getDatanascita());
            setNumerotelefono(value.getNumerotelefono());
            setEmail(value.getEmail());
            setPassword(value.getPassword());
            setFasciareddito(value.getFasciareddito());
        }
    }
}