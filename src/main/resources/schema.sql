CREATE TABLE repos (
    id int PRIMARY KEY AUTO_INCREMENT,
    fullName varchar(255),
    author varchar(255) NOT NULL,
    pathName varchar(255) NOT NULL
);

CREATE TABLE conspects (
    id int PRIMARY KEY AUTO_INCREMENT,
    path varchar(255) NOT NULL,
    repo_id int NOT NULL
);

CREATE TABLE user_conspect (
    username varchar NOT NULL,
    task_id int NOT NULL
);

CREATE TABLE tasks (
    id int PRIMARY KEY AUTO_INCREMENT,
    text varchar NOT NULL,
    answer varchar NOT NULL,
    conspect_id int NOT NULL
);

CREATE TABLE user_task (
    username varchar(255) NOT NULL,
    task_id int NOT NULL
);

CREATE TABLE current_task (
    username varchar(255) NOT NULL UNIQUE,
    task_id int NOT NULL
);

INSERT INTO repos (fullName, author, pathName) VALUES
    ('super repo', 'jejutic', 'itsme'),
    ('another guy yes', 'him', 'his'),
    ('Матлогика', 'shd', 'logic2023a');

INSERT INTO conspects (path, repo_id) VALUES ( '/usr/bin', 1 ), ( '/yes/me', 1 );

INSERT INTO tasks (text, answer, conspect_id) VALUES ( 'okay here is a big long story', '42 24', 1 ), ( 'okay here is a big short story', '42 34', 1 );
