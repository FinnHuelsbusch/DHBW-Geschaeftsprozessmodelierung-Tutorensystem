# Tutor system of the DHBW Mannheim (WIP)

This repository contains the frontend and backend of the tutor system of the DHBW Mannheim, which is currently under development. A guide for setting up the project can be found in [this](./Setup.md) document. 

## Architecture 

The architecture for the final deployment can be chosen to your own needs. For Development purposes the database, frontend and backend are run in separate containers as shown in the picture below. 

![](/home/finn/Schreibtisch/Github/tutorensystem/documentation/architectur.png)

### Frontend

The frontend is written in TypeScript (version 4.5.5) using the React Framework (version 17.0.2). The development Container is based on the latest alpine based node image available on DockerHub. As a design-library AntDesign (version 4.18.8) is used. The frontend communication with the backend is handeld by axios (version 0.26.0). 

### Backend

The backend is written in Java (version 11) using the spring boot framework. It presents a REST-API that is documented [to be done](to be done). 

### Database 

As a database PostgreSQL is used in the development Containers but it can easily be swapped out by replacing the jdbc connector. For development purposes the DB is populated by the backend at startup and torn down when the backend is shutdown. 