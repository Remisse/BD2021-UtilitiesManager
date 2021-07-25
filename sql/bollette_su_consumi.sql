insert into bollette (IdContratto, DataEmissione, DataScadenza, Consumi, CostoAttivazione, ParzialeCanoneRAI, Importo)
/* Trova, se esiste, l'ultima volta in cui ogni contratto è stato interrotto */
with ultima_interruzione as (
select I.*
  from contratti C, interruzioni I
 where C.DataInizio is not null
   and C.DataCessazione is null
   and C.IdContratto = I.IdContratto
 order by DataInterruzione desc
 limit 1
),
/* Trova l'ultima lettura confermata di ogni contatore */
ultima_lettura as (
select L.*
  from letture L, contatori C
 where L.Contatore = C.NumeroProgressivo
   and L.Confermata = true
 order by DataEffettuazione desc
 limit 1
),
/* Trova la penultima lettura confermata di ogni contatore */
penultima_lettura as (
select LW2.Contatore, LW2.DataEffettuazione as DataEffettuazione, LW2.Fascia1, LW2.Fascia2, LW2.Fascia3
  from letture LW2, ultima_lettura LM
 where LW2.Contatore = LM.Contatore
   and LW2.Confermata = true
   and LW2.DataEffettuazione < LM.DataEffettuazione
 order by LW2.DataEffettuazione desc
 limit 1
),
/* Calcola i consumi nel periodo di riferimento */
consumi_periodo as (
select LM.Contatore, LM.Fascia1 + LM.Fascia2 + LM.Fascia3 - (SLM.Fascia1 + SLM.Fascia2 + SLM.Fascia3) as ConsumiPeriodo
  from ultima_lettura LM, penultima_lettura SLM
 where LM.Contatore = SLM.Contatore
),
/* Trova l'ultima bolletta per ogni contratto */
ultima_bolletta as (
select C.IdContratto, DataEmissione
  from bollette B, contratti C
 where C.DataCessazione is null
   and C.DataInizio is not null
   and B.IdContratto = C.IdContratto
 order by DataEmissione desc
 limit 1
),
/* Calcola il numero di giorni trascorsi dall'ultima bolletta, se esiste, o dall'attivazione del contratto */
giorni_da_ultima_bolletta as (
select contratti.IdContratto, coalesce(datediff(curdate(), ultima_bolletta.DataEmissione), datediff(curdate(), contratti.DataInizio)) as GiorniTrascorsi
  from contratti, ultima_bolletta
 where contratti.DataCessazione is null
   and contratti.DataInizio is not null
   and contratti.IdContratto = ultima_bolletta.IdContratto
),
/* Se questa è la prima bolletta, preleva il costo di attivazione della fornitura */
costo_attivazione_dovuto as (
select C4.IdContratto, A4.CostoUnaTantum
  from contratti C4, tipi_attivazione A4
 where C4.NomeAttivazione = A4.Nome
   and C4.IdContratto not in (select IdContratto from ultima_bolletta)
),
/* Calcola, se dovuta, la parte di canone RAI da pagare per questo periodo di riferimento */
canone_rai_dovuto as (
select contratti.IdContratto, CanoneRAI * (GiorniTrascorsi / 360) as ParzialeCanoneRAI
  from contratti, contatori, tipologie_uso, giorni_da_ultima_bolletta
 where contratti.Contatore = contatori.NumeroProgressivo
   and contratti.TipoUso = tipologie_uso.nome
   and contratti.IdContratto = giorni_da_ultima_bolletta.IdContratto
   and tipologie_uso.CanoneRAI > 0.0
   and contatori.MateriaPrima = "Luce"
)
select C1.IdContratto,
	curdate(),
    date_add(curdate(), interval 1 month),
    U1.ConsumiPeriodo,
    coalesce(AD1.CostoUnaTantum, 0.0),
    coalesce(R1.ParzialeCanoneRAI, 0.0),
	(coalesce(R1.ParzialeCanoneRAI, 0.0) + O1.CostoMateriaPrima * U1.ConsumiPeriodo
	  /* Partendo dall'ultima bolletta con consumi reali, sottrai gli importi stimati delle bollette successive */
	- coalesce((select(sum(Importo))
		 from bollette B2
		where B2.IdContratto = C1.IdContratto
		  and B2.Consumi is null
		  /* Trova, se esiste, l'ultima bolletta con importo calcolato in base ai consumi */
		  and B2.DataEmissione > (select max(DataEmissione)
				 from bollette B3
				where B3.IdContratto = C1.IdContratto
				  and B3.Consumi is not null)),
	0.0)
	/* Se questa è la prima bolletta, aggiungi il costo di attivazione */
	+ coalesce(AD1.CostoUnaTantum, 0.0)
	/* Costo per kW */
	+ M1.Potenza * 5.0
   /* Applica sconto per reddito */
	) * (select Sconto
		 from persone_fisiche P5, redditi R5
		where P5.CodiceCliente = P1.CodiceCliente
		  and P5.FasciaReddito = R5.Fascia
		  and T1.ScontoReddito is true)
 from contratti C1
 join contatori M1 on (C1.Contatore = M1.NumeroProgressivo and M1.Attivo = true)
 join consumi_periodo U1 on U1.Contatore = C1.Contatore
 join ultima_lettura L1 on L1.Contatore = M1.NumeroProgressivo
 join offerte O1 on C1.CodiceOfferta = O1.Codice
 join tipologie_uso T1 on C1.TipoUso = T1.Nome
 join persone_fisiche P1 on P1.CodiceCliente = C1.CodiceCliente
 join giorni_da_ultima_bolletta G1 on (C1.IdContratto = G1.IdContratto and G1.GiorniTrascorsi >= 60) -- Verifica che siano passati almeno 2 mesi dall'ultima bolletta emessa
 left join costo_attivazione_dovuto AD1 on C1.IdContratto = AD1.IdContratto
 left join canone_rai_dovuto R1 on C1.IdContratto = R1.IdContratto
where C1.DataInizio is not null
  and C1.DataCessazione is null
  and datediff(curdate(), L1.DataEffettuazione) <= 5; -- Verifica che l'ultima lettura, se esiste, sia stata effettuata al più 5 giorni fa