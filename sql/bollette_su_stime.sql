-- insert into bollette (IdContratto, DataEmissione, DataScadenza, Consumi, CostoAttivazione, ParzialeCanoneRAI, Importo)
/* Trova l'ultima lettura di ogni contatore */
with ultima_lettura as (
select L.*
  from letture L, contatori C
 where L.Contatore = C.NumeroProgressivo
   and C.Attivo = true
 order by DataEffettuazione desc
 limit 1
),
/* Trova l'ultima bolletta per ogni contratto */
ultima_bolletta as (
select C.IdContratto, DataEmissione as DataUltimaBolletta
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
   and ultima_bolletta.IdContratto = contratti.IdContratto
),
/* Se questa è la prima bolletta, preleva il costo di attivazione della fornitura */
costo_attivazione_dovuto as (
select C4.IdContratto, A4.CostoUnaTantum
  from contratti C4, tipi_attivazione A4
 where C4.NomeAttivazione = A4.Nome
   and C4.IdContratto not in (select IdContratto from ultima_bolletta)
),
canone_rai_dovuto as (
select contratti.IdContratto, CanoneRAI * (GiorniTrascorsi / 360) as ParzialeCanoneRAI
  from contratti, contatori, tipologie_uso, giorni_da_ultima_bolletta
 where contratti.Contatore = contatori.NumeroProgressivo
   and contratti.TipoUso = tipologie_uso.nome
   and contratti.IdContratto = giorni_da_ultima_bolletta.IdContratto
   and tipologie_uso.CanoneRAI > 0.0
   and contatori.MateriaPrima = "Luce"
)
select C1.IdContratto, curdate(), date_add(curdate(), interval 1 month), null, coalesce(AD1.CostoUnaTantum, 0.0), coalesce(R1.ParzialeCanoneRAI, 0.0), (
											 case
												when M1.MateriaPrima = "Luce" then T1.CanoneRAI * (G1.GiorniTrascorsi / 360)
												else 0
											 end
											+ C1.NumeroPersone * T1.StimaPerPersona * G1.GiorniTrascorsi
											+ coalesce(AD1.CostoUnaTantum, 0.0)
											+ M1.Potenza * 5.0 -- Costo per kW
											) * (select Sconto
												-- Applica sconto per reddito
												from persone_fisiche P5, redditi R5
												where P5.CodiceCliente = P1.CodiceCliente
												and P5.FasciaReddito = R5.Fascia
												and T1.ScontoReddito is true), G1.GiorniTrascorsi, M1.NumeroProgressivo, M1.MateriaPrima
from contratti C1
join contatori M1 on C1.Contatore = M1.NumeroProgressivo and M1.Attivo = true
/* Verifica che non esista una lettura effettuata al più 5 giorni fa */
left join ultima_lettura U1 on M1.NumeroProgressivo = U1.Contatore and not(coalesce(datediff(curdate(), U1.DataEffettuazione), datediff(curdate(), 19000101)) <= 5)
join tipologie_uso T1 on C1.TipoUso = T1.Nome
join persone_fisiche P1 on P1.CodiceCliente = C1.CodiceCliente
join giorni_da_ultima_bolletta G1 on G1.IdContratto = C1.IdContratto
left join costo_attivazione_dovuto AD1 on C1.IdContratto = AD1.IdContratto
left join canone_rai_dovuto R1 on C1.IdContratto = R1.IdContratto
where C1.DataInizio is not null
and C1.DataCessazione is null
-- and G1.GiorniTrascorsi >= 60 -- Verifica che siano passati almeno 2 mesi dall'ultima bolletta o dall'attivazione