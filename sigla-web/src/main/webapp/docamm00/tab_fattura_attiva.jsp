<%@ page pageEncoding="UTF-8"
	import="it.cnr.jada.util.jsp.*,
		it.cnr.jada.action.*,
		java.util.*,
		it.cnr.jada.util.action.*,
		it.cnr.contab.docamm00.tabrif.bulk.*,
		it.cnr.jada.*,
		it.cnr.contab.docamm00.docs.bulk.*,
		it.cnr.contab.docamm00.bp.*,
		it.cnr.contab.anagraf00.tabrif.bulk.*"
%>
<%	CRUDFatturaAttivaBP bp = (CRUDFatturaAttivaBP)BusinessProcess.getBusinessProcess(request);
	Fattura_attivaBulk fatturaAttiva = (Fattura_attivaBulk)bp.getModel();
	it.cnr.contab.anagraf00.core.bulk.TerzoBulk terzoUO = fatturaAttiva.getTerzo_uo();
	UserContext uc = HttpActionContext.getUserContext(session);
	boolean roOnAutoGen = false;
	if (bp instanceof IDocumentoAmministrativoBP)
		roOnAutoGen = ((IDocumentoAmministrativoBP)bp).isAutoGenerated();
%>
   <div class="Group card">
	 <table>
	  <% if (fatturaAttiva.isCongelata() && !bp.isSearching()) { %>	
	      <tr>
			<span class="FormLabel" style="color:red">
				Il documento è stato CONGELATO!
			</span>
	      </tr>
	  <% } %>
	  <% if (fatturaAttiva.isRiportataInScrivania() && !bp.isSearching()) { %>	
	      <tr>
			<span class="FormLabel" style="color:red">
				<%=fatturaAttiva.getRiportataKeys().get(fatturaAttiva.getRiportataInScrivania()) %>
			</span>
	      </tr>
	  <% } %>
	  <tr>
	  <td>
	 	<% bp.getController().writeFormLabel(out,"esercizio");%>
	   </td>
	   <td>
	   	<% bp.getController().writeFormInput(out,null,"esercizio",false,null,"");%>
	   </td>
	   <td>   
	 	<% bp.getController().writeFormLabel(out,"pg_fattura_attiva");%>
	 	</td><td>
	 	<% bp.getController().writeFormInput(out,null,"pg_fattura_attiva",false,null,"");%>
	   </td>
	 </tr>
	 <tr>
		<% if (!bp.isSearching()) { %>	 
	   		<td><% bp.getController().writeFormLabel(out,"stato_cofi");%></td>
	   		<td><% bp.getController().writeFormInput(out,null,"stato_cofi",false,null,"");%></td>
		    <td><% bp.getController().writeFormLabel(out,"ti_associato_manrev");%></td>
		    <td><% bp.getController().writeFormInput(out, null,"ti_associato_manrev",false,null,"");%></td>
		<%} else {%>
	   		<td><% bp.getController().writeFormLabel(out,"stato_cofiForSearch");%></td>
	   		<td><% bp.getController().writeFormInput(out,null,"stato_cofiForSearch",roOnAutoGen,null,"");%></td>
			<td><% bp.getController().writeFormLabel(out,"ti_associato_manrevForSearch");%></td>
			<td><% bp.getController().writeFormInput(out, null,"ti_associato_manrevForSearch",roOnAutoGen,null,"");%></td>
		<%}%>
   	 </tr>
	 <tr>      	
  		<td>
      		<% bp.getController().writeFormLabel(out,"dt_registrazione");%>
 		</td><td>
 			<% bp.getController().writeFormInput(out,null,"dt_registrazione",false,null,"submitForm('doOnDataRegistrazioneChange')\"");%>
      	</td> 
		<td>
      		<% bp.getController().writeFormLabel(out,"dt_scadenza");%>
	 	</td><td>
 			<% bp.getController().writeFormInput(out,null,"dt_scadenza",false,null,"");%>
      	</td> 
     </tr>
     <tr>      	
		<td>
      		<% bp.getController().writeFormLabel(out,"fl_liquidazione_differita");%>
      	</td><td>
      		<% bp.getController().writeFormInput(out,null,"fl_liquidazione_differita",roOnAutoGen||(fatturaAttiva.getFl_liquidazione_differita()!=null && fatturaAttiva.getFl_liquidazione_differita()),null,"onClick=\"submitForm('doOnLiquidazioneDifferitaChange')\"");%>
      	</td>
		<td>
      		<% bp.getController().writeFormLabel(out,"protocollo_iva");%>
      	</td><td>
      		<% bp.getController().writeFormInput(out,null,"protocollo_iva",false,null,"");%>
      	</td> 
     </tr>
     <tr>      	
		<td>
      		<% bp.getController().writeFormLabel(out,"dt_emissione");%>
      	</td><td>
      		<% bp.getController().writeFormInput(out,null,"dt_emissione",false,null,"");%>
      	</td>
		<td>
      		<% bp.getController().writeFormLabel(out,"protocollo_iva_generale");%>
      	</td><td>
      		<% bp.getController().writeFormInput(out,null,"protocollo_iva_generale",false,null,"");%>
      	</td> 
     </tr>
     <tr>
	    <td>
      		<% bp.getController().writeFormLabel(out,"dt_da_competenza_coge");%>
        </td><td>
      		<% bp.getController().writeFormInput(out,null,"dt_da_competenza_coge",false,null,"");%>
      	</td>
      	<td>
      		<% bp.getController().writeFormLabel(out,"dt_a_competenza_coge");%>
      	</td><td>
      		<% bp.getController().writeFormInput(out,null,"dt_a_competenza_coge",false,null,"");%>
      	</td>
     </tr>
      
     </table>
   </div>

   <div class="Group card">
  	 <table>
	  <% if (bp.isSearching()) { %>
	      <tr>     	
		     	<td>
			   		<% bp.getController().writeFormLabel(out, "sezionaliFlagsRadioGroup");%>
		      	</td>      	
		     	<td colspan="3">
			   		<% bp.getController().writeFormInput(out, null, "sezionaliFlagsRadioGroup", false, null, "onClick=\"submitForm('doOnSezionaliFlagsChange')\"");%>
		      	</td>
	      </tr>
		<% } else { %>
		      <tr>     	
		     	<td>
		      		<% bp.getController().writeFormLabel(out,"fl_intra_ue");%>
		      	</td>      	
		     	<td>
		      		<% bp.getController().writeFormInput(out,null,"fl_intra_ue",roOnAutoGen,null,"onClick=\"submitForm('doOnFlIntraUEChange')\"");%>
		      	</td>      	
		     	<td>
		      		<% bp.getController().writeFormLabel(out,"fl_extra_ue");%>
		      	</td>      	
		     	<td>
		      		<% bp.getController().writeFormInput(out,null,"fl_extra_ue",roOnAutoGen,null,"onClick=\"submitForm('doOnFlExtraUEChange')\"");%>
		      	</td>      	
		      	<td>
		      		<% bp.getController().writeFormLabel(out,"fl_san_marino");%>
		      	</td>      	
		     	<td>
		     		<% bp.getController().writeFormInput(out,null,"fl_san_marino",roOnAutoGen,null,"onClick=\"submitForm('doOnFlSanMarinoChange')\"");%>
		     	</td>
		      </tr>
       <% } %>
       <% if (
		  		(fatturaAttiva.getFl_intra_ue() != null && fatturaAttiva.getFl_intra_ue().booleanValue())) { 
		  	if (bp.isSearching()) { %>
		      <tr>     	
		      	<td>
		      		<% bp.getController().writeFormLabel(out,"ti_bene_servizioForSearch");%>
		      	</td>      	
		     	<td colspan="5">
		     		<% bp.getController().writeFormInput(out,null,"ti_bene_servizioForSearch",roOnAutoGen,null,"");%>
		     	</td>
		      </tr>
		    <% } else { %>
		      <tr>     	
		      	<td>
		      		<% bp.getController().writeFormLabel(out,"ti_bene_servizio");%>
		      	</td>      	
		     	<td colspan="5">
		     		<% bp.getController().writeFormInput(out,null,"ti_bene_servizio",roOnAutoGen,null,"");%>
		     	</td>
		      </tr>
	  <% 	}
	  	  } %>
      
      <tr>
      	<td>
      		<% bp.getController().writeFormLabel(out,"sezionale");%>
      	</td>      	
     	<td colspan="5">
     		<% bp.getController().writeFormInput(out,null,"sezionale",roOnAutoGen,null,"");%>
     	</td>
      </tr>
     </table>
    </div>

     <div class="Group card">
     <table>
		<tr>
		<td>
				<% bp.getController().writeFormLabel(out,"ti_causale_emissione");%>
			</td>      	
			<td>
				<% bp.getController().writeFormInput(out,null,"ti_causale_emissione",roOnAutoGen,null,"onChange=\"submitForm('doOnCausaleEmissioneChange')\"");%>
			</td>
	   </tr>
      <tr>
			<td>
				<% bp.getController().writeFormLabel(out,"im_totale_imponibile");%>
			</td>      	
			<td>
				<% bp.getController().writeFormInput(out,null,"im_totale_imponibile",false,null,"");%>
			</td>
			<td>
				<% bp.getController().writeFormLabel(out,"im_totale_iva");%>
			</td>      	
			<td>
				<% bp.getController().writeFormInput(out,null,"im_totale_iva",false,null,"");%>
			</td>
      </tr>      
      <tr>
			<td>
				<% bp.getController().writeFormLabel(out,"im_totale_fattura");%>
			</td>      	
			<td>
				<% bp.getController().writeFormInput(out,null,"im_totale_fattura",false,null,"");%>
			</td>
      </tr>      
      <tr>
	       <td>
				<% bp.getController().writeFormLabel(out,"ds_fattura_attiva");%>
		   </td>      	
		   <td colspan=3>
				<% bp.getController().writeFormInput(out,null,"ds_fattura_attiva",false,null,"");%>
		</td>
      </tr>
      <tr>
	       <td>
				<% bp.getController().writeFormLabel(out,"riferimento_ordine");%>
		   </td>      	
		   <td colspan=3>
				<% bp.getController().writeFormInput(out,null,"riferimento_ordine",false,null,"");%>
		</td>
      </tr>          	
    </table>
   </div>

    <div class="Group card">
    <table>
       <tr>
			<td>
				<% bp.getController().writeFormLabel(out,"valuta");%>			  
			</td>
			<td>
				<% bp.getController().writeFormInput(out,null,"valuta",roOnAutoGen,null,"");%>
			</td>
			<td>
				<% bp.getController().writeFormLabel(out,"cambio");%>			  
			</td>
			<td>
				<% bp.getController().writeFormInput(out,null,"cambio",roOnAutoGen,null,"");%>
			</td>
      </tr>
    </table>
    </div>

    <div class="Group card">
	<table>	
	 <tr>
  		<% bp.getController().writeFormField(out,"termini_pagamento_uo");%>
      </tr>
      <tr>
     	<td>
 	     	<% bp.getController().writeFormLabel(out,"modalita_pagamento_uo");%>
      	</td>      	
     	<td colspan="2">
	      	<% bp.getController().getBulkInfo().writeFormInput(out,bp.getModel(),null,"modalita_pagamento_uo",bp.isROBank_ModPag(uc,fatturaAttiva),null,"onChange=\"submitForm('doOnModalitaPagamentoUOChange')\"",bp.getInputPrefix(), bp.getStatus(), bp.getFieldValidationMap(), bp.getParentRoot().isBootstrap());%>	
			<% 	if (fatturaAttiva.getBanca_uo() != null) {
					bp.getController().getBulkInfo().writeFormInput(out,bp.getModel(),null,"listabancheuo",bp.isROBank(uc,fatturaAttiva),null,null,bp.getInputPrefix(), bp.getStatus(), bp.getFieldValidationMap(), bp.getParentRoot().isBootstrap());
				} %>
   		</td>
      </tr>
      
		<tr>
		  	<td colspan="2">
		<%	if (fatturaAttiva.getBanca_uo() != null) {
				if (Rif_modalita_pagamentoBulk.BANCARIO.equalsIgnoreCase(fatturaAttiva.getBanca_uo().getTi_pagamento())) {
			 	     	bp.getController().writeFormInput(out,"contoBUO");
				} else if (Rif_modalita_pagamentoBulk.POSTALE.equalsIgnoreCase(fatturaAttiva.getBanca_uo().getTi_pagamento())) {
			 	     	bp.getController().writeFormInput(out,"contoPUO");
				} else if (Rif_modalita_pagamentoBulk.QUIETANZA.equalsIgnoreCase(fatturaAttiva.getBanca_uo().getTi_pagamento())) {
			 	     	bp.getController().writeFormInput(out,"contoQUO");
				} else if (Rif_modalita_pagamentoBulk.ALTRO.equalsIgnoreCase(fatturaAttiva.getBanca_uo().getTi_pagamento())) { 
			 	     	bp.getController().writeFormInput(out,"contoAUO");
				} else if (Rif_modalita_pagamentoBulk.BANCA_ITALIA.equalsIgnoreCase(fatturaAttiva.getBanca_uo().getTi_pagamento())) { 
			 	     	bp.getController().writeFormInput(out,"contoIUO");
				} else if (Rif_modalita_pagamentoBulk.IBAN.equalsIgnoreCase(fatturaAttiva.getBanca_uo().getTi_pagamento())) { 
		 	     	bp.getController().writeFormInput(out,"contoNUO");
				}
   			} else {
				if ((fatturaAttiva.getModalita_uo()==null || fatturaAttiva.getModalita_uo().isEmpty()) && !bp.isSearching()) { %>
					<span class="FormLabel" style="color:red">
						Nessuna modalità di pagamento trovata per la UO
					</span>
				<%} else if ((fatturaAttiva.getModalita_pagamento_uo() != null) && !bp.isSearching()) {%>
				<span class="FormLabel" style="color:red">
					Nessun riferimento trovato per la modalità di pagamento selezionata!
				</span>
								
		<%	}} %>
		
			  	</td>
			</tr>
		
    </table>
   </div>
