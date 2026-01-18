# Proiect SASPS - Sistem de programari la ghiseu

## Descriere
Sistemul va permite unui utilizator sa se programeze creeze programari pentru ghiseele mai multor institutii.
Aplicatia este compusa dintr-o interfata utilizator web si un serviciu de back-end. Vor exista 2 implementari pe branch-uri diferite: 
 * O varianta fara a utiliza design patterns
 * O varianta refactorizata cu design patterns

## 🎯 Design Patterns Implementate

| Pattern | Categorie | Descriere | Locație |
|---------|-----------|-----------|---------|
| **Factory Pattern** | Creational | Crearea programărilor specifice fiecărui tip de serviciu | `factory/` |
| **Strategy Pattern** | Behavioral | Validare flexibilă a programărilor | `service/validation/` |
| **Adapter Pattern** | Structural | Integrare uniformă provideri email/SMS | `adapter/` |
| **Template Method** | Behavioral | Template-uri email reutilizabile | `template/` |
| **Builder Pattern** | Creational | Construire fluent obiecte complexe | `builder/` |
| **Observer Pattern** | Behavioral | Sistem event-driven pentru notificări | `observer/` |
| **Singleton Pattern** | Creational | Cache manager pentru instituții | `singleton/` |
| **Decorator Pattern** | Structural | Logging, retry, metrici pentru email | `decorator/` |

📚 **Documentație detaliată:** [DESIGN_PATTERNS_IMPLEMENTED.md](DESIGN_PATTERNS_IMPLEMENTED.md)  
📊 **Analiza inițială:** [DESIGN_PATTERNS_ANALYSIS.md](DESIGN_PATTERNS_ANALYSIS.md)

## Functionalitati
 * Calendar pentru programare la o anumita institutie
 * Formular pentru introducerea datelor
 * Notificari prin diverse mijloace dupa ce formularul a fost trimis


## Metrici urmărite

### 1. Efortul de dezvoltare și extensibilitate
* **Timpul necesar** pentru adăugarea unei noi instituții (ore de development)
* **Numărul de fișiere modificate** la adăugarea unui nou mijloc de notificare
* **Complexitatea schimbărilor** (numărul de linii de cod modificate/adăugate)
* **Efortul de testare** pentru funcționalități noi

### 2. Metrici de calitate a codului
* **Maintainability**: 
  - Maintainability issues (numărul de probleme)
  - Technical debt (timpul estimat pentru remedierea problemelor create)
  - Maintainability rating (A-E)
* **Complexity**: 
  - Cyclomatic complexity (complexitatea ciclomatică medie)
  - Cognitive complexity (complexitatea cognitivă)
* **Duplications**: 
  - Duplicated lines density (% de cod duplicat)
  - Duplicated lines (numărul absolut de linii duplicate)
* **Reliability**: 
  - Reliability issues și rating
  - Reliability remediation effort

### 3. Metrici comparative
* **Dimensiunea codului** (lines of code, numărul de clase/module)
* **Acoperirea testelor** (code coverage %)
* **Performanța runtime** (timp de răspuns pentru operații standard)

## Planning

### Setup și implementarea de bază
* Configurarea proiectului (repository, dependencies, build tools)
* Implementarea versiunii **fără design patterns**
* Setup-ul tool-urilor de monitoring
* Colectarea metricilor inițiale

### Refactorizare cu design patterns
* Aplicarea design patterns-urilor în implementarea existentă
* Crearea branch-ului separat pentru versiunea refactorizată
* Documentarea schimbărilor efectuate

### Testare și comparare
* Rularea testelor de calitate pe ambele implementări
* Colectarea și analiza metricilor comparative
* Simularea adăugării de funcționalități noi pe ambele versiuni
* Documentarea rezultatelor și concluziilor

### Finalizare
* Redactarea raportului final cu analiza comparativă
* Pregătirea prezentării rezultatelor
* Review și optimizări finale