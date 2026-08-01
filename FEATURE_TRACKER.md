# ScrapPro Feature Tracker

## Phase 2 — Persistent Home dashboard

| Requirement | Status | Implementation |
|---|---|---|
| Repository-backed Home states | Complete | `HomeViewModel`, immutable `HomeUiState`, `HomeRepository`, and Room flows expose loading, empty, populated, and error states. |
| Persistent active haul | Complete | Room schema v2 stores a singleton haul, ordered metal line items, expenses, labor, and timestamps. Totals are derived by `ProfitCalculationEngine`. |
| Profit integration | Complete | Profit restores the active haul and saves current multi-metal inputs and deductions back to Room. |
| Haul lifecycle actions | Complete | Home can start, edit, complete/log, and confirmation-clear a haul; Profit adds/removes items and saves calculations. |
| Recent activity | Complete | Completed hauls are inserted into `trip_logs` and observed newest-first with optional persisted yard names. |
| Restart/recreation persistence | Complete | Room is the source of truth; ViewModels collect cold database flows and navigation retains tab state. |
| Safe migration | Complete | Explicit migration 1→2 creates active-haul tables and preserves schema-v1 yard/trip data; destructive migration is removed. |
| Empty-state integrity | Complete | No sample haul, earnings, or recent activity is inserted. The empty state explains how to start and populate a haul. |
| Tests | Complete | Repository and migration tests cover empty state, creation, totals, deductions, restore, completion, ordering, and schema migration. |

## Follow-up asset delivery

This branch remains binary-free. Restore the authoritative wrapper and artwork listed in `README.md` in a separate asset-only change; do not substitute placeholders or recompressed files.

## Phase 3 — Persistent yards directory and price tracker

| Requirement | Status | Implementation |
|---|---|---|
| Permission-safe directory | Complete | Room yards render immediately; location is requested only through “Use My Location” and protected access is gated. |
| Location states and distance | Complete | Approximate, precise, denied, permanently denied, disabled, unavailable, and active states are explicit; acquired coordinates recalculate and sort distances. |
| Yard CRUD and favorites | Complete | Users can add, edit, delete, favorite, and search complete local yard profiles. |
| Starter dataset integrity | Complete | Optional import creates clearly labeled editable templates without phone numbers, prices, or claims of live accuracy. |
| Filters and empty states | Complete | Metal, vehicle, e-waste, hours, scale, payout, favorites, radius, empty, and no-match states are implemented. |
| Price tracking | Complete | User-reported yard prices support units, timestamps, age/staleness, history, creation, and deletion. |
| Keyless intents | Complete | Validated dialer and native geo intents use resolver checks, browser fallback, and visible failure messages. |
| Safe migration | Complete | Explicit migration 2→3 preserves legacy yard/price columns and adds the complete directory schema. |
