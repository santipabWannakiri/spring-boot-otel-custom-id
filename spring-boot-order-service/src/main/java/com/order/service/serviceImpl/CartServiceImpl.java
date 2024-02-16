package com.order.service.serviceImpl;

import com.order.service.constants.Constants;
import com.order.service.exception.type.*;
import com.order.service.model.Cart;
import com.order.service.model.json.AppResponse;
import com.order.service.model.json.ProductInfo;
import com.order.service.model.User;
import com.order.service.model.json.QuantityRequest;
import com.order.service.repository.CartRepository;
import com.order.service.repository.UserRepository;
import com.order.service.service.CartService;
import com.order.service.util.OpenTelemetryContextUtil;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.semconv.SemanticAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Value("${PRODUCT_ENDPOINT_SERVICE}")
    private String endpoint;
    private final OpenTelemetry openTelemetry;
    private final RestTemplate restTemplate;
    private CartRepository cartRepository;
    private UserRepository userRepository;
    private final Tracer tracer;
    private final TextMapSetter<HttpHeaders> setter;


    @Autowired
    public CartServiceImpl(OpenTelemetry openTelemetry, RestTemplate restTemplate, CartRepository cartRepository, UserRepository userRepository, TextMapSetter<HttpHeaders> setter, TextMapGetter<HttpHeaders> getter) {
        this.openTelemetry = openTelemetry;
        this.restTemplate = restTemplate;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.tracer = openTelemetry.getTracer(CartServiceImpl.class.getName(), "0.1.0");
        this.setter = setter;
    }

    private boolean validateProductStatusAndQuantity(int productId, int quantity) {
        Context context = OpenTelemetryContextUtil.extractContextFromRequest();
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Span validateSpan = tracer.spanBuilder(methodName).setParent(context).setSpanKind(SpanKind.CLIENT).startSpan();
        validateSpan.setAttribute(SemanticAttributes.HTTP_METHOD, "GET");
        try (Scope scope = validateSpan.makeCurrent()) {
            SpanContext spanContext = validateSpan.getSpanContext();
            String traceId = spanContext != null ? spanContext.getTraceId() : "Unable get traceId";
            System.out.println("Order - Validate = Trace ID : " + traceId);
            String productInfoEndpoint = endpoint + "/api/product/" + productId;
            HttpHeaders headers = new HttpHeaders();
            openTelemetry.getPropagators().getTextMapPropagator().inject(Context.current(), headers, setter);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            try {
                Optional<ProductInfo> result = Optional.ofNullable(restTemplate.exchange(productInfoEndpoint, HttpMethod.GET, httpEntity, ProductInfo.class).getBody());
                if (result.isPresent() && result.get().getStatus() != null && result.get().getStatus().equals("CURRENT")) {
                    if (result.get().getQuantity() >= quantity) {
                        return true;
                    } else {
                        log.error("Quantity exceed!!");
                        throw new QuantityExceedException(Constants.QUANTITY_EXCEED_MESSAGE);
                    }
                } else {
                    log.error("Product id : " + productId + " status not active.");
                    throw new ProductStatusNotActiveException(Constants.PRODUCT_STATUS_NOT_ACTIVE_MESSAGE);
                }
            } catch (RestClientException ex) {
                log.error("Error during REST call: " + ex.getMessage());
                throw new UnableToConnectToEndpointException(Constants.UNABLE_CONNECT_TO_ENDPOINT_MESSAGE);
            }
        } finally {
            validateSpan.end();
        }
    }

    private boolean updateExistingProduct(Cart existingProduct, int quantity, String nowDate) {
        existingProduct.setQuantity(existingProduct.getQuantity() + quantity);
        existingProduct.setUpdatedAt(nowDate);
        return saveProduct(existingProduct);
    }

    private boolean createNewProduct(User existingUser, int productId, int quantity, String nowDate) {
        Cart newProduct = new Cart();
        newProduct.setProductId(productId);
        newProduct.setQuantity(quantity);
        newProduct.setCreatedAt(nowDate);
        newProduct.setUpdatedAt(nowDate);
        newProduct.setUser(existingUser);
        return saveProduct(newProduct);
    }

    private boolean saveProduct(Cart cart) {
        Optional<Cart> result = Optional.of(cartRepository.save(cart));
        return result.isPresent();
    }

    private User findUserOrThrow(String userName) {
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername(userName));
        return existingUser.orElseThrow(() -> {
            log.error("User not found!");
            throw new UserNotFoundException(Constants.USER_NOT_FOUND_MESSAGE);
        });
    }

    private boolean deductQuantity(int productId, int quantity) {
        Context context = OpenTelemetryContextUtil.extractContextFromRequest();
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Span deductSpan = tracer.spanBuilder(methodName).setParent(context).setSpanKind(SpanKind.CLIENT).startSpan();
        deductSpan.setAttribute(SemanticAttributes.HTTP_METHOD, "PUT");
        try (Scope scope = deductSpan.makeCurrent()) {
            SpanContext spanContext = deductSpan.getSpanContext();
            String traceId = spanContext != null ? spanContext.getTraceId() : "Unable get traceId";
            System.out.println("Order - Deduct = Trace ID : " + traceId);
            String deductEndpoint = endpoint + "/api/product/deduct/" + productId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            openTelemetry.getPropagators().getTextMapPropagator().inject(Context.current(), headers, setter);
            QuantityRequest requestBody = new QuantityRequest();
            requestBody.setRequestedQuantity(quantity);
            HttpEntity<QuantityRequest> requestEntity = new HttpEntity<>(requestBody, headers);
            try {
                ResponseEntity<AppResponse> responseEntity = restTemplate.exchange(deductEndpoint, HttpMethod.PUT, requestEntity, AppResponse.class);
                if (responseEntity.getBody().getAppMessageCode().equals("SUCCESS")) {
                    log.info("Deducted quantity the product id :" + productId);
                    return true;
                } else {
                    log.error("Unable to deduct product quantity.");
                    throw new UnableToConnectToEndpointException(Constants.UNABLE_TO_DEDUCT_QUANTITY_MESSAGE);
                }
            } catch (RestClientException ex) {
                log.error("Error during REST call: " + ex.getMessage());
                throw new UnableToConnectToEndpointException(Constants.UNABLE_CONNECT_TO_ENDPOINT_MESSAGE);
            }
        } finally {
            deductSpan.end();
        }
    }

    @Override
    public boolean addProduct(int productId, int quantity, String userName) {
        Context context = OpenTelemetryContextUtil.extractContextFromRequest();
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Span addProductSpan = tracer.spanBuilder(methodName).setParent(context).setSpanKind(SpanKind.INTERNAL).startSpan();
        addProductSpan.setAttribute(SemanticAttributes.HTTP_METHOD, "POST");
        try (Scope scope = addProductSpan.makeCurrent()) {
            String nowDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString();
            User existingUser = findUserOrThrow(userName);
            validateProductStatusAndQuantity(productId, quantity);
            try {
                Optional<Cart> existingProduct = cartRepository.findByProductIdAndUserId(productId, existingUser.getId());
                if (existingProduct.isPresent()) {
                    updateExistingProduct(existingProduct.get(), quantity, nowDate);
                } else {
                    createNewProduct(existingUser, productId, quantity, nowDate);
                }
                return deductQuantity(productId, quantity);
            } catch (Exception ex) {
                log.error("Error while saving the product : " + productId);
                throw new UnableToAddProductException(Constants.UNABLE_TO_SAVE_PRODUCT_MESSAGE);
            }
        } finally {
            addProductSpan.end();
        }
    }

    @Override
    public List<Cart> getAllCarts() {
        return (List<Cart>) cartRepository.findAll();
    }
}
