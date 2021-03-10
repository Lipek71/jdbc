CREATE TABLE `images` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`activities_id` INT(10) UNSIGNED NOT NULL,
	`filename` VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
	`content` BLOB NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE,
	INDEX `FK_image_activities` (`activities_id`) USING BTREE,
	CONSTRAINT `FK_image_activities` FOREIGN KEY (`activities_id`) REFERENCES `activitytracker`.`activities` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=0;