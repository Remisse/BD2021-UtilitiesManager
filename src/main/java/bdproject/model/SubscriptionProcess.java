package bdproject.model;

import bdproject.tables.pojos.*;

import java.util.Optional;

public interface SubscriptionProcess {

    void setClientId(int id);

    void setPremises(Immobili premises);

    void setPlan(Offerte plan);

    void setActivationMethod(TipiAttivazione method);

    void setUse(String use);

    void setPeopleNo(int n);

    void setPowerRequested(double p);

    void setMeter(Contatori m);

    void setOtherClient(Persone client);

    void setOtherSubscription(Contratti sub);

    void setMeasurement(Letture m);

    int getClientId();

    Optional<Immobili> premises();

    Optional<Offerte> plan();

    Optional<TipiAttivazione> activation();

    Optional<String> usage();

    int peopleNo();

    double powerRequested();

    Optional<Contatori> meter();

    Optional<Persone> otherClient();

    Optional<Contratti> otherSubscription();

    Optional<Letture> measurement();
}
