# Health Tracker Backend

This is the backend service for a health tracking web application. The backend is implemented in Kotlin, using Exposed as the ORM, and provides RESTful APIs for tracking activities and water intake.

## Features
- **Activity Tracking**: Allows users to record their physical activities, including details like description, duration, calories burned, and start time.
- **Water Intake Tracking**: Allows users to record their daily water intake.

## Technologies Used
- **Kotlin**: The programming language used for the backend.
- **Exposed**: ORM library for interacting with the database.
- **Javalin**: Web framework for handling HTTP requests and responses.
- **PostgreSQL**: Database used for storing user data, activities, and water intake records.
- **Jackson**: For JSON serialization/deserialization.

## API Endpoints

### Activity API
- **POST /api/activities**: Add a new activity.
- **GET /api/users/{user-id}/activities**: Fetch all activities of a specific user.

### Water Intake API
- **POST /api/water-intakes**: Add a new water intake record.
- **GET /api/users/{user-id}/water-intakes**: Fetch all water intake records of a specific user.

## Running the Application
1. **Clone the Repository**:
   ```sh
   git clone https://github.com/yourusername/health-tracker-backend.git
   cd health-tracker-backend
   ```
2. **Set Up PostgreSQL Database**:
   - Create a PostgreSQL database.
   - Configure the connection details in the application (e.g., in `application.conf`).
3. **Build the Project**:
   ```sh
   mvn clean install
   ```
4. **Run the Application**:
   ```sh
   mvn exec:java
   ```

## Dependencies
- **Maven**: Build tool used for dependency management and running the project.
- **PostgreSQL**: Ensure PostgreSQL is running and accessible.

## How to Use
- Use tools like **Postman** to send HTTP requests to the endpoints.
- Ensure you have valid user IDs to associate activities and water intake records.


## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

