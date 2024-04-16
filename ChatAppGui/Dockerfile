FROM node:20.9.0-alpine As builder

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm install

COPY . .

RUN npm run build --omit=dev

FROM nginx:stable-alpine-slim

COPY --from=builder /app/dist/chat-app-gui/browser/ /usr/share/nginx/html

ENV PORT=80
EXPOSE $PORT
