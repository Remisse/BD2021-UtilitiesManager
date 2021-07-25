-- Calcolare la media dei consumi riportati in bolletta per ogni contatore di una data materia prima in un dato comune e in un dato bimestre

select avg(SommaConsumi)
from(select M1.NumeroProgressivo, sum(B1.Consumi) as SommaConsumi
		from bollette B1, contratti C1, contatori M1
		where B1.IdContratto = C1.IdContratto
		and C1.Contatore = M1.NumeroProgressivo
		and C1.Contatore in (
			select distinct M.NumeroProgressivo
			from contatori M, immobili I
			where M.IdImmobile = I.IdImmobile
			and M.MateriaPrima = "Luce"
			and I.IdZona in (select Z2.IdZona
							   from zone Z2
							  where Z2.Comune = "Forlì")
		)
		and B1.DataEmissione >= 20210301
		and B1.DataEmissione <= 20210701
		group by M1.NumeroProgressivo) as T;



