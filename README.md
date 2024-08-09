Deploying Vert.x in Quarkus Application on GKE
=======================================

This project demonstrates deploying a Quarkus-based Vert.x application to Google Kubernetes Engine (GKE).

## Development Environment
- **Operating System**: macOS 13.2.1 (Build 22D68)

## Technologies Used
- **Quarkus**: A Kubernetes-native Java framework designed for fast startup and low memory footprint.
- **Minikube**: A tool to run a single-node Kubernetes cluster locally for development and testing.
- **Google Kubernetes Engine (GKE)**: A managed Kubernetes service provided by Google Cloud Platform (GCP).
- **Jib**: A container image building tool that simplifies packaging Java applications into container images without needing a Dockerfile.

# Local Deployment with Minikube

### Prerequisites
- Configure Kubernetes node port and Spotify credentials in `application.properties` file as follows:
```properties
%prod.host={minikube_ip}
%prod.port={service_port}
quarkus.kubernetes.node-port={port}
...
spotify.client-id={SPOTIFY_APP_CLIENT_ID}
spotify.client-secret={SPOTIFY_APP_CLIENT_SECRET}
```

- Ensure Minikube is installed and initialized as VM:  
```bash
minikube start 
```
 
### Configure Docker Client for Minikube
Configure the local Docker client to use the Docker daemon running inside Minikube:
```bash
eval $(minikube docker-env)
```

### Persistence Configuration and Running up the Service
Generate Kubernetes Secret to initialize the database server and grant access:
```bash
kubectl create secret generic db-credentials --from-literal=username={USERNAME} --from-literal=password={PASSWORD}
kubectl apply -f postgresql_kubernetes.yml
kubectl apply -f mongodb_kubernetes.yml  
``` 

### Package and Deploy the Application
Build and deploy the application to Minikube with ARM64 platform support:
```bash
mvn clean package -Dquarkus.container-image.build=true \
    -Dquarkus.jib.platforms=linux/arm64/v8 \
    -Dquarkus.kubernetes.deploy=true
```

### Test Deployment
Make a request to the deployed service to ensure successful deployment:
```bash
http {minikube_ip}:{service_port}/api/person \
  Content-Type:application/json \
  name="John Doe" \
  age:=30

http {minikube_ip}:{service_port}/api/people   
```

# Deployment to Google Kubernetes Engine (GKE)

### Configure Google Cloud SDK
Initialize and configure the Google Cloud SDK for authentication:
```bash
gcloud auth login
gcloud init
```

### Configure Docker Authentication for GCR
Configure Docker authentication information to interact with Google Artifact Registry for Docker:
```bash
gcloud auth configure-docker
```

### Build and Push Container Image to GCR
Build and push the container image to Google Container Registry using Jib:
```bash
mvn clean package -Dquarkus.container-image.build=true \
    -Dquarkus.container-image.push=true \
    -Dquarkus.jib.platforms=linux/arm64/v8
```

### Create a GKE Cluster
Create a Kubernetes cluster on Google Kubernetes Engine:
<img width="1423" alt="Capture 2024-04-07 at 11 48 25 AM" src="https://github.com/CynicDog/Vertx-Quarkus-GKE/assets/96886982/d9d05f46-4f13-4736-a6dc-8a89201b9208">


### Connect to GKE Cluster
Connect your terminal to the generated Kubernetes cluster on GKE:
```bash
gcloud container clusters get-credentials {YOUR_CLUSTER_NAME} --region {YOUR_REGION} --project {YOUR_PROJECT_ID}
```

### Deploy the Application to GKE
Deploy the Quarkus application to the GKE cluster:
```bash
mvn clean package -Dquarkus.kubernetes.deploy=true
```

## Conclusion
By following these steps, you can deploy your Vert.x in Quarkus application both locally with Minikube for testing and on Google Kubernetes Engine (GKE) for production. This streamlined deployment process leverages modern tools like Jib and GKE to simplify container image building and Kubernetes orchestration.


