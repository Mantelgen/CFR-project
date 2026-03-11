# Stage 1: Build React
FROM node:18-slim AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY default.conf /etc/nginx/conf.d/default.conf
RUN rm /etc/nginx/conf.d/default.conf.default 2>/dev/null || true

RUN apk add --no-cache wget

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
