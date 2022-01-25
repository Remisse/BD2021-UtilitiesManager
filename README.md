# BD20-UtilitiesManager

## Istruzioni

1. Assicurarsi di avere installato JDK 11 e MySQL Community Edition
2. Importare la cartella del progetto come progetto Gradle in IntelliJ IDEA (consigliato) o Eclipse
3. Aprire il file `build.gradle.kts` (situato nella root del progetto), cercare il blocco `jdbc.apply` e modificare i campi `url`, `user` e `password` in base alla vostra configurazione di MySQL
   - In `url`, non eliminare il nome dello schema ('utenze')
4. Creare lo schema eseguendo da MySQL Workbench lo script `sql/schema.sql`
5. Eseguire il task Gradle `generateJooq`
6. Eseguire il task Gradle `run` per avviare l'applicazione

## Dati degli utenti

1. mariamario@gmail.com | password
2. bartolomeo@gmail.com | password
3. travaglino@gmail.com | password

## Dati dell'operatore

operatore@op.com | password