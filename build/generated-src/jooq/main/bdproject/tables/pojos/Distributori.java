/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Distributori implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer id;
    private final String  nome;
    private final String  numerocontatto;
    private final String  emailcontatto;

    public Distributori(Distributori value) {
        this.id = value.id;
        this.nome = value.nome;
        this.numerocontatto = value.numerocontatto;
        this.emailcontatto = value.emailcontatto;
    }

    public Distributori(
        Integer id,
        String  nome,
        String  numerocontatto,
        String  emailcontatto
    ) {
        this.id = id;
        this.nome = nome;
        this.numerocontatto = numerocontatto;
        this.emailcontatto = emailcontatto;
    }

    /**
     * Getter for <code>utenze.distributori.Id</code>.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Getter for <code>utenze.distributori.Nome</code>.
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Getter for <code>utenze.distributori.NumeroContatto</code>.
     */
    public String getNumerocontatto() {
        return this.numerocontatto;
    }

    /**
     * Getter for <code>utenze.distributori.EmailContatto</code>.
     */
    public String getEmailcontatto() {
        return this.emailcontatto;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Distributori (");

        sb.append(id);
        sb.append(", ").append(nome);
        sb.append(", ").append(numerocontatto);
        sb.append(", ").append(emailcontatto);

        sb.append(")");
        return sb.toString();
    }
}