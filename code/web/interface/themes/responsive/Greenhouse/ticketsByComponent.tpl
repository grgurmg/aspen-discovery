{strip}
	<div id="main-content" class="col-sm-12">
		<h1>{translate text="Active Tickets By Component" isAdminFacing=true}</h1>

		<table id="adminTable" class="table table-striped table-bordered">
			<thead>
			<tr>
				<th>{translate text="Site Name" isAdminFacing=true}</th>
				<th>{translate text="Implementation" isAdminFacing=true}</th>
				<th>{translate text="Support" isAdminFacing=true}</th>
				<th>{translate text="Bugs" isAdminFacing=true}</th>
				<th>{translate text="Development" isAdminFacing=true}</th>
				<th>{translate text="Total" isAdminFacing=true}</th>
			</tr>
			</thead>
			<tbody>
				{foreach from=$ticketsByComponent item=componentTicketInfo}
					<tr>
						<td>{$componentTicketInfo.component}</td>
						<td>{$componentTicketInfo.Implementation}</td>
						<td>{$componentTicketInfo.Support}</td>
						<td>{$componentTicketInfo.Bugs}</td>
						<td>{$componentTicketInfo.Development}</td>
						<td>{$componentTicketInfo.Total}</td>
					</tr>
				{/foreach}
			</tbody>
		</table>
	</div>
{/strip}
<script type="text/javascript">
	{literal}
	$("#adminTable").tablesorter({cssAsc: 'sortAscHeader', cssDesc: 'sortDescHeader', cssHeader: 'unsortedHeader', widgets:['zebra', 'filter'] });
	{/literal}
</script>