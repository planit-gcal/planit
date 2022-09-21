# Introduction to Docker

Docker is a tool to create containers which act very similarly to Virtual Machines.

### Differences:

- containers are less resource-intensive
- containers are more portable
- boots quickly
- containers run on the same host OS

### Definitions:

- Host - your PC
- Image - readonly template for creating containers. Every image includes a base OS, for example Linux Alpine (8MB)
- Container - an image instance created from blueprint.
- Volume - for persisting data used by containers. Stored on the host. Points the container where should it store data.
- Network - used e.g. to establish connection e.g. HTTP requests between containers. Or to open containers to the outside (internet).
  - By default every container is assigned an IP for every Docker network it connects to.
  - There's a default network so you don't need to think about it usually.
- Dockerfile - describes how to build a docker image. Contains commands. It's a blueprint.
- Docker Compose file - `docker-compose.yml` connects Dockerfiles and configures the resulting containers. Used for multicontainer applications.
