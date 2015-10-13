# Intent
This library provides very basic support for JAX-RS 2.0 `@BeanParam`.

# Limitations

 * You can have at most one `@BeanParam` on a method
 * `@FormParam` is not supported on beans at this time
 * You may not have an body entity and a `@BeanParam`, though you should almost never have a need for this configuration

# Usage
You can create a Feign client using the `JAXRS2Profile` which is effectively a `Feign.Builder`.

```
TestResource client = JAXRS2Profile.create()
    .encoder(new GsonEncoder())
    .target(TestResource.class, "localhost")
```