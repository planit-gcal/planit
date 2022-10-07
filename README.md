# About

This repository contains frontend and backend apps for the Planit project.

# Useful links

- [Introduction to Docker](/docs/Docker.md)
- [Miro board](https://miro.com/app/board/uXjVPVOoQV0=/)

# Running the project locally

### React front-end

1. Install NodeJS
2. In the `frontend-react` directory execute:
   - `npm install`
   - `npm start` - starts a hot-reloading React app at http://localhost:3000

### Spring Boot back-end

1. Create a Postgres container
   - `docker run --name my-postgres-db -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres` creates a containerized Postgres at http://localhost:5432
2. Initialize the database
   - find "my-postgres-db" container in Docker
   - click "CLI" button to enter the terminal inside container, inside execute:
     - `psql -U postgres`
     - `CREATE DATABASE people;`
     - `exit`
3. Start the back-end
   - `./mvnw spring-boot:run` - starts backend server at http://localhost:8080

...and you're good to go! Visit http://localhost:3000.

# Deploying the project

### Running the deployment locally

- `docker-compose --verbose --build --force-recreate up` starts a deployed web app at http://localhost:80/

### Deploying to AWS

- Assuming AWS expects an app to be exposed at port 80, running the `docker-compose.yml` file should deploy the app out of the box.

# Architecture overview

![alt text](architecture.png)

### Deploying locally

1. Generate keypairs for SSL. Run `openssl req -x509 -nodes -newkey rsa:2048 -keyout key.pem -out cert.pem -sha256 -days 365 \

# -subj "/C=GB/ST=London/L=London/O=Alros/OU=IT Department/CN=localhost"` in root directory

2. `docker-compose build --no-cache`
3. `docker-compose up`

In case of caching problems:
`docker-compose build --no-cache frontend-service && docker-compose up`
