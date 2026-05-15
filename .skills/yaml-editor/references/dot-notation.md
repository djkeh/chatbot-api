# Dot Notation Examples

## Single-child chain

Do:
```yaml
this.is.my: property
```

Don't:
```yaml
this:
  is:
    my: property
```

## Mixed siblings

Do:
```yaml
this:
  apple.is: red
  pineapple:
    is: yellow
    and: sweet
    also.has: leaves
  melon.is.very: large
  berry.has:
    color: red
    flavor: sour
```

Don't:
```yaml
this:
  apple:
    is: red
  pineapple:
    is: yellow
    and: sweet
    also:
      has: leaves
  melon:
    is:
      very: large
  berry:
    has:
      color: red
      flavor: sour
```
