package it.cnr.contab.doccont00.service;

import com.google.gson.GsonBuilder;
import it.cnr.contab.doccont00.core.bulk.MandatoBulk;
import it.cnr.contab.doccont00.core.bulk.MandatoIBulk;
import it.cnr.contab.doccont00.core.bulk.ReversaleBulk;
import it.cnr.contab.doccont00.core.bulk.ReversaleIBulk;
import it.cnr.contab.doccont00.ejb.MandatoComponentSession;
import it.cnr.contab.doccont00.ejb.ReversaleComponentSession;
import it.cnr.contab.doccont00.intcass.bulk.Distinta_cassiereBulk;
import it.cnr.contab.doccont00.intcass.bulk.StatoTrasmissione;
import it.cnr.contab.model.Lista;
import it.cnr.contab.model.MessaggioXML;
import it.cnr.contab.model.Risultato;
import it.cnr.contab.service.OrdinativiSiopePlusService;
import it.cnr.contab.utenze00.bp.WSUserContext;
import it.cnr.contab.utenze00.bulk.UtenteBulk;
import it.cnr.contab.util.ApplicationMessageFormatException;
import it.cnr.contab.util.PdfSignApparence;
import it.cnr.contab.util.SIGLAStoragePropertyNames;
import it.cnr.contab.util.SignP7M;
import it.cnr.jada.DetailedRuntimeException;
import it.cnr.jada.UserContext;
import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.comp.ApplicationException;
import it.cnr.jada.comp.ComponentException;
import it.cnr.jada.ejb.CRUDComponentSession;
import it.cnr.jada.firma.arss.ArubaSignServiceClient;
import it.cnr.jada.firma.arss.ArubaSignServiceException;
import it.cnr.jada.util.ejb.EJBCommonServices;
import it.cnr.jada.util.mail.SimplePECMail;
import it.cnr.si.spring.storage.*;
import it.cnr.si.spring.storage.bulk.StorageFile;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import it.siopeplus.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentiContabiliService extends StoreService implements InitializingBean {
    private transient static final Logger logger = LoggerFactory.getLogger(DocumentiContabiliService.class);
    final String pattern = "dd MMMM YYYY' alle 'HH:mm:ss";
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    @Autowired
    private StorageService storageService;
    @Autowired
    private OrdinativiSiopePlusService ordinativiSiopePlusService;
    @Value("${sign.document.png.url}")
    private String signDocumentURL;
    private ArubaSignServiceClient arubaSignServiceClient;
    private String pecHostName, pecMailFromBanca, pecMailFromBancaPassword, pecMailToBancaNoEuroSepa, pecMailToBancaItaliaF23F24;

    private CRUDComponentSession crudComponentSession;
    private MandatoComponentSession mandatoComponentSession;
    private ReversaleComponentSession reversaleComponentSession;

    private UserContext userContext;
    private List<Risultato> risultatiACK = new ArrayList<Risultato>();
    private List<Risultato> risultatiEsito = new ArrayList<Risultato>();
    private List<Risultato> risultatiEsitoApplicativo = new ArrayList<Risultato>();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.userContext = new WSUserContext("SIOPEPLUS", null,
                new Integer(Calendar.getInstance().get(Calendar.YEAR)),
                null, null, null);
        this.crudComponentSession = Optional.ofNullable(EJBCommonServices.createEJB("JADAEJB_CRUDComponentSession"))
                .filter(CRUDComponentSession.class::isInstance)
                .map(CRUDComponentSession.class::cast)
                .orElseThrow(() -> new DetailedRuntimeException("cannot find ejb JADAEJB_CRUDComponentSession"));
        this.mandatoComponentSession = Optional.ofNullable(EJBCommonServices.createEJB("CNRDOCCONT00_EJB_MandatoComponentSession"))
                .filter(MandatoComponentSession.class::isInstance)
                .map(MandatoComponentSession.class::cast)
                .orElseThrow(() -> new DetailedRuntimeException("cannot find ejb CNRDOCCONT00_EJB_MandatoComponentSession"));
        this.reversaleComponentSession = Optional.ofNullable(EJBCommonServices.createEJB("CNRDOCCONT00_EJB_ReversaleComponentSession"))
                .filter(ReversaleComponentSession.class::isInstance)
                .map(ReversaleComponentSession.class::cast)
                .orElseThrow(() -> new DetailedRuntimeException("cannot find ejb CNRDOCCONT00_EJB_ReversaleComponentSession"));
    }

    public ArubaSignServiceClient getArubaSignServiceClient() {
        return arubaSignServiceClient;
    }

    public void setArubaSignServiceClient(
            ArubaSignServiceClient arubaSignServiceClient) {
        this.arubaSignServiceClient = arubaSignServiceClient;
    }

    public String getPecHostName() {
        return pecHostName;
    }

    public void setPecHostName(String pecHostName) {
        this.pecHostName = pecHostName;
    }

    public String getPecMailFromBanca() {
        return pecMailFromBanca;
    }

    public void setPecMailFromBanca(String pecMailFromBanca) {
        this.pecMailFromBanca = pecMailFromBanca;
    }

    public String getPecMailFromBancaPassword() {
        return pecMailFromBancaPassword;
    }

    public void setPecMailFromBancaPassword(String pecMailFromBancaPassword) {
        this.pecMailFromBancaPassword = pecMailFromBancaPassword;
    }

    public String getPecMailToBancaNoEuroSepa() {
        return pecMailToBancaNoEuroSepa;
    }

    public void setPecMailToBancaNoEuroSepa(String pecMailToBancaNoEuroSepa) {
        this.pecMailToBancaNoEuroSepa = pecMailToBancaNoEuroSepa;
    }

    public String getPecMailToBancaItaliaF23F24() {
        return pecMailToBancaItaliaF23F24;
    }

    public void setPecMailToBancaItaliaF23F24(String pecMailToBancaItaliaF23F24) {
        this.pecMailToBancaItaliaF23F24 = pecMailToBancaItaliaF23F24;
    }

    public String getDocumentKey(StatoTrasmissione bulk) {
        return getDocumentKey(bulk, false);
    }

    public String getDocumentKey(StatoTrasmissione bulk, boolean fullNodeRef) {
        return Optional.ofNullable(bulk)
                .map(statoTrasmissione -> Optional.ofNullable(getStorageObjectByPath(statoTrasmissione.getStorePath()
                        .concat(StorageService.SUFFIX)
                        .concat(statoTrasmissione.getCMISName())))
                        .map(storageObject ->
                                fullNodeRef ? Optional.ofNullable(storageObject.getPropertyValue(StoragePropertyNames.ALFCMIS_NODEREF.value()))
                                        .map(String.class::cast)
                                        .orElse(storageObject.getKey()) : storageObject.getKey())
                        .orElse(null)).orElse(null);
    }

    public InputStream getStreamDocumento(StatoTrasmissione bulk) {
        return Optional.ofNullable(getStorageObjectByPath(bulk.getStorePath().concat(StorageService.SUFFIX).concat(bulk.getCMISName())))
                .map(StorageObject::getKey)
                .map(key -> getResource(key))
                .orElse(null);
    }

    public Map<String, String> getCertSubjectDN(String username, String password) throws Exception {
        Map<String, String> result = new HashMap<String, String>();
        Principal principal = arubaSignServiceClient.getUserCertSubjectDN(username, password);
        if (principal == null)
            return null;
        String[] subjectArray = principal.toString().split(",");
        for (String s : subjectArray) {
            String[] str = s.trim().split("=");
            String key = str[0];
            String value = str[1];
            result.put(key, value);
        }
        return result;
    }

    public void controllaCodiceFiscale(Map<String, String> subjectDN, UtenteBulk utente) throws Exception {
        String codiceFiscale = subjectDN.get("SERIALNUMBER");
        if (Optional.ofNullable(utente.getCodiceFiscaleLDAP())
                .map(codiceFiscaleLDAP -> !codiceFiscale.contains(codiceFiscaleLDAP))
                .orElse(Boolean.FALSE)) {
            throw new ApplicationException("Il codice fiscale \"" + codiceFiscale + "\" presente sul certicato di Firma, " +
                    "è diverso da quello dell'utente collegato \"" + utente.getCodiceFiscaleLDAP() + "\"!");
        }
    }

    public void controllaCodiceFiscale(String username, String password, UtenteBulk utente) throws Exception {
        Map<String, String> subjectDN = Optional.ofNullable(getCertSubjectDN(username, password))
                .orElseThrow(() -> new ApplicationException("Errore nella lettura dei certificati!\nVerificare Nome Utente e Password!"));
        controllaCodiceFiscale(subjectDN, utente);
    }

    public void inviaDistintaPEC1210(List<String> nodes) throws EmailException, ApplicationException, IOException {
        inviaDistintaPEC1210(nodes, true, null);
    }

    public void inviaDistintaPEC1210(List<String> nodes, boolean isNoEuroOrSepa, String nrDistinta) throws EmailException, ApplicationException, IOException {
        // Create the email message
        SimplePECMail email = new SimplePECMail(pecMailFromBanca, pecMailFromBancaPassword);
        email.setHostName(pecHostName);
        if (isNoEuroOrSepa) {
            if (pecMailToBancaNoEuroSepa != null && pecMailToBancaNoEuroSepa.split(";").length != 0) {
                email.addTo(pecMailToBancaNoEuroSepa.split(";"));
            } else {
                email.addTo(pecMailToBancaNoEuroSepa, pecMailToBancaNoEuroSepa);
            }
        } else {
            if (pecMailToBancaItaliaF23F24 != null && pecMailToBancaItaliaF23F24.split(";").length != 0) {
                email.addTo(pecMailToBancaItaliaF23F24.split(";"));
            } else {
                email.addTo(pecMailToBancaItaliaF23F24, pecMailToBancaItaliaF23F24);
            }
        }

        email.setFrom(pecMailFromBanca, pecMailFromBanca);
        if (nrDistinta != null)
            email.setSubject("Invio Distinta 1210 " + nrDistinta);
        else
            email.setSubject("Invio Distinta 1210");

        // add the attachment
        for (String key : nodes) {
            StorageObject storageObject = getStorageObjectBykey(key);
            StorageDataSource dataSource = new StorageDataSource(storageObject);
            email.attach(dataSource, dataSource.getName(), "", EmailAttachment.ATTACHMENT);
        }
        // send the email
        email.send();
        logger.debug("Inviata distinta PEC");
    }

    public void inviaDistintaPEC(List<String> nodes, boolean isNoEuroOrSepa, String nrDistinta) throws EmailException, ApplicationException, IOException {
        // Create the email message
        SimplePECMail email = new SimplePECMail(pecMailFromBanca, pecMailFromBancaPassword);
        email.setHostName(pecHostName);

        if (isNoEuroOrSepa) {
            if (pecMailToBancaNoEuroSepa != null && pecMailToBancaNoEuroSepa.split(";").length != 0) {
                email.addTo(pecMailToBancaNoEuroSepa.split(";"));
            } else {
                email.addTo(pecMailToBancaNoEuroSepa, pecMailToBancaNoEuroSepa);
            }
        } else {
            if (pecMailToBancaItaliaF23F24 != null && pecMailToBancaItaliaF23F24.split(";").length != 0) {
                email.addTo(pecMailToBancaItaliaF23F24.split(";"));
            } else {
                email.addTo(pecMailToBancaItaliaF23F24, pecMailToBancaItaliaF23F24);
            }
        }
        email.setFrom(pecMailFromBanca, pecMailFromBanca);
        if (nrDistinta != null)
            email.setSubject("Invio Distinta " + nrDistinta + " e Documenti");
        else
            email.setSubject("Invio Distinta e Documenti");
        email.setMsg("In allegato i documenti");
        // add the attachment
        for (String key : nodes) {
            StorageObject storageObject = getStorageObjectBykey(key);
            StorageDataSource dataSource = new StorageDataSource(storageObject);
            email.attach(dataSource, dataSource.getName(), "", EmailAttachment.ATTACHMENT);
        }
        // send the email
        email.send();
        logger.debug("Inviata distinta PEC");
    }

    public Set<String> getAllegatoForModPag(String key, String modPag) {
        return getChildren(key).stream()
                .filter(storageObject -> modPag.equals(storageObject.getPropertyValue("doccont:rif_modalita_pagamento")))
                .map(StorageObject::getKey)
                .collect(Collectors.toSet());
    }

    public String signDocuments(PdfSignApparence pdfSignApparence, String url) throws StorageException {
        if (storageService.getStoreType().equals(StorageService.StoreType.CMIS)) {
            return signDocuments(new GsonBuilder().create().toJson(pdfSignApparence), url);
        } else {
            List<byte[]> bytes = Optional.ofNullable(pdfSignApparence)
                    .map(pdfSignApparence1 -> pdfSignApparence1.getNodes())
                    .map(list ->
                            list.stream()
                                    .map(s -> storageService.getInputStream(s))
                                    .map(inputStream -> {
                                        try {
                                            return IOUtils.toByteArray(inputStream);
                                        } catch (IOException e) {
                                            throw new StorageException(StorageException.Type.GENERIC, e);
                                        }
                                    })
                                    .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());
            try {
                it.cnr.jada.firma.arss.stub.PdfSignApparence apparence = new it.cnr.jada.firma.arss.stub.PdfSignApparence();
                apparence.setImage(signDocumentURL);
                apparence.setLeftx(pdfSignApparence.getApparence().getLeftx());
                apparence.setLefty(pdfSignApparence.getApparence().getLefty());
                apparence.setLocation(pdfSignApparence.getApparence().getLocation());
                apparence.setPage(pdfSignApparence.getApparence().getPage());
                apparence.setReason(pdfSignApparence.getApparence().getReason());
                apparence.setRightx(pdfSignApparence.getApparence().getRightx());
                apparence.setRighty(pdfSignApparence.getApparence().getRighty());
                apparence.setTesto(pdfSignApparence.getApparence().getTesto());

                List<byte[]> bytesSigned = arubaSignServiceClient.pdfsignatureV2Multiple(
                        pdfSignApparence.getUsername(),
                        pdfSignApparence.getPassword(),
                        pdfSignApparence.getOtp(),
                        bytes,
                        apparence
                );
                for (int i = 0; i < pdfSignApparence.getNodes().size(); i++) {
                    storageService.updateStream(
                            pdfSignApparence.getNodes().get(i),
                            new ByteArrayInputStream(bytesSigned.get(i)),
                            MimeTypes.PDF.mimetype()
                    );
                }

            } catch (ArubaSignServiceException e) {
                throw new StorageException(StorageException.Type.GENERIC, e);
            }
            return null;
        }
    }

    public String signDocuments(SignP7M signP7M, String url) throws StorageException {
        if (storageService.getStoreType().equals(StorageService.StoreType.CMIS)) {
            return signDocuments(new GsonBuilder().create().toJson(signP7M), url);
        } else {
            StorageObject storageObject = storageService.getObject(signP7M.getNodeRefSource());
            try {
                final byte[] bytes = arubaSignServiceClient.pkcs7SignV2(
                        signP7M.getUsername(),
                        signP7M.getPassword(),
                        signP7M.getOtp(),
                        IOUtils.toByteArray(storageService.getInputStream(signP7M.getNodeRefSource())));
                Map<String, Object> metadataProperties = new HashMap<>();
                metadataProperties.put(StoragePropertyNames.NAME.value(), signP7M.getNomeFile());
                metadataProperties.put(StoragePropertyNames.OBJECT_TYPE_ID.value(), SIGLAStoragePropertyNames.CNR_ENVELOPEDDOCUMENT.value());

                return storeSimpleDocument(
                        new ByteArrayInputStream(bytes),
                        MimeTypes.P7M.mimetype(),
                        storageObject.getPath().substring(0, storageObject.getPath().lastIndexOf(StorageService.SUFFIX) + 1),
                        metadataProperties).getKey();
            } catch (ArubaSignServiceException | IOException e) {
                throw new StorageException(StorageException.Type.GENERIC, e);
            } finally {
                List<String> aspects = storageObject.getPropertyValue(StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value());
                aspects.add("P:cnr:signedDocument");
                updateProperties(Collections.singletonMap(
                        StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value(),
                        aspects
                ), storageObject);
            }
        }
    }

    private String signDocuments(String json, String url) throws StorageException {
        return storageService.signDocuments(json, url);
    }

    private Distinta_cassiereBulk fetchDistinta_cassiereBulk(String identificativoFlusso) throws RemoteException, ComponentException {
        Distinta_cassiereBulk distinta = Distinta_cassiereBulk.fromIdentificativoFlusso(identificativoFlusso);
        final List<Distinta_cassiereBulk> findDistintaCasserie = crudComponentSession.find(userContext, Distinta_cassiereBulk.class, "findDistintaCasserie", distinta);
        return findDistintaCasserie.stream()
                .findFirst()
                .orElseThrow(() -> new ApplicationMessageFormatException("Distinta non trovata per identificativo flusso: {}", identificativoFlusso));
    }

    private MandatoBulk fetchMandatoBulk(CtEsitoMandati ctEsitoMandati) {
        MandatoBulk mandato = new MandatoIBulk();
        mandato.setEsercizio(ctEsitoMandati.getEsercizio());
        mandato.setPg_mandato(ctEsitoMandati.getNumeroMandato().longValue());
        try {
            final List<MandatoBulk> findMandato = crudComponentSession.find(userContext, MandatoIBulk.class, "findMandato", mandato);
            return findMandato.stream()
                    .findFirst()
                    .orElseThrow(() -> new ApplicationMessageFormatException("Mandato non trovato per esito flusso esercizio: {} numero: {}",
                            ctEsitoMandati.getEsercizio(), ctEsitoMandati.getNumeroMandato()));

        } catch (ComponentException | RemoteException _ex) {
            throw new DetailedRuntimeException(_ex);
        }
    }


    private ReversaleBulk fetchReversaleBulk(CtEsitoReversali ctEsitoReversali) {
        ReversaleBulk reversale = new ReversaleIBulk();
        reversale.setEsercizio(ctEsitoReversali.getEsercizio());
        reversale.setPg_reversale(ctEsitoReversali.getNumeroReversale().longValue());
        try {
            final List<ReversaleBulk> findReversale = crudComponentSession.find(userContext, ReversaleIBulk.class, "findReversale", reversale);
            return findReversale.stream()
                    .findFirst()
                    .orElseThrow(() -> new ApplicationMessageFormatException("Reversale non trovata per esito flusso esercizio: {} numero: {}",
                            ctEsitoReversali.getEsercizio(), ctEsitoReversali.getNumeroReversale()));
        } catch (ComponentException | RemoteException _ex) {
            throw new DetailedRuntimeException(_ex);
        }
    }

    private void messaggioACK(Risultato risultato) throws RemoteException, ComponentException {
        final MessaggioXML<MessaggioAckSiope> messaggioXML = ordinativiSiopePlusService.getLocation(risultato.getLocation(), MessaggioAckSiope.class);
        final MessaggioAckSiope messaggioAckSiope = messaggioXML.getObject();
        logger.info("Identificativo flusso: {}", messaggioAckSiope.getIdentificativoFlusso());
        Distinta_cassiereBulk distinta = fetchDistinta_cassiereBulk(messaggioAckSiope.getIdentificativoFlusso());
        /**
         * Carico il file del messaggio
         */
        StorageFile storageFile = new StorageFile(messaggioXML.getContent(), MimeTypes.XML.mimetype(), messaggioXML.getName());
        storageFile.setTitle("Acquisito il " + formatter.format(risultato.getDataProduzione().toInstant().atZone(ZoneId.systemDefault())));

        final Integer progFlusso = risultato.getProgFlusso();
        StringBuffer description = new StringBuffer();
        switch (messaggioAckSiope.getStatoFlusso()) {
            case WARNING: {
                messaggioAckSiope.getWarning()
                        .stream()
                        .map(ctErroreACK -> ctErroreACK.getDescrizione().concat(" - ").concat(ctErroreACK.getElemento()))
                        .peek(logger::warn);
                distinta.setStato(Distinta_cassiereBulk.Stato.ACCETTATO_SIOPEPLUS);
                break;
            }
            case OK: {
                distinta.setStato(Distinta_cassiereBulk.Stato.ACCETTATO_SIOPEPLUS);
                break;
            }
            case KO: {
                messaggioAckSiope.getErrore()
                        .stream()
                        .map(ctErroreACK -> ctErroreACK.getDescrizione().concat(" - ").concat(ctErroreACK.getElemento()))
                        .peek(logger::error)
                        .forEach(s -> {
                            description.append(s.concat("\n"));
                        });
                distinta.setStato(Distinta_cassiereBulk.Stato.RIFIUTATO_SIOPEPLUS);
                break;
            }
        }
        storageFile.setDescription(description.toString());
        final StorageObject storageObject = restoreSimpleDocument(
                storageFile,
                new ByteArrayInputStream(storageFile.getBytes()),
                storageFile.getContentType(),
                storageFile.getFileName(),
                distinta.getStorePath(),
                true);
        distinta.setToBeUpdated();
        crudComponentSession.modificaConBulk(userContext, distinta);

    }

    private void messaggioEsito(Risultato risultato) throws RemoteException, ComponentException {
        final MessaggioXML<EsitoFlusso> messaggioXML = ordinativiSiopePlusService.getLocation(risultato.getLocation(), EsitoFlusso.class);
        final EsitoFlusso esitoFlusso = messaggioXML.getObject();
        logger.info("Identificativo flusso: {}", esitoFlusso.getIdentificativoFlusso());
        Distinta_cassiereBulk distinta = fetchDistinta_cassiereBulk(esitoFlusso.getIdentificativoFlusso());
        /**
         * Carico il file del messaggio
         */
        StorageFile storageFile = new StorageFile(messaggioXML.getContent(), MimeTypes.XML.mimetype(),
                String.valueOf(risultato.getProgFlusso()).concat("-").concat(messaggioXML.getName()));
        storageFile.setTitle("Acquisito il " + formatter.format(risultato.getDataUpload().toInstant().atZone(ZoneId.systemDefault())));

        distinta.setIdentificativoFlussoBT(esitoFlusso.getIdentificativoFlussoBT());
        StringBuffer description = new StringBuffer();
        if (esitoFlusso.isRifiutato()) {
            distinta.setStato(Distinta_cassiereBulk.Stato.RIFIUTATO_BT);
            MessaggioRifiutoFlusso messaggioRifiutoFlusso = (MessaggioRifiutoFlusso) esitoFlusso;
            messaggioRifiutoFlusso
                    .getErrore()
                    .stream()
                    .map(ctErrore -> ctErrore.getCodice().toString().concat(" - ").concat(ctErrore.getDescrizione()))
                    .peek(logger::error)
                    .forEach(s -> {
                        description.append(s.concat("\n"));
                    });
        } else {
            distinta.setStato(Distinta_cassiereBulk.Stato.ACCETTATO_BT);
        }
        storageFile.setDescription(description.toString());
        final StorageObject storageObject = restoreSimpleDocument(
                storageFile,
                new ByteArrayInputStream(storageFile.getBytes()),
                storageFile.getContentType(),
                storageFile.getFileName(),
                distinta.getStorePath(),
                true);
        distinta.setToBeUpdated();
        crudComponentSession.modificaConBulk(userContext, distinta);
    }

    private void messaggioEsitoApplicativo(Risultato risultato) throws RemoteException, ComponentException {
        final MessaggioXML<MessaggiEsitoApplicativo> messaggioXML = ordinativiSiopePlusService.getLocation(risultato.getLocation(), MessaggiEsitoApplicativo.class);
        final MessaggiEsitoApplicativo messaggiEsitoApplicativo = messaggioXML.getObject();
        final List<Object> esitoReversaliOrEsitoMandati = messaggiEsitoApplicativo.getEsitoReversaliOrEsitoMandati();
        final List<OggettoBulk> mandatoStream = esitoReversaliOrEsitoMandati
                .stream()
                .filter(CtEsitoMandati.class::isInstance)
                .map(CtEsitoMandati.class::cast)
                .map(ctEsitoMandato -> {
                    logger.info("Identificativo flusso: {} Mandato {}/{}",
                            ctEsitoMandato.getIdentificativoFlusso(),
                            ctEsitoMandato.getEsercizio(),
                            ctEsitoMandato.getNumeroMandato());
                    StringBuffer error = new StringBuffer();
                    ctEsitoMandato.getListaErrori()
                            .stream()
                            .map(errore -> errore.getCodiceErrore().toString()
                                    .concat(" - ")
                                    .concat(errore.getDescrizione())
                                    .concat(" - ")
                                    .concat(errore.getElemento()))
                            .peek(logger::error)
                            .forEach(s -> {
                                error.append(s.concat("\n"));
                            });
                    MandatoBulk mandato = fetchMandatoBulk(ctEsitoMandato);
                    mandato.setEsitoOperazione(MandatoBulk.EsitoOperazione.getValueFromLabel(ctEsitoMandato.getEsitoOperazione().value()));
                    mandato.setDtOraEsitoOperazione(
                            new Timestamp(ctEsitoMandato
                                    .getDataOraEsitoOperazione()
                                    .toGregorianCalendar()
                                    .getTimeInMillis())
                    );
                    mandato.setErroreSiopePlus(
                            Optional.ofNullable(error)
                                    .filter(stringBuffer -> stringBuffer.length() > 0)
                                    .map(StringBuffer::toString)
                                    .orElse(null)
                    );
                    mandato.setToBeUpdated();
                    return mandato;
                }).collect(Collectors.toList());
        final List<OggettoBulk> reversaleStream = esitoReversaliOrEsitoMandati
                .stream()
                .filter(CtEsitoReversali.class::isInstance)
                .map(CtEsitoReversali.class::cast)
                .map(ctEsitoReversale -> {
                    logger.info("Identificativo flusso: {} Reversale {}/{}", ctEsitoReversale.getIdentificativoFlusso(), ctEsitoReversale.getEsercizio(), ctEsitoReversale.getNumeroReversale());
                    StringBuffer error = new StringBuffer();
                    ctEsitoReversale.getListaErrori()
                            .stream()
                            .map(errore -> errore.getCodiceErrore().toString()
                                    .concat(" - ")
                                    .concat(errore.getDescrizione())
                                    .concat(" - ")
                                    .concat(errore.getElemento()))
                            .peek(logger::error)
                            .forEach(s -> {
                                error.append(s.concat("\n"));
                            });
                    ReversaleBulk reversale = fetchReversaleBulk(ctEsitoReversale);
                    reversale.setEsitoOperazione(ReversaleBulk.EsitoOperazione.getValueFromLabel(ctEsitoReversale.getEsitoOperazione().value()));
                    reversale.setDtOraEsitoOperazione(
                            new Timestamp(ctEsitoReversale
                                    .getDataOraEsitoOperazione()
                                    .toGregorianCalendar()
                                    .getTimeInMillis())
                    );
                    reversale.setErroreSiopePlus(
                            Optional.ofNullable(error)
                                    .filter(stringBuffer -> stringBuffer.length() > 0)
                                    .map(StringBuffer::toString)
                                    .orElse(null)
                    );
                    reversale.setToBeUpdated();
                    return reversale;
                }).collect(Collectors.toList());
        crudComponentSession.modificaConBulk(userContext,
                Stream.concat(mandatoStream.stream(), reversaleStream.stream())
                        .toArray(size -> new OggettoBulk[size]));

        mandatoStream
                .stream()
                .filter(MandatoIBulk.class::isInstance)
                .map(MandatoIBulk.class::cast)
                .filter(mandatoBulk -> mandatoBulk.getEsitoOperazione().equals(MandatoBulk.EsitoOperazione.NON_ACQUISITO.value()))
                .forEach(mandatoBulk -> {
                    try {
                        logger.info("SIOPE+ ANNULLA MANDATO [{}/{}]", mandatoBulk.getEsercizio(), mandatoBulk.getPg_mandato());
                        mandatoComponentSession.annullaMandato(userContext, mandatoBulk);
                    } catch (ComponentException|RemoteException e) {
                        logger.error("SIOPE+ ANNULLA MANDATO [{}/{}] ERROR", mandatoBulk.getEsercizio(), mandatoBulk.getPg_mandato(), e);
                    }
                });

        reversaleStream
                .stream()
                .filter(ReversaleIBulk.class::isInstance)
                .map(ReversaleIBulk.class::cast)
                .filter(reversaleBulk -> reversaleBulk.getEsitoOperazione().equals(ReversaleBulk.EsitoOperazione.NON_ACQUISITO.value()))
                .forEach(reversaleBulk -> {
                    try {
                        logger.info("SIOPE+ ANNULLA REVERSALE [{}/{}]", reversaleBulk.getEsercizio(), reversaleBulk.getPg_reversale());
                        reversaleComponentSession.annullaReversale(userContext, reversaleBulk);
                    } catch (ComponentException|RemoteException e) {
                        logger.error("SIOPE+ ANNULLA REVERSALE [{}/{}] ERROR", reversaleBulk.getEsercizio(), reversaleBulk.getPg_reversale(), e);
                    }
                });
    }

    /**
     * Leggi messaggi dalla piattaforma SIOPE+
     */
    public void messaggiSiopeplus() {
        logger.info("SIOPE+ SCAN started at: {}", LocalDateTime.now());

        risultatiACK.removeAll(
                risultatiACK
                        .stream()
                        .filter(risultato -> {
                            try {
                                messaggioACK(risultato);
                                logger.info("SIOPE+  elaborato risultato: {}", risultato);
                                return true;
                            } catch (RemoteException | ComponentException _ex) {
                                logger.error("SIOPE+ ERROR for risultato: {}", risultato, _ex);
                                return false;
                            }
                        }).collect(Collectors.toList()));
        risultatiEsito.removeAll(
                risultatiEsito
                        .stream()
                        .filter(risultato -> {
                            try {
                                messaggioEsito(risultato);
                                logger.info("SIOPE+  elaborato risultato: {}", risultato);
                                return true;
                            } catch (RemoteException | ComponentException _ex) {
                                logger.error("SIOPE+ ERROR for risultato: {}", risultato, _ex);
                                return false;
                            }
                        }).collect(Collectors.toList()));
        risultatiEsitoApplicativo.removeAll(
                risultatiEsitoApplicativo
                        .stream()
                        .filter(risultato -> {
                            try {
                                messaggioEsitoApplicativo(risultato);
                                logger.info("SIOPE+  elaborato risultato: {}", risultato);
                                return true;
                            } catch (RemoteException | ComponentException _ex) {
                                logger.error("SIOPE+ ERROR for risultato: {}", risultato, _ex);
                                return false;
                            }
                        }).collect(Collectors.toList()));
        final Lista listaACK = ordinativiSiopePlusService.getListaMessaggi(OrdinativiSiopePlusService.Esito.ACK,
                null, null, false, null);
        logger.info("Lista ACK: {}", listaACK);
        listaACK.getRisultati()
                .stream()
                .peek(risultato -> risultatiACK.add(risultato))
                .forEach(risultato -> {
                    try {
                        messaggioACK(risultato);
                        logger.info("SIOPE+  elaborato risultato: {}", risultato);
                        risultatiACK.remove(risultato);
                    } catch (RemoteException | ComponentException _ex) {
                        logger.error("SIOPE+ ERROR for risultato: {}", risultato, _ex);
                    }
                });

        final Lista listaEsito = ordinativiSiopePlusService.getListaMessaggi(OrdinativiSiopePlusService.Esito.ESITO,
                null, null, false, null);
        logger.info("SIOPE+ Lista Esito: {}", listaEsito);
        listaEsito.getRisultati()
                .stream()
                .peek(risultato -> risultatiEsito.add(risultato))
                .forEach(risultato -> {
                    try {
                        messaggioEsito(risultato);
                        logger.info("SIOPE+  elaborato risultato: {}", risultato);
                        risultatiEsito.remove(risultato);
                    } catch (RemoteException | ComponentException _ex) {
                        logger.error("SIOPE+ ERROR for risultato: {}", risultato, _ex);
                    }
                });

        final Lista listaEsitoApplicativo = ordinativiSiopePlusService.getListaMessaggi(OrdinativiSiopePlusService.Esito.ESITOAPPLICATIVO,
                null, null, false, null);
        logger.info("SIOPE+ Lista Esito Applicativo: {}", listaEsitoApplicativo);
        listaEsitoApplicativo.getRisultati()
                .stream()
                .peek(risultato -> risultatiEsitoApplicativo.add(risultato))
                .forEach(risultato -> {
                    try {
                        messaggioEsitoApplicativo(risultato);
                        logger.info("SIOPE+  elaborato risultato: {}", risultato);
                        risultatiEsitoApplicativo.remove(risultato);
                    } catch (RemoteException | ComponentException _ex) {
                        logger.error("SIOPE+ ERROR for risultato: {}", risultato, _ex);
                    }
                });
        logger.info("SIOPE+ SCAN end at: {}", LocalDateTime.now());
    }

    class StorageDataSource implements DataSource {

        private StorageObject storageObject;

        public StorageDataSource(StorageObject storageObject) {
            this.storageObject = storageObject;
        }


        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new NotImplementedException();
        }

        @Override
        public String getName() {
            return Optional.ofNullable(storageObject.getPropertyValue(StoragePropertyNames.NAME.value()))
                    .map(String.class::cast)
                    .orElse(null);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return getResource(storageObject);
        }

        @Override
        public String getContentType() {
            return Optional.ofNullable(storageObject.getPropertyValue(StoragePropertyNames.CONTENT_STREAM_MIME_TYPE.value()))
                    .map(String.class::cast)
                    .orElse(null);
        }
    }
}
