# Dockerfile multistage para Angular frontend
# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar dependencias
RUN npm install --legacy-peer-deps

# Copiar código fuente
COPY . .

# Construir la aplicación para producción
RUN npm run build

# Stage 2: Runtime con NGINX
FROM nginx:alpine

# Copiar configuración personalizada de NGINX
COPY nginx.conf /etc/nginx/nginx.conf

# Copiar los archivos estáticos construidos
COPY --from=builder /app/dist/coopcredit-frontend/browser /usr/share/nginx/html

# Exponer puerto
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Ejecutar NGINX
CMD ["nginx", "-g", "daemon off;"]

