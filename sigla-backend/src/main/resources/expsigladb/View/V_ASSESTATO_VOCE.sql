--------------------------------------------------------
--  DDL for View V_ASSESTATO_VOCE
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "V_ASSESTATO_VOCE" ("ESERCIZIO", "ESERCIZIO_RES", "CD_CENTRO_RESPONSABILITA", "TI_APPARTENENZA", "TI_GESTIONE", "CD_VOCE", "DS_VOCE", "CD_ELEMENTO_VOCE", "IM_OBBL_RES_PRO", "ASSESTATO", "VAR_PIU_OBBL_RES_PRO", "VAR_MENO_OBBL_RES_PRO", "IM_MANDATI_REVERSALI_PRO", "IM_PAGAMENTI_INCASSI") AS 
  Select
  ESERCIZIO,
  ESERCIZIO_RES,
  CD_CENTRO_RESPONSABILITA,
  TI_APPARTENENZA,
  TI_GESTIONE,
  CD_VOCE,
  DS_VOCE,
  CD_ELEMENTO_VOCE,
  Sum(IM_OBBL_RES_PRO),
  Sum(ASSESTATO),
  Sum(VAR_PIU_OBBL_RES_PRO),
  Sum(VAR_MENO_OBBL_RES_PRO),
  Sum(IM_MANDATI_REVERSALI_PRO),
  Sum(IM_PAGAMENTI_INCASSI)
From (
Select
--
-- Date: 27/02/2006
-- Version: 1.0
--
-- Vista di estrazione dei Saldi per l'assestato ragruppato per voce
--
-- History:
--
-- Date: 27/02/2006
-- Version: 1.0
-- Creazione
--
-- Body:
--
VOCE_F_SALDI_CDR_LINEA.ESERCIZIO,
VOCE_F_SALDI_CDR_LINEA.ESERCIZIO_RES,
VOCE_F_SALDI_CDR_LINEA.CD_CENTRO_RESPONSABILITA,
VOCE_F_SALDI_CDR_LINEA.TI_APPARTENENZA,
VOCE_F_SALDI_CDR_LINEA.TI_GESTIONE,
VOCE_F_SALDI_CDR_LINEA.CD_VOCE,
VOCE_F.DS_VOCE,
VOCE_F_SALDI_CDR_LINEA.CD_ELEMENTO_VOCE,
IM_OBBL_RES_PRO,
(IM_OBBL_RES_PRO - IM_MANDATI_REVERSALI_PRO) + VAR_PIU_OBBL_RES_PRO - VAR_MENO_OBBL_RES_PRO ASSESTATO,
VAR_PIU_OBBL_RES_PRO,
VAR_MENO_OBBL_RES_PRO,
IM_MANDATI_REVERSALI_PRO,
IM_PAGAMENTI_INCASSI
From VOCE_F_SALDI_CDR_LINEA, VOCE_F
Where VOCE_F_SALDI_CDR_LINEA.ESERCIZIO > 2005
  And VOCE_F_SALDI_CDR_LINEA.ESERCIZIO = VOCE_F.ESERCIZIO
  And VOCE_F_SALDI_CDR_LINEA.TI_APPARTENENZA = VOCE_F.TI_APPARTENENZA
  And VOCE_F_SALDI_CDR_LINEA.TI_GESTIONE = VOCE_F.TI_GESTIONE
  And VOCE_F_SALDI_CDR_LINEA.CD_VOCE = VOCE_F.CD_VOCE
Union All
Select
VOCE_F_SALDI_CMP.ESERCIZIO,
VOCE_F_SALDI_CMP.ESERCIZIO,
cnrctb020.getCdCdrEnte,
VOCE_F_SALDI_CMP.TI_APPARTENENZA,
VOCE_F_SALDI_CMP.TI_GESTIONE,
VOCE_F_SALDI_CMP.CD_VOCE,
VOCE_F.DS_VOCE,
NULL,
IM_OBBLIG_IMP_ACR IM_OBBL_RES_PRO,
Nvl(IM_OBBLIG_IMP_ACR, 0) - Nvl(IM_MANDATI_REVERSALI, 0) ASSESTATO,
0 VAR_PIU_OBBL_RES_PRO,
0 VAR_MENO_OBBL_RES_PRO,
IM_MANDATI_REVERSALI IM_MANDATI_REVERSALI_PRO,
IM_PAGAMENTI_INCASSI IM_PAGAMENTI_INCASSI
From VOCE_F_SALDI_CMP, VOCE_F
Where VOCE_F_SALDI_CMP.ESERCIZIO < 2006
  And VOCE_F_SALDI_CMP.CD_CDS = cnrctb020.getCdCdsENTE(VOCE_F.ESERCIZIO)
  And VOCE_F_SALDI_CMP.TI_COMPETENZA_RESIDUO = 'C'
  And VOCE_F_SALDI_CMP.ESERCIZIO = VOCE_F.ESERCIZIO
  And VOCE_F_SALDI_CMP.TI_APPARTENENZA = VOCE_F.TI_APPARTENENZA
  And VOCE_F_SALDI_CMP.TI_APPARTENENZA = VOCE_F.TI_APPARTENENZA
  And VOCE_F_SALDI_CMP.TI_GESTIONE = VOCE_F.TI_GESTIONE
  And VOCE_F_SALDI_CMP.CD_VOCE = VOCE_F.CD_VOCE
    union all
  SELECT
  voce_f.esercizio,
  b.esercizio esercizio_res,
  cnrctb020.getcdcdrente,
  voce_f.ti_appartenenza,
  voce_f.ti_gestione,
  voce_f.cd_voce, voce_f.ds_voce,
  voce_f.cd_elemento_voce, 0,0 ,0, 0,0,0
	FROM  voce_f ,esercizio_base b
	WHERE
	( VOCE_F.ESERCIZIO > 2005 ) AND
	( VOCE_F.TI_APPARTENENZA = 'C' ) AND
	( VOCE_F.TI_GESTIONE = 'S' ) AND
	( VOCE_F.TI_VOCE = 'E' ) AND
	( VOCE_F.CD_PARTE = '1' ) and
	b.esercizio > 2005 and
	not exists (select 1 from VOCE_F_SALDI_CDR_LINEA  where
	  VOCE_F_SALDI_CDR_LINEA.ESERCIZIO > 2005
  And VOCE_F_SALDI_CDR_LINEA.ESERCIZIO = VOCE_F.ESERCIZIO
  and VOCE_F_SALDI_CDR_LINEA.ESERCIZIO_res = b.esercizio
  And VOCE_F_SALDI_CDR_LINEA.TI_APPARTENENZA = VOCE_F.TI_APPARTENENZA
  And VOCE_F_SALDI_CDR_LINEA.TI_GESTIONE = VOCE_F.TI_GESTIONE
  And VOCE_F_SALDI_CDR_LINEA.CD_VOCE = VOCE_F.CD_VOCE))
Group By ESERCIZIO,
ESERCIZIO_RES,
CD_CENTRO_RESPONSABILITA,
TI_APPARTENENZA,
TI_GESTIONE,
CD_VOCE,
DS_VOCE,
CD_ELEMENTO_VOCE;
