/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TipologieUso implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer    coduso;
    private final String     nome;
    private final BigDecimal stimaperpersona;
    private final Byte       scontoreddito;

    public TipologieUso(TipologieUso value) {
        this.coduso = value.coduso;
        this.nome = value.nome;
        this.stimaperpersona = value.stimaperpersona;
        this.scontoreddito = value.scontoreddito;
    }

    public TipologieUso(
        Integer    coduso,
        String     nome,
        BigDecimal stimaperpersona,
        Byte       scontoreddito
    ) {
        this.coduso = coduso;
        this.nome = nome;
        this.stimaperpersona = stimaperpersona;
        this.scontoreddito = scontoreddito;
    }

    /**
     * Getter for <code>utenze.tipologie uso.CodUso</code>.
     */
    public Integer getCoduso() {
        return this.coduso;
    }

    /**
     * Getter for <code>utenze.tipologie uso.Nome</code>.
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Getter for <code>utenze.tipologie uso.StimaPerPersona</code>.
     */
    public BigDecimal getStimaperpersona() {
        return this.stimaperpersona;
    }

    /**
     * Getter for <code>utenze.tipologie uso.ScontoReddito</code>.
     */
    public Byte getScontoreddito() {
        return this.scontoreddito;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TipologieUso (");

        sb.append(coduso);
        sb.append(", ").append(nome);
        sb.append(", ").append(stimaperpersona);
        sb.append(", ").append(scontoreddito);

        sb.append(")");
        return sb.toString();
    }
}
