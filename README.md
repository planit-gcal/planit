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
     - `CREATE DATABASE planit;`
     - `exit`
3. Paste `client_secret.json` file to `/backend/src/main/java/planit/backend/src/main/resources`
4. Start the back-end
   - `./mvnw spring-boot:run` - starts backend server at http://localhost:8080

...and you're good to go! Visit http://localhost:3000.

### Google Workspace Addon

#### Prerequisites

1. Clasp is a CLI to upload the addon code.
2. Run `npm install clasp` to install Clasp. You might want to add `--global` attribute.
3. Run `clasp login` to login to Clasp. You will need to authorize with your Google Account.
4. Run `clasp create --type standalone` to create a clasp connection with your Google account.

#### Deploying

1. `clasp push -f && clasp deploy`
2. Create a test deployment at https://script.google.com and install it to your calendar
3. Visit Google Calendar website

#### Viewing logs

Checking the results of `console.log` requires entering the the "Executions" section on the left in Apps Script dashboard.

# Deploying the project

### Deploying locally

1. Generate keypairs for SSL. Run `openssl req -x509 -nodes -newkey rsa:2048 -keyout key.pem -out cert.pem -sha256 -days 365 -subj "/C=GB/ST=London/L=London/O=Alros/OU=IT Department/CN=localhost"`

2. `docker-compose build --no-cache`
3. `docker-compose up`

In case of caching problems:
`docker-compose build --no-cache frontend-service && docker-compose up`

In the end, it's available at `https://3bb6-89-64-6-212.ngrok.io`.

# Architecture overview

![alt text](architecture.png)
