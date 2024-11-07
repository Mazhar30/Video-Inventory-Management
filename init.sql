-- init.sql
CREATE DATABASE IF NOT EXISTS video_inventory;
USE video_inventory;

CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL,
    UNIQUE(username)
);

INSERT IGNORE INTO user(username, password, role) VALUES('admin','$2a$10$DEZw7wnPMUaCobXZfyg1m.Ge5Q6U1lURS6uLJOgPzUf2MJoK8BN0.','ADMIN');
