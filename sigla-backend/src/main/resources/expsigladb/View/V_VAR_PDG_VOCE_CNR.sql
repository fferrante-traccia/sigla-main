--------------------------------------------------------
--  DDL for View V_VAR_PDG_VOCE_CNR
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "V_VAR_PDG_VOCE_CNR" ("ESERCIZIO", "PG_VARIAZIONE_PDG", "TIPOLOGIA", "PG_RIGA", "CD_CDR_ASSEGNATARIO", "CD_CDS_AREA", "CD_LINEA_ATTIVITA", "TI_APPARTENENZA", "TI_GESTIONE", "CD_ELEMENTO_VOCE", "CATEGORIA_DETTAGLIO", "CD_VOCE_CNR", "IM_SPESE_GEST_DECENTRATA_INT", "IM_SPESE_GEST_DECENTRATA_EST", "IM_SPESE_GEST_ACCENTRATA_INT", "IM_SPESE_GEST_ACCENTRATA_EST") AS 
  Select  PDG_VARIAZIONE_RIGA_GEST.ESERCIZIO,
        PDG_VARIAZIONE_RIGA_GEST.PG_VARIAZIONE_PDG,
        PDG_VARIAZIONE.TIPOLOGIA,
        PDG_VARIAZIONE_RIGA_GEST.PG_RIGA,
        PDG_VARIAZIONE_RIGA_GEST.CD_CDR_ASSEGNATARIO,
        PDG_VARIAZIONE_RIGA_GEST.CD_CDS_AREA,
        PDG_VARIAZIONE_RIGA_GEST.CD_LINEA_ATTIVITA,
        PDG_VARIAZIONE_RIGA_GEST.TI_APPARTENENZA,
        PDG_VARIAZIONE_RIGA_GEST.TI_GESTIONE,
        PDG_VARIAZIONE_RIGA_GEST.CD_ELEMENTO_VOCE,
        PDG_VARIAZIONE_RIGA_GEST.CATEGORIA_DETTAGLIO,
        CNRCTB075.getvocecnrfromvocecds (PDG_VARIAZIONE_RIGA_GEST.ESERCIZIO,
                                 PDG_VARIAZIONE_RIGA_GEST.CD_CDR_ASSEGNATARIO,
                                 PDG_VARIAZIONE_RIGA_GEST.CD_LINEA_ATTIVITA,
                                 PDG_VARIAZIONE_RIGA_GEST.ESERCIZIO,
                                 PDG_VARIAZIONE_RIGA_GEST.TI_GESTIONE,
                                 PDG_VARIAZIONE_RIGA_GEST.TI_APPARTENENZA,
                                 PDG_VARIAZIONE_RIGA_GEST.CD_ELEMENTO_VOCE) CD_VOCE_CNR,
        IM_SPESE_GEST_DECENTRATA_INT,
        IM_SPESE_GEST_DECENTRATA_EST,
        IM_SPESE_GEST_ACCENTRATA_INT,
        IM_SPESE_GEST_ACCENTRATA_EST
From    PDG_VARIAZIONE_RIGA_GEST, PDG_VARIAZIONE
Where   PDG_VARIAZIONE.ESERCIZIO = PDG_VARIAZIONE_RIGA_GEST.ESERCIZIO And
        PDG_VARIAZIONE.PG_VARIAZIONE_PDG = PDG_VARIAZIONE_RIGA_GEST.PG_VARIAZIONE_PDG And     
        PDG_VARIAZIONE.STATO != 'ANN' AND
        PDG_VARIAZIONE_RIGA_GEST.TI_GESTIONE = 'S' ;
