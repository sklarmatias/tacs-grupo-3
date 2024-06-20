
# Stage 1: Build the application
FROM node:22 as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve the application with Nginx
FROM nginx:1.27
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the locales directory from source to Nginx serving directory
COPY src/locales /usr/share/nginx/html/src/locales

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]