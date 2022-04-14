 # Tutorsystem - Frontend Dokumentation

## Inhalt

[Development Setup](#development-setup)

[Architektur](#architektur)

[Ordnerstruktur](#ordnerstruktur)

[Ordnerinhalte](#ordnerinhalte)

 - [src/](#src)
 - [src/api](#srcapi)
 - [src/components](#srccomponents)
 - [src/context](#srccontext)
 - [src/types](#srctypes)
 - [src/utils](#srcutils)

## Development Setup

Siehe [Frontend Setup](./Setup.md).

Für die Entwicklung wird ein Dev-Container verwendet und mit Docker gestartet. Bevorzugte Entwicklungsumgebung ist VSCode. Das Projek wird mit `npm start` gestartet.

## Architektur

Das Projekt verwendet folgende Technologien:

- [React](https://reactjs.org/) als Frontend-Framework
- [Ant Design](https://ant.design/) als UI-Bibliothek
- [TypeScript](https://www.typescriptlang.org/) als Programmiersprache (`.tsx` Dateien für React-Kompatibilität)
- [SCSS](https://sass-lang.com/) als CSS-Erweiterung für Styles

## Ordnerstruktur

Die Ordnerstruktur folgt generell der bei `create-react-app` erstellten Struktur. Darüber hinaus enthält der `src` Ordner den Quellcode. Folgende Aufstellung beschreibt den Inhalt des `src` Ordners und seinen Unterordnern.

| Ordner           | Inhalt                                                       |
| ---------------- | ------------------------------------------------------------ |
| `src/api`        | Schnittstelle für Backend-Kommunikation                      |
| `src/components` | UI-Komponenten der App, die angezeigt werden                 |
| `src/context`    | Definition des Kontexts eines eingeloggten Nutzers           |
| `src/types`      | TypeScript-Interfaces als Objekttypen, Konstanten und Enums, die in der App verwendet werden |
| `src/utils`      | Von mehreren Komponenten gemeinsam genutzte Funktionen       |
| `src/`           | Startdateien für React und Einstiegspunkt `App.tsx`          |



## Ordnerinhalte

Im Folgenden wird im Detail auf die einzelnen Unterordner und die enthaltenen Dateien eingegangen.

### src/

Enthält Elemente, die bei der Erstellung der Grundstruktur mit `create-react-app` erstellt wurden. Enthält zentralen Einsteigspunkt `App.tsx` für die Anwendung, in dem der Login-Status als App-weit geltende Information festgelegt wird. Routing mit [ReactRouter v6](https://reactrouter.com/) wird zusammen mit der Autorisierung für jede Route App-weit festgelegt.

### src/api

Enthält `api.tsx`, in der die gesamte Kommunikation zum Backend geregelt wird. Als HTTP-Client wird [Axios](https://axios-http.com/) verwendet. Die URL des Backend wird über eine `.env` Datei ausgelesen (siehe [Setup](#development-setup) und [env variables](https://create-react-app.dev/docs/adding-custom-environment-variables/)).

Die Realisierung der Authentifizierung und Autorisierung über [JsonWebTokens (JWT)](https://jwt.io/) erfolgt zentralisert in dieser Datei. Dabei wird bei `login` und `logout` entsprechend das Token als `Authorization` Header bei jedem nachfolgenden Backend-Aufruf mitgesendet, wodurch gegenüber dem Backend der Nutzer als solcher identifiziert werden kann und entsprechende Rechte gewährt werden. Siehe dazu auch das [UserContext Objekt](# srccontext) und die Implementierung innerhalb der `App.tsx`.

Die Fehlerbehandlung wird mithilfe des `RequestError.tsx` in [`src/types`](#srctypes) zentral gesteuert. Dort werden Error-Codes zusammen mit einer Textrepräsentation gepflegt.

### src/components

Komponenten wie einzelne UI-Elemente oder ganze Seiten werden in diesem Ordner in weiteren Unterordnern strukturiert. Jeder dieser Unterordner enthält mindestens eine `.tsx` Datei, die eine UI-Komponente enthält. Optional kann eine `.scss` Datei enthalten sein, die von der Komponente genutzte Styles enthält.

Die folgende Aufstellung zeigt die Unterordner mit einer Beschreibung des Inhalts. Einige Dateien sind zwar angelegt, besitzen aber noch keine oder nur eine unzureichende Funktion. Diese Komponenten sind im Folgenden entsprechend gekennzeichnet.

| Ordner (src/components/) | Unzureichende Funktion | Inhalt/Zweck                                                 |
| ------------------------ | ---------------------- | ------------------------------------------------------------ |
| adminOverview            | X                      | Überblicksseite für Administratoren ohne Funktion.           |
| directorOverview         | X                      | Überblickseite für Studiengangsleiter ohne Funktion.         |
| inputs                   |                        | Inputelemente für Email, Passwort und Text. Nutzung dieser Komponente innerhalb der App versichert einheitliche Validierung dieser Eingabefelder. |
| login                    |                        | Login-Seite für Anmeldung. Modal für "Passwort vergessen" Funktion, in dem neues Passwort gewählt werden kann. |
| navigation               |                        | Navigationsleiste, die im Einstiegspunkt `App.tsx` im Seitenlayout verankert wird. Zeigt abhängig vom Login-Status eines Nutzers die entsprechenden Reiter für sämtliche Seiten wie Übersichtsseite, Login, Einstellungen, Administratorübersicht, Tutoriumsübersicht und so weiter an. |
| overview                 | X                      | Öffentliche Übersichtsseite, die in Kartenform wichtige Funktionen anzeigt, die je nach Login-Status ausführbar sind. Weitere Funktionen sollten dieser Seite hinzugefügt werden. Ebenfalls ist die Gestaltung nicht final. |
| pagingList               |                        | Listenkomponente, die eine Liste mit variablen Elementen anzeigt. Kann größere Datenmengen mithilfe einer Paging-Konfiguration anzeigen und ist flexibel für Listen einsetzbar. |
| register                 |                        | Registrierungsseite zur Erstellung neuer Nutzerkonten.       |
| routes                   |                        | Generalisierte Routen `ProtectedRoute.tsx` und `Unauthorized.tsx`, die zusammen mit dem Router in `App.tsx` verwendet werden. Im Falle des Aufrufens einer Route über die URL, zu der gemäß Login-Status keine Berechtigung besteht, kann so zu einer einheitlichen `Unauthorized` Route weitergeleitet werden. |
| settings                 | X                      | Einstellungsseite, auf der Daten des angemeldeten Nutzers angezeigt werden. Enthält Modal, das bei Passwortänderung aufgerufen wird und dem Nutzer erlaubt, ein neues Passwort zu setzen. Ist der Nutzer ein Administrator, so kann er aus Sicherheitsgründen sein Passwort nicht ändern. Nur die E-Mail des Nutzers wird angezeigt, die restlichen Nutzerdaten fehlen. Nur das Passwort kann geändert werden. Es fehlt die Implementierung einer Aktualisierungsfunktion, um weitere Daten wie Vor-/Nachname ändern zu können. |
| tutorialCreateModal      |                        | Modal mit Eingabemaske zur Erstellung eines Tutoriums, wobei nur ein angemeldeter Studiengangsleiter diese Aktion durchführen soll. Das erstellte Tutorium wird im Backend gespeichert. |
| tutorialOfferModal       |                        | Modal mit Eingabemaske zur Erstellung eines Tutoriumsangebots. Tutoriumsangebote werden gemäß der Prozesse vom Studiengangsleiter auf dem Weg von Emails verwaltet und werden **nicht** im Backend gespeichert. Die eingegebenen Daten werden daher in einen String gesetzt, der mithilfe von `mailto` vom Nutzer über sein eigenes Email-Programm versendet werden kann. Je nach ausgewähltem Kurs werden die Email-Adressen aller zugehöriger Studiengangsleiter in das Empfänger-Feld gesetzt. |
| tutorialRequestModal     |                        | Modal mit Eingabemaske zur Erstellung einer Tutoriumsanfrage. Die erstellte Anfrage wird im Backend gespeichert. |
| tutorials                |                        | Öffentliche Übersichtsseite für Tutorien `TutorialsOverview.tsx`, die vom Studiengangsleiter eingetragen wurden. Nutzt die `PagingList`, um große Datenmengen in einzelne Seiten aufzuteilen, sowie eine Eingabemaske mit Filterkriterien. Diese Filterkriterien werden erweitert um die Kriterien *markiert*, *teilgenommen* und *halten*, falls der Nutzer ein angemeldeter Student ist. Diese Kriterien erlauben ein Filtern nach Tutorien, die man markiert hat, an denen man teilnimmt, oder die man hält.<br />Beim Auswählen eines Tutoriums wird zur Seite `TutorialDetails.tsx` weitergeleitet, in der das Tutorium im Detail angezeigt wird. Die Aktionen *vormerken* und *teilnehmen* sind auf dieser Seite für angemeldete Studenten möglich, respektive *nicht mehr vormerken* und *nicht mehr teilnehmen*. Ist der angemeldete Student ein Tutor dieses Tutoriums, so sieht er statt den beiden Aktionen nur die Information, dass er dieses Tutorium hält. Ein angemeldeter Studiengangsleiter kann statt der beiden Aktionen nur das Tutorium *löschen*. Nicht angemeldete Nutzer sehen wie angemeldete Studenten die Aktionen *vormerken* und *teilnehmen*, wobei beim Auswählen dieser Aktionen auf die Login-Seite weitergeleitet wird.<br />Im Falle des Löschens eines Tutoriums durch den Studiengangsleiter, wird bei der *löschen* Aktion zunächst das `TutorialDeleteModal.tsx` angezeigt, in dem optional ein Grund für die Löschung angegeben werden kann. |
| verify                   |                        | Seiten für die Verifizierung, auf die in den entsprechenden Emails verlinkt wird. `VerifyRegistration.tsx` wird zur Bestätigung der Registrierung verwendet und verifiziert einen Hash-Wert durch Kontaktierung des Backend. `VerifyResetPassword.tsx` wird im Passwort-Vergessen-Prozess verwendet und fragt zunächst als Sicherheitsmaßnahme erneut das neu gewählte Passwort ab. Danach wird dieses Passwort zusammen mit einem Hash-Wert durch das Backend verifiziert. Im Erfolgfall leiten beide Seiten auf die Startseite weiter. |



### src/context

Enthält eine abstrakte Definition des [React Context Objekts](https://reactjs.org/docs/context.html), das verwendet wird, um den Status des Nutzer-Logins in allen UI-Komponenten verfügbar zu machen. Es definiert den eingeloggten Nutzer `loggedUser`, eine Funktion zum `login` und `logout`, sowie eine Funktion `hashRoles` zum Prüfen, ob ein eingeloggter Nutzer eine bestimmte Nutzerrolle besitzt, wie Student oder Administator. In `App.tsx` wird eine Implementierung der definierten Funktionen eingesetzt.

### src/types

Enthält Typdeklarationen als TypeScript-Interfaces, um die Struktur der verwendeten Datenobjekte, wie zB eines Tutoriums, zu definieren. `AppRoutes.tsx` enthält Konstanten für die Strings der einzelnen Routen innerhalb der Webseite. Der Typ `RequestError.tsx` enthält Error-Codes und eine Übersetzung dieser Codes in deutsche Textnachrichten. Diese Liste ist **nicht komplett** und sollte mit möglichen Java-Exceptions im Backend im Sync gehalten werden.

### src/utils

Enthält Messages-Konstante für global einheitliche, deutsche Validierungstexte in Formularen bzw. Eingabemasken. Ebenfalls ist eine Funktion für Zeitkonvertierung enthalten.



