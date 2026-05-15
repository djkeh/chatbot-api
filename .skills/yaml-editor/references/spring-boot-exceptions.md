# Spring Boot Exceptions

## Exception 1: spring.jpa.properties.hibernate and logging.level

### spring.jpa.properties.hibernate

Do:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        format_sql: true
        default_batch_fetch_size: 10
```

Don't:
```yaml
spring.jpa.properties.hibernate:
  format_sql: true
  default_batch_fetch_size: 10
```

### logging.level

Do:
```yaml
logging:
  level:
    root: debug
```

Don't:
```yaml
logging.level.root: debug
```

## Exception 2: logging.level package entries

Do:
```yaml
logging:
  level:
    root: debug
    com.google.feature1: info
    com.google.feature2: warn
    com.google.feature3.detail1: info
    com.google.feature3.detail2: warn
    com.google.feature3.detail3: info
```

Don't:
```yaml
logging:
  level:
    root: debug
    com:
      google:
        feature1: info
        feature2: warn
        feature3:
          detail1: info
          detail2: warn
          detail3: info
```
