package it.cnr.contab.util00.bp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

import it.cnr.contab.util00.bulk.storage.AllegatoGenericoBulk;
import it.cnr.contab.util00.bulk.storage.AllegatoGenericoTypeBulk;
import it.cnr.contab.util00.bulk.storage.AllegatoParentBulk;
import it.cnr.jada.action.ActionContext;
import it.cnr.jada.action.BusinessProcessException;
import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.comp.ApplicationException;
import it.cnr.si.spring.storage.StorageException;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;

public abstract class AllegatiTypeCRUDBP<T extends AllegatoGenericoTypeBulk, K extends AllegatoParentBulk> extends AllegatiCRUDBP<T,K> {
    private static final long serialVersionUID = 1L;

    public AllegatiTypeCRUDBP() {
        super();
    }

    public AllegatiTypeCRUDBP(String s) {
        super(s);
    }

    protected void completeAllegato(T allegato) throws ApplicationException {
		StorageObject storageObject = storeService.getStorageObjectBykey(allegato.getStorageKey());
		allegato.setObjectType(storageObject.getPropertyValue(StoragePropertyNames.OBJECT_TYPE_ID.value()));
    }
 
    @SuppressWarnings("unchecked")
    protected void archiviaAllegati(ActionContext actioncontext) throws BusinessProcessException, ApplicationException {
        AllegatoParentBulk allegatoParentBulk = (AllegatoParentBulk) getModel();
        for (AllegatoGenericoBulk model : allegatoParentBulk.getArchivioAllegati()) {
            AllegatoGenericoTypeBulk allegato = (AllegatoGenericoTypeBulk)model;
        	if (allegato.isToBeCreated()) {
                final File file = Optional.ofNullable(allegato.getFile())
                        .orElseThrow(() -> new ApplicationException("File non presente"));
                try {
                    storeService.storeSimpleDocument(allegato,
                            new FileInputStream(file),
                            allegato.getContentType(),
                            allegato.getNome(),
                            getStorePath((K) allegatoParentBulk,
                                    true),
                            allegato.getObjectType(),
                            false
                    		);
                    allegato.setCrudStatus(OggettoBulk.NORMAL);
                } catch (FileNotFoundException e) {
                    throw handleException(e);
                } catch (StorageException e) {
                    if (e.getType().equals(StorageException.Type.CONSTRAINT_VIOLATED))
                        throw new ApplicationException("File [" + allegato.getNome() + "] gia' presente. Inserimento non possibile!");
                    throw handleException(e);
                }
            } else if (allegato.isToBeUpdated()) {
                if (isPossibileModifica(allegato)) {
                    try {
                        if (allegato.getFile() != null) {
                            storeService.updateStream(allegato.getStorageKey(),
                                    new FileInputStream(allegato.getFile()),
                                    allegato.getContentType());
                        }
                        storeService.updateProperties(allegato, storeService.getStorageObjectBykey(allegato.getStorageKey()));
                        allegato.setCrudStatus(OggettoBulk.NORMAL);
                    } catch (FileNotFoundException e) {
                        throw handleException(e);
                    } catch (StorageException e) {
                        if (e.getType().equals(StorageException.Type.CONSTRAINT_VIOLATED))
                            throw new ApplicationException("File [" + allegato.getNome() + "] gia' presente. Inserimento non possibile!");
                        throw handleException(e);
                    }
                }
            }
        }
        gestioneCancellazioneAllegati(allegatoParentBulk);
    }
}
