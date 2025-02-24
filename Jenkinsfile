pipeline {
    agent any

    tools {
        maven 'Maven399'
        jdk 'JDK17'
    }

    environment {
        DOCKER_CREDENTIALS = credentials('docker-credentials')
        DOCKER_IMAGE = 'priya21sin/book-service:PS1'
        CONTAINER_NAME = 'book-service'
        HOST_PORT = '8081'
        CONTAINER_PORT = '8080'
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
                    bat "docker build -t ${DOCKER_IMAGE} ."
                    
                    // Login to Docker Hub
                    withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                    }
                    
                    // Push the image
                    bat "docker push ${DOCKER_IMAGE}"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Clean up existing container
                    bat """
                        @echo off
                        echo Cleaning up existing container...
                        docker ps -a -f name=${CONTAINER_NAME} -q > temp.txt
                        set /p CONTAINER_ID=<temp.txt
                        if defined CONTAINER_ID (
                            echo Stopping container %CONTAINER_ID%...
                            docker stop %CONTAINER_ID%
                            echo Removing container %CONTAINER_ID%...
                            docker rm %CONTAINER_ID%
                        )
                        del temp.txt
                    """

                    // Deploy new container
                    bat """
                        echo Deploying new container...
                        docker run -d --name ${CONTAINER_NAME} -p ${HOST_PORT}:${CONTAINER_PORT} ${DOCKER_IMAGE}
                        
                        echo Waiting for container to start...
                        ping -n 10 127.0.0.1 >nul
                        
                        echo Verifying container status...
                        docker ps -f name=${CONTAINER_NAME} --format "{{.Status}}"
                    """
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    // Wait for application to be ready
                    bat "echo Waiting for application to start..."
                    bat "ping -n 20 127.0.0.1 >nul"

                    // Verify container is running
                    bat """
                        echo Verifying container status...
                        docker ps -f name=${CONTAINER_NAME} -q > status.txt
                        set /p RUNNING=<status.txt
                        if not defined RUNNING (
                            echo Container is not running
                            exit 1
                        )
                        del status.txt
                        echo Container is running successfully
                    """

                    // Test API endpoints
                    bat """
                        echo Testing API endpoints...
                        curl -f -s http://localhost:${HOST_PORT}/api/books
                        if %ERRORLEVEL% NEQ 0 (
                            echo API test failed
                            exit 1
                        )
                        echo API is responding successfully
                    """
                }
            }
        }
    }

    post {
        success {
            echo """
                =========================================
                Pipeline completed successfully!
                Container: ${CONTAINER_NAME}
                Port: ${HOST_PORT}
                API: http://localhost:${HOST_PORT}/api/books
                =========================================
            """
        }
        failure {
            script {
                echo 'Pipeline failed! Cleaning up...'
                bat """
                    @echo off
                    echo Cleaning up containers...
                    docker ps -a -f name=${CONTAINER_NAME} -q > temp.txt
                    set /p CONTAINER_ID=<temp.txt
                    if defined CONTAINER_ID (
                        docker stop %CONTAINER_ID%
                        docker rm %CONTAINER_ID%
                    )
                    del temp.txt
                """
            }
        }
        always {
            cleanWs()
        }
    }
}