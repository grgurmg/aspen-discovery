<!DOCTYPE html>
<html lang="{$userLang->code}">
<head prefix="og: http://ogp.me/ns#">
	{strip}
		<title>{$pageTitleShortAttribute|truncate:64:"..."}{if !$isMobile} | {$librarySystemName}{/if}</title>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
		{if !empty($google_verification_key)}
			<meta name="google-site-verification" content="{$google_verification_key}">
		{/if}

		{if !empty($metadataTemplate)}
			{include file=$metadataTemplate}
		{/if}
		<meta property="og:site_name" content="{$site.title|removeTrailingPunctuation|escape:html}"/>
		{if !empty($og_title)}
			<meta property="og:title" content="{$og_title|removeTrailingPunctuation|escape:html}"/>
		{/if}
		{if !empty($og_description)}
			<meta property="og:description" content="{$og_description|escape:html}"/>
		{/if}
		{if !empty($og_type)}
			<meta property="og:type" content="{$og_type|escape:html}"/>
		{/if}
		{if !empty($dc_creator)}
			<meta property="DC.Creator" content="{$dc_creator|escape:html}">
		{/if}
		{if !empty($dc_pubName)}
		<meta property="DC.publisher" content="{$dc_pubName}">
		{/if}
		{if !empty($dc_pubDate)}
		<meta property="DC.date.issued" content="{$dc_pubDate}">
		{/if}
		{if !empty($og_image)}
			<meta property="og:image" content="{$og_image|escape:html}"/>
		{/if}
		{if !empty($og_url)}
			<meta property="og:url" content="{$og_url|escape:html}"/>
		{/if}
		<link type="image/x-icon" href="{$favicon}" rel="shortcut icon">
		<link rel="search" type="application/opensearchdescription+xml" title="{$site.title} Catalog Search" href="/Search/OpenSearch?method=describe">
		{include file="cssAndJsIncludes.tpl"}
		{$themeCss}
		{if $loadRecaptcha}
		    <script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit" async defer></script>
		{/if}
	{/strip}
</head>
<body class="module_{$module} action_{$action}{if $masqueradeMode} masqueradeMode{/if}{if $loggedIn} loggedIn{else} loggedOut{/if}" id="{$module}-{$action}{if $module=="WebBuilder" && $action=="BasicPage" || $action=="PortalPage"}-{$id}{/if}" dir="{if $userLang->isRTL()}rtl{else}auto{/if}">
{strip}
	{if $showTopOfPageButton}
	<a class="top-link hide" href="" id="js-top">
		<i class="fas fa-arrow-up fa-2x fa-fw"></i>
		<span class="screen-reader-text">{translate text="Back to top" isPublicFacing=true}</span>
	</a>
	{/if}
	{if $shouldShowAdminAlert}
		{include file="adminMessages.tpl"}
	{/if}
	<div {if !$fullWidthTheme}class="container"{/if} id="page-container">
		<div {if $fullWidthTheme}class="container-fluid"{/if} id="system-messages">
			{if !empty($systemMessages)}
				<div id="system-message-header" class="row">
					{include file="systemMessages.tpl" messages=$systemMessages}
				</div>
			{/if}

			{foreach from=$messages item="message"}
			<div class="col-xs-12">
				<div class="alert alert-{$message->messageLevel} alert-dismissable">
						<button type="button" class="close" data-dismiss="alert" aria-label="close" onclick="AspenDiscovery.Account.dismissMessage({$message->id})"><span aria-hidden="true">&times;</span></button>
						{translate text=$message->message isPublicFacing=true}
						{if !empty($message->action1Title) && !empty($message->action1)}
							&nbsp;<a data-dismiss="alert" class="btn btn-default" onclick="{$message->action1}">{translate text=$message->action1Title isPublicFacing=true}</a>
						{/if}
						{if !empty($message->action2Title) && !empty($message->action2)}
							<a data-dismiss="alert" class="btn btn-default" onclick="{$message->action2}">{translate text=$message->action2Title isPublicFacing=true}</a>
						{/if}
						{if !empty($message->addendum)}
							<a href="/MyAccount/LinkedAccounts" data-dismiss="alert" id="addendum"><br>{translate text=$message->addendum isPublicFacing=true}</a>
						{/if}
				</div>
			</div>
			{/foreach}
		</div>

		<div {if $fullWidthTheme}class="container-fluid"{/if} id="page-header">
			<div id="header-wrapper" role="banner" class="row {if $fullWidthTheme}row-no-gutters fullWidth{/if}">
				{include file='header_responsive.tpl'}
			</div>
		</div>

		<div {if $fullWidthTheme}class="container-fluid"{/if} id="page-menu-bar">
			<div id="{if $fullWidthTheme}horizontal-menu-bar-wrapper-fullWidth{else}horizontal-menu-bar-wrapper{/if}" class="row {if $fullWidthTheme}row-no-gutters{/if}">
				<div id="horizontal-menu-bar-container" class="col-tn-12 col-xs-12 menu-bar {if $fullWidthTheme}fullWidth{/if}" role="navigation" aria-label="{translate text="Top Navigation" isPublicFacing=true inAttribute=true}">
					{include file='horizontal-menu-bar.tpl'}
				</div>
				<div id="horizontal-search-container" class="col-tn-12 {if $fullWidthTheme}fullWidth{/if}" role="search">
					{include file="Search/horizontal-searchbox.tpl"}
				</div>
			</div>
		</div>

	{if $fullWidthTheme}<div class="container">{/if}
		<div id="content-container">
			<div class="row">
				{if !empty($sidebar)} {* Main Content & Sidebars *}
					{* Sidebar on the left *}
					<div class="col-tn-12 col-xs-12 col-sm-4 col-md-3 col-lg-3" id="side-bar" role="navigation" aria-labelledby="sidebarNav">
						{include file="sidebar.tpl"}
					</div>
					<div class="col-tn-12 col-xs-12 col-sm-8 col-md-9 col-lg-9" id="main-content-with-sidebar">
						{if $showBreadcrumbs}
							<div role="navigation" aria-label="{translate text="Breadcrumbs" isPublicFacing=true inAttribute=true}">
							{include file="breadcrumbs.tpl"}
							</div>
						{/if}
						<div role="main">
							{if $module}
								{include file="$module/$pageTemplate"}
							{else}
								{include file="$pageTemplate"}
							{/if}
						</div>
					</div>
				{else} {* Main Content Only, no sidebar *}
					<div class="col-xs-12" id="main-content">
						{if $showBreadcrumbs}
							<div role="navigation" aria-label="{translate text="Breadcrumbs" isPublicFacing=true inAttribute=true}">
							{include file="breadcrumbs.tpl"}
							</div>
						{/if}
						<div role="main">
							{if $module}
								{include file="$module/$pageTemplate"}
							{else}
								{include file="$pageTemplate"}
							{/if}
						</div>
					</div>
				{/if}
			</div>
		</div>
	{if $fullWidthTheme}</div>{/if}

		<div {if $fullWidthTheme}class="container-fluid"{/if} id="page-footer">
			<div id="footer-container" role="contentinfo" class="row {if $fullWidthTheme}row-no-gutters{/if}">
				{include file="footer_responsive.tpl"}
			</div>
		</div>

	</div>
	{include file="modal_dialog.tpl"}

	{include file="tracking.tpl"}

	{if !empty($semanticData)}
		{include file="jsonld.tpl"}
	{/if}

	{if !empty($customJavascript)}
		{$customJavascript}
	{/if}
{/strip}
</body>
</html>