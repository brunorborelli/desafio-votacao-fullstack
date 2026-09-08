FROM node:24-alpine AS construcao

WORKDIR /aplicacao

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:1.28-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=construcao /aplicacao/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80