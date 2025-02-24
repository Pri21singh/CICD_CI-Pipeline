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
                    try {
                        // Stop and remove existing container
                        bat '''
                            docker stop book-service || true
                            docker rm book-service || true
                        '''
                        
                        // Run new container
                        bat """
                            docker run -d --name book-service -p 8081:8080 ${DOCKER_CREDENTIALS_USR}/book-service:PS1
                            
                            :: Wait for container to start
                            timeout /t 10 /nobreak
                            
                            :: Verify container is running
                            docker ps | findstr book-service || exit 1
                        """
                    } catch (Exception e) {
                        error "Deployment failed: ${e.message}"
                    }
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    try {
                        // Wait for application to start
                        bat "timeout /t 20 /nobreak"
                        
                        // Test GET all books
                        bat '''
                            curl -f http://localhost:8081/api/books
                            if %ERRORLEVEL% NEQ 0 (
                                echo "GET all books failed"
                                exit 1
                            )
                        '''
                        
                        // Test GET specific book
                        bat '''
                            curl -f http://localhost:8081/api/books/1
                            if %ERRORLEVEL% NEQ 0 (
                                echo "GET specific book failed"
                                exit 1
                            )
                        '''
                        
                        // Test POST new book
                        bat '''
                            curl -X POST http://localhost:8081/api/books ^
                            -H "Content-Type: application/json" ^
                            -d "{\"title\":\"Test Book\",\"author\":\"Test Author\",\"isbn\":\"TEST123\",\"price\":29.99}"
                            if %ERRORLEVEL% NEQ 0 (
                                echo "POST new book failed"
                                exit 1
                            )
                        '''
                        
                        echo "All API tests passed successfully!"
                    } catch (Exception e) {
                        error "API verification failed: ${e.message}"
                    }
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