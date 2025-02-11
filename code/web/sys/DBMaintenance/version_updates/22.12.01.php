<?php
/** @noinspection PhpUnused */
function getUpdates22_12_01(): array {
	$curTime = time();
	return [
		/*'name' => [
			'title' => '',
			'description' => '',
			'sql' => [
				''
			]
        ], //sample*/

		'library_holdRange' => [
			'title' => 'Add holdRange to Library settings',
			'description' => 'Add holdRange to Library settings',
			'sql' => [
				"ALTER TABLE library ADD COLUMN holdRange VARCHAR(20) DEFAULT 'SYSTEM'",
			],
		],
		//library_holdRange
		'ptype_vdx_client_category' => [
			'title' => 'PType - VDX Client Category',
			'description' => 'Add VDX Client Category to PType settings',
			'sql' => [
				"ALTER TABLE ptype ADD COLUMN vdxClientCategory VARCHAR(10) DEFAULT ''",
			],
		],
		//ptype_vdx_client_category
	];
}