# Proiect SASPS - Sistem de programari la ghiseu

## Descriere
Sistemul va permite unui utilizator sa se programeze creeze programari pentru ghiseele mai multor institutii.
Aplicatia este compusa dintr-o interfata utilizator web si un serviciu de back-end. Vor exista 2 implementari pe branch-uri diferite: 
 * O varianta fara a utiliza design patterns
 * O varianta refactorizata cu design patterns
###
Design patterns luate in vedere: Factory Method, Facade, Adapter, Strategy

## Functionalitati
 * Calendar pentru programare la o anumita institutie
 * Formular pentru introducerea datelor
 * Notificari prin diverse mijloace dupa ce formularul a fost trimis


## Metrici urmarite
1. Efortul depus in adaugarea de functionalitati noi:
    * adaugarea unei noi institutii
    * adaugarea unui nou mijloc de notificare
####
2. Metrici de calitate a codului folosind SonarQube:
    * Maintainability (Maintainability issues, Technical debt, Maintainability rating)
    * Complexity (Cyclomatic complexity, Cognitive complexity)
    * Duplications (Duplicated lines density (%), Duplicated lines)
    * Reliability (Reliability issues, Reliability rating, Reliability remediation effort)