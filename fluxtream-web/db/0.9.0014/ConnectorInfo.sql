ALTER TABLE `Connector` ADD COLUMN `supportsRenewTokens` char(1) NOT NULL DEFAULT 'N';
ALTER TABLE `Connector` ADD COLUMN `renewTokensUrlTemplate` varchar(255) NULL;
DELETE FROM `Connector`;