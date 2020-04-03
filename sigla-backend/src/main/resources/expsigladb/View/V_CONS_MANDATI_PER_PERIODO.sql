--------------------------------------------------------
--  DDL for View V_CONS_MANDATI_PER_PERIODO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "V_CONS_MANDATI_PER_PERIODO" ("ESERCIZIO", "MESE", "CD_CDS", "CD_UNITA_ORGANIZZATIVA", "TOT_EMESSO", "TOT_ANNULLATO", "TOT_VALIDO", "TOT_INVIATO_NEL_PERIODO", "TOT_INVIATO_FUORI_PERIODO", "TOT_DA_INVIARE") AS 
  select Distinct MANDATO.ESERCIZIO,
       Decode(To_Char(MANDATO.DT_EMISSIONE, 'MM'),
                '01', 'GENNAIO',
                '02', 'FEBBRAIO',
                '03', 'MARZO',
                '04', 'APRILE',
                '05', 'MAGGIO',
                '06', 'GIUGNO',
                '07', 'LUGLIO',
                '08', 'AGOSTO',
                '09', 'SETTEMBRE',
                '10', 'OTTOBRE',
                '11', 'NOVEMBRE',
                '12', 'DICEMBRE'),
       MANDATO.CD_CDS,
       MANDATO.CD_UNITA_ORGANIZZATIVA,
       (Select nvl(sum(MAN_EME.IM_MANDATO), 0)
        From MANDATO MAN_EME
        Where MAN_EME.CD_CDS = MANDATO.CD_CDS And
              MAN_EME.ESERCIZIO = MANDATO.ESERCIZIO AND
              MAN_EME.CD_UNITA_ORGANIZZATIVA = MANDATO.CD_UNITA_ORGANIZZATIVA And
              TO_CHAR(MAN_EME.DT_EMISSIONE, 'YYYYMM') = TO_CHAR(MANDATO.DT_EMISSIONE, 'YYYYMM')) EMESSO,
       (Select nvl(sum(MAN_ANN.IM_MANDATO), 0)
        From  MANDATO MAN_ANN
        Where MAN_ANN.CD_CDS = MANDATO.CD_CDS AND
              MAN_ANN.ESERCIZIO = MANDATO.ESERCIZIO AND
              MAN_ANN.CD_UNITA_ORGANIZZATIVA = MANDATO.CD_UNITA_ORGANIZZATIVA And
              TO_CHAR(MAN_ANN.DT_EMISSIONE, 'YYYYMM') = TO_CHAR(MANDATO.DT_EMISSIONE, 'YYYYMM') AND
              STATO = 'A') ANNULLATO,
       (Select nvl(sum(MAN_VAL.IM_MANDATO), 0)
        From MANDATO MAN_VAL
        Where MAN_VAL.CD_CDS = MANDATO.CD_CDS AND
              MAN_VAL.ESERCIZIO = MANDATO.ESERCIZIO AND
              MAN_VAL.CD_UNITA_ORGANIZZATIVA = MANDATO.CD_UNITA_ORGANIZZATIVA And
              TO_CHAR(MAN_VAL.DT_EMISSIONE, 'YYYYMM') = TO_CHAR(MANDATO.DT_EMISSIONE, 'YYYYMM') And
              STATO != 'A') VALIDO,
(Select nvl(sum(MAN_INV_IN.IM_MANDATO), 0)
From  MANDATO MAN_INV_IN, DISTINTA_CASSIERE, DISTINTA_CASSIERE_DET
Where MAN_INV_IN.CD_CDS                        = DISTINTA_CASSIERE_DET.CD_CDS     AND
      MAN_INV_IN.ESERCIZIO                     = DISTINTA_CASSIERE_DET.ESERCIZIO  AND
      MAN_INV_IN.PG_MANDATO                    = DISTINTA_CASSIERE_DET.PG_MANDATO AND
      DISTINTA_CASSIERE.CD_CDS                 = DISTINTA_CASSIERE_DET.CD_CDS                 AND
      DISTINTA_CASSIERE.ESERCIZIO              = DISTINTA_CASSIERE_DET.ESERCIZIO              AND
      DISTINTA_CASSIERE.CD_UNITA_ORGANIZZATIVA = DISTINTA_CASSIERE_DET.CD_UNITA_ORGANIZZATIVA AND
      DISTINTA_CASSIERE.PG_DISTINTA            = DISTINTA_CASSIERE_DET.PG_DISTINTA and
      MAN_INV_IN.STATO                        != 'A' AND
      MAN_INV_IN.CD_CDS                        = MANDATO.CD_CDS And
      MAN_INV_IN.ESERCIZIO                     = MANDATO.ESERCIZIO AND
      MAN_INV_IN.CD_UNITA_ORGANIZZATIVA        = MANDATO.CD_UNITA_ORGANIZZATIVA And
      To_Char(MAN_INV_IN.dt_emissione, 'yyyymm') = To_Char(MANDATO.DT_EMISSIONE, 'YYYYMM') And
      To_Char(DISTINTA_CASSIERE.DT_INVIO, 'yyyymm') = To_Char(MANDATO.DT_EMISSIONE, 'YYYYMM')) INVIATO_IN,
(Select nvl(sum(MAN_INV_IN.IM_MANDATO), 0)
from  MANDATO MAN_INV_IN, DISTINTA_CASSIERE, DISTINTA_CASSIERE_DET
WHERE MAN_INV_IN.CD_CDS                        = DISTINTA_CASSIERE_DET.CD_CDS     AND
      MAN_INV_IN.ESERCIZIO                     = DISTINTA_CASSIERE_DET.ESERCIZIO  AND
      MAN_INV_IN.PG_MANDATO                    = DISTINTA_CASSIERE_DET.PG_MANDATO AND
      DISTINTA_CASSIERE.CD_CDS                 = DISTINTA_CASSIERE_DET.CD_CDS                 AND
      DISTINTA_CASSIERE.ESERCIZIO              = DISTINTA_CASSIERE_DET.ESERCIZIO              AND
      DISTINTA_CASSIERE.CD_UNITA_ORGANIZZATIVA = DISTINTA_CASSIERE_DET.CD_UNITA_ORGANIZZATIVA AND
      DISTINTA_CASSIERE.PG_DISTINTA            = DISTINTA_CASSIERE_DET.PG_DISTINTA and
      MAN_INV_IN.STATO                        != 'A' AND
      MAN_INV_IN.CD_CDS                        = MANDATO.CD_CDS And
      MAN_INV_IN.ESERCIZIO                     = MANDATO.ESERCIZIO AND
      MAN_INV_IN.CD_UNITA_ORGANIZZATIVA        = MANDATO.CD_UNITA_ORGANIZZATIVA And
      To_Char(MAN_INV_IN.dt_emissione, 'yyyymm') = To_Char(MANDATO.DT_EMISSIONE, 'YYYYMM') And
      To_Char(DISTINTA_CASSIERE.DT_INVIO, 'yyyymm') > To_Char(MANDATO.DT_EMISSIONE, 'YYYYMM')) INVIATO_OUT,
(Select nvl(sum(MAN_VAL.IM_MANDATO), 0)
        From MANDATO MAN_VAL
        Where MAN_VAL.CD_CDS = MANDATO.CD_CDS AND
              MAN_VAL.ESERCIZIO = MANDATO.ESERCIZIO AND
              MAN_VAL.CD_UNITA_ORGANIZZATIVA = MANDATO.CD_UNITA_ORGANIZZATIVA And
              TO_CHAR(MAN_VAL.DT_EMISSIONE, 'YYYYMM') = TO_CHAR(MANDATO.DT_EMISSIONE, 'YYYYMM') And
              STATO != 'A') -
(Select nvl(sum(MAN_INV_IN.IM_MANDATO), 0)
From  MANDATO MAN_INV_IN, DISTINTA_CASSIERE, DISTINTA_CASSIERE_DET
Where MAN_INV_IN.CD_CDS                        = DISTINTA_CASSIERE_DET.CD_CDS     AND
      MAN_INV_IN.ESERCIZIO                     = DISTINTA_CASSIERE_DET.ESERCIZIO  AND
      MAN_INV_IN.PG_MANDATO                    = DISTINTA_CASSIERE_DET.PG_MANDATO AND
      DISTINTA_CASSIERE.CD_CDS                 = DISTINTA_CASSIERE_DET.CD_CDS                 AND
      DISTINTA_CASSIERE.ESERCIZIO              = DISTINTA_CASSIERE_DET.ESERCIZIO              AND
      DISTINTA_CASSIERE.CD_UNITA_ORGANIZZATIVA = DISTINTA_CASSIERE_DET.CD_UNITA_ORGANIZZATIVA AND
      DISTINTA_CASSIERE.PG_DISTINTA            = DISTINTA_CASSIERE_DET.PG_DISTINTA and
      MAN_INV_IN.CD_CDS                        = MANDATO.CD_CDS And
      MAN_INV_IN.ESERCIZIO                     = MANDATO.ESERCIZIO AND
      MAN_INV_IN.CD_UNITA_ORGANIZZATIVA        = MANDATO.CD_UNITA_ORGANIZZATIVA And
      MAN_INV_IN.STATO                        != 'A' AND
      To_Char(MAN_INV_IN.dt_emissione, 'yyyymm') = To_Char(MANDATO.DT_EMISSIONE, 'YYYYMM') And
      To_Char(DISTINTA_CASSIERE.DT_INVIO, 'yyyymm') = To_Char(MANDATO.DT_EMISSIONE, 'YYYYMM')) -
(Select nvl(sum(MAN_INV_IN.IM_MANDATO), 0)
from  MANDATO MAN_INV_IN, DISTINTA_CASSIERE, DISTINTA_CASSIERE_DET
WHERE MAN_INV_IN.CD_CDS                        = DISTINTA_CASSIERE_DET.CD_CDS     AND
      MAN_INV_IN.ESERCIZIO                     = DISTINTA_CASSIERE_DET.ESERCIZIO  AND
      MAN_INV_IN.PG_MANDATO                    = DISTINTA_CASSIERE_DET.PG_MANDATO AND
      DISTINTA_CASSIERE.CD_CDS                 = DISTINTA_CASSIERE_DET.CD_CDS                 AND
      DISTINTA_CASSIERE.ESERCIZIO              = DISTINTA_CASSIERE_DET.ESERCIZIO              AND
      DISTINTA_CASSIERE.CD_UNITA_ORGANIZZATIVA = DISTINTA_CASSIERE_DET.CD_UNITA_ORGANIZZATIVA AND
      DISTINTA_CASSIERE.PG_DISTINTA            = DISTINTA_CASSIERE_DET.PG_DISTINTA and
      MAN_INV_IN.CD_CDS                        = MANDATO.CD_CDS And
      MAN_INV_IN.ESERCIZIO                     = MANDATO.ESERCIZIO AND
      MAN_INV_IN.CD_UNITA_ORGANIZZATIVA        = MANDATO.CD_UNITA_ORGANIZZATIVA And
      MAN_INV_IN.STATO                        != 'A' AND
      To_Char(MAN_INV_IN.dt_emissione, 'yyyymm') = To_Char(MANDATO.DT_EMISSIONE, 'YYYYMM') And
      To_Char(DISTINTA_CASSIERE.DT_INVIO, 'yyyymm') > To_Char(MANDATO.DT_EMISSIONE, 'YYYYMM')) TOT_DA_INVIARE
From MANDATO
;