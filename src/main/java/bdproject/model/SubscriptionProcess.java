package bdproject.model;

import bdproject.tables.pojos.*;

import java.util.Optional;

public interface SubscriptionProcess {

    void setClientId(int id);

    void setEstate(Immobili premise);

    void setPlan(Offerte plan);

    void setActivationMethod(TipiAttivazione method);

    void setUse(TipologieUso use);

    void setPeopleNo(int n);

    void setMeter(Contatori m);

    void setOtherClient(ClientiDettagliati client);

    void setMeasurement(Letture m);

    int getClientId();

    Optional<Immobili> estate();

    Optional<Offerte> plan();

    Optional<TipiAttivazione> activation();

    Optional<TipologieUso> usage();

    int peopleNo();

    Optional<Contatori> meter();

    Optional<ClientiDettagliati> otherClient();

    Optional<Letture> measurement();
}
