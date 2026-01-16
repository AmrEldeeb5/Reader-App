# Reader App Architecture Refactor Specification

## Overview

This specification defines a comprehensive architectural refactoring of the Reader Android application to transform it from a prototype with critical violations into a production-ready, maintainable, and testable application.

## What's Wrong (Summary)

### 🔴 Critical Issues
1. **Exposed API key in source code** - Security breach
2. **Broken MVVM** - ViewModels hold Context references
3. **Multiple sources of truth** - Favorites managed in 3 places
4. **Direct Firebase/Retrofit access** - No Repository Pattern
5. **Callback hell** - Mixing async paradigms

### 🟠 Major Issues
6. **Global mutable state** - Singleton anti-patterns
7. **Data layer leakage** - Realm/Firebase types in UI
8. **No dependency injection** - Tight coupling everywhere
9. **Business logic in Composables** - Untestable code
10. **Mixed LiveData/StateFlow** - Inconsistent state management

### 🟡 Moderate Issues
11. **Hardcoded strings** - No localization support
12. **Missing error handling** - Generic error messages
13. **No LazyList keys** - Performance issues
14. **Mutable state in Composables** - Lost on rotation
15. **No unit tests** - Zero confidence in refactoring

## Solution Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────┐
│      Presentation Layer             │
│  (Composables + ViewModels)         │
│  - UI State (sealed classes)        │
│  - StateFlow for reactive updates   │
│  - Hilt injection                   │
└─────────────────────────────────────┘
              ↓ depends on
┌─────────────────────────────────────┐
│        Domain Layer                 │
│  (Interfaces + Models)              │
│  - Repository interfaces            │
│  - Domain models (User, Book)       │
│  - Business logic                   │
└─────────────────────────────────────┘
              ↓ implements
┌─────────────────────────────────────┐
│         Data Layer                  │
│  (Repository Implementations)       │
│  - Data sources (Firebase, Retrofit)│
│  - Data mappers (DTO → Domain)      │
│  - Hilt modules                     │
└─────────────────────────────────────┘
```

### Key Technologies

- **Dependency Injection**: Hilt/Dagger (replacing Koin)
- **State Management**: StateFlow with sealed UI states
- **Architecture**: Clean Architecture with Repository Pattern
- **Testing**: JUnit + MockK + Kotest Property Testing
- **Database**: Realm (properly abstracted)
- **Network**: Retrofit (properly abstracted)
- **Auth**: Firebase Auth (properly abstracted)

## Implementation Plan

### 8-Week Phased Approach

**Week 1-2**: Foundation (Hilt, API security, domain layer)
**Week 3-4**: Data layer (repositories, data sources, mappers)
**Week 5-6**: Presentation layer (ViewModels, Composables)
**Week 7-8**: Testing, documentation, ProGuard

### Task Breakdown

- **60+ implementation tasks** organized into 10 phases
- **20+ property-based tests** for architecture compliance
- **30+ unit tests** for ViewModels and repositories
- **10+ integration tests** for end-to-end flows

## Files in This Spec

1. **requirements.md** - 20 detailed requirements with EARS acceptance criteria
2. **design.md** - Complete architecture design with diagrams and code examples
3. **tasks.md** - 60+ implementation tasks with requirement traceability
4. **README.md** - This file (overview and quick reference)

## Quick Start

To begin implementation:

1. Read `requirements.md` to understand what needs to be fixed
2. Review `design.md` to understand the target architecture
3. Follow `tasks.md` sequentially, starting with Phase 1

## Success Criteria

✅ Zero Koin dependencies (migrated to Hilt)
✅ Zero hardcoded API keys (using BuildConfig)
✅ Zero Context references in ViewModels
✅ Zero direct Firebase/Retrofit access in ViewModels
✅ Single source of truth for all data
✅ 80%+ test coverage for ViewModels and repositories
✅ All strings in string resources
✅ ProGuard enabled for release builds
✅ Comprehensive KDoc documentation

## Next Steps

Open `tasks.md` and click "Start task" on task 1.1 to begin the refactoring journey!
