package it.cnr.contab.doccont00.bp;

import it.cnr.contab.config00.bulk.Codici_siopeBulk;
import it.cnr.contab.config00.sto.bulk.Tipo_unita_organizzativaHome;
import it.cnr.contab.config00.sto.bulk.Unita_organizzativaBulk;
import it.cnr.contab.doccont00.ejb.ReversaleComponentSession;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.*;

import it.cnr.contab.doccont00.comp.ReversaleComponent;
import it.cnr.contab.doccont00.core.bulk.*;
import it.cnr.contab.reports.bp.OfflineReportPrintBP;
import it.cnr.contab.reports.bulk.Print_spooler_paramBulk;
import it.cnr.contab.utenze00.bp.CNRUserContext;
import it.cnr.contab.util.Utility;
import it.cnr.jada.DetailedRuntimeException;
import it.cnr.jada.action.*;
import it.cnr.jada.bulk.*;
import it.cnr.jada.comp.ComponentException;
import it.cnr.jada.util.*;
import it.cnr.jada.util.action.*;
import it.cnr.jada.util.jsp.Button;

/**
 * Business Process che gestisce le attivit� di CRUD per l'entita' Reversale
 */
 
public class CRUDReversaleBP extends it.cnr.jada.util.action.SimpleCRUDBP {
	private final SimpleDetailCRUDController documentiAttivi = new SimpleDetailCRUDController("DocumentiAttivi",it.cnr.contab.doccont00.core.bulk.V_doc_attivo_accertamentoBulk.class,"docAttiviColl",this);
	private final CRUDReversaleRigaController documentiAttiviSelezionati = new CRUDReversaleRigaController("DocumentiAttiviSelezionati",it.cnr.contab.doccont00.core.bulk.Reversale_rigaIBulk.class,"reversale_rigaColl",this);
	private final CRUDSospesoController sospesiSelezionati = new CRUDSospesoController("SospesiSelezionati",Sospeso_det_etrBulk.class,"sospeso_det_etrColl",this);
	private final SimpleDetailCRUDController mandatiRev = new SimpleDetailCRUDController("Mandati",V_ass_doc_contabiliBulk.class,"doc_contabili_collColl",this);		
	private final SimpleDetailCRUDController codiciSiopeCollegati = new SimpleDetailCRUDController("codiciSiopeCollegati",Reversale_siopeBulk.class,"reversale_siopeColl",documentiAttiviSelezionati);	
	private final SimpleDetailCRUDController codiciSiopeCollegabili = new SimpleDetailCRUDController("codici_siopeColl",Codici_siopeBulk.class,"codici_siopeColl",documentiAttiviSelezionati);
	private final SimpleDetailCRUDController cupCollegati = new SimpleDetailCRUDController("cupCollegati",ReversaleCupIBulk.class,"reversaleCupColl",documentiAttiviSelezionati){
		public void validate(ActionContext context,OggettoBulk model) throws ValidationException {			
			validateCupCollegati(context,model);
		}
	};
	private final SimpleDetailCRUDController siopeCupCollegati = new SimpleDetailCRUDController("siopeCupCollegati",ReversaleSiopeCupIBulk.class,"reversaleSiopeCupColl",codiciSiopeCollegati){
		public void validate(ActionContext context,OggettoBulk model) throws ValidationException {			
			validateSiopeCupCollegati(context,model);
		}
	};
	private boolean siope_attiva = false;
	private boolean cup_attivo =false;
	private boolean siope_cup_attivo =false;
	private Unita_organizzativaBulk uoSrivania;

public CRUDReversaleBP() {
	super();
	setTab("tab","tabReversale");
}
public CRUDReversaleBP(String function) {
	super(function);
	setTab("tab","tabReversale");
}
/**
 * Metodo utilizzato per gestire l'aggiunta dei documenti attivi.
  	 * @param context <code>ActionContext</code> in uso.
 */

public void aggiungiDocAttivi(it.cnr.jada.action.ActionContext context) throws it.cnr.jada.action.BusinessProcessException 
{
	try 
	{
		ReversaleIBulk reversale = (ReversaleIBulk) getModel();
		if ( getDocumentiAttivi().getSelectedModels(context).size() != 0 )
		{
			reversale = (ReversaleIBulk) ((ReversaleComponentSession) createComponentSession()).aggiungiDocAttivi( context.getUserContext(), reversale, getDocumentiAttivi().getSelectedModels(context));
			setModel( context, reversale );
			getDocumentiAttivi().getSelection().clear();
			resyncChildren( context );
		}
		else
			setMessage( "Non sono stati selezionati documenti attivi" );
		
	} catch(Exception e) {
		throw handleException(e);
	}
	
}
/**
 * Aggiunge un nuovo sospeso alla lista dei Sospeso associati ad una Reversale
 * @param context contesto dell'azione
 */
public void aggiungiSospesi(ActionContext context) throws it.cnr.jada.action.BusinessProcessException 
{
	try
	{
		ReversaleIBulk reversale = (ReversaleIBulk)getModel();
		HookForward caller = (HookForward)context.getCaller();
		List sospesi = (List) caller.getParameter("selectedElements");

		if ( sospesi == null )
			return;

		reversale.validaSospesi( sospesi );
		SospesoBulk sospeso;
		Sospeso_det_etrBulk sde;
		for ( Iterator i = sospesi.iterator() ;i.hasNext() ;) 
		{
			sospeso = (SospesoBulk) i.next();
			sde = reversale.addToSospeso_det_etrColl( sospeso );
			// nella lista dei sospesi disponibili sono stati selezionati pi� elementi
			if( sospesi.size() > 1 )
				sde.setIm_associato( sospeso.getIm_disponibile() );
			// nella lista dei sospesi disponibili � stato selezionato un solo elemento
			else if( sospesi.size() == 1 )
			{
				if( sospeso.getIm_disponibile().compareTo( reversale.getIm_residuo_reversale() ) > 0 )
						sde.setIm_associato( reversale.getIm_residuo_reversale() );
				else
						sde.setIm_associato( sospeso.getIm_disponibile() );
			}
			sde.setToBeCreated();
			sde.setUser(context.getUserInfo().getUserid());
		}
		setModel( context, reversale );
		resyncChildren( context );
	} catch(Exception e) {
		throw handleException(e);
	}		
}
/**
 *	Metodo per disabilitare tutti i campi, nel caso la reversale sia stata annullata ( come se fosse in stato di visualizzazione )
 */
public void basicEdit(it.cnr.jada.action.ActionContext context,it.cnr.jada.bulk.OggettoBulk bulk, boolean doInitializeForEdit) throws BusinessProcessException {
	
	super.basicEdit(context, bulk, doInitializeForEdit);
	
	ReversaleComponentSession session = (ReversaleComponentSession) createComponentSession();

	if (getStatus()!=VIEW){
		ReversaleBulk reversale = (ReversaleBulk)getModel();
		if ( reversale != null && !reversale.getCd_uo_origine().equals( it.cnr.contab.utenze00.bulk.CNRUserInfo.getUnita_organizzativa( context ).getCd_unita_organizzativa()))
		{
			setStatus(VIEW);
			setMessage("Reversale creata dall'Unit� Organizzativa " + reversale.getCd_uo_origine() + ". Non consentita la modifica.");
		}
		else if ( reversale != null &&  reversale.getStato().equals( reversale.STATO_REVERSALE_ANNULLATO ))
		{
			setStatus(VIEW);
			setMessage("Reversale annullata. Non consentita la modifica.");
		}
		else try {
			if ( reversale != null &&  reversale.getTi_reversale().equals( ReversaleBulk.TIPO_TRASFERIMENTO) &&
				 Utility.createParametriCnrComponentSession().getParametriCnr(context.getUserContext(), reversale.getEsercizio()).getFl_siope().equals(Boolean.TRUE))
				{
					setStatus(VIEW);
					setMessage("Reversale di trasferimento. Non consentita la modifica.");
				}
			if (!session.isChiudibileReversaleProvvisoria(context.getUserContext(), reversale))
				{
					setStatus(VIEW);
					setMessage("Reversale Provvisoria relativa a Liquidazioni IVA ancora aperte. Non consentita la modifica.");
				}
			if ( reversale != null && !reversale.getStato_trasmissione().equals(ReversaleBulk.STATO_TRASMISSIONE_NON_INSERITO)) 
				{
					setStatus(VIEW);
					setMessage("Reversale inserita in distinta. Non consentita la modifica.");
				}
			else if (session.isRevProvvLiquidCoriCentroAperta(context.getUserContext(), reversale))
				{
					setMessage("Reversale Provvisoria relativa a Liquidazioni CORI ancora aperte. La modifica � comunque consentita.");
				}
		}catch(Exception e){
			throw handleException(e);
		}
	}
}
/**
 * Metodo utilizzato per gestire il caricamento dei documenti attivi.
  	 * @param context <code>ActionContext</code> in uso.
 	 * @exception <code>BusinessProcessException</code>
 */

public void cercaDocAttivi(it.cnr.jada.action.ActionContext context) throws it.cnr.jada.action.BusinessProcessException 
{
	ReversaleIBulk reversaleI = (ReversaleIBulk) getModel();	
	try 
	{
		ReversaleComponentSession session = (ReversaleComponentSession) createComponentSession();
		// ReversaleBulk reversale = session.listaDocAttivi( context.getUserContext(), (ReversaleBulk) getModel() );
		reversaleI = (ReversaleIBulk) session.listaDocAttivi( context.getUserContext(), (ReversaleBulk) getModel() );

		setModel( context, reversaleI );
		resyncChildren( context );
	} 
	catch(Exception e) 
	{
		reversaleI.setDocAttiviColl( new ArrayList() )		;
		setModel( context, reversaleI );
		resyncChildren( context );
		throw handleException(e);
	}

	
}
/**
 * Metodo utilizzato per gestire il caricamento dei sospesi associati ad una reversale.
  	 * @param context <code>ActionContext</code> in uso.
	 *
	 * @exception <code>BusinessProcessException</code>
 */

public RemoteIterator cercaSospesi(it.cnr.jada.action.ActionContext context) throws it.cnr.jada.action.BusinessProcessException 
{
	try 
	{
		ReversaleComponentSession session = (ReversaleComponentSession) createComponentSession();
	    return session.cercaSospesi( context.getUserContext(), null, (ReversaleBulk) getModel() );
	} catch(Exception e) {
		throw handleException(e);
	}

	
}
/**
 * Metodo utilizzato per creare una toolbar applicativa personalizzata.
	 * @return toolbar La nuova toolbar creata
 */

protected it.cnr.jada.util.jsp.Button[] createToolbar() {
	Button[] toolbar = new Button[8];
	int i = 0;
	toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()),"CRUDToolbar.search");
	toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()),"CRUDToolbar.startSearch");
	toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()),"CRUDToolbar.freeSearch");
	toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()),"CRUDToolbar.new");
	toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()),"CRUDToolbar.save");
	toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()),"CRUDToolbar.delete");
	toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()),"CRUDToolbar.print");	
	toolbar[i++] = new it.cnr.jada.util.jsp.Button(it.cnr.jada.util.Config.getHandler().getProperties(getClass()),"CRUDToolbar.startSearchSiope");	
	return toolbar;
}
/**
 * Gestisce l'annullamento di una Reversale.
 * @param context contesto dell'azione
 */
public void delete(ActionContext context) throws it.cnr.jada.action.BusinessProcessException {
	int crudStatus = getModel().getCrudStatus();
	try {
			validate(context);
			getModel().setToBeUpdated();
			setModel( context, ((ReversaleComponentSession) createComponentSession()).annullaReversale(context.getUserContext(),(ReversaleBulk)getModel()));
			setStatus(VIEW);			
		} catch(Exception e) {
			getModel().setCrudStatus(crudStatus);
			throw handleException(e);
	}
}
/**
 * Metodo con cui si ottiene il valore della variabile <code>documentiAttivi</code>
 * di tipo <code>SimpleDetailCRUDController</code>.
 * @return it.cnr.jada.util.action.SimpleDetailCRUDController
 */
public final it.cnr.jada.util.action.SimpleDetailCRUDController getDocumentiAttivi() {
	return documentiAttivi;
}
/**
 * Metodo con cui si ottiene il valore della variabile <code>documentiAttiviSelezionati</code>
 * di tipo <code>SimpleDetailCRUDController</code>.
 * @return it.cnr.jada.util.action.SimpleDetailCRUDController
 */
public final it.cnr.jada.util.action.SimpleDetailCRUDController getDocumentiAttiviSelezionati() {
	return documentiAttiviSelezionati;
}
/**
 * @return it.cnr.jada.util.action.SimpleDetailCRUDController
 */
public final it.cnr.jada.util.action.SimpleDetailCRUDController getMandatiRev() {
	return mandatiRev;
}
/**
 * Metodo con cui si ottiene il valore della variabile <code>sospesiSelezionati</code>
 * di tipo <code>CRUDSospesoController</code>.
 * @return it.cnr.contab.doccont00.bp.CRUDSospesoController
 */
public final CRUDSospesoController getSospesiSelezionati() {
	return sospesiSelezionati;
}
/* inizializza il BP delle stampe impostando il nome del report da stampare e i suoi parametri */
protected void initializePrintBP(AbstractPrintBP bp) 
{
	OfflineReportPrintBP printbp = (OfflineReportPrintBP) bp;
	printbp.setReportName("/doccont/doccont/vpg_reversale.jasper");
	ReversaleBulk reversale = (ReversaleBulk)getModel();
	Print_spooler_paramBulk param;

	param = new Print_spooler_paramBulk();
	param.setNomeParam("aCd_cds");
	param.setValoreParam(reversale.getCd_cds());
	param.setParamType("java.lang.String");
	printbp.addToPrintSpoolerParam(param);
	
	param = new Print_spooler_paramBulk();
	param.setNomeParam("aEs");
	param.setValoreParam(reversale.getEsercizio().toString());
	param.setParamType("java.lang.Integer");
	printbp.addToPrintSpoolerParam(param);

	param = new Print_spooler_paramBulk();
	param.setNomeParam("aPg_da");
	param.setValoreParam(reversale.getPg_reversale().toString());
	param.setParamType("java.lang.Long");
	printbp.addToPrintSpoolerParam(param);

	param = new Print_spooler_paramBulk();
	param.setNomeParam("aPg_a");
	param.setValoreParam(reversale.getPg_reversale().toString());
	param.setParamType("java.lang.Long");
	printbp.addToPrintSpoolerParam(param);

	param = new Print_spooler_paramBulk();
	param.setNomeParam("aDt_da");
	param.setValoreParam(OfflineReportPrintBP.DATE_FORMAT.format(reversale.getDt_emissione()));
	param.setParamType("java.util.Date");
	printbp.addToPrintSpoolerParam(param);

	param = new Print_spooler_paramBulk();
	param.setNomeParam("aDt_a");
	param.setValoreParam(OfflineReportPrintBP.DATE_FORMAT.format(reversale.getDt_emissione()));
	param.setParamType("java.util.Date");
	printbp.addToPrintSpoolerParam(param);

	param = new Print_spooler_paramBulk();
	param.setNomeParam("aCd_terzo");
	param.setValoreParam("%");
	param.setParamType("java.lang.String");
	printbp.addToPrintSpoolerParam(param);
}
/**
 * Abilito il bottone di Caricamento dei Sospesi solo se la reversale non � stata incassata o annullata e
 * se non e' di regolarizzazione ne' di incasso
 * @return			TRUE	Il bottone di Caricamento dei Sospesi � abilitato 			
 *					FALSE 	Il bottone di Caricamento dei Sospesi non � abilitato
 */
public boolean isCaricaSospesiButtonEnabled() {
	return
	((ReversaleBulk)getModel()).getTi_reversale() != null && 
	!((ReversaleBulk)getModel()).getTi_reversale().equals(ReversaleBulk.TIPO_REGOLARIZZAZIONE) &&
	!((ReversaleBulk)getModel()).getTi_reversale().equals(ReversaleBulk.TIPO_INCASSO) &&	
/*	!((ReversaleBulk)getModel()).isIncassato() && */
	!((ReversaleBulk)getModel()).isAnnullato();
}
/**
 * Abilito il bottone di Delete solo se la reversale non � stata incassata o annullata.
 * @return			TRUE	Il bottone di Delete � abilitato 			
 *					FALSE 	Il bottone di Delete non � abilitato
 */
public boolean isDeleteButtonEnabled() {
	return (super.isDeleteButtonEnabled() || 
			(!isEditing() && 
			 getModel()!=null && 
			 getModel() instanceof ReversaleBulk && 
			 ((ReversaleBulk)getModel()).getStato_trasmissione() !=null && 
			 !((ReversaleBulk)getModel()).getStato_trasmissione().equals(ReversaleBulk.STATO_TRASMISSIONE_NON_INSERITO))) &&
			//!((ReversaleBulk)getModel()).isIncassato() && 
			!((ReversaleBulk)getModel()).isAnnullato() ;
}
public boolean isPrintButtonHidden() 
{
	return super.isPrintButtonHidden() || isInserting() || isSearching();
}
/**
 * Abilito il tab di Ricerca dei Documenti solo se la reversale non � stata incassata o annullata.
 * @return			TRUE	Il tab di Ricerca dei Documenti � abilitato 			
 *					FALSE 	Il tab di Ricerca dei Documenti non � abilitato
 */
public boolean isRicercaDocumentiTabEnabled() {
	return isEditable() && !((ReversaleBulk)getModel()).isIncassato() && !((ReversaleBulk)getModel()).isAnnullato() ;	
}
/**
 * Abilito il bottone di Rimozione dei Sospesi solo se la reversale non � stata incassata o annullata.
 * @return			TRUE	Il bottone di Rimozione dei Sospesi � abilitato 			
 *					FALSE 	Il bottone di Rimozione dei Sospesi non � abilitato
 */
public boolean isRimuoviSospesiButtonEnabled() {
	return /*!((ReversaleBulk)getModel()).isIncassato() && */
			!((ReversaleBulk)getModel()).isAnnullato() ;
}
/**
 * Abilito il bottone di Salva solo se la reversale non � stata annullata.
 * @return			TRUE	Il bottone di Salva � abilitato 			
 *					FALSE 	Il bottone di Salva non � abilitato
 */
public boolean isSaveButtonEnabled() {
	return (super.isSaveButtonEnabled() && !((ReversaleBulk)getModel()).isAnnullato()) ||
		   (isUoEnte() && ((ReversaleBulk)getModel()) != null && ((ReversaleBulk)getModel()).getCd_uo_origine() != null && 
		    !((ReversaleBulk)getModel()).getCd_uo_origine().equals(getUoSrivania().getCd_unita_organizzativa()) &&
		    ((ReversaleBulk)getModel()).isSiopeDaCompletare()
		    )
;
}
/**
 * Inzializza il ricevente nello stato di SEARCH.
 * @param context <code>ActionContext</code> in uso.
 */
public void resetForSearch(it.cnr.jada.action.ActionContext context) throws it.cnr.jada.action.BusinessProcessException {
	try {
		super.resetForSearch(context);
		setTab("tab","tabReversale");
	} catch(Throwable e) {
		throw new it.cnr.jada.action.BusinessProcessException(e);
	}
}
/**
 * Effettua un salvataggio del modello corrente.
 * Valido solo se il ricevente � nello stato di INSERT o EDIT.
 *
 * @param context <code>ActionContext</code> in uso.
 */
public void save(ActionContext context) throws ValidationException,BusinessProcessException {
	if (getStatus()==CRUDBP.VIEW &&
	    isUoEnte() && 
	    !((ReversaleBulk)getModel()).getCd_uo_origine().equals(getUoSrivania().getCd_unita_organizzativa()) &&
		((ReversaleBulk)getModel()).isSiopeDaCompletare()) {
		setStatus(CRUDBP.EDIT);
		super.save(context);
		setStatus(CRUDBP.VIEW);
	}
	else
		super.save(context);
		
 	this.setTab("tab", "tabReversale");
}
/**
 * Metodo con cui si ottiene il valore della variabile <code>codiciSiopeCollegati</code>
 * di tipo <code>SimpleDetailCRUDController</code>.
 * @return it.cnr.jada.util.action.SimpleDetailCRUDController
 */
public final it.cnr.jada.util.action.SimpleDetailCRUDController getCodiciSiopeCollegati() {
	return codiciSiopeCollegati;
}
/**
 * Metodo con cui si ottiene il valore della variabile <code>codiciSiopeCollegabili</code>
 * di tipo <code>SimpleDetailCRUDController</code>.
 * @return it.cnr.jada.util.action.SimpleDetailCRUDController
 */
public final it.cnr.jada.util.action.SimpleDetailCRUDController getCodiciSiopeCollegabili() {
	return codiciSiopeCollegabili;
}
protected void initialize(ActionContext actioncontext) throws BusinessProcessException {
	super.initialize(actioncontext);
	try {
		setSiope_attiva(Utility.createParametriCnrComponentSession().getParametriCnr(actioncontext.getUserContext(), CNRUserContext.getEsercizio(actioncontext.getUserContext())).getFl_siope().booleanValue());
		setUoSrivania(it.cnr.contab.utenze00.bulk.CNRUserInfo.getUnita_organizzativa(actioncontext));
		
//		if (Utility.createParametriCnrComponentSession().getParametriCnr(actioncontext.getUserContext(), ((ReversaleBulk)this.getModel()).getEsercizio()).getFl_cup().booleanValue() &&
//				Utility.createParametriCnrComponentSession().getParametriCnr(actioncontext.getUserContext(),  ((ReversaleBulk)this.getModel()).getEsercizio()).getFl_siope_cup().booleanValue()){
//			
//			 if (((ReversaleBulk)this.getModel()).getDt_emissione()!=null){
//					Timestamp dataLimite=Utility.createConfigurazioneCnrComponentSession().getDt01(actioncontext.getUserContext(), "DATA_LIMITE_CUP_SIOPE_CUP");
//					if( ((ReversaleBulk)this.getModel()).getDt_emissione().after(dataLimite))
//						setSiope_cup_attivo(Utility.createParametriCnrComponentSession().getParametriCnr(actioncontext.getUserContext(),CNRUserContext.getEsercizio(actioncontext.getUserContext())).getFl_siope_cup().booleanValue());
//					else
//						setCup_attivo(Utility.createParametriCnrComponentSession().getParametriCnr(actioncontext.getUserContext(),CNRUserContext.getEsercizio(actioncontext.getUserContext())).getFl_cup().booleanValue());
//			 }
//		}else{
			setCup_attivo(Utility.createParametriCnrComponentSession().getParametriCnr(actioncontext.getUserContext(),CNRUserContext.getEsercizio(actioncontext.getUserContext())).getFl_cup().booleanValue());
			setSiope_cup_attivo(Utility.createParametriCnrComponentSession().getParametriCnr(actioncontext.getUserContext(),CNRUserContext.getEsercizio(actioncontext.getUserContext())).getFl_siope_cup().booleanValue());
//		}
	}
    catch(Throwable throwable)
    {
        throw new BusinessProcessException(throwable);
    }
}
public boolean isSiope_attiva() {
	return siope_attiva;
}
private void setSiope_attiva(boolean siope_attiva) {
	this.siope_attiva = siope_attiva;
}
public boolean isSiopeBloccante(ActionContext actioncontext) throws BusinessProcessException {
	try{
		return ((ReversaleBulk)getModel()).getUnita_organizzativa().getCd_tipo_unita().equalsIgnoreCase(Tipo_unita_organizzativaHome.TIPO_UO_SAC) || 
		       ((ReversaleBulk)getModel()).getUnita_organizzativa().equalsByPrimaryKey(Utility.createUnita_organizzativaComponentSession().getUoEnte(actioncontext.getUserContext()));
	}catch(it.cnr.jada.comp.ComponentException ex){
		throw handleException(ex);
	}catch(java.rmi.RemoteException ex){
		throw handleException(ex);
	}
}
public boolean isAggiungiRimuoviCodiciSiopeEnabled(){
	return ((ReversaleBulk)getModel())!= null &&
           ((ReversaleBulk)getModel()).getCd_uo_origine() != null && 
           (
            (
             ((ReversaleBulk)getModel()).getCd_uo_origine().equals(getUoSrivania().getCd_unita_organizzativa()) &&
             !isInputReadonly() &&
             getStatus()!= VIEW &&
		     ((ReversaleBulk)getModel()).getStato_trasmissione().equals(ReversaleBulk.STATO_TRASMISSIONE_NON_INSERITO)
		    ) ||
		    (
		     isUoEnte() && 
		     !((ReversaleBulk)getModel()).getCd_uo_origine().equals(getUoSrivania().getCd_unita_organizzativa()) &&
		     ((ReversaleBulk)getModel()).isSiopeDaCompletare()
		    )
		   );
}
public void selezionaRigaSiopeDaCompletare(ActionContext actioncontext) throws it.cnr.jada.action.BusinessProcessException 
{
	ReversaleBulk reversale = (ReversaleBulk)getModel();
	Reversale_rigaBulk rigaDaCompletare=null; 
	if (reversale!=null) { 
		reversale:for (Iterator i=reversale.getReversale_rigaColl().iterator();i.hasNext();){
			Reversale_rigaBulk riga = (Reversale_rigaBulk)i.next();
			if (!riga.getTipoAssociazioneSiope().equals(Reversale_rigaBulk.SIOPE_TOTALMENTE_ASSOCIATO)) {
				rigaDaCompletare=riga;
				break reversale;
			}
		}
	}
	if (rigaDaCompletare != null) {
		documentiAttiviSelezionati.getSelection().setFocus(documentiAttiviSelezionati.getDetails().indexOf(rigaDaCompletare));
		documentiAttiviSelezionati.setModelIndex(actioncontext, documentiAttiviSelezionati.getDetails().indexOf(rigaDaCompletare));
		resyncChildren( actioncontext );		
	}
}
public boolean isSearchSiopeButtonHidden() 
{
	return isSearchButtonHidden() || !isUoEnte();
}
public Unita_organizzativaBulk getUoSrivania() {
	return uoSrivania;
}
public void setUoSrivania(Unita_organizzativaBulk uoSrivania) {
	this.uoSrivania = uoSrivania;
}
public boolean isUoEnte(){
	return (getUoSrivania().getCd_tipo_unita().compareTo(it.cnr.contab.config00.sto.bulk.Tipo_unita_organizzativaHome.TIPO_UO_ENTE)==0);
}
public void impostaSiopeDaCompletare(ActionContext actioncontext) throws it.cnr.jada.action.BusinessProcessException {
	try{
		ReversaleComponentSession session = (ReversaleComponentSession) createComponentSession();
 	    ((ReversaleBulk)getModel()).setSiopeDaCompletare(true);
		setModel(actioncontext, (OggettoBulk)session.setCodiciSIOPECollegabili(actioncontext.getUserContext(), (ReversaleBulk)getModel()));
		if (getMessage() != null)
		   setMessage(getMessage()+" E' consentita l'associazione dei codici SIOPE.");
		else
		   setMessage("E' consentita l'associazione dei codici SIOPE.");
	}catch(it.cnr.jada.comp.ComponentException ex){
		throw handleException(ex);
	}catch(java.rmi.RemoteException ex){
		throw handleException(ex);
	}
}
public boolean isCup_attivo() {
	return cup_attivo;
}
public void setCup_attivo(boolean cup_attivo) {
	this.cup_attivo = cup_attivo;
}
public SimpleDetailCRUDController getCupCollegati() {
	return cupCollegati;
}
private void validateCupCollegati(ActionContext context, OggettoBulk model) throws ValidationException {
	try {
		if (getCupCollegati() != null && getCupCollegati().getModel() != null){
			getCupCollegati().getModel().validate(getModel());
		   completeSearchTools(context, this);
		}
	} catch (BusinessProcessException e) {
		handleException(e);
	} 
	   ReversaleCupBulk bulk =(ReversaleCupBulk)model;
	   java.math.BigDecimal tot_col=java.math.BigDecimal.ZERO;
	   if (bulk!=null && bulk.getReversale_riga()!=null && bulk.getReversale_riga().getReversaleCupColl()!=null && !bulk.getReversale_riga().getReversaleCupColl().isEmpty()){
		if(bulk.getCdCup()==null)
		   throw new ValidationException("Attenzione. Il codice Cup � obbligatorio");
		if(bulk.getImporto()==null)
			   throw new ValidationException("Attenzione. L'importo associato al codice Cup � obbligatorio");
		
		BulkList list=bulk.getReversale_riga().getReversaleCupColl();
		for (Iterator i = list.iterator(); i.hasNext();){
			ReversaleCupBulk l=(ReversaleCupBulk)i.next();
			if(l.getCdCup()!=null){
				if(bulk!=l && bulk.getCdCup().compareTo(l.getCdCup())==0)
					throw new ValidationException("Attenzione. Ogni Cup pu� essere utilizzato una sola volta per ogni riga della reversale. ");
				tot_col=tot_col.add(l.getImporto());
			}
		}
		if(tot_col.compareTo(bulk.getReversale_riga().getIm_reversale_riga())>0)
			throw new ValidationException("Attenzione. Il totale associato al CUP � superiore all'importo della riga della reversale.");
	   }
		
	}
private void validateSiopeCupCollegati(ActionContext context, OggettoBulk model) throws ValidationException {
	try {
		if (getSiopeCupCollegati() != null && getSiopeCupCollegati().getModel() != null){
			getSiopeCupCollegati().getModel().validate(getModel());
		   completeSearchTools(context, this);
		}
	} catch (BusinessProcessException e) {
		handleException(e);
	} 
	
   ReversaleSiopeCupBulk bulk =(ReversaleSiopeCupBulk)model;
   BigDecimal tot_col=BigDecimal.ZERO;
   if (bulk!=null && bulk.getReversaleSiope()!=null && bulk.getReversaleSiope().getReversaleSiopeCupColl()!=null && !bulk.getReversaleSiope().getReversaleSiopeCupColl().isEmpty()){
	if(bulk.getCdCup()==null)
	   throw new ValidationException("Attenzione. Il codice Cup � obbligatorio");
	if(bulk.getImporto()==null)
		   throw new ValidationException("Attenzione. L'importo associato al codice Cup � obbligatorio");
		
	BulkList list=bulk.getReversaleSiope().getReversaleSiopeCupColl();
	for (Iterator i = list.iterator(); i.hasNext();){
		ReversaleSiopeCupBulk l=(ReversaleSiopeCupBulk)i.next();
		if(l.getCdCup()!=null){
			if(bulk!=l && bulk.getCdCup().compareTo(l.getCdCup())==0)
				throw new ValidationException("Attenzione. Ogni Cup pu� essere utilizzato una sola volta per ogni riga della reversale/siope. ");
			tot_col=tot_col.add(l.getImporto());
		}
	}
	if(tot_col.compareTo(bulk.getReversaleSiope().getImporto())>0)
		throw new ValidationException("Attenzione. Il totale associato al CUP � superiore all'importo della riga della reversale associato al siope.");
   }
}


public boolean isSiope_cup_attivo() {
	return siope_cup_attivo;
}

public void setSiope_cup_attivo(boolean siope_cup_attivo) {
	this.siope_cup_attivo = siope_cup_attivo;
}

public SimpleDetailCRUDController getSiopeCupCollegati() {
	return siopeCupCollegati;
}
}
