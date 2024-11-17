# Video Inventory Management

### Project Hierarchy
```
|        |-vim-server
|        |-vim-web
|        |-mysql_data
|        |-videos

```

### Technology Stack

- java17
- maven
- redis
- React:18.3.1
- node:v16.17.0
- mysql:8
- docker 20.x


### How to start mysql
- Run `docker-compose up --build mysql`

### How to start redis
- Run `docker-compose up --build redis`

## How to start vim-server
- Run `docker-compose up --build vim`

## How to start vim-web
- Run `docker-compose up --build vim-web`

# We could have just used `docker-compose up --build` command to start all the containers. 
# But the vim-server has dependency on mysql, so if mysql doesn't start before vim-server, there will be error.


## Videos will be stored on videos directory
## Please notice only mp4 and mkv formatted video files upto 50 MB can be uploaded
## All database data will be stored inside mysql_data directory
## Redis will be used to store the user JWT.

## An Admin user is already created when initializing mysql db.
## username: admin, password: password, role: Admin

## In the system only non admin user will be able to register. 
## Please note, only user named 'admin' has the admin priviledges.

## Go to http://localhost:3000 in the browser to find the UI.


# To Do
# Add logs, un-assigning user from a video
# No uniform response object is created for API responses, need to add
# More than two role need to be handled by the system
# Currently it is a mix of reactive and functional programming, need to make the whole system reactive
