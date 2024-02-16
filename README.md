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


## Context Management Approaches
When implementing OpenTelemetry (Otel) manually, there are 2 primary contexts to manage:

#### 1. Context between classes and function: This refers to sharing context within a single service, where different classes or methods might need access to the same tracing information.
Common approaches:
## 1. Method Arguments

**Description:** Pass the context object as an argument to methods that require it.

**Pros:**
- Explicit and modular, showing dependencies clearly in method signatures.
- Improves testability as dependencies are explicitly provided.

#### 2. ThreadLocal Storage

**Description:** Store the context in a thread-specific variable for access within methods of the same thread.

**Pros:**
- Simplifies parameter passing, as the context is accessible within the same thread.
- Well-suited for single-threaded or thread-local contexts.

#### 3. Dependency Injection Frameworks

**Description:** Manage context as a bean and inject it into required classes.

**Pros:**
- Centralized management of dependencies, promoting modularity.
- Can simplify dependency handling, especially in larger applications.
- Can be compatible with various frameworks that support dependency injection.

| Approach               | Pros                                                                                                         | Cons                                                                                                              |
|------------------------|--------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| Method Arguments   | - Explicit Dependency: Clearly shows dependencies by passing the OTel context as an argument.                | - Parameter Passing Overhead: May lead to larger method signatures and require passing the context through all intermediate method calls.                                      |
|                        | - Modularity: Enhances modularity, making methods independent of global state.                                |                                                                                                                    |
|                        | - Testability: Improves testability as dependencies are explicitly provided.                                  |                                                                                                                    |
| **ThreadLocal Storage**| - Simplified Parameter Passing: Simplifies parameter passing within the same thread.                          | - Limited to Single Thread: May not work well in multi-threaded scenarios without careful management.              |
|                        | - Thread-Specific Access: Well-suited for single-threaded or thread-local contexts.                             | - Potential for Global State: If not managed properly, thread-local storage can introduce global state concerns.  |
| **Dependency Injection Frameworks** | - Centralized Management: Promotes centralized management of dependencies, enhancing modularity.           | - Learning Curve: May introduce a learning curve, especially for developers unfamiliar with the specific dependency injection framework.                                           |
|                        | - Simplified Dependency Handling: Can simplify dependency handling, especially in larger applications.         | - Potential for Global State: If not managed carefully, dependency injection frameworks can introduce global state concerns. |

---


Considerations:
Concurrency Requirements: If your application involves multi-threading or asynchronous operations, ThreadLocal might not be the most suitable choice unless managed with caution. Method arguments or dependency injection might offer better thread safety.

Simplicity vs. Explicitness: ThreadLocal can simplify the code by avoiding explicit parameter passing, but it might make dependencies less visible compared to method arguments or dependency injection.

Project Scale: For larger projects with complex dependency management requirements, dependency injection might provide a more scalable solution. For smaller projects, a simpler approach like method arguments or ThreadLocal might suffice.

2. Context between services: This involves propagating context across network boundaries, typically between different microservices or components in a distributed system.
* Crucial for tracing: Enables end-to-end tracing by ensuring all services involved in a request share the same tracing context.
* Primary approach: Inject context into outgoing messages (e.g., HTTP headers, gRPC headers) using OpenTelemetry propagators.

[Medium - Context Propagation in OpenTelemetry](https://medium.com/@danielbcorreia/context-propagation-in-opentelemetry-3f53ab31bcf5)\
[Elastic - Manual instrumentation of Java applications with OpenTelemetry](https://www.elastic.co/blog/manual-instrumentation-of-java-applications-opentelemetry)\
[Otel - Manual instrumentation for OpenTelemetry Java](https://opentelemetry.io/docs/languages/java/instrumentation/)\
[Dynatrace - Manually instrument your Java application with OpenTelemetry](https://docs.dynatrace.com/docs/extend-dynatrace/opentelemetry/walkthroughs/java/java-manual)\
[OTLP Collector Setup](https://opentelemetry.io/docs/languages/java/exporters/)\
[Example setup OTLP](https://github.com/elastic/observability-examples/blob/main/Elastiflix/java-favorite-otel-manual/src/main/java/com/movieapi/FavoriteApplication.java)
