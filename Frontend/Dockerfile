# Этап 1: Сборка приложения
FROM node:20-alpine AS builder

WORKDIR /app

# Копируем манифесты зависимостей
COPY package.json yarn.lock ./

COPY . .
# Устанавливаем зависимости через yarn (аналог npm ci для yarn)
RUN yarn install
# Собираем приложение
# Если нужно передать URL API во время сборки (опционально)
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL

RUN npx vite build

# Этап 2: Раздача статики через Nginx
FROM nginx:alpine

# Копируем собранные файлы из этапа builder
COPY --from=builder /app/dist /usr/share/nginx/html

# (Опционально) копируем кастомный конфиг nginx, если нужен
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
