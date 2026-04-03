FROM node:22-alpine AS build
WORKDIR /app

# Install dependencies first for better layer caching.
COPY package.json yarn.lock ./
RUN yarn install --frozen-lockfile

# Build the production bundle.
COPY . .
RUN yarn build

FROM nginx:1.27-alpine AS runtime

# Use SPA-friendly Nginx config.
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

