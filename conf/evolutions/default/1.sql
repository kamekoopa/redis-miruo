# サーバ設定

# --- !Ups

CREATE TABLE server_settings (
  id          varchar(128) NOT NULL,
  host        VARCHAR(756) NOT NULL,
  port        INT          NOT NULL DEFAULT 6379,
  password    VARCHAR(756),
  description TEXT,
  created_at  DATETIME,
  updated_at  DATETIME,
  PRIMARY KEY (id),
  index idx_created_at(created_at),
  index idx_updated_at(updated_at)
);

# --- !Downs

DROP TABLE server_settings;