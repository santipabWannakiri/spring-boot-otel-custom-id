# spring-boot-otel-txn-id

3 type of instrumentation
* Automatic Instrumentation
* Automatic Instrumentation with Annotations
* Manual Instrumentation



```java
void inject(Context context, @Nullable C carrier, TextMapSetter<C> setter);
```
The inject method is a key component in this process. It allows you to inject trace context into a carrier, such as HTTP headers or messaging system properties, facilitating context propagation.

* context: OpenTelemetry context containing trace context information. ex (Traceparent: 00-0123456789abcdef0123456789abcdef-0123456789abcdef-01)
* carrier: Carrier where the context information will be injected (e.g., HTTP headers, messaging system properties).
* setter: Implementation of the TextMapSetter interface responsible for setting key-value pairs in the carrier.


[Context Propagation in OpenTelemetry](https://medium.com/@danielbcorreia/context-propagation-in-opentelemetry-3f53ab31bcf5)\
[Manual instrumentation of Java applications with OpenTelemetry](https://www.elastic.co/blog/manual-instrumentation-of-java-applications-opentelemetry)\
[Manual instrumentation for OpenTelemetry Java](https://opentelemetry.io/docs/languages/java/instrumentation/)
