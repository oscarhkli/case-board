# Case Board

A simple full-stack CRUD app.

## Purpose

- Show how Java + Spring Boot work with a React frontend.
- Demonstrate full-stack development in a clear, minimal way.

## Stack

- **Backend**: Java 23 + Spring Boot 3.4
- **Frontend**: React ([repo](https://github.com/oscarhkli/react-case-board))
- **Database**: MySQL
- **Containerization**: Docker

## Getting Started

1. Clone this repo and the [react-case-board](https://github.com/oscarhkli/react-case-board) repo.  
   **Make sure the frontend Docker image is built before continuing.**

2. Create a `.env` file in the backend root:

   ```env
   DB_NAME="<your_db_name>"
   DB_USERNAME="<your_db_username>"
   DB_PASSWORD="<your_db_password>"
   DB_ROOTPASS="<your_db_root_password>"
   ```

3. Run the setup:

    ```bash
    ./dockerize.sh
    docker-compose up -d
    ```

4. Access the app:
- API docs: http://localhost:61001/swagger-ui.html
- Frontend: http://localhost:8080

---
Enjoy!