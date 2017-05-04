<!-- 
 ?ResourceName "TemplateForm.jsp"
 ?ResourceTimestamp "08/11/00 16.43.22"
 ?ResourceEdition "1.0"
-->

<%@ page 
	import=	"it.cnr.jada.util.jsp.*,it.cnr.jada.action.*,java.util.*, it.cnr.jada.util.action.*, it.cnr.contab.doccont00.bp.*,it.cnr.contab.doccont00.action.*, it.cnr.contab.doccont00.core.bulk.*,
			it.cnr.contab.doccont00.core.bulk.*"
%>


<%  
	CRUDAccertamentoBP bp = (CRUDAccertamentoBP)BusinessProcess.getBusinessProcess(request);
	AccertamentoBulk accertamento = (AccertamentoBulk)bp.getModel();
	Accertamento_scadenzarioBulk scadenza = (Accertamento_scadenzarioBulk)bp.getScadenzario().getModel();
%>
	<table border="0" cellspacing="0" cellpadding="2">
		<% 	if ( bp.isEditingScadenza() && scadenza != null && scadenza.getScadenza_iniziale() != null ) 
			{%>
				<tr>			
				<td><% bp.getScadenzario().writeFormLabel( out, "dt_scadenza_incasso"); %></td>
				<td><% bp.getScadenzario().writeFormInput( out,"default","dt_scadenza_incasso",!bp.isEditingScadenza(),null,null); %>
					<% bp.getScadenzario().writeFormLabel( out, "fl_aggiorna_scad_successiva"); %>
    				<% bp.getScadenzario().writeFormInput( out,"default","fl_aggiorna_scad_successiva",!bp.isEditingScadenza(),null,null); %>		
				</td>
				</tr>
			<%} 
		 	else 
		 	{%> 
				<tr>
				<td><% bp.getScadenzario().writeFormLabel( out, "dt_scadenza_incasso"); %></td>
				<td><% bp.getScadenzario().writeFormInput( out,"default","dt_scadenza_incasso",!bp.isEditingScadenza(),null,null);%></td>
				</tr>
			<%}
		 %>
		 
	<tr>
	<td><% bp.getScadenzario().writeFormLabel( out, "im_scadenza"); %></td>
	<td><% bp.getScadenzario().writeFormInput( out,"default","im_scadenza",!bp.isEditingScadenza(),null,null); %></td>
	</tr>
	
	<tr>
	<td><% bp.getScadenzario().writeFormLabel( out, "ds_scadenza"); %></td>
	<td><% bp.getScadenzario().writeFormInput( out,"default","ds_scadenza",!bp.isEditingScadenza(),null,null); %></td>
	</tr>
	
	<tr>
	<td colspan=2 ALIGN="CENTER">
		<% JSPUtils.button(out,bp.encodePath("img/edit24.gif"),bp.encodePath("img/edit24.gif"), "Modifica","javascript:submitForm('doEditaScadenza')", bp.isEditScadenzaButtonEnabled(),bp.getParentRoot().isBootstrap()); %>
		<% JSPUtils.button(out,bp.encodePath("img/save24.gif"),bp.encodePath("img/save24.gif"), "Conferma","javascript:submitForm('doConfermaScadenza')", bp.isConfermaScadenzaButtonEnabled(),bp.getParentRoot().isBootstrap()); %>
		<% JSPUtils.button(out,bp.encodePath("img/undo24.gif"),bp.encodePath("img/undo24.gif"), "Annulla","javascript:submitForm('doUndoScadenza')", bp.isUndoScadenzaButtonEnabled(),bp.getParentRoot().isBootstrap()); %>		
		<% if (bp instanceof CRUDAccertamentoResiduoBP && accertamento.isAccertamentoResiduo())  %>
			<% JSPUtils.button(out,bp.encodePath("img/remove24.gif"),bp.encodePath("img/remove24.gif"), "Azzera","javascript:submitForm('doAnnullaScadenza')", bp.isAnnullaScadenzaButtonEnabled(),bp.getParentRoot().isBootstrap()); %>		
	</td>
	</tr>
	
	</table>