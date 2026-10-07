# ===== Stage 1: BUILD the Angular app into plain HTML/CSS/JS files =====
FROM node:24-alpine AS build
WORKDIR /app

# Install packages first (cached while package.json / package-lock.json are unchanged)
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npx ng build

# ===== Stage 2: SERVE those files with nginx (a small, fast web server) =====
FROM nginx:1.29-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/deals-web/browser /usr/share/nginx/html
EXPOSE 80
