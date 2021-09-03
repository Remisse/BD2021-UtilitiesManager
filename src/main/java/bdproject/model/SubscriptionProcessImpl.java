package bdproject.model;

import bdproject.tables.pojos.*;
import java.util.Optional;

public class SubscriptionProcessImpl implements SubscriptionProcess {

    private int clientId;
    private Immobili premises;
    private Offerte plan;
    private TipiAttivazione activationMethod;
    private TipologieUso use;
    private int peopleNo;
    private Contatori meter;
    private ClientiDettagliati otherClient;
    private Letture measurement;

    @Override
    public void setClientId(int id) {
        clientId = id;
    }

    @Override
    public void setPremises(final Immobili premises) {
        this.premises = premises;
    }

    @Override
    public void setPlan(final Offerte plan) {
        this.plan = plan;
    }

    @Override
    public void setActivationMethod(final TipiAttivazione method) {
        activationMethod = method;
    }

    @Override
    public void setUse(final TipologieUso use) {
        this.use = use;
    }

    @Override
    public void setPeopleNo(int n) {
        peopleNo = n;
    }

    @Override
    public void setMeter(Contatori m) {
        meter = m;
    }

    @Override
    public void setOtherClient(ClientiDettagliati client) {
        otherClient = client;
    }

    @Override
    public void setMeasurement(Letture m) {
        measurement = m;
    }

    @Override
    public int getClientId() {
        return clientId;
    }

    @Override
    public Optional<Immobili> premises() {
        return Optional.ofNullable(premises);
    }

    @Override
    public Optional<Offerte> plan() {
        return Optional.ofNullable(plan);
    }

    @Override
    public Optional<TipiAttivazione> activation() {
        return Optional.ofNullable(activationMethod);
    }

    @Override
    public Optional<TipologieUso> usage() {
        return Optional.ofNullable(use);
    }

    @Override
    public int peopleNo() {
        return peopleNo;
    }

    @Override
    public Optional<Contatori> meter() {
        return Optional.ofNullable(meter);
    }

    @Override
    public Optional<ClientiDettagliati> otherClient() {
        return Optional.ofNullable(otherClient);
    }

    @Override
    public Optional<Letture> measurement() {
        return Optional.ofNullable(measurement);
    }
}
