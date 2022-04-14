 # Tutorsystem - Backend Dokumentation

## Inhalt

[Development Setup](#development-setup)

[Architektur](#architektur)

[Ordnerstruktur](#ordnerstruktur)

[Ordnerinhalte](#ordnerinhalte)

## Development Setup

Siehe [Backend Setup](./Setup.md).

Für die Entwicklung kann ein Dev-Container verwendet werden, um Kompatibilitätsproblemen Entgegen zu wirken. Bevorzugte Entwicklungsumgebung ist demnach VSCode. Alternativ kann das Backend auch lokal gestartet werden, hierbei muss nur eine postgres erreichbar sein. 

## Architektur

Das Projekt verwendet folgende Technologien:

- SpringBoot
- Lombok 

## Ordnerstruktur

Die Ordnerstruktur folgt generell der durch den `Spring Initializer` erstellten Struktur. Demnach ist in `src/main` die Implementierung enthalten, während in `src/test` die Test implementiert sind. `src/main` untergliedert sich weiter in `java` und `resources`. Letzterer Ordner beinhaltet die application.properties, Mail-Templates (thymeleaf) und ein Logging-Ordner. In ihm werden die Logs abgelegt, außerdem ist die Konfiguration der Logs dort konfiguriert. Diese muss beim Deployment auf einem Server so angepasst werden, dass die Logs außerhalb des Projekts angelegt werden. Alle Packages sind innerhalb von   `java/com/dhbw/tutorensystem`  abgelegt.

| Packages / Klassen              | Inhalt                                                       |
| ------------------------------- | ------------------------------------------------------------ |
| `course`                        | Entität der Studiengänge                                     |
| `exception`                     | Alle Klassen zu gesonderten Exception behandlung innerhalb des Projekts |
| `mails`                         | Implementierung des E-Mail versands                          |
| `ping`                          | Ping routen zum testen                                       |
| `role`                          | Rollen der User                                              |
| `security`                      | Zugriffsrechte und sicherheitsrelevante Einstellungen        |
| `specialisationCourse`          | Entität der Studiengangsschwerpunkte                         |
| `tutorial`                      | Entität der Tutorien                                         |
| `tutorialRequest`               | Entität der Tutoriumsanfragen                                |
| `user`                          | Entität der Benutzer                                         |
| `DevDataManager.java`           | Methoden zum erstellen von Demodaten zum leichteren debuggen |
| `TutorensystemApplication.java` | Root-Klasse, von der das Programm startet                    |

## Weitere Struktur

### Entitäten 

Die Entitäten enthalten einen Controller, in dem die Routen und ihre Logik enthalten sind, ein Repository für den Datenbankzugriff und eine Klasse, in der die Entität und ihre Beziehungen definiert sind. Um nur die nötigsten Daten ans Frontend zu übermitteln werden Data Transfer Objects verwendet. Jedes DTO enthält Methoden, um die Hauptentität in das DTO zu konvertieren. 

### mails

E-Mails werden über den EmailSenderService versendet. Dieser Bietet Methoden zum versenden einzelner oder mehrer E-Mails unter Angabe der E-Mailadresse, dem E-Mailtypen welcher aus dem entsprechenden E-Num stammen muss, sowie die für die E-Mail benötigten Argumente als Map. 

### role

Beinhaltet, die den Benutzern zuweisbaren Rollen. Ein Benutzer kann mehrere Rollen inne haben, die über das Enum definiert werden. 

### exception

Alle Exceptions des Systems `RestResponseEntityExceptionHandler` behandelt. Hierbei wird in drei Kategorien unterschieden: 

- javax validation Error
  Feher, die dadurch auftreten, dass die Parameter, die bei einem Request im Body mitgegeben wurden nicht den Ansprüchen der Annotationen genügt. 
- eigene Exceptions
  Exceptions, die bewusst geschmissen werden, müssen zwangsläufig von der TSBaseException erben. Diese Exceptions sind mit korrekten Fehlernachrichten und Fehlercodes ausgestattet und können am Frontend weiterverarbeitet werden 
- alle anderen Exceptions
  Wird eine Exception geschmissen, die nicht behandelt wird, wird diese Gefangen und durch eine InternelServerError Exception ersetzt, um keine weiteren Informationen nach außen zu geben. 



## Besonderheiten

### user

Der user hat weiter Spezialisierungen in Form vom Student und director, dies wird über Vererbung dargestellt. In der DB ist diese Vererbung über eine Indikatiorenspalte gelöst. 



