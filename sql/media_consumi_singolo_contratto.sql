select avg(SommaConsumi)
from(select sum(B1.Consumi) as SommaConsumi
		from bollette B1, contratti C1
		where C1.IdContratto = 1
        and B1.IdContratto = C1.IdContratto
		and B1.DataEmissione >= 20210301
		and B1.DataEmissione <= 20210701) as T;