# Intent
This library provides very basic support for JAX-RS 2.0 `@BeanParam`.

# Limitations

 * You can have exactly one `@BeanParam` on a method
 * `@FormParam` is not supported on beans at this time
 * You may not have an body entity and a `@BeanParam`, though you should almost never have a need for this configuration

# Usage
You must register both the encoder and the invocation interceptor.

```
Feign.builder()
        .encoder(new BeanParamEncoder())
        .invocationHandlerFactory(new BeanParamInvocationHandlerFactory())
        .contract(new JAXRSContract())
        .target(TestResource.class, "localhost");
```