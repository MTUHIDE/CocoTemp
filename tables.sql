CREATE TABLE data
(
  id          BINARY(16) PRIMARY KEY              NOT NULL,
  user_id     INT(11)                             NOT NULL,
  device_id   BINARY(16)                          NOT NULL,
  date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  temperature DOUBLE                              NOT NULL
);
CREATE TABLE device
(
  id                 BINARY(16) PRIMARY KEY NOT NULL,
  user_id            INT(11)                NOT NULL,
  device_name        TEXT,
  device_latitude    DOUBLE(10, 8),
  device_longitude   DECIMAL(11, 8)         NOT NULL,
  device_description TEXT                   NOT NULL
);
CREATE TABLE device_statistics
(
  statistics_id   BINARY(16) PRIMARY KEY              NOT NULL,
  device_id       BINARY(16),
  date            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  week_max        DOUBLE DEFAULT '0',
  week_min        DOUBLE DEFAULT '0',
  week_avg        DOUBLE DEFAULT '0',
  week_deviation  DOUBLE DEFAULT '0',
  month_max       DOUBLE DEFAULT '0',
  month_min       DOUBLE DEFAULT '0',
  month_avg       DOUBLE DEFAULT '0',
  month_deviation DOUBLE DEFAULT '0',
  year_max        DOUBLE DEFAULT '0',
  year_min        DOUBLE DEFAULT '0',
  year_avg        DOUBLE DEFAULT '0',
  year_deviation  DOUBLE DEFAULT '0',
  all_max         DOUBLE DEFAULT '0',
  all_min         DOUBLE DEFAULT '0',
  all_avg         DOUBLE DEFAULT '0',
  all_deviation   DOUBLE DEFAULT '0'
);
CREATE TABLE role
(
  id   INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(45)
);
CREATE TABLE upload_history
(
  id          BINARY(16) PRIMARY KEY              NOT NULL,
  device_id   BINARY(16),
  date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  duration    MEDIUMTEXT,
  description TEXT,
  error       TINYINT(1) DEFAULT '0',
  viewed      TINYINT(1) DEFAULT '0',
  user_id     INT(11),
  records     INT(11) DEFAULT '0'                 NOT NULL
);
CREATE TABLE user
(
  id             INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  username       VARCHAR(255),
  password       VARCHAR(255),
  first_name     TEXT,
  middle_initial TEXT,
  last_name      TEXT,
  email          TEXT                NOT NULL
);
CREATE TABLE user_role
(
  user_id INT(11) NOT NULL,
  role_id INT(11) NOT NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (user_id, role_id)
);
ALTER TABLE data
  ADD FOREIGN KEY (user_id) REFERENCES user (id);
ALTER TABLE data
  ADD FOREIGN KEY (device_id) REFERENCES device (id);
CREATE INDEX data_device_id_fk
  ON data (device_id);
CREATE INDEX data_user_id_fk
  ON data (user_id);
ALTER TABLE device
  ADD FOREIGN KEY (user_id) REFERENCES user (id);
CREATE INDEX device_user_id_fk
  ON device (user_id);
ALTER TABLE device_statistics
  ADD FOREIGN KEY (device_id) REFERENCES device (id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
CREATE INDEX device_statistics_device_id_fk
  ON device_statistics (device_id);
ALTER TABLE upload_history
  ADD FOREIGN KEY (device_id) REFERENCES device (id);
ALTER TABLE upload_history
  ADD FOREIGN KEY (user_id) REFERENCES user (id);
CREATE INDEX upload_history_device_id_fk
  ON upload_history (device_id);
CREATE UNIQUE INDEX upload_history_id_uindex
  ON upload_history (id);
CREATE INDEX upload_history_user_id_fk
  ON upload_history (user_id);
ALTER TABLE user_role
  ADD FOREIGN KEY (user_id) REFERENCES user (id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
ALTER TABLE user_role
  ADD FOREIGN KEY (role_id) REFERENCES role (id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
CREATE INDEX fk_user_role_roleid_idx
  ON user_role (role_id);