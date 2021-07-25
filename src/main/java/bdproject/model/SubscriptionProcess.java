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

    void setOtherClient(PersoneFisiche client);

    void setOtherSubscription(Contratti sub);

    void setMeasurement(Letture m);

    void setFirm(PersoneGiuridiche f);

    int getClientId();

    Optional<Immobili> getPremises();

    Optional<Offerte> getPlan();

    Optional<TipiAttivazione> getActivationMethod();

    Optional<String> getUse();

    int getPeopleNo();

    double getPowerRequested();

    Optional<Contatori> getMeter();

    Optional<PersoneFisiche> getOtherClient();

    Optional<Contratti> getOtherSubscription();

    Optional<Letture> getMeasurement();

    Optional<PersoneGiuridiche> getFirm();
}
