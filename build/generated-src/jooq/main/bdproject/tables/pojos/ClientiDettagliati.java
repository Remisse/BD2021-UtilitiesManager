/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;
import java.time.LocalDate;


/**
 * VIEW
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ClientiDettagliati implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer   identificativo;
    private final String    codicefiscale;
    private final String    nome;
    private final String    cognome;
    private final String    via;
    private final String    numcivico;
    private final String    cap;
    private final String    comune;
    private final String    provincia;
    private final LocalDate datanascita;
    private final String    numerotelefono;
    private final String    email;
    private final String    password;
    private final Integer   fasciareddito;

    public ClientiDettagliati(ClientiDettagliati value) {
        this.identificativo = value.identificativo;
        this.codicefiscale = value.codicefiscale;
        this.nome = value.nome;
        this.cognome = value.cognome;
        this.via = value.via;
        this.numcivico = value.numcivico;
        this.cap = value.cap;
        this.comune = value.comune;
        this.provincia = value.provincia;
        this.datanascita = value.datanascita;
        this.numerotelefono = value.numerotelefono;
        this.email = value.email;
        this.password = value.password;
        this.fasciareddito = value.fasciareddito;
    }

    public ClientiDettagliati(
        Integer   identificativo,
        String    codicefiscale,
        String    nome,
        String    cognome,
        String    via,
        String    numcivico,
        String    cap,
        String    comune,
        String    provincia,
        LocalDate datanascita,
        String    numerotelefono,
        String    email,
        String    password,
        Integer   fasciareddito
    ) {
        this.identificativo = identificativo;
        this.codicefiscale = codicefiscale;
        this.nome = nome;
        this.cognome = cognome;
        this.via = via;
        this.numcivico = numcivico;
        this.cap = cap;
        this.comune = comune;
        this.provincia = provincia;
        this.datanascita = datanascita;
        this.numerotelefono = numerotelefono;
        this.email = email;
        this.password = password;
        this.fasciareddito = fasciareddito;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Identificativo</code>.
     */
    public Integer getIdentificativo() {
        return this.identificativo;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.CodiceFiscale</code>.
     */
    public String getCodicefiscale() {
        return this.codicefiscale;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Nome</code>.
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Cognome</code>.
     */
    public String getCognome() {
        return this.cognome;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Via</code>.
     */
    public String getVia() {
        return this.via;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.NumCivico</code>.
     */
    public String getNumcivico() {
        return this.numcivico;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.CAP</code>.
     */
    public String getCap() {
        return this.cap;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Comune</code>.
     */
    public String getComune() {
        return this.comune;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Provincia</code>.
     */
    public String getProvincia() {
        return this.provincia;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.DataNascita</code>.
     */
    public LocalDate getDatanascita() {
        return this.datanascita;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.NumeroTelefono</code>.
     */
    public String getNumerotelefono() {
        return this.numerotelefono;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Email</code>.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.Password</code>.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Getter for <code>utenze.clienti_dettagliati.FasciaReddito</code>.
     */
    public Integer getFasciareddito() {
        return this.fasciareddito;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ClientiDettagliati (");

        sb.append(identificativo);
        sb.append(", ").append(codicefiscale);
        sb.append(", ").append(nome);
        sb.append(", ").append(cognome);
        sb.append(", ").append(via);
        sb.append(", ").append(numcivico);
        sb.append(", ").append(cap);
        sb.append(", ").append(comune);
        sb.append(", ").append(provincia);
        sb.append(", ").append(datanascita);
        sb.append(", ").append(numerotelefono);
        sb.append(", ").append(email);
        sb.append(", ").append(password);
        sb.append(", ").append(fasciareddito);

        sb.append(")");
        return sb.toString();
    }
}
