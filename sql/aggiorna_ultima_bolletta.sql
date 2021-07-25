insert into contratti
select * from contratti as C
on duplicate key update C.DataUltimaBolletta = (select DataEmissione
											     from bollette B1
											    where B1.IdContratto = C.IdContratto
											    order by DataEmissione desc
											    limit 1);