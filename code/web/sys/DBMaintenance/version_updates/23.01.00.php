<?php
/** @noinspection PhpUnused */
function getUpdates23_01_00(): array
{
	$curTime = time();
	return [
		/*'name' => [
			'title' => '',
			'description' => '',
			'sql' => [
				''
			]
		], //sample*/

		//mark
		'users_to_tasks' => [
			'title' => 'Development - Link Users To Tasks',
			'description' => 'Development - Link Users To Tasks',
			'sql' => [
				'CREATE TABLE IF NOT EXISTS development_task_developer_link (
					id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
					userId INT(11), 
					taskId INT(11), 
					UNIQUE INDEX (userId, taskId)
				) ENGINE INNODB',
				'CREATE TABLE IF NOT EXISTS development_task_qa_link (
					id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
					userId INT(11), 
					taskId INT(11), 
					UNIQUE INDEX (userId, taskId)
				) ENGINE INNODB',
			],
		],
		//users_to_tasks
		'ptype_length' => [
			'title' => 'PType length',
			'description' => 'Increase the length of the PType column',
			'sql' => [
				"ALTER TABLE ptype CHANGE COLUMN pType pType VARCHAR(50) COLLATE utf8mb4_general_ci NOT NULL",
			],
		],
		//ptype_length
		'record_parents_index' => [
			'title' => 'Record Parents Index',
			'description' => 'Add an index to record_parents to improve index performance',
			'sql' => [
				"alter table record_parents add index parentRecordId(parentRecordId)",
			],
		],
		//record_parents_index

		//kirstien
		'add_account_alerts_notification' => [
			'title' => 'Add account alert notification type',
			'description' => 'Adds account alert notifications',
			'sql' => [
				'ALTER TABLE user_notification_tokens ADD COLUMN notifyAccount TINYINT(1) DEFAULT 0',
			],
		],
		//add_account_alerts_notification
		'add_invoiceCloud' => [
			'title' => 'Add eCommerce vendor InvoiceCloud',
			'description' => 'Create InvoiceCloud settings table, update available permissions',
			'sql' => [
				'CREATE TABLE IF NOT EXISTS invoice_cloud_settings (
					id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
					name VARCHAR(50) NOT NULL UNIQUE,
					apiKey VARCHAR(500) NOT NULL,
					invoiceTypeId INT(10),
					ccServiceFee VARCHAR(50)
				) ENGINE INNODB',
				"INSERT INTO permissions (sectionName, name, requiredModule, weight, description) VALUES ('eCommerce', 'Administer InvoiceCloud', '', 10, 'Controls if the user can change InvoiceCloud settings. <em>This has potential security and cost implications.</em>')",
				"INSERT INTO role_permissions(roleId, permissionId) VALUES ((SELECT roleId from roles where name='opacAdmin'), (SELECT id from permissions where name='Administer InvoiceCloud'))",
				'ALTER TABLE library ADD COLUMN invoiceCloudSettingId INT(11) DEFAULT -1',
			],
		],
		//add_invoiceCloud

		//kodi
		'user_browse_add_home' => [
			'title' => 'Add New Browse Categories to Home',
			'description' => 'Store user selection for adding browse categories to home page',
			'sql' => [
				'ALTER TABLE user ADD COLUMN browseAddToHome TINYINT(1) DEFAULT 1',
			],
		],
		//user_browse_add_home
		//other
	];
}