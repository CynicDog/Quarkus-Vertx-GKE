[![Backup PostgreSQL Database for macOS arm64](https://github.com/CynicDog/archeio/actions/workflows/psql-backup-macOS-arm64.yml/badge.svg)](https://github.com/CynicDog/archeio/actions/workflows/psql-backup-macOS-arm64.yml)
[![Deploy Quarkus App onto GKE](https://github.com/CynicDog/archeio/actions/workflows/deploy-quarkus-to-gke.yml/badge.svg)](https://github.com/CynicDog/archeio/actions/workflows/deploy-quarkus-to-gke.yml)
[![CodeQL](https://github.com/CynicDog/archeio/actions/workflows/codeql.yml/badge.svg)](https://github.com/CynicDog/archeio/actions/workflows/codeql.yml)
# Index 

1. [Deploying Vert.x in Quarkus Application on GKE](#deploying-vertx-in-quarkus-application-on-gke)
2. [Development Environment](#development-environment)
3. [Technologies Used](#technologies-used)
4. [Local Deployment with Minikube](#local-deployment-with-minikube)
    1. [Prerequisites](#prerequisites)
    2. [Configure Docker Client for Minikube](#configure-docker-client-for-minikube)
    3. [Persistence Configuration and Running up the Service](#persistence-configuration-and-running-up-the-service)
    4. [Package and Deploy the Application](#package-and-deploy-the-application)
    5. [Test Deployment](#test-deployment)
5. [Deployment to Google Kubernetes Engine (GKE)](#deployment-to-google-kubernetes-engine-gke)
    1. [Configure Google Cloud SDK](#configure-google-cloud-sdk)
    2. [Configure Docker Authentication for GCR](#configure-docker-authentication-for-gcr)
    3. [Build and Push Container Image to GCR](#build-and-push-container-image-to-gcr)
    4. [Create a GKE Cluster](#create-a-gke-cluster)
    5. [Connect to GKE Cluster](#connect-to-gke-cluster)
    6. [Deploy the Application to GKE](#deploy-the-application-to-gke)
    7. [Reserve Static IP Address for the Deployed App](#reserve-static-ip-address-for-the-deployed-app)
    8. [Create DNS record set](#create-dns-record-set)
6. [Conclusion](#conclusion)

# Deploying Vert.x in Quarkus Application on GKE

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
- Ensure Minikube is installed and initialized:
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
kubectl exec -it vertx-quarkus-demo-<POD_ID> -- /bin/bash
curl http://vertx-quarkus-demo/greeting
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

### Reserve Static IP Address for the Deployed App

To expose a Quarkus application to the public via Ingress with a global static IP address, follow these steps:

1. **Create a Global Static IP Address:**
   ```bash
   gcloud compute addresses create {A_NAME_FOR_GLOBAL_STATIC_IP} --global
   ```

2. **Update Configuration:**
   Add the following configuration, ensuring to include double quotation marks as shown:
   ```yaml
   quarkus:
     kubernetes:
       ingress:
         expose: true
         annotations:
           "kubernetes.io/ingress.global-static-ip-name": "{A_NAME_FOR_GLOBAL_STATIC_IP}"
   ```

3. **Verify Configuration:**
   Once configured, the generated `kubernetes.yml` manifest should contain the following in the `metadata.annotations` section:
   ```yaml
   metadata:
     annotations:
       kubernetes.io/ingress.global-static-ip-name: {A_NAME_FOR_GLOBAL_STATIC_IP}
   ```

4. **Check Deployment:**
   Verify if the application is deployed with the reserved static IP address:
   ```bash
   kubectl get ingress
   ```

   Example output:
   ```plaintext
   NAME      CLASS    HOSTS   ADDRESS              PORTS   AGE
   {PROJECT} <none>   *       {GIVEN_STATIC_IP}    80      31m
   ```

5. **Test Deployment:**
   Ensure the application is accessible using the reserved static IP address:
   ```bash
   http http://{GIVEN_STATIC_IP}/
   ```


### Create DNS record set 
To point your domain to the deployed application using the reserved static IP address, you can use the Google Cloud DNS service:
```bash
gcloud dns --project={YOUR_PROJECT_NAME} record-sets create {ENTER_PREFIX_HERE}.{YOUR_DNS_NAME} --zone={YOUR_ZONE} --type="A" --ttl="300" --rrdatas={RESERVED_STATIC_IP}
```

## Conclusion
By following these steps, you can deploy your Vert.x in Quarkus application both locally with Minikube for testing and on Google Kubernetes Engine (GKE) for production. This streamlined deployment process leverages modern tools like Jib and GKE to simplify container image building and Kubernetes orchestration.
