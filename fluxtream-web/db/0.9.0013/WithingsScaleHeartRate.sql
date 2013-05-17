CREATE TABLE `Facet_WithingsHeartPulseMeasure` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `api` int(11) NOT NULL,
  `apiKeyId` bigint(20) DEFAULT NULL,
  `comment` longtext,
  `end` bigint(20) NOT NULL,
  `fullTextDescription` longtext,
  `guestId` bigint(20) NOT NULL,
  `isEmpty` char(1) NOT NULL,
  `objectType` int(11) NOT NULL,
  `start` bigint(20) NOT NULL,
  `tags` longtext,
  `timeUpdated` bigint(20) NOT NULL,
  `heartPulse` float NOT NULL,
  PRIMARY KEY (`id`),
  KEY `isEmpty_index` (`isEmpty`),
  KEY `end_index` (`end`),
  KEY `start_index` (`start`),
  KEY `api_index` (`api`),
  KEY `objectType_index` (`objectType`),
  KEY `guestId_index` (`guestId`),
  KEY `apiKey` (`apiKeyId`),
  KEY `timeUpdated_index` (`timeUpdated`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;