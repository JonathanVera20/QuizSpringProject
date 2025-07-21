# Security Documentation - Quiz Project

## 🔒 Security Implementation Overview

This document outlines the security measures implemented in the Quiz Project Spring Boot application.

## 🛡️ Security Features Implemented

### 1. **Authentication & Authorization**
- **JWT Token-based Authentication**: Stateless authentication using JSON Web Tokens
- **Role-based Access Control (RBAC)**: USER and ADMIN roles with different permissions
- **Password Encryption**: BCrypt hashing for password storage
- **Token Expiration**: Configurable JWT expiration (default: 1 hour)

### 2. **Input Validation & Data Protection**
- **Spring Security**: Comprehensive security framework integration
- **Method-level Security**: `@PreAuthorize` annotations on sensitive endpoints
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **XSS Protection**: Content Security Policy headers

### 3. **Network Security**
- **CORS Configuration**: Restricted to trusted origins only
- **Security Headers**: HSTS, X-Frame-Options, X-Content-Type-Options
- **Rate Limiting**: Basic implementation to prevent abuse
- **HTTPS Ready**: SSL configuration for production

### 4. **Configuration Security**
- **Environment Variables**: Sensitive data externalized
- **Profile-based Configuration**: Different security levels per environment
- **Secret Management**: JWT secrets and DB credentials via env vars

## 🔧 Security Configuration Details

### JWT Implementation
```
- Algorithm: HMAC-SHA256
- Expiration: 1 hour (configurable)
- Claims: username, authorities, issued-at
- Secret: Minimum 256-bit key (environment variable)
```

### CORS Policy
```
- Allowed Origins: Configurable per environment
- Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
- Allowed Headers: Authorization, Content-Type, Accept
- Credentials: Disabled for security
```

### Rate Limiting
```
- Authentication endpoints: 10 requests/minute per IP
- General API endpoints: 100 requests/minute per IP
- Response headers: X-RateLimit-* information
```

## 🚨 CSRF Policy Justification

**CSRF Protection: DISABLED**

**Justification:**
1. **Stateless API**: JWT tokens in Authorization headers (not cookies)
2. **SPA/Mobile Clients**: Modern frontend applications using AJAX/fetch
3. **Custom Authentication**: Not using Spring's session-based auth
4. **Origin Validation**: CORS policy restricts request origins

**Alternative Protections:**
- Strict CORS policy
- JWT token validation
- Origin header verification
- Rate limiting

## 🌐 Production Security Checklist

### Environment Variables Required:
```bash
# Database Security
DATABASE_URL=jdbc:postgresql://prod-host:5432/quiz_prod
DATABASE_USERNAME=quiz_user
DATABASE_PASSWORD=your_secure_password

# JWT Security
JWT_SECRET=your_256_bit_secret_key
JWT_EXPIRATION=3600000

# CORS Security
CORS_ALLOWED_ORIGINS=https://yourdomain.com

# SSL Configuration
SSL_ENABLED=true
REQUIRE_SSL=true
```

### Security Headers Applied:
- `X-Frame-Options: DENY` - Prevents clickjacking
- `X-Content-Type-Options: nosniff` - Prevents MIME sniffing
- `Strict-Transport-Security` - Enforces HTTPS
- `Referrer-Policy: strict-origin-when-cross-origin` - Controls referrer info

### Database Security:
- Connection pooling with HikariCP
- Prepared statements (JPA/Hibernate)
- Foreign key constraints
- User role separation

## 🎯 Endpoint Security Matrix

| Endpoint Category | Authentication | Authorization | Rate Limit |
|------------------|---------------|---------------|------------|
| `/api/auth/**` | Public | None | 10/min |
| `/api/test/public` | Public | None | 100/min |
| `/api/users/me` | Required | Own data | 100/min |
| `/api/users/**` | Required | Admin only | 100/min |
| `/api/quizzes/**` | Required | User/Admin | 100/min |
| `/api/questions/**` | Required | User/Admin | 100/min |
| `/api/answers/**` | Required | User/Admin | 100/min |
| `/api/stories/**` | Required | User/Admin | 100/min |
| Admin operations | Required | Admin only | 100/min |

## 🔍 Security Monitoring

### Logs to Monitor:
- Failed authentication attempts
- Rate limit violations
- Unauthorized access attempts
- JWT token validation failures
- Database connection errors

### Security Events:
- User registration/login patterns
- Admin operations
- Bulk data access
- Configuration changes

## 🚀 Deployment Security

### HTTPS Configuration:
1. Obtain SSL certificate
2. Configure Spring Boot for HTTPS
3. Redirect HTTP to HTTPS
4. Update CORS origins to HTTPS only

### Database Security:
1. Use dedicated database user
2. Limit database permissions
3. Enable SSL connections
4. Regular security updates

### Infrastructure Security:
1. Firewall configuration
2. VPC/private networks
3. Regular security patches
4. Monitoring and alerting

## 📝 Security Notes

1. **Development vs Production**: Different security levels per environment
2. **Token Storage**: Client responsible for secure token storage
3. **Password Policy**: Consider implementing password complexity rules
4. **Session Management**: Stateless design - no server-side sessions
5. **Audit Trail**: Consider implementing user action logging

## 🔄 Security Maintenance

### Regular Tasks:
- Review and rotate JWT secrets
- Update dependency versions
- Monitor security advisories
- Review access logs
- Test security configurations

### Security Testing:
- Penetration testing
- Dependency vulnerability scans
- CORS policy testing
- Authentication bypass testing
- SQL injection testing
