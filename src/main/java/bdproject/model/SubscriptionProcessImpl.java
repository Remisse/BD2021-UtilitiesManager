package bdproject.model;

import bdproject.tables.pojos.*;
import java.util.Optional;

public class SubscriptionProcessImpl implements SubscriptionProcess {

    private int clientId;
    private Immobili premises;
    private Offerte plan;
    private TipiAttivazione activationMethod;
    private String use;
    private int peopleNo;
    private double power;
    private Contatori meter;
    private PersoneFisiche otherClient;
    private Contratti otherSub;
    private Letture measurement;
    private PersoneGiuridiche firm;

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
    public void setUse(final String use) {
        this.use = use;
    }

    @Override
    public void setPeopleNo(int n) {
        peopleNo = n;
    }

    @Override
    public void setPowerRequested(double p) {
        power = p;
    }

    @Override
    public void setMeter(Contatori m) {
        meter = m;
    }

    @Override
    public void setOtherClient(PersoneFisiche client) {
        otherClient = client;
    }

    @Override
    public void setOtherSubscription(Contratti sub) {
        otherSub = sub;
    }

    @Override
    public void setMeasurement(Letture m) {
        measurement = m;
    }

    @Override
    public void setFirm(PersoneGiuridiche f) {
        firm = f;
    }

    @Override
    public int getClientId() {
        return clientId;
    }

    @Override
    public Optional<Immobili> getPremises() {
        return Optional.ofNullable(premises);
    }

    @Override
    public Optional<Offerte> getPlan() {
        return Optional.ofNullable(plan);
    }

    @Override
    public Optional<TipiAttivazione> getActivationMethod() {
        return Optional.ofNullable(activationMethod);
    }

    @Override
    public Optional<String> getUse() {
        return Optional.ofNullable(use);
    }

    @Override
    public int getPeopleNo() {
        return peopleNo;
    }

    @Override
    public double getPowerRequested() {
        return power;
    }

    @Override
    public Optional<Contatori> getMeter() {
        return Optional.ofNullable(meter);
    }

    @Override
    public Optional<PersoneFisiche> getOtherClient() {
        return Optional.ofNullable(otherClient);
    }

    @Override
    public Optional<Contratti> getOtherSubscription() {
        return Optional.ofNullable(otherSub);
    }

    @Override
    public Optional<Letture> getMeasurement() {
        return Optional.ofNullable(measurement);
    }

    @Override
    public Optional<PersoneGiuridiche> getFirm() {
        return Optional.ofNullable(firm);
    }
}
