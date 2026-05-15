---
name: markdown-writer
description: >
  Use this skill whenever writing or editing any Markdown file (`*.md`).
  Triggers: creating docs, README, changelogs, reports, or any `*.md` output.
---

# markdown-writer

Markdown authoring rules using GFM for AI agents.

## Rules

* Headers: use h1–h3 hierarchy only
* Blank line required between all block elements (headings, paragraphs, lists, code blocks)
* No blank lines between sibling list items
* Unordered list symbol: `*` bullet only
* Space between list symbol and text: exactly one space after `*`
* Nested list indent: 4 spaces per level

## GFM Extensions

* **Mermaid**: visualize complex workflows/architecture with fenced ` ```mermaid ` blocks
* **Checklist**: `- [ ] todo` / `- [x] done`
* **Table**: align columns for readability
