/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;
import java.time.LocalDate;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Persone implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer   codicecliente;
    private final Byte      amministratore;
    private final String    codicefiscale;
    private final String    partitaiva;
    private final String    nome;
    private final String    cognome;
    private final String    via;
    private final String    numcivico;
    private final Integer   cap;
    private final String    comune;
    private final String    provincia;
    private final LocalDate datanascita;
    private final String    numerotelefono;
    private final String    email;
    private final String    password;
    private final String    fasciareddito;

    public Persone(Persone value) {
        this.codicecliente = value.codicecliente;
        this.amministratore = value.amministratore;
        this.codicefiscale = value.codicefiscale;
        this.partitaiva = value.partitaiva;
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

    public Persone(
        Integer   codicecliente,
        Byte      amministratore,
        String    codicefiscale,
        String    partitaiva,
        String    nome,
        String    cognome,
        String    via,
        String    numcivico,
        Integer   cap,
        String    comune,
        String    provincia,
        LocalDate datanascita,
        String    numerotelefono,
        String    email,
        String    password,
        String    fasciareddito
    ) {
        this.codicecliente = codicecliente;
        this.amministratore = amministratore;
        this.codicefiscale = codicefiscale;
        this.partitaiva = partitaiva;
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
     * Getter for <code>utenze.persone.CodiceCliente</code>.
     */
    public Integer getCodicecliente() {
        return this.codicecliente;
    }

    /**
     * Getter for <code>utenze.persone.Amministratore</code>.
     */
    public Byte getAmministratore() {
        return this.amministratore;
    }

    /**
     * Getter for <code>utenze.persone.CodiceFiscale</code>.
     */
    public String getCodicefiscale() {
        return this.codicefiscale;
    }

    /**
     * Getter for <code>utenze.persone.PartitaIVA</code>.
     */
    public String getPartitaiva() {
        return this.partitaiva;
    }

    /**
     * Getter for <code>utenze.persone.Nome</code>.
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Getter for <code>utenze.persone.Cognome</code>.
     */
    public String getCognome() {
        return this.cognome;
    }

    /**
     * Getter for <code>utenze.persone.Via</code>.
     */
    public String getVia() {
        return this.via;
    }

    /**
     * Getter for <code>utenze.persone.NumCivico</code>.
     */
    public String getNumcivico() {
        return this.numcivico;
    }

    /**
     * Getter for <code>utenze.persone.CAP</code>.
     */
    public Integer getCap() {
        return this.cap;
    }

    /**
     * Getter for <code>utenze.persone.Comune</code>.
     */
    public String getComune() {
        return this.comune;
    }

    /**
     * Getter for <code>utenze.persone.Provincia</code>.
     */
    public String getProvincia() {
        return this.provincia;
    }

    /**
     * Getter for <code>utenze.persone.DataNascita</code>.
     */
    public LocalDate getDatanascita() {
        return this.datanascita;
    }

    /**
     * Getter for <code>utenze.persone.NumeroTelefono</code>.
     */
    public String getNumerotelefono() {
        return this.numerotelefono;
    }

    /**
     * Getter for <code>utenze.persone.Email</code>.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Getter for <code>utenze.persone.Password</code>.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Getter for <code>utenze.persone.FasciaReddito</code>.
     */
    public String getFasciareddito() {
        return this.fasciareddito;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Persone (");

        sb.append(codicecliente);
        sb.append(", ").append(amministratore);
        sb.append(", ").append(codicefiscale);
        sb.append(", ").append(partitaiva);
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