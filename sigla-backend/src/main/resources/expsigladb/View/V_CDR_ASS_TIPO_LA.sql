--------------------------------------------------------
--  DDL for View V_CDR_ASS_TIPO_LA
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "V_CDR_ASS_TIPO_LA" ("CD_CENTRO_RESPONSABILITA", "CD_UNITA_ORGANIZZATIVA", "DS_CDR", "LIVELLO", "CD_PROPRIO_CDR", "CD_RESPONSABILE", "INDIRIZZO", "PG_VER_REC", "CD_CDR_AFFERENZA", "ESERCIZIO", "ESERCIZIO_FINE", "ESERCIZIO_INIZIO", "DACR", "UTCR", "DUVA", "UTUV", "CD_TIPO_LINEA_ATTIVITA", "FL_ASSOCIATO") AS 
  SELECT  
--  
-- Date: 18/02/2002
-- Version: 1.0
--  
-- Elenco di tutti i CDR con l'esercizio di validita e il flag FL_ASSOCIATO
-- che vale 'Y' se esiste un'associazione tra la linea di attivita CD_TIPO_LINEA_ATTIVITA
-- e il CDR
--
-- History:  
--  
-- Creazione 
-- Date: 18/02/2002
-- Creazione
--  
CDR.CD_CENTRO_RESPONSABILITA,  
CDR.CD_UNITA_ORGANIZZATIVA,  
CDR.DS_CDR,  
CDR.LIVELLO,  
CDR.CD_PROPRIO_CDR,  
CDR.CD_RESPONSABILE,  
CDR.INDIRIZZO,  
CDR.PG_VER_REC,  
CDR.CD_CDR_AFFERENZA,  
CDR.ESERCIZIO,  
CDR.ESERCIZIO_FINE,  
CDR.ESERCIZIO_INIZIO,  
CDR.DACR,  
CDR.UTCR,  
CDR.DUVA,  
CDR.UTUV,  
TIPO_LINEA_ATTIVITA.CD_TIPO_LINEA_ATTIVITA,  
DECODE(  
(SELECT COUNT(*) FROM DUAL  
WHERE  EXISTS  
(SELECT 1  
FROM   ASS_TIPO_LA_CDR  
WHERE  ASS_TIPO_LA_CDR.CD_CENTRO_RESPONSABILITA = CDR.CD_CENTRO_RESPONSABILITA AND  
ASS_TIPO_LA_CDR.CD_TIPO_LINEA_ATTIVITA = TIPO_LINEA_ATTIVITA.CD_TIPO_LINEA_ATTIVITA)  
), 0, 'N', 'Y')  
FROM  V_CDR_VALIDO CDR,  
TIPO_LINEA_ATTIVITA;

   COMMENT ON TABLE "V_CDR_ASS_TIPO_LA"  IS 'Elenco di tutti i CDR con l''esercizio di validita e il flag FL_ASSOCIATO
che vale ''Y'' se esiste un''associazione tra la linea di attivita CD_TIPO_LINEA_ATTIVITA
e il CDR';
