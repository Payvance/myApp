# Simple Multi-Branch React Container Setup

## 🏗️ Architecture

```
Spring Boot Backend (Ports 8090/8091/8092) → Private Registry (Port 5000)
                                               ↓
React Dev Container (Port 3000) → /dev path → Backend 8090
React Test Container (Port 3000) → /test path → Backend 8091
React Demo Container (Port 3000) → /demo path → Backend 8092
```

## 🌐 Access URLs

| Environment | Container Port | Host Port | URL | Base Path | Backend URL |
|------------|----------------|-----------|-----|-----------|-------------|
| Dev | 3000 | 3001 | http://172.16.1.225:3001/dev/ | /dev/ | http://172.16.1.225:8090 |
| Test | 3000 | 3002 | http://172.16.1.225:3002/test/ | /test/ | http://172.16.1.225:8091 |
| Demo | 3000 | 3003 | http://172.16.1.225:3003/demo/ | /demo/ | http://172.16.1.225:8092 |

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
1. **Checkout** - Get latest code from Git
2. **Prepare** - Branch detection and environment configuration
3. **Generate Version** - Semantic versioning (`1.0.{phase}.{commitHash}`)
4. **Create Env File** - Dynamic environment variables
5. **Build Docker Image** - Simple React container build
6. **Push Docker Image** - Push to registry `172.16.1.225:5000`
7. **Deploy Container** - Deploy using docker-compose

### Environment Configuration:
- **Spring Profiles**: `dev`, `test`, `demo` (backend)
- **Base Paths**: `/dev/`, `/test/`, `/demo/` (frontend)
- **Backend URLs**: `8090`, `8091`, `8092` (Core SaaS)
- **Registry**: `172.16.1.225:5000/web_portal_fe:{version}`
- **Host Ports**: 3000, 3001, 3002 (frontend access)
- **Container Port**: 3000 (React app)

## 📝 Configuration Files

### Dockerfile Features:
- ✅ Simple Node.js container (no nginx)
- ✅ Uses `serve` to host React app
- ✅ Exposes port 3000
- ✅ Environment variable support
- ✅ Multi-stage build optimization

### Docker Compose Features:
- ✅ Multi-environment support
- ✅ Different host ports (3000, 3001, 3002)
- ✅ Environment variable injection
- ✅ Registry integration
- ✅ Backend URL configuration

### Jenkinsfile Features:
- ✅ Multi-branch pipeline
- ✅ Dynamic versioning
- ✅ Environment-specific configuration
- ✅ Automated deployment
- ✅ Success notifications
- ✅ Modern Docker Compose commands

## 🔧 Key Benefits

1. **Port Separation** - No conflicts between frontend environments
2. **Backend Integration** - Each frontend connects to specific backend
3. **Path-based Routing** - Clean URL structure
4. **Registry Integration** - Centralized image management
5. **Automated CI/CD** - Jenkins pipeline integration
6. **Environment Isolation** - Separate configs for dev/test/demo

## 🎯 Quick Start

```bash
# 1. Clone and navigate
cd web_portal

# 2. Build and run dev
docker-compose -f docker-compose.dev.yml up --build

# 3. Access application
open http://localhost:3001/dev/
```

## 🔄 Deployment Process

1. **Push to branch** → Triggers Jenkins pipeline
2. **Automatic build** → Creates React container
3. **Push to registry** → Stores image centrally
4. **Deploy container** → Runs on appropriate host port
5. **Access via URL** → Environment-specific path

## 📡 Full-Stack Integration

### Development Environment:
- **Frontend**: `http://172.16.1.225:3001/dev/`
- **Backend**: `http://172.16.1.225:8090`
- **Registry**: `172.16.1.225:5000`

### Testing Environment:
- **Frontend**: `http://172.16.1.225:3002/test/`
- **Backend**: `http://172.16.1.225:8091`
- **Registry**: `172.16.1.225:5000`

### Demo Environment:
- **Frontend**: `http://172.16.1.225:3003/demo/`
- **Backend**: `http://172.16.1.225:8092`
- **Registry**: `172.16.1.225:5000`

---

**🎉 Complete multi-branch React container setup with backend integration!**
