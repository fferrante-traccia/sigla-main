/*
 * Created by Aurelio's BulkGenerator 1.0
 * Date 18/03/2008
 */
package it.cnr.contab.utenze00.bulk;
import it.cnr.jada.action.ActionContext;
import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.util.action.CRUDBP;
public class PrivilegioBulk extends PrivilegioBase {
	final public static String ABILITA_APPROVA_BILANCIO = "APPBIL";
	final public static String ABILITA_AGGIORNA_INVENTARIO = "AGGINV";
	final public static String ABILITA_INVENTARIO_UFFICIALE = "INVUFF";
	final public static String ABILITA_GESTIONE_ISTAT_SIOPE = "INSSIO";
	final public static String ABILITA_SUPERVISORE = "SUPVIS";
	final public static String ABILITA_ELENCO_CF = "ELENCF";
	final public static String ABILITA_F24EP = "INSF24";
	final public static String ABILITA_FATTURA_ATTIVA = "FATATT";
	final public static String ABILITA_PUBBLICAZIONE_SITO = "PUBSIT";
	final public static String ABILITA_FUNZIONI_INCARICHI = "FUNINC";
	final public static String ABILITA_FUNZIONI_DIRETTORE = "DIRIST";
	final public static String ABILITA_FUNZIONI_SUPERUTENTE_INCARICHI = "SUPINC";
	final public static String ABILITA_SOSPCORI = "INSSOS";
	final public static String ABILITA_VARIAZIONI = "MODVAR";
	final public static String ABILITA_ALL_TRATT = "ALLTRA";
	
	public static final String TIPO_RISERVATO_CNR 	= "C";
	public static final String TIPO_PUBBLICO 		= "D";

	public PrivilegioBulk() {
		super();
	}
	public PrivilegioBulk(java.lang.String cd_privilegio) {
		super(cd_privilegio);
	}
}