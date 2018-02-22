package it.cnr.contab.config00.action;

import it.cnr.contab.config00.pdcep.bulk.ContoBulk;
import it.cnr.jada.action.*;
/**
 * Azione che gestisce le richieste relative alla Gestione Conti Economici Patrimoniali
 */
public class CRUDContoAction extends it.cnr.jada.util.action.CRUDAction {
/**
 * CRUDContoAction constructor comment.
 */
public CRUDContoAction() {
	super();
}
/**
	 * Metodo utilizzato per gestire la conferma dell'inserimento/modifica di un conto con un gruppo
	 * e una natura non congrui
	 * @param context <code>ActionContext</code> in uso.
	 * @param option Esito della risposta alla richiesta di sostituzione.
	 *
	 * @return <code>Forward</code>
	 *
	 * @exception <code>RemoteException</code>
	 *
	 */

public Forward doConfermaGruppoNaturaNonCongrui(ActionContext context,int option)  throws java.rmi.RemoteException 
{
	if (option == it.cnr.jada.util.action.OptionBP.YES_BUTTON) 
	{
		try 
		{
			((ContoBulk) getBusinessProcess(context).getModel()).setFl_gruppoNaturaNonCongruiConfermati( true );			
//			getBusinessProcess(context).setStatus(it.cnr.jada.util.action.CRUDBP.EDIT);
			getBusinessProcess(context).save(context);
			((ContoBulk) getBusinessProcess(context).getModel()).setFl_gruppoNaturaNonCongruiConfermati( false );			
		} 
		catch(Throwable e) 
		{
			return handleException(context,e);
		}
	}
		return context.findDefaultForward();
}
	/**
	 * Metodo utilizzato per gestire l'eccezione generata dalla non associabilità fra il gruppo del 
	 * conto e la sua natura
	 *
	 * @param context <code>ActionContext</code> in uso.
	 * @param ex Eccezione da gestire.
	 *
	 * @return <code>Forward</code>
	 *
	 * @exception <code>RemoteException</code>
	 *
	 */

	public Forward handleException(ActionContext context, Throwable ex) {
		try {
			throw ex;
		} catch(it.cnr.contab.config00.comp.GruppoNaturaNonCongrui e) {
			try {
				String message = "Il gruppo del Capoconto e la Natura specificata non sono congrui.\n"
								+ "Vuoi continuare?";
				return openConfirm(context, message, it.cnr.jada.util.action.OptionBP.CONFIRM_YES_NO, "doConfermaGruppoNaturaNonCongrui");
			} catch(BusinessProcessException e2) {
				return handleException(context,e2);
			}
		} catch(Throwable e) {
			return super.handleException(context,e);
		}
	}
}
