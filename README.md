# Aero
Inbox Outbox Pattern with Kafka

### Flugzeuginformationssystem

**Apps**:
* common: https://start.spring.io/#!type=gradle-project-kotlin&language=kotlin&platformVersion=3.1.1&packaging=jar&jvmVersion=17&groupId=de.aero&artifactId=common&name=common&description=&packageName=de.aero.common&dependencies=kafka,kafka-streams
* Anzeigetafelsteuerung -> Display
    * https://start.spring.io/#!type=gradle-project-kotlin&language=kotlin&platformVersion=3.1.1&packaging=jar&jvmVersion=17&groupId=de.aero&artifactId=display&name=display&description=&packageName=de.aero.display&dependencies=web,kafka,data-jpa,liquibase
* Verkehrsflugsystemanbindung -> IFIS
    * https://start.spring.io/#!type=gradle-project-kotlin&language=kotlin&platformVersion=3.1.1&packaging=jar&jvmVersion=17&groupId=de.aero&artifactId=ifis&name=ifis&description=&packageName=de.aero.ifis&dependencies=web,kafka,data-jpa,liquibase
* Recovery App -> recover
    * https://start.spring.io/#!type=gradle-project-kotlin&language=kotlin&platformVersion=3.1.1&packaging=jar&jvmVersion=17&groupId=de.aero&artifactId=recovery&name=recovery&description=&packageName=de.aero.recovery&dependencies=web,kafka,data-jpa,kafka-streams,liquibase

**Topics**:
* flights
* flights-display-recovery
* dlt

**Kommunikationsswege**:
IFIS -> _flights_ -> Display -> _dlt_ -> recover -> _flights-display-recovery_ -> Display

Outbox Pattern bei IFIS implementieren

**Annahmen**:
* Jede empfangende App definiert genau eine Consumer Group, sodass die App die Ebene ist, auf der Nachricht erneut ausgesteuert werden
  * Kafka bietet im Recoverer die Möglichkeit die Conumer-Group mit auszulesen
  * Wenn auf der Ebene recovered wird, dann muss die Conumer-Group im Topic-Schema mit abgebildet werden
  * https://docs.spring.io/spring-kafka/docs/current/reference/html/#dead-letters
* Das DLT hat genau so viele Partitionen wir das originale Topic