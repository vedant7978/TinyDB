DROP TABLE IF EXISTS student;
CREATE TABLE student (
  id int PRIMARY KEY,
  name varchar
);

INSERT INTO student (id, name) VALUES ('10', 'jems');
INSERT INTO student (id, name) VALUES ('null', 'jems');
INSERT INTO student (id, name) VALUES ('0', 'jems');
INSERT INTO student (id, name) VALUES ('null', 'jems');

DROP TABLE IF EXISTS student;
CREATE TABLE student (
  name PRIMARY KEY,
  email
);

INSERT INTO student (name, email) VALUES ('jems', 'jems@gmail.com');
INSERT INTO student (name, email) VALUES ('jems', 'jems@gmail.com');

