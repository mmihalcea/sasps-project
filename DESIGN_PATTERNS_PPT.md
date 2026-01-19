# Design Patterns - SASPS Project
## Rezumat pentru Prezentare

---

## 🏗️ Creational Patterns

### 1. **Singleton Pattern**
📍 `InstitutionCacheManager.java`

**Ce face:** Asigură o singură instanță globală pentru cache-ul instituțiilor.

**Cum ajută:**
- ✅ Economisește memorie (o singură instanță)
- ✅ Evită query-uri repetate la DB
- ✅ Acces global consistent

```java
@Component
public class InstitutionCacheManager {
    private static InstitutionCacheManager instance;
    // O singură instanță partajată în toată aplicația
}
```

---

### 2. **Builder Pattern**
📍 `InstitutionBuilder.java`, DTOs cu `@Builder`

**Ce face:** Construiește obiecte complexe pas cu pas.

**Cum ajută:**
- ✅ Cod mai lizibil (nu 10 parametri în constructor)
- ✅ Obiectele sunt imutabile
- ✅ Ușor de extins cu câmpuri noi

```java
Institution institution = new InstitutionBuilder()
    .withName("Primăria București")
    .withType(PRIMARIA)
    .withAddress("Str. Example 1")
    .build();
```

---

### 3. **Factory Pattern**
📍 `RecommendationStrategyFactory.java`

**Ce face:** Creează strategia potrivită bazată pe un parametru.

**Cum ajută:**
- ✅ Decuplare - clientul nu știe ce clasă concretă primește
- ✅ Ușor de adăugat strategii noi
- ✅ Centralizează logica de creare

```java
RecommendationStrategy strategy = factory.getStrategy("BEST_RATED");
// Returnează BestRatedStrategy fără ca clientul să știe
```

---

## 🔄 Structural Patterns

### 4. **Decorator Pattern**
📍 `RecommendationFilter.java`, `SameCountyFilter.java`, `MinimumScoreBoostFilter.java`

**Ce face:** Adaugă funcționalități noi fără a modifica codul existent.

**Cum ajută:**
- ✅ Filtre combinate dinamic (SameCounty + Boost + Sort)
- ✅ Open/Closed - adaugi filtre noi fără să schimbi codul
- ✅ Fiecare filtru e independent și testabil

```java
// Lanț de filtre aplicate în ordine
filters: [SameCountyFilter] → [MinimumScoreBoostFilter] → [FinalSortFilter]
```

---

### 5. **Composite Pattern** ⭐ NEW
📍 `CompositeRecommendationStrategy.java`

**Ce face:** Combină mai multe strategii cu ponderi personalizate.

**Cum ajută:**
- ✅ Utilizatorul alege: 40% distanță + 30% rating + 30% disponibilitate
- ✅ Strategii tratate uniform (individual sau combinate)
- ✅ Flexibilitate maximă pentru utilizator

```java
CompositeStrategy composite = new CompositeStrategy();
composite.addStrategy(nearestLocation, 0.40);  // 40%
composite.addStrategy(bestRated, 0.30);        // 30%
composite.addStrategy(fastestAvailability, 0.30); // 30%
```

---

## 🎯 Behavioral Patterns

### 6. **Strategy Pattern**
📍 `RecommendationStrategy.java`, 4 implementări

**Ce face:** Algoritmi interschimbabili la runtime.

**Cum ajută:**
- ✅ Utilizatorul alege algoritmul din UI
- ✅ Ușor de adăugat algoritmi noi
- ✅ Fiecare algoritm e izolat și testabil

| Strategie | Criteriu Principal |
|-----------|-------------------|
| `NearestLocationStrategy` | Distanță (Haversine) |
| `FastestAvailabilityStrategy` | Primul slot liber |
| `BestRatedStrategy` | Rating (Bayesian Average) |
| `LeastBusyStrategy` | Grad de ocupare scăzut |

---

### 7. **Template Method Pattern**
📍 `RecommendationTemplate.java`, `StandardRecommendationProcessor.java`

**Ce face:** Definește scheletul algoritmului, subclasele personalizează pașii.

**Cum ajută:**
- ✅ Flow consistent: Validare → Procesare → Filtrare → Răspuns
- ✅ Evită duplicarea codului
- ✅ Ușor de extins cu pași noi

```
┌─────────────────────────────────────────────────────┐
│  1. validate()  →  2. preProcess()  →  3. execute() │
│  4. applyFilters()  →  5. postProcess()  →  6. build│
└─────────────────────────────────────────────────────┘
```

---

### 8. **Observer Pattern**
📍 `@Scheduled`, Notification System

**Ce face:** Notifică automat utilizatorii când se apropie programarea.

**Cum ajută:**
- ✅ Reminder-uri automate (24h înainte)
- ✅ Decuplare - scheduler-ul nu știe de UI
- ✅ Ușor de adăugat canale noi (SMS, email, push)

---

## 📊 Sumar Vizual

```
┌────────────────────────────────────────────────────────────┐
│                    RECOMMENDATION ENGINE                    │
├────────────────────────────────────────────────────────────┤
│  REQUEST                                                    │
│     ↓                                                       │
│  [Factory] → Creează strategia potrivită                   │
│     ↓                                                       │
│  [Strategy] → Execută algoritmul (sau Composite)           │
│     ↓                                                       │
│  [Template Method] → Procesează în pași definiți           │
│     ↓                                                       │
│  [Decorator] → Aplică filtre în lanț                       │
│     ↓                                                       │
│  RESPONSE                                                   │
└────────────────────────────────────────────────────────────┘
```

---

## 💡 Beneficii Generale

| Principiu | Cum e respectat |
|-----------|-----------------|
| **Single Responsibility** | Fiecare strategie/filtru face un singur lucru |
| **Open/Closed** | Adaugi strategii/filtre noi fără să modifici codul existent |
| **Dependency Inversion** | Depindem de interfețe, nu de implementări concrete |
| **Don't Repeat Yourself** | Template Method elimină duplicarea |
| **Separation of Concerns** | Factory creează, Strategy execută, Decorator filtrează |

---

## 🎓 Întrebări Frecvente

**Q: De ce Strategy și nu if-else?**
> Cu Strategy, adaugi un algoritm nou = o clasă nouă. Cu if-else, modifici codul existent și riști să strici ce funcționa.

**Q: De ce Factory și nu `new Strategy()`?**
> Factory centralizează crearea. Dacă schimbi cum se creează o strategie, o faci într-un singur loc.

**Q: De ce Composite?**
> Utilizatorul poate combina criterii: "Vreau aproape, DAR și bine cotat". Un singur algoritm nu poate face asta.

**Q: De ce Decorator și nu un singur filtru mare?**
> Decorator = filtre modulare. Poți activa/dezactiva filtre individual, poți reordona, poți adăuga fără să schimbi nimic.

---

## 📁 Fișiere Cheie

```
src/main/java/edu/saspsproject/
├── strategy/recommendation/
│   ├── RecommendationStrategy.java        ← Interfață Strategy
│   ├── NearestLocationStrategy.java       ← Implementare
│   ├── FastestAvailabilityStrategy.java   
│   ├── BestRatedStrategy.java             
│   └── LeastBusyStrategy.java             
├── factory/recommendation/
│   └── RecommendationStrategyFactory.java ← Factory Pattern
├── decorator/recommendation/
│   ├── RecommendationFilter.java          ← Interfață Decorator
│   ├── SameCountyFilter.java              
│   ├── MinimumScoreBoostFilter.java       
│   └── FinalSortAndLimitFilter.java       
├── composite/recommendation/
│   └── CompositeRecommendationStrategy.java ← Composite Pattern
├── template/recommendation/
│   ├── RecommendationTemplate.java        ← Template Method
│   └── StandardRecommendationProcessor.java
├── singleton/
│   └── InstitutionCacheManager.java       ← Singleton
└── builder/
    └── InstitutionBuilder.java            ← Builder
```

---

**Total: 8 Design Patterns implementate** 🎉
