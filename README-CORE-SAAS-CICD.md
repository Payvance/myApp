# Core SaaS Multi-Branch Backend Setup

## 🏗️ Architecture

```
Spring Boot Backend (Port 8080) → Private Registry (Port 5000)
                                    ↓
Core SaaS Dev Container (Port 8090) → dev profile
Core SaaS Test Container (Port 8091) → test profile  
Core SaaS Demo Container (Port 8092) → demo profile
```

## 🌐 Access URLs

| Environment | Container Port | Host Port | URL | Spring Profile |
|------------|----------------|-----------|-----|----------------|
| Dev | 8080 | 8090 | http://172.16.1.225:8090 | dev |
| Test | 8080 | 8091 | http://172.16.1.225:8091 | test |
| Demo | 8080 | 8092 | http://172.16.1.225:8092 | demo |

## 🐳 Local Development

### Build and Run Dev Environment:
```bash
# Build and run
docker-compose -f docker-compose.dev.yml up --build

# Stop
docker-compose -f docker-compose.dev.yml down
```

### Test and Demo:
```bash
# Test environment
docker-compose -f docker-compose.test.yml up --build

# Demo environment  
docker-compose -f docker-compose.demo.yml up --build
```

## 🚀 Jenkins CI/CD Pipeline

### Pipeline Stages:
1. **Prepare** - Branch detection and environment configuration
2. **Generate Version** - Semantic versioning (`1.0.{phase}.{pr}.{commitHash}`)
3. **Copy Env File** - Dynamic environment variables (creates .env during pipeline)
4. **Build Docker Image** - Spring Boot container build
5. **Push Docker Image** - Push to registry `172.16.1.225:5000`
6. **Deploy Container** - Deploy using docker-compose

### Environment Configuration:
- **Spring Profiles**: `dev`, `test`, `demo`
- **Registry**: `172.16.1.225:5000/core_saas:{version}`
- **Host Ports**: 8090, 8091, 8092
- **Container Port**: 8080 (Spring Boot default)
- **Dynamic .env**: Created during Jenkins pipeline (not tracked in Git)

## 📝 Configuration Files

### Dockerfile Features:
- ✅ Eclipse Temurin OpenJDK 17 base image
- ✅ Maven wrapper support
- ✅ Multi-stage build optimization
- ✅ Spring Boot jar execution
- ✅ Exposes port 8080

### Docker Compose Features:
- ✅ Multi-environment support
- ✅ Spring profile configuration
- ✅ Port mapping (8090:8080, 8091:8080, 8092:8080)
- ✅ Environment variable injection
- ✅ Registry integration
- ✅ No obsolete version declaration

### Jenkinsfile Features:
- ✅ Multi-branch pipeline
- ✅ Dynamic versioning
- ✅ Environment-specific profiles
- ✅ Automated deployment
- ✅ Failure notifications
- ✅ Dynamic .env creation during pipeline

### .gitignore Features:
- ✅ Environment files ignored (.env, .env.local, etc.)
- ✅ Security - No sensitive data in Git
- ✅ Build artifacts ignored
- ✅ IDE files ignored

## 🔧 Key Benefits

1. **Spring Profiles** - Environment-specific configurations
2. **Port Separation** - No conflicts between environments
3. **Registry Integration** - Centralized image management
4. **Maven Wrapper** - Consistent build environment
5. **Automated CI/CD** - Jenkins pipeline integration
6. **Security** - .env files not tracked in Git
7. **Health Monitoring** - Container restart policies

## 🎯 Quick Start

```bash
# 1. Clone and navigate
cd Core_saas

# 2. Create local .env (for development)
echo "SPRING_PROFILES_ACTIVE=dev" > .env
echo "VERSION=latest" >> .env
echo "IMAGE_NAME=core_saas" >> .env
echo "REGISTRY=172.16.1.225:5000" >> .env

# 3. Build and run dev
docker-compose -f docker-compose.dev.yml up --build

# 4. Access API
curl http://localhost:8090/actuator/health
```

## 🔄 Deployment Process

1. **Push to branch** → Triggers Jenkins pipeline
2. **Automatic build** → Creates Spring Boot container
3. **Push to registry** → Stores image centrally
4. **Deploy container** → Runs on appropriate port
5. **Access via URL** → Environment-specific endpoint

## 📡 API Access

### Development:
- **URL**: `http://172.16.1.225:8090`
- **Swagger**: `http://172.16.1.225:8090/swagger-ui.html`
- **Profile**: `dev`
- **Health**: `http://172.16.1.225:8090/actuator/health`

### Testing:
- **URL**: `http://172.16.1.225:8091`
- **Swagger**: `http://172.16.1.225:8091/swagger-ui.html`
- **Profile**: `test`
- **Health**: `http://172.16.1.225:8091/actuator/health`

### Demo:
- **URL**: `http://172.16.1.225:8092`
- **Swagger**: `http://172.16.1.225:8092/swagger-ui.html`
- **Profile**: `demo`
- **Health**: `http://172.16.1.225:8092/actuator/health`

## 🔒 Security & Best Practices

- **Environment Variables**: .env files ignored in Git
- **Dynamic Configuration**: Jenkins creates .env during pipeline
- **Container Isolation**: Each environment runs in separate container
- **Registry Security**: Private registry at 172.16.1.225:5000
- **Port Separation**: Prevents conflicts between environments

## 🌐 Integration with Web Portal

Your React Web Portal connects to:
- **Dev**: `http://172.16.1.225:8090` (backend for port 3000 frontend)
- **Test**: `http://172.16.1.225:8091` (backend for port 3001 frontend)
- **Demo**: `http://172.16.1.225:8092` (backend for port 3002 frontend)

---

**🎉 Complete multi-branch Spring Boot backend CI/CD setup with security best practices!**
