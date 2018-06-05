/*
 * Created by Aurelio's BulkGenerator 1.0
 * Date 03/04/2008
 */
package it.cnr.contab.incarichi00.bulk;

import it.cnr.si.spring.storage.bulk.StorageFile;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.jada.UserContext;
import it.cnr.jada.persistency.Persister;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.StringTokenizer;
public abstract class Incarichi_archivioBulk extends Incarichi_archivioBase {
	public static final java.util.Dictionary tipo_archivioKeys = new it.cnr.jada.util.OrderedHashtable();
	public static final java.util.Dictionary statoKeys = new it.cnr.jada.util.OrderedHashtable();

	private java.lang.String blob;
	private File file;
	private String contentType;
	private boolean fileRequired=Boolean.TRUE;
	private boolean urlRequired=Boolean.FALSE;
 
	final public static String TIPO_BANDO = "B";
	final public static String TIPO_GENERICO = "G";
	final public static String TIPO_CONTRATTO = "C";
	final public static String TIPO_CURRICULUM_VINCITORE = "M";
	final public static String TIPO_ALLEGATO_CONTRATTO = "A";
	final public static String TIPO_DECISIONE_A_CONTRATTARE = "D";
	final public static String TIPO_DECRETO_DI_NOMINA = "N";
	final public static String TIPO_ATTO_ESITO_CONTROLLO = "E";	
	final public static String TIPO_PROGETTO = "T";

	final public static String DEFAULT_NOME_FILE = "Allegato";	
	final public static String DEFAULT_ESTENSIONE = "xls";	
	
	final public static String STATO_PROVVISORIO = "P";
	final public static String STATO_DEFINITIVO = "D";
	final public static String STATO_VALIDO = "V";
	final public static String STATO_ANNULLATO = "A";

	static {
		statoKeys.put(STATO_PROVVISORIO,"Provvisorio");
		statoKeys.put(STATO_DEFINITIVO,"Definitivo");
		statoKeys.put(STATO_VALIDO,"Valido");
		statoKeys.put(STATO_ANNULLATO,"Annullato");
	}

	static {
		tipo_archivioKeys.put(TIPO_BANDO,"Avviso da pubblicare");
		tipo_archivioKeys.put(TIPO_CONTRATTO,"Contratto");
		tipo_archivioKeys.put(TIPO_CURRICULUM_VINCITORE,"Curriculum vincitore");
		tipo_archivioKeys.put(TIPO_DECRETO_DI_NOMINA,"Decreto di nomina");
		tipo_archivioKeys.put(TIPO_DECISIONE_A_CONTRATTARE,"Decisione a contrattare");
		tipo_archivioKeys.put(TIPO_ATTO_ESITO_CONTROLLO,"Esito Controllo Corte Conti");
		tipo_archivioKeys.put(TIPO_PROGETTO,"Progetto");
		tipo_archivioKeys.put(TIPO_GENERICO,"Allegato generico");
		tipo_archivioKeys.put(TIPO_ALLEGATO_CONTRATTO,"Allegato Contratto");
	}

	public Incarichi_archivioBulk() {
		super();
	}
	
	public java.lang.String getBlob () {
		return blob;
	}
	public void setBlob(java.lang.String blob)  {
		this.blob=blob;
	}
	public static java.util.Dictionary getTipo_archivioKeys() {
		return tipo_archivioKeys;
	}
	public Timestamp getData_creazione() {
		return getDacr();
	}

	/**
	 * Metodo che elimina il path dal nome del file
	 *
	 * @return Il nome del file senza path
	 * 
	 * @param Il nome completo di path
	 */
	public static String parseFilename(String file) {
		StringTokenizer fileName = new StringTokenizer(file,"\\",false);
		String newFileName = null;
		
		while (fileName.hasMoreTokens()){
			newFileName = fileName.nextToken();   	
		}

		if (newFileName != null)
			return newFileName;
		
		return file;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public boolean isROAttivaFileBlob() {
		return this == null;
	}
	public boolean isBando() {
		return isAllegatoValido() && getTipo_archivio() != null && getTipo_archivio().equals(TIPO_BANDO);
	}
	public boolean isAllegatoGenerico() {
		return isAllegatoValido() && getTipo_archivio() != null && getTipo_archivio().equals(TIPO_GENERICO);
	}
	public boolean isContratto() {
		return isAllegatoValido() && getTipo_archivio() != null && getTipo_archivio().equals(TIPO_CONTRATTO);
	}
	public boolean isAllegatoContratto() {
		return isAllegatoValido() && getTipo_archivio() != null && getTipo_archivio().equals(TIPO_ALLEGATO_CONTRATTO);
	}
	public boolean isCurriculumVincitore() {
		return isAllegatoValido() && getTipo_archivio() != null && getTipo_archivio().equals(TIPO_CURRICULUM_VINCITORE);
	}
	public boolean isDecisioneAContrattare() {
		return isAllegatoValido() && getTipo_archivio() != null && getTipo_archivio().equals(TIPO_DECISIONE_A_CONTRATTARE);
	}
	public boolean isDecretoDiNomina() {
		return isAllegatoValido() && getTipo_archivio() != null && getTipo_archivio().equals(TIPO_DECRETO_DI_NOMINA);
	}
	public boolean isProgetto() {
		return isAllegatoValido() && getTipo_archivio() != null && getTipo_archivio().equals(TIPO_PROGETTO);
	}
	public boolean isAttoEsitoControllo() {
		return isAllegatoValido() && getTipo_archivio() != null && getTipo_archivio().equals(TIPO_ATTO_ESITO_CONTROLLO);
	}
	public void insertingUsing(Persister persister, UserContext userContext) {
		if (getStato()==null)
			setStato(STATO_VALIDO);
		super.insertingUsing(persister, userContext);
	}
	public boolean isAllegatoValido() {
		return isToBeCreated() || isValido() || isProvvisorio() || isDefinitivo();
	}
	public Integer getFaseProcesso() {
		return null;
	}
	public void setFileRequired(boolean fileRequired) {
		this.fileRequired = fileRequired;
	}
	public boolean isFileRequired(){
		return fileRequired;
	}
	public void setUrlRequired(boolean urlRequired) {
		this.urlRequired = urlRequired;
	}
	public boolean isUrlRequired(){
		return urlRequired;
	}
	public boolean isValido(){
		return getStato()!=null && getStato().equals(STATO_VALIDO);
	}
	public boolean isProvvisorio(){
		return getStato()!=null && getStato().equals(STATO_PROVVISORIO);
	}
	public boolean isDefinitivo(){
		return getStato()!=null && getStato().equals(STATO_DEFINITIVO);
	}
	public boolean isAnnullato(){
		return getStato()!=null && getStato().equals(STATO_ANNULLATO);
	}

	public StorageFile getCMISFile() throws IOException{
		return null;
	}
	public StorageFile getCMISFile(StorageObject storageObject){
		return null;
	}
	public String getDownloadUrl() {
		if(this == null ||this.getCms_node_ref() == null)
			return null;
		StringBuffer stringbuffer = new StringBuffer("download_incarichi/"+this.getNome_file());
		stringbuffer.append("?cmisNodeRef="+this.getCms_node_ref());	
		return stringbuffer.toString();
	}
	public String constructCMISNomeFile() {
		StringBuffer nome = new StringBuffer();
    	if (this.isBando())
    		nome = nome.append("BAN");
    	else if (this.isDecisioneAContrattare())
    		nome = nome.append("DECCTR");
    	else if (this.isContratto())
    		nome = nome.append("CTR");
    	else if (this.isAllegatoContratto())
    		nome = nome.append("ALLCTR");
    	else if (this.isCurriculumVincitore())
    		nome = nome.append("CUR");
    	else if (this.isDecretoDiNomina())
    		nome = nome.append("DECNOM");
    	else if (this.isAttoEsitoControllo())
    		nome = nome.append("ESICTR");
    	else if (this.isProgetto())
    		nome = nome.append("PRG");
    	else 
    		nome = nome.append("GEN");
    	return nome.toString();
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}