# Stage 1 – Build the frontend
FROM node:20 AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Just Nginx – no build step
FROM nginx:alpine
COPY ./dist /usr/share/nginx/html
EXPOSE 80

