# Book Service CI/CD Pipeline

A Spring Boot application demonstrating CI/CD implementation using GitHub Actions and Jenkins pipeline.

## Repository Structure

CICD_CI-Pipeline/
├── .github/
│ └── workflows/
│ └── ci.yml # GitHub Actions workflow
├── src/
│ ├── main/
│ │ ├── java/ # Java source files
│ │ └── resources/ # Application properties
│ └── test/ # Test files
├── .gitignore
├── Dockerfile # Docker configuration
├── Jenkinsfile # Jenkins pipeline configuration
├── pom.xml # Maven dependencies
└── README.md # Project documentation

## Building and Running the Application

### Prerequisites
- Java 17
- Maven 3.9.9
- Docker
- Git
- Jenkins (for pipeline execution)

### Local Development

1. Clone the repository:
git clone https://github.com/Pri21singh/CICD_CI-Pipeline.git

cd CICD_CI-Pipeline

2. Build the application:mvn clean package

3. Run the application: 
java -jar target/book-service.jar

4. Access the application:
- Base URL: (http://localhost:8081/api/books)
- Endpoints:
  - GET /api/books - List all books
  - GET /api/books/{id} - Get book by ID
  - POST /api/books - Create new book
  - DELETE /api/books/{id} - Delete book

### Docker Deployment

1. Build Docker image: 
docker build -t priya21sin/book-service:PS1 .

2. Run Docker container:
docker run -d -p 8081:8080 --name book-service priya21sin/book-service:PS1

3. Access the application:
- Base URL: http://localhost:8081
- Test endpoint: http://localhost:8081/api/books

## CI Pipeline Testing

### GitHub Actions Pipeline

1. Push changes to trigger the pipeline:git add .
git commit -m "Your commit message"
git push origin main


2. Monitor the pipeline:
- Go to GitHub repository
- Click "Actions" tab
- View latest workflow run

### Jenkins Pipeline

1. Jenkins Setup:
- Access Jenkins at http://localhost:3001
- Install required plugins:
  - Docker Pipeline
  - Pipeline Utility Steps
  - Git
  - Junit
  - GitHub Integration Plugin
  - GitHub API Plugin

2. Configure Jenkins Tools:
- Manage Jenkins → Tools
- Add JDK:
  ```
  Name: JDK17
  JAVA_HOME: [path to Java 17]
  ```
- Add Maven:
  ```
  Name: Maven399
  Version: 3.9.9
  ```

3. Create Pipeline:
- New Item → Pipeline
- Configure:
  - Pipeline from SCM
  - Git
  - Repository URL: https://github.com/Pri21singh/CICD_CI-Pipeline.git
  - Branch: */main
  - Script Path: Jenkinsfile

4. Run Pipeline:
- Click "Build Now"
- Monitor build progress
- Check Console Output for details

## Docker Image

The Docker image is available on Docker Hub:

1. Pull the image:
docker pull priya21sin/book-service:PS1

2. Run the container:
docker run -d -p 8081:8080 --name book-service priya21sin/book-service:PS1

**# Check container status**
3. Verify deployment: docker ps

**Test API**
curl http://localhost:8081/api/books


**## API Testing**

Test the REST endpoints: curl http://localhost:8081/api/books

**Get book by ID**
curl http://localhost:8081/api/books/1

**Create new book**
curl -X POST http://localhost:8081/api/books \
-H "Content-Type: application/json" \
-d "{\"title\":\"Test Book\",\"author\":\"Test Author\",\"isbn\":\"123456\",\"price\":29.99}"


## Troubleshooting

1. Port conflicts:

# Check ports in use
netstat -ano | findstr :8081


2. Container issues:
docker logs book-service


# Restart container
docker restart book-service


**3. Jenkins pipeline failures:**
- Check Console Output for error details
- Verify Docker Hub credentials
- Ensure ports are available

## Contact

Priya Singh - psingh0481@conestogac.on.ca

Project Link: https://github.com/Pri21singh/CICD_CI-Pipeline
