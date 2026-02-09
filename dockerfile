# Simple React container without nginx
FROM node:20-alpine

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy all source files
COPY . .

# Build React app
RUN npm run build

# Expose port 3000
EXPOSE 3000

# Serve built app with a simple static server
RUN npm install -g serve

# Command to run app
CMD ["serve", "-s", "dist", "-l", "3000"]