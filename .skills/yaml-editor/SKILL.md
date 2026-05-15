---
name: yaml-editor
description: >
  Apply when creating or editing YAML files. Enforces personal style rules
  that override general YAML conventions. Trigger on any .yaml/.yml write task,
  config file editing, or YAML structure decisions.
---

# YAML Editor

## Rule: Collapse Single-Child Keys with Dot Notation

When a key has only one child at the same nesting level, collapse it using `.` instead of breaking into a new indented level. Keys with two or more siblings stay expanded. Apply recursively bottom-up.

See `references/dot-notation.md` for Do/Don't examples.

### Exceptions (Spring Boot)

**1. `spring.jpa.properties.hibernate` and `logging.level`** — always expand with line breaks, never collapse with `.`.

**2. `logging.level` package entries** — each package name must be written as a flat key on its own line. Never expand package segments into nested hierarchy.

See `references/spring-boot-exceptions.md` for Do/Don't examples.

## Rule: Replace Sensitive Values with Environment Variables

Never write sensitive data directly in YAML. Replace with a `${ENV_VAR_NAME}` placeholder, then list all substituted variables separately after the YAML output.

Treat as sensitive: secrets, passwords, tokens, API keys, private keys, credentials, connection strings, DSNs.

See `references/sensitive-data.md` for Do/Don't examples.
