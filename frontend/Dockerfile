# frontend/Dockerfile
FROM node:20-alpine
WORKDIR /app
COPY . .
RUN npm install && npm run build

# Use a static file server (like nginx) to serve the frontend
FROM nginx:alpine
COPY --from=0 /app/dist /usr/share/nginx/html
EXPOSE 80
