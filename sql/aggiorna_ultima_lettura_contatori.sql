insert into contatori
select * from contatori as C
on duplicate key update contatori.DataUltimaLettura = (select DataEffettuazione
											   from letture L
                                               where L.Contatore = C.NumeroProgressivo
                                               order by DataEffettuazione desc
                                               limit 1)