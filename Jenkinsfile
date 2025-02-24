pipeline {
    agent any

    tools {
        maven 'Maven399'  // Changed from 'Maven 3.9.9' to 'Maven399'
        jdk 'JDK17'      // Changed from 'JDK 17' to 'JDK17'
    }

    environment {
        DOCKER_CREDENTIALS = credentials('docker-credentials')
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    // Build the Docker image
                    bat "docker build -t ${DOCKER_CREDENTIALS_USR}/book-service:PS1 ."
                    
                    // Login to Docker Hub
                    bat "echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin"
                    
                    // Push the image
                    bat "docker push ${DOCKER_CREDENTIALS_USR}/book-service:PS1"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Stop existing container if running
                    bat 'docker ps -q --filter "name=book-service" | findstr . && docker stop book-service && docker rm book-service || echo "No container running"'
                    
                    // Run new container
                    bat "docker run -d -p 8081:8080 --name book-service ${DOCKER_CREDENTIALS_USR}/book-service:PS1"
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}