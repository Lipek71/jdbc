CREATE TABLE `track_point` (
	`id` INT(10) UNSIGNED NOT NULL,
	`activities_id` INT(10) UNSIGNED NOT NULL,
	`time` TIMESTAMP NULL DEFAULT NULL,
	`lat` DOUBLE(22,0) NULL DEFAULT NULL,
	`lon` DOUBLE(22,0) NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE,
	INDEX `FK_track_point_activities` (`activities_id`) USING BTREE,
	CONSTRAINT `FK_track_point_activities` FOREIGN KEY (`activities_id`) REFERENCES `activitytracker`.`activities` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;