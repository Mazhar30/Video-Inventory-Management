# Use an official Node.js runtime as a parent image
FROM node:16

# Set the working directory in the container
WORKDIR /app

# Copy the package.json and package-lock.json files to the container
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application files to the container
COPY . .

# Build the React app for production
RUN npm run build

# Expose port 3000 (or the port your React app uses)
EXPOSE 3000

# Start the React app
CMD ["npm", "start"]