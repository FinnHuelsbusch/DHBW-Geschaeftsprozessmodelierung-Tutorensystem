# pull official base image
FROM node:alpine



RUN mkdir -p /app && chown -R node:node /app

# set working directory
WORKDIR /app
USER node

# add `/app/node_modules/.bin` to $PATH
ENV PATH /app/node_modules/.bin:$PATH

# Update args in docker-compose.yaml to set the UID/GID of the "node" user.
ARG USER_UID=1000
ARG USER_GID=$USER_UID
RUN if [ "$USER_GID" != "1000" ] || [ "$USER_UID" != "1000" ]; then groupmod --gid $USER_GID node && usermod --uid $USER_UID --gid $USER_GID node; fi

# install app dependencies

COPY --chown=node:node frontend/package.json ./
COPY --chown=node:node frontend/package-lock.json ./
RUN npm install 
USER root
RUN npm install react-scripts -g 
USER node

# add app
COPY --chown=node:node frontend ./
