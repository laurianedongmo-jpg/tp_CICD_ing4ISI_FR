# API Gateway & Discovery Service - Conception détaillée

## 1. API Gateway - Responsabilités
- Point d'entrée unique pour toutes les requêtes
- Authentification et autorisation (JWT)
- Routage vers les microservices
- Rate limiting
- Load balancing
- Logging et monitoring
- CORS configuration

## 2. Architecture API Gateway

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                             │
├─────────────────────────────────────────────────────────────┤
│  Security Layer                                              │
│  ├── JWT Authentication Filter                              │
│  ├── Authorization Filter                                   │
│                                                              │
│  Gateway Layer                                               │
│  ├── Route Configuration                                    │
│  ├── Load Balancer                                          │
│  ├── Circuit Breaker                                        │
│                                                              │
│  Cross-Cutting Concerns                                      │
│  ├── Rate Limiter                                           │
│  ├── Request Logger                                         │
│  ├── CORS Handler                                           │
└─────────────────────────────────────────────────────────────┘
         │
         ├──────────┬──────────┬──────────┬──────────┐
         ▼          ▼          ▼          ▼          ▼
    Client     Compte    Transaction Composite Notification
    Service    Service    Service     Service   Service
```

## 3. Configuration des routes

### application.yml
```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        # Client Service
        - id: client-service
          uri: lb://client-service
          predicates:
            - Path=/api/clients/**
          filters:
            - name: CircuitBreaker
              args:
                name: clientServiceCircuitBreaker
                fallbackUri: forward:/fallback/client
            - name: RequestRateLimiter
              args:
                redis-rate-limiter:
                  replenishRate: 10
                  burstCapacity: 20
        
        # Compte Service
        - id: compte-service
          uri: lb://compte-service
          predicates:
            - Path=/api/comptes/**
          filters:
            - name: CircuitBreaker
              args:
                name: compteServiceCircuitBreaker
            - name: RequestRateLimiter
              args:
                redis-rate-limiter:
                  replenishRate: 20
                  burstCapacity: 40
        
        # Transaction Service
        - id: transaction-service
          uri: lb://transaction-service
          predicates:
            - Path=/api/transactions/**
          filters:
            - name: CircuitBreaker
              args:
                name: transactionServiceCircuitBreaker
            - name: RequestRateLimiter
              args:
                redis-rate-limiter:
                  replenishRate: 50
                  burstCapacity: 100
        
        # Composite Service
        - id: composite-service
          uri: lb://composite-service
          predicates:
            - Path=/api/composite/**
          filters:
            - name: CircuitBreaker
              args:
                name: compositeServiceCircuitBreaker
        
        # Notification Service (admin only)
        - id: notification-service
          uri: lb://notification-service
          predicates:
            - Path=/api/notifications/**
          filters:
            - name: CircuitBreaker
              args:
                name: notificationServiceCircuitBreaker

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,gateway
```

## 4. Authentification JWT

### JWT Filter
```java
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    private static final List<String> PUBLIC_URLS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/actuator/health"
    );
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        
        // Skip authentication for public URLs
        if (PUBLIC_URLS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }
        
        String token = extractToken(exchange.getRequest());
        
        if (token == null || !tokenProvider.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        // Add user info to headers
        String userId = tokenProvider.getUserIdFromToken(token);
        ServerHttpRequest modifiedRequest = exchange.getRequest()
            .mutate()
            .header("X-User-Id", userId)
            .build();
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
    
    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    @Override
    public int getOrder() {
        return -100;
    }
}
```

### JWT Token Provider
```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public String generateToken(String userId, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
            .setSubject(userId)
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    }
}
```

## 5. Rate Limiting

### Configuration Redis
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

### Rate Limiter personnalisé
```java
@Configuration
public class RateLimiterConfig {
    
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id");
            return Mono.just(userId != null ? userId : "anonymous");
        };
    }
}
```

## 6. CORS Configuration

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://willbank.com"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}
```

## 7. Eureka Discovery Service

### Configuration
```yaml
server:
  port: 8761

spring:
  application:
    name: discovery-service

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 5000
```

### Application principale
```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}
```

## 8. Fallback Controller

```java
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/client")
    public ResponseEntity<Map<String, String>> clientFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Client Service temporairement indisponible");
        response.put("message", "Veuillez réessayer dans quelques instants");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/compte")
    public ResponseEntity<Map<String, String>> compteFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Compte Service temporairement indisponible");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/transaction")
    public ResponseEntity<Map<String, String>> transactionFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Transaction Service temporairement indisponible");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
```

## 9. Logging et Monitoring

### Global Filter pour logging
```java
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        log.info("Request: {} {} from {}",
            request.getMethod(),
            request.getPath(),
            request.getRemoteAddress());
        
        long startTime = System.currentTimeMillis();
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            log.info("Response: {} in {}ms",
                exchange.getResponse().getStatusCode(),
                duration);
        }));
    }
    
    @Override
    public int getOrder() {
        return -1;
    }
}
```

## 10. Table de routage

| Path | Service | Port | Rate Limit | Auth Required |
|------|---------|------|------------|---------------|
| /api/clients/** | client-service | 8081 | 10/s | Yes |
| /api/comptes/** | compte-service | 8082 | 20/s | Yes |
| /api/transactions/** | transaction-service | 8083 | 50/s | Yes |
| /api/composite/** | composite-service | 8085 | 10/s | Yes |
| /api/notifications/** | notification-service | 8084 | 5/s | Yes (Admin) |
| /api/auth/** | auth-service | 8086 | 5/s | No |

## 11. Configuration complète

### pom.xml dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.9.1</version>
    </dependency>
</dependencies>
```
