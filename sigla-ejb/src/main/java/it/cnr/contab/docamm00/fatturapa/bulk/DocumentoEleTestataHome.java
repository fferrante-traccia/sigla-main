/*
 * Created by BulkGenerator 2.0 [07/12/2009]
 * Date 25/02/2015
 */
package it.cnr.contab.docamm00.fatturapa.bulk;
import it.cnr.contab.anagraf00.core.bulk.AnagraficoBulk;
import it.cnr.contab.anagraf00.core.bulk.AnagraficoHome;
import it.cnr.contab.anagraf00.core.bulk.TerzoBulk;
import it.cnr.contab.anagraf00.core.bulk.TerzoHome;
import it.cnr.contab.cmis.service.CMISPath;
import it.cnr.contab.cmis.service.SiglaCMISService;
import it.cnr.contab.config00.bulk.Configurazione_cnrBulk;
import it.cnr.contab.config00.sto.bulk.Tipo_unita_organizzativaHome;
import it.cnr.contab.config00.sto.bulk.UnitaOrganizzativaPecBulk;
import it.cnr.contab.config00.sto.bulk.Unita_organizzativaBulk;
import it.cnr.contab.docamm00.cmis.CMISDocAmmAspect;
import it.cnr.contab.docamm00.service.FatturaPassivaElettronicaService;
import it.cnr.contab.pdd.ws.client.FatturazioneElettronicaClient;
import it.cnr.contab.service.SpringUtil;
import it.cnr.contab.util.StringEncrypter;
import it.cnr.contab.util.Utility;
import it.cnr.contab.util.StringEncrypter.EncryptionException;
import it.cnr.jada.UserContext;
import it.cnr.jada.bulk.BulkHome;
import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.comp.ApplicationException;
import it.cnr.jada.comp.ComponentException;
import it.cnr.jada.persistency.PersistencyException;
import it.cnr.jada.persistency.PersistentCache;
import it.cnr.jada.persistency.sql.CompoundFindClause;
import it.cnr.jada.persistency.sql.FindClause;
import it.cnr.jada.persistency.sql.SQLBuilder;
import it.gov.fatturapa.sdi.messaggi.v1.EsitoCommittenteType;
import it.gov.fatturapa.sdi.messaggi.v1.NotificaEsitoCommittenteType;
import it.gov.fatturapa.sdi.messaggi.v1.RiferimentoFatturaType;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIType;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.ObjectFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.ejb.EJBException;
import javax.mail.AuthenticationFailedException;
import javax.mail.PasswordAuthentication;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis2.builder.unknowncontent.InputStreamDataSource;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DocumentoEleTestataHome extends BulkHome {
	private transient final static Logger logger = LoggerFactory.getLogger(DocumentoEleTestataHome.class);

	private static final long serialVersionUID = 1L;
	public DocumentoEleTestataHome(Connection conn) {
		super(DocumentoEleTestataBulk.class, conn);
	}
	public DocumentoEleTestataHome(Connection conn, PersistentCache persistentCache) {
		super(DocumentoEleTestataBulk.class, conn, persistentCache);
	}
	
	public SQLBuilder selectOptionsByClause(String s, OggettoBulk oggettobulk, BulkHome bulkhome, OggettoBulk oggettobulk1, CompoundFindClause compoundfindclause)
			throws InvocationTargetException, IllegalAccessException,
			PersistencyException {
		DocumentoEleTestataBulk testata = (DocumentoEleTestataBulk)oggettobulk;
		java.sql.Timestamp dataRic = testata.getDocumentoEleTrasmissione().getDataRicezione();
		java.util.GregorianCalendar dataRicGregorian = (java.util.GregorianCalendar) java.util.GregorianCalendar
				.getInstance();
		dataRicGregorian.setTime(dataRic== null ? new Date() : dataRic);		
		if (s.equalsIgnoreCase("documentoEleTrasmissione.prestatore"))
			return selectTerzoForCFIVA(testata, (TerzoBulk)oggettobulk1, compoundfindclause, 
					testata.getDocumentoEleTrasmissione().getPrestatoreCodicefiscale(), 
					testata.getDocumentoEleTrasmissione().getPrestatoreCodice());
		else if (s.equalsIgnoreCase("documentoEleTrasmissione.prestatoreAnag"))
			return selectAnagraficoForCFIVA(testata, (AnagraficoBulk)oggettobulk1, compoundfindclause, 
					testata.getDocumentoEleTrasmissione().getPrestatoreCodicefiscale(), 
					testata.getDocumentoEleTrasmissione().getPrestatoreCodice());
		if (s.equalsIgnoreCase("documentoEleTrasmissione.intermediario"))
			return selectTerzoForCFIVA(testata, (TerzoBulk)oggettobulk1, compoundfindclause, 
					testata.getDocumentoEleTrasmissione().getIntermediarioCodicefiscale(), 
					testata.getDocumentoEleTrasmissione().getIntermediarioCodice());
		else if (s.equalsIgnoreCase("documentoEleTrasmissione.intermediarioAnag"))
			return selectAnagraficoForCFIVA(testata, (AnagraficoBulk)oggettobulk1, compoundfindclause, 
					testata.getDocumentoEleTrasmissione().getIntermediarioCodicefiscale(), 
					testata.getDocumentoEleTrasmissione().getIntermediarioCodice());
		if (s.equalsIgnoreCase("documentoEleTrasmissione.rappresentante"))
			return selectTerzoForCFIVA(testata, (TerzoBulk)oggettobulk1, compoundfindclause, 
					testata.getDocumentoEleTrasmissione().getRappresentanteCodicefiscale(), 
					testata.getDocumentoEleTrasmissione().getRappresentanteCodice());
		else if (s.equalsIgnoreCase("documentoEleTrasmissione.rappresentanteAnag"))
			return selectAnagraficoForCFIVA(testata, (AnagraficoBulk)oggettobulk1, compoundfindclause, 
					testata.getDocumentoEleTrasmissione().getRappresentanteCodicefiscale(), 
					testata.getDocumentoEleTrasmissione().getRappresentanteCodice());
		else if (s.equalsIgnoreCase("documentoEleTrasmissione.unitaCompetenza")){
			SQLBuilder sql = getHomeCache().getHome(Unita_organizzativaBulk.class, "V_UNITA_ORGANIZZATIVA_VALIDA").selectByClause(compoundfindclause);
			sql.addSQLClause("AND", "ESERCIZIO", SQLBuilder.EQUALS, dataRicGregorian.get(java.util.GregorianCalendar.YEAR));
			return sql;
		}else
			return bulkhome.selectByClause(compoundfindclause);
	}

	public SQLBuilder selectAnagraficoForCFIVA(DocumentoEleTestataBulk testata, 
			AnagraficoBulk anagrafico, CompoundFindClause clauses, String codiceFiscale, String partitaIVA) throws PersistencyException {
		AnagraficoHome anagraficoHome = (AnagraficoHome)getHomeCache().getHome(AnagraficoBulk.class);
    	SQLBuilder sql = anagraficoHome.selectByClause(clauses);
        
    	sql.openParenthesis(FindClause.AND);
    	sql.addClause(FindClause.OR, "dt_fine_rapporto", SQLBuilder.ISNULL, null);
    	sql.addClause(FindClause.OR, "dt_fine_rapporto", SQLBuilder.GREATER, it.cnr.jada.util.ejb.EJBCommonServices.getServerDate());
		sql.closeParenthesis();

		sql.openParenthesis(FindClause.AND);
		if (codiceFiscale != null)
			sql.addSQLClause(FindClause.AND, "codice_fiscale", SQLBuilder.EQUALS, codiceFiscale);
		if (partitaIVA != null)
			sql.addSQLClause(FindClause.OR, "partita_iva", SQLBuilder.EQUALS, partitaIVA);  
		sql.closeParenthesis();
        return sql;
    }	

	public SQLBuilder selectTerzoForCFIVA(DocumentoEleTestataBulk testata, 
    		TerzoBulk terzo, CompoundFindClause clauses, String codiceFiscale, String partitaIVA) throws PersistencyException {
    	TerzoHome terzoHome = (TerzoHome)getHomeCache().getHome(TerzoBulk.class);
    	SQLBuilder sql = terzoHome.selectByClause(clauses);

    	sql.openParenthesis(FindClause.AND);
    	sql.addClause(FindClause.OR, "dt_fine_rapporto", SQLBuilder.ISNULL, null);
    	sql.addClause(FindClause.OR, "dt_fine_rapporto", SQLBuilder.GREATER, it.cnr.jada.util.ejb.EJBCommonServices.getServerDate());
		sql.closeParenthesis();

		sql.openParenthesis(FindClause.AND);
		if (codiceFiscale != null)
			sql.addSQLClause(FindClause.AND, "CODICE_FISCALE_ANAGRAFICO", SQLBuilder.EQUALS, codiceFiscale);
		if (partitaIVA != null)
			sql.addSQLClause(FindClause.OR, "PARTITA_IVA_ANAGRAFICO", SQLBuilder.EQUALS, partitaIVA);  
		sql.closeParenthesis();
        return sql;
    }	
	
	public PasswordAuthentication getAuthenticatorPecSdi(UserContext userContext) throws ComponentException {
		Configurazione_cnrBulk email;
		try {
			email = Utility.createConfigurazioneCnrComponentSession().getConfigurazione(userContext, new Integer(0),null,Configurazione_cnrBulk.PK_EMAIL_PEC, Configurazione_cnrBulk.SK_SDI);
		} catch (RemoteException e) {
			throw new ApplicationException(e);
		} catch (EJBException e) {
			throw new ApplicationException(e);
		}

		if (email != null)
			try {
				String password = StringEncrypter.decrypt(email.getVal01(), email.getVal02());
				return new PasswordAuthentication(email.getVal01(), password);	
			} catch (EncryptionException e1) {
				new AuthenticationFailedException("Cannot decrypt password");
			}
		throw new ApplicationException("Confiurazione PEC non trovata, contattare il servizio di HelpDesk!");
	}
	public NotificaEsitoCommittenteType createNotificaEsitoCommittente(DocumentoEleTestataBulk documentoEleTestataBulk) {
    	it.gov.fatturapa.sdi.messaggi.v1.ObjectFactory objMessaggi = new it.gov.fatturapa.sdi.messaggi.v1.ObjectFactory();        	
    	NotificaEsitoCommittenteType notificaEsitoCommittenteType = objMessaggi.createNotificaEsitoCommittenteType();
    	notificaEsitoCommittenteType.setIdentificativoSdI(BigInteger.valueOf(documentoEleTestataBulk.getIdentificativoSdi()));
    	notificaEsitoCommittenteType.setVersione("1.0");
    	RiferimentoFatturaType riferimentoFatturaType = objMessaggi.createRiferimentoFatturaType();
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(documentoEleTestataBulk.getDataDocumento().getTime());
    	riferimentoFatturaType.setAnnoFattura(BigInteger.valueOf(cal.get(Calendar.YEAR)));
    	riferimentoFatturaType.setNumeroFattura(documentoEleTestataBulk.getNumeroDocumento());     
    	riferimentoFatturaType.setPosizioneFattura(BigInteger.valueOf(documentoEleTestataBulk.getProgressivo() + 1));   	
    	notificaEsitoCommittenteType.setRiferimentoFattura(riferimentoFatturaType);
    	notificaEsitoCommittenteType.setEsito(documentoEleTestataBulk.getStatoDocumentoEle().equals(StatoDocumentoEleEnum.RIFIUTATO)?EsitoCommittenteType.EC_02: EsitoCommittenteType.EC_01);
    	notificaEsitoCommittenteType.setMessageIdCommittente(documentoEleTestataBulk.getStatoDocumentoEle().name());        	
    	notificaEsitoCommittenteType.setDescrizione(documentoEleTestataBulk.getStatoDocumentoEle().equals(StatoDocumentoEleEnum.RIFIUTATO)?
    			documentoEleTestataBulk.getMotivoRifiuto():documentoEleTestataBulk.getStatoDocumentoEle().name());
    	return notificaEsitoCommittenteType;
	}

	public void storeEsitoDocument(DocumentoEleTestataBulk documentoEleTestata,  ByteArrayInputStream byteArrayInputStream, String aspect) throws ApplicationException {
		SiglaCMISService cmisService = SpringUtil.getBean("cmisService", SiglaCMISService.class);
		Map<String, Object> metadataProperties = new HashMap<String, Object>();
		metadataProperties.put(PropertyIds.OBJECT_TYPE_ID, "D:sigla_fatture_attachment:document");
		metadataProperties.put(PropertyIds.NAME, documentoEleTestata.getNomeFile("EC"));
		metadataProperties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, 
				Arrays.asList("P:sigla_commons_aspect:utente_applicativo_sigla", aspect));
		metadataProperties.put("sigla_commons_aspect:utente_applicativo", "SDI");
		try {
			cmisService.storeSimpleDocument(byteArrayInputStream, "text/xml", 
					CMISPath.construct(((Folder)cmisService.getNodeByNodeRef(
							documentoEleTestata.getDocumentoEleTrasmissione().getCmisNodeRef())).getPath()), 
					metadataProperties);
		} catch(CmisContentAlreadyExistsException _ex) {
		}
	}

	public void notificaEsito(UserContext userContext, TipoIntegrazioneSDI tipoIntegrazioneSDI, DocumentoEleTestataBulk documentoEleTestataBulk) throws ApplicationException, IOException {
		if (documentoEleTestataBulk.isRicevutaDecorrenzaTermini()) {
			logger.info("Notifica esito per identificativo SDI:"+documentoEleTestataBulk.getIdentificativoSdi()+" non inviata per avvenuta decorrenza termini!");
			return;
		}
    	FatturazioneElettronicaClient client = SpringUtil.getBean("fatturazioneElettronicaClient", 
    			FatturazioneElettronicaClient.class);
    	FatturaPassivaElettronicaService fatturaService = SpringUtil.getBean("fatturaPassivaElettronicaService", 
    			FatturaPassivaElettronicaService.class);

    	NotificaEsitoCommittenteType notificaEsitoCommittenteType = 
    			this.createNotificaEsitoCommittente(documentoEleTestataBulk);
    	if (tipoIntegrazioneSDI.equals(TipoIntegrazioneSDI.PEC)) {
    		try {
            	it.gov.fatturapa.sdi.messaggi.v1.ObjectFactory objMessaggi = new it.gov.fatturapa.sdi.messaggi.v1.ObjectFactory();        	
            	JAXBElement<NotificaEsitoCommittenteType> notificaEsitoCommittente = objMessaggi.
            			createNotificaEsitoCommittente(notificaEsitoCommittenteType);
            	ByteArrayOutputStream outputStreamNotificaEsito = new ByteArrayOutputStream();
            	client.getMarshaller().marshal(notificaEsitoCommittente, new StreamResult(outputStreamNotificaEsito));
            	PasswordAuthentication authentication = getAuthenticatorPecSdi(userContext);
            	if (authentication == null) {
        			logger.error("Errore applicativo durante la Notifica di Esito Committente, contattare il servizio di HelpDesk!");
            		throw new ApplicationException("Errore applicativo durante la Notifica di Esito Committente, contattare il servizio di HelpDesk!");
            	}
            	storeEsitoDocument(documentoEleTestataBulk, new ByteArrayInputStream(outputStreamNotificaEsito.toByteArray()), 
            			documentoEleTestataBulk.getStatoDocumentoEle().equals(StatoDocumentoEleEnum.RIFIUTATO)?
            					CMISDocAmmAspect.SIGLA_FATTURE_ATTACHMENT_ESITO_RIFIUTATO.value():
            						CMISDocAmmAspect.SIGLA_FATTURE_ATTACHMENT_ESITO_ACCETTATO.value());
            	fatturaService.notificaEsito(authentication.getUserName(), authentication.getPassword(), 
            			documentoEleTestataBulk, notificaEsitoCommittente);
    		} catch(ApplicationException _ex) {
        		throw _ex;
    		} catch(Exception _ex) {
    			logger.error("Errore applicativo durante la Notifica di Esito Committente, contattare il servizio di HelpDesk!" + _ex.getMessage(), _ex.getCause());
    			throw new ApplicationException("Errore applicativo durante la Notifica di Esito Committente, contattare il servizio di HelpDesk!\n" + _ex.getMessage(), _ex.getCause());
    		}
    	} else if (!tipoIntegrazioneSDI.equals(TipoIntegrazioneSDI.PEC)) {
        	ObjectFactory obj = new ObjectFactory();
        	FileSdIType fileSdIType = obj.createFileSdIType();
        	fileSdIType.setIdentificativoSdI(BigInteger.valueOf(documentoEleTestataBulk.getIdentificativoSdi()));
        	fileSdIType.setNomeFile(documentoEleTestataBulk.getNomeFile("EC"));
        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();        	
        	client.getMarshaller().marshal(notificaEsitoCommittenteType, new StreamResult(outputStream));
        	ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        	fileSdIType.setFile(new DataHandler(new InputStreamDataSource(inputStream)));
        	JAXBElement<FileSdIType> notificaEsito = obj.createNotificaEsito(fileSdIType);
        	ByteArrayOutputStream outputStreamNotificaEsito = new ByteArrayOutputStream();
        	client.getMarshaller().marshal(notificaEsito, new StreamResult(outputStreamNotificaEsito));
        	storeEsitoDocument(documentoEleTestataBulk, new ByteArrayInputStream(outputStreamNotificaEsito.toByteArray()), 
        			documentoEleTestataBulk.getStatoDocumentoEle().equals(StatoDocumentoEleEnum.RIFIUTATO)?
        					CMISDocAmmAspect.SIGLA_FATTURE_ATTACHMENT_ESITO_RIFIUTATO.value():
        						CMISDocAmmAspect.SIGLA_FATTURE_ATTACHMENT_ESITO_ACCETTATO.value());        		
    	}
		
	}	
}