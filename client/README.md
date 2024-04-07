
# Deploying React Application on Minikube

This project demonstrates deploying a React application to Minikube using Docker and Kubernetes for local development and testing.

## Development Environment
- **Operating System**: macOS 13.2.1 (Build 22D68)

# Local Deployment with Minikube

### Prerequisites
Ensure Minikube is installed and initialized:
```bash
minikube start
```

### Configure Docker Client for Minikube
Configure the local Docker client to use the Docker daemon running inside Minikube:
```bash
eval $(minikube docker-env)
```

### Build Docker Image
Build the Docker image for the React application:
```bash
docker build -t cynicdog/vertx-quarkus-react:v1.0 .
```

### Deploy Application to Minikube
Deploy the React application to Minikube using a Kubernetes manifest file:
```bash
kubectl apply -f manifest.yaml
```

### Test Deployment
Access the deployed React application:
```bash
minikube service <SERVICE_NAME>
```

## Conclusion
By following these steps, you can deploy your React application to Minikube using Docker and Kubernetes. This local deployment process allows for testing and development of your application in a Kubernetes environment before production deployment.
