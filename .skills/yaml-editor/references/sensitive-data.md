# Sensitive Data Examples

## Substitution

Do:
```yaml
jwt:
  secret: ${JWT_SECRET}
  access-token-ttl: 5m
  refresh-token-ttl: 1d
```

Don't:
```yaml
jwt:
  secret: SE*F&NIUSHFN(YBE*UFB
  access-token-ttl: 5m
  refresh-token-ttl: 1d
```

## Post-output variable summary

After writing the YAML, list all substituted variables:

```
Environment variables to set:
  JWT_SECRET=<your-jwt-secret>
```
