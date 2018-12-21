package it.cnr.contab.config00.bulk;

public class Configurazione_cnrBulk extends Configurazione_cnrBase {

	public final static String SK_SDI = "SDI";
	public final static String PK_EMAIL_PEC = "EMAIL_PEC";
	public final static String PK_TERZO_SPECIALE = "TERZO_SPECIALE";
	public final static String SK_CODICE_DIVERSI_PGIRO = "CODICE_DIVERSI_PGIRO";
	public final static String SK_CODICE_DIVERSI_IMPEGNI = "CODICE_DIVERSI_IMPEGNI";
	public final static String PK_OBBLIGATORIETA_ORDINI = "OBBLIGATORIETA_ORDINI";
	public final static String PK_PARAMETRI_ORDINI = "PARAMETRI_ORDINI";

	public final static String PK_LINEA_ATTIVITA_SPECIALE = "LINEA_ATTIVITA_SPECIALE";
	public final static String SK_LINEA_COMUNE_VERSAMENTO_IVA = "LINEA_COMUNE_VERSAMENTO_IVA";
	
	public final static String PK_BOLLO_VIRTUALE = "BOLLO_VIRTUALE";
	public final static String SK_BOLLO_VIRTUALE_CODICE_FATTURA_ATTIVA = "CODICE_DOCUMENTO_FATTURA_ATTIVA";
	

	public final static String SK_BOLLO_VIRTUALE_IMPORTO_LIMITE = "IMPORTO_LIMITE";
	
	public final static String SK_GG_DT_PREV_CONSEGNA = "GIORNI_DATA_PREVISTA_CONSEGNA";

	public final static String SK_PROGETTO_RICHIESTA = "PROGETTO_RICHIESTA";
	public final static String SK_GAE_RICHIESTA = "GAE_RICHIESTA";
	public final static String SK_VOCE_RICHIESTA = "VOCE_RICHIESTA";

	public final static String SK_LINEA_ATTIVITA_ENTRATA_ENTE = "LINEA_ATTIVITA_ENTRATA_ENTE";
	public final static String SK_LINEA_ATTIVITA_SPESA_ENTE = "LINEA_ATTIVITA_SPESA_ENTE";

	public final static String PK_CD_DIVISA = "CD_DIVISA";
	public final static String SK_EURO = "EURO";

	public final static String PK_ELEMENTO_VOCE_SPECIALE = "ELEMENTO_VOCE_SPECIALE";
	public final static String SK_VOCE_IVA_FATTURA_ESTERA = "VOCE_IVA_FATTURA_ESTERA";

	public final static String PK_CONTO_CORRENTE_SPECIALE = "CONTO_CORRENTE_SPECIALE";
	public final static String SK_ENTE = "ENTE";
	public final static String SK_BANCA_ITALIA = "BANCA_ITALIA";

	public final static String PK_UO_SPECIALE = "UO_SPECIALE";
	public final static String SK_UO_ACCREDITAMENTO_SAC = "UO_ACCREDITAMENTO_SAC";
	public final static String SK_UO_DISTINTA_SAC = "UO_DISTINTA_SAC";
	
	public final static String PK_PDG_VARIAZIONE = "PDG_VARIAZIONE";
	public final static String SK_TIPO_VAR_APPROVA_CDS = "TIPO_VAR_APPROVA_CDS";

	public final static String PK_VAR_STANZ_RES = "VAR_STANZ_RES";
	public final static String SK_TIPO_VAR_APPROVA_CNR = "TIPO_VAR_APPROVA_CNR";
	
	public final static String PK_CDR_SPECIALE = "CDR_SPECIALE";
	public final static String SK_CDR_PERSONALE = "CDR_PERSONALE";

	public final static String PK_FATTURAZIONE_ELETTRONICA = "FATTURAZIONE_ELETTRONICA";

	public final static String SK_MAIL_REFERENTE_TECNICO = "MAIL_REFERENTE_TECNICO";
	public final static String SK_TELEFONO_REFERENTE_TECNICO = "TELEFONO_REFERENTE_TECNICO";
	public final static String SK_ATTIVA = "ATTIVA";
	public final static String SK_PASSIVA = "PASSIVA";
	public final static String SK_PASSIVA_PROF = "PASSIVA_PROF";
	

	public final static String PK_CODICE_SIOPE_DEFAULT = "CODICE_SIOPE_DEFAULT";
	public final static String SK_MANDATO_ACCREDITAMENTO = "MANDATO_ACCREDITAMENTO";
	public final static String SK_REVERSALE_TRASFERIMENTO = "REVERSALE_TRASFERIMENTO";

	public final static String PK_LIMITE_COLL_MERAMENTE_OCCASIONALI = "LIMITE_COLL_MERAMENTE_OCCASIONALI";
	
	public final static String PK_COSTANTI = "COSTANTI";
	public final static String SK_TOTALE_GIORNI_LAVORATIVI_COSTI_PERSONALE = "TOTALE_GIORNI_LAVORATIVI_COSTI_PERSONALE";

	public final static String PK_ANNI_RESIDUI_VAR_ST_RES = "ANNI_RESIDUI_VAR_ST_RES";
	public final static String PK_ANNI_RESIDUI_IM_RES_IMP = "ANNI_RESIDUI_IM_RES_IMP";
	public final static String PK_ANNI_RESIDUI_IM_RES_PRO = "ANNI_RESIDUI_IM_RES_PRO";
	public final static String SK_MODELLO_INTRA_12="MODELLO_INTRA_12";
	public final static String SK_MODELLO_INTRASTAT="MODELLO_INTRASTAT";
	public final static String SK_BLACKLIST="BLACKLIST";
	public final static String PK_INCARICHI_MODIFICA_ALLEGATI = "INCARICHI_MODIFICA_ALLEGATI";
	public final static String SK_INCARICHI_MOD_CONTRATTO = "INCARICHI_MOD_CONTRATTO";
	public final static String SK_INCARICHI_MOD_CURRICULUM = "INCARICHI_MOD_CURRICULUM";
		
	public final static String PK_LIMITE_UTILIZZO_CONTANTI = "LIMITE_UTILIZZO_CONTANTI";
	public final static String SK_LIMITE1 = "LIMITE1";
	public final static String PK_SPLIT_PAYMENT = "SPLIT_PAYMENT";
	public final static String PK_CONTO_CORRENTE_BANCA_ITALIA = "CONTO_CORRENTE_BANCA_ITALIA";
	public final static String SK_CODICE = "CODICE";
	
	public final static String PK_INTEGRAZIONE_SDI = "INTEGRAZIONE_SDI";
	public final static String SK_INTEGRAZIONE_SDI = "MODALITA";

    public final static String SK_GESTIONE_ORDINI = "GESTIONE";
    public final static String PK_ORDINI = "ORDINI";

    public final static String SK_ORDINE_AUT_ROTTURA_UO_DESTINAZIONE = "ORDINI_AUT_ROTTURA_UO_DEST";
    public final static String SK_ORDINE_IMPEGNO_UO_DESTINAZIONE = "IMPEGNO_ORDINI_UO_DEST";
    public final static String PK_FATTURA_PASSIVA = "FATTURA_PASSIVA";
    public final static String SK_LIMITE_REG_TARDIVA = "LIMITE_REG_TARDIVA";

    public final static String PK_GESTIONE_PROGETTI = "PROGETTI";
    public final static String SK_PROGETTO_PIANO_ECONOMICO = "PIANO_ECONOMICO";

	public final static String PK_FLUSSO_ORDINATIVI = "FLUSSO_ORDINATIVI";
	public final static String SK_CODICE_ABI_BT = "CODICE_ABI_BT";
	public final static String SK_CODICE_A2A = "CODICE_A2A";
	public final static String SK_CODICE_ENTE = "CODICE_ENTE";
	public final static String SK_CODICE_ENTE_BT = "CODICE_ENTE_BT";
	public final static String SK_CODICE_TRAMITE_BT = "CODICE_TRAMITE_BT";
	public final static String SK_CODICE_ISTAT_ENTE = "CODICE_ISTAT_ENTE";
	public final static String SK_ATTIVO_SIOPEPLUS = "ATTIVO_SIOPEPLUS";

	public Configurazione_cnrBulk() {
	super();
}

    public Configurazione_cnrBulk(java.lang.String cd_chiave_primaria,java.lang.String cd_chiave_secondaria,java.lang.String cd_unita_funzionale,java.lang.Integer esercizio) {
		super(cd_chiave_primaria,cd_chiave_secondaria,cd_unita_funzionale,esercizio);
	}
}
