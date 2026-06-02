SQL Database Creation: 
CREATE DATABASE IF NOT EXISTS student_db; 
USE student_db; 
 
CREATE TABLE IF NOT EXISTS students ( 
    student_id INT AUTO_INCREMENT PRIMARY KEY, 
    roll_no VARCHAR(50) UNIQUE NOT NULL, 
    first_name VARCHAR(50) NOT NULL, 
    last_name VARCHAR(50) NOT NULL, 
    email VARCHAR(100), 
    phone VARCHAR(20), 
    course VARCHAR(100), 
    enrollment_date DATE 
); 
