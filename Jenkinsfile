pipeline {
    agent any

    tools {
        maven 'Maven 3.8.4'
        jdk 'JDK 17'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build and Push') {
            environment {
                DOCKER_CREDENTIALS = credentials('docker-credentials')
            }
            steps {
                sh '''
                    docker build -t ${DOCKER_CREDENTIALS_USR}/book-service:latest .
                    docker login -u ${DOCKER_CREDENTIALS_USR} -p ${DOCKER_CREDENTIALS_PSW}
                    docker push ${DOCKER_CREDENTIALS_USR}/book-service:latest
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
} 