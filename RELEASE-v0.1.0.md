# Chaozhi Admin v0.1.0

First public release of Chaozhi Admin.

## Positioning

Chaozhi Admin is an AI-first full-stack admin scaffold focused on generating business modules from Markdown requirements.

This release is about establishing the core workflow:

- repository-level AI rules
- backend and frontend generation conventions
- demo business docs
- permission specification
- full-stack module prompt examples

## Included In This Release

- `chaozhi-backend` Spring Boot scaffold
- `chaozhi-web` Vue 3 + Vite + Ant Design Vue admin frontend
- unified response contract: `{ code: 0, data: ... }`
- unified auth contract: `token + Redis session + Authorization header`
- default full-stack generation conventions
- demo system overview and module prompt examples
- permission system specification

## Demo Business Modules

- Material Management
- Purchase Order
- Stock-In Order
- Inventory Management
- Inventory Log
- Sales Outbound

## What This Version Proves

- business requirements can be structured as Markdown docs
- AI output can follow stable project conventions through `CLAUDE.md`
- new modules can default to real backend APIs instead of mock-only flows
- permission points and status actions can be standardized early

## Notes

- some backend placeholder naming is still reserved for later replacement
- the project is still improving around public-facing docs, demo polish, and generation quality

## Next

- better README and onboarding assets
- richer full-stack demo modules
- more polished permission integration
- stronger public release packaging
