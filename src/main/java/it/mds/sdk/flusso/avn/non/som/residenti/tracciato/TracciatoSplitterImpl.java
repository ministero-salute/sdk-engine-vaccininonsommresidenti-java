/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.avn.non.som.residenti.tracciato;


import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import it.mds.sdk.flusso.avn.non.som.residenti.parser.regole.RecordDtoAvnNonSomResidenti;
import it.mds.sdk.flusso.avn.non.som.residenti.parser.regole.conf.ConfigurazioneFlussoAvnNonSomResidenti;
import it.mds.sdk.flusso.avn.non.som.residenti.tracciato.bean.output.prestazioni.nonsomministrate.Modalita;
import it.mds.sdk.flusso.avn.non.som.residenti.tracciato.bean.output.prestazioni.nonsomministrate.ObjectFactory;
import it.mds.sdk.flusso.avn.non.som.residenti.tracciato.bean.output.prestazioni.nonsomministrate.VaccinazioniNonEffettuate;
import it.mds.sdk.gestorefile.GestoreFile;
import it.mds.sdk.gestorefile.factory.GestoreFileFactory;
import it.mds.sdk.libreriaregole.tracciato.TracciatoSplitter;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

@Component("tracciatoSplitterAvnNonSomRes")
@Slf4j
public class TracciatoSplitterImpl implements TracciatoSplitter<RecordDtoAvnNonSomResidenti> {

    @Override
    public List<Path> dividiTracciato(Path tracciato) {
        return null;
    }

    @Override
    public List<Path> dividiTracciato(List<RecordDtoAvnNonSomResidenti> records, String idRun) {
        try {
            ConfigurazioneFlussoAvnNonSomResidenti conf = new ConfigurazioneFlussoAvnNonSomResidenti();

            //XML PRESTAZIONI NON SOMM
            it.mds.sdk.flusso.avn.non.som.residenti.tracciato.bean.output.prestazioni.nonsomministrate.ObjectFactory objPrestNonEff = new it.mds.sdk.flusso.avn.non.som.residenti.tracciato.bean.output.prestazioni.nonsomministrate.ObjectFactory();
            VaccinazioniNonEffettuate vaccinazioniNonEffettuate = objPrestNonEff.createVaccinazioniNonEffettuate();

            vaccinazioniNonEffettuate.setCodiceRegione(records.get(0).getCodRegione());
            vaccinazioniNonEffettuate.setModalita(Modalita.fromValue(records.get(0).getModalita()));

            for (RecordDtoAvnNonSomResidenti r : records) {
                if (!r.getTipoTrasmissione().equalsIgnoreCase("NM")) {
                    creaPrestazioniNonEffettuateXml(r, vaccinazioniNonEffettuate, objPrestNonEff);
                }
            }

            GestoreFile gestoreFile = GestoreFileFactory.getGestoreFile("XML");

            //recupero il path del file xsd di vaccinazioni
            URL urlVaccXsd = this.getClass().getClassLoader().getResource("VNX.xsd");
            log.debug("URL dell'XSD per la validazione idrun {} : {}", idRun, urlVaccXsd);

            //scrivi XML PRESTAZIONI
            String pathPrestazioniNonEff = conf.getXmlOutput().getPercorso() + "SDK_AVN_VNX_" + records.get(0).getCampiInput().getPeriodoRiferimentoInput() + "_" + idRun + ".xml";
            //gestoreFile.scriviDto(vaccinazioniNonEffettuate, pathPrestazioniNonEff, urlVaccXsd);

            return List.of(Path.of(pathPrestazioniNonEff));

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            log.error("[{}].dividiTracciato  - records[{}]  - idRun[{}] -" + e.getMessage(),
                    this.getClass().getName(),
                    e
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossibile validare il csv in ingresso. message: " + e.getMessage());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    private void creaPrestazioniNonEffettuateXml(RecordDtoAvnNonSomResidenti r, VaccinazioniNonEffettuate vaccinazioniNonEffettuate,
                                                 it.mds.sdk.flusso.avn.non.som.residenti.tracciato.bean.output.prestazioni.nonsomministrate.ObjectFactory objPrestNonEff) {

        VaccinazioniNonEffettuate.Assistito currentAssistito = vaccinazioniNonEffettuate.getAssistito().
                stream()
                .filter(ass -> r.getCodIdAssistito().equalsIgnoreCase(ass.getIdAssistito()))
                .findFirst()
                .orElse(null);
        if (currentAssistito == null) {
            currentAssistito = creaAssistito(r.getCodIdAssistito(), objPrestNonEff);
            vaccinazioniNonEffettuate.getAssistito().add(currentAssistito);

        }

        VaccinazioniNonEffettuate.Assistito.MancataVaccinazione currentMancataVaccinazione = currentAssistito.getMancataVaccinazione().
                stream()
                .filter(mv -> (r.getCodiceAntigene().equalsIgnoreCase(mv.getCodAntigene()) && r.getDose().equals(mv.getDose())))
                .findFirst()
                .orElse(null);
        if (currentMancataVaccinazione == null) {
            currentMancataVaccinazione = creaMancataVaccinazione(r, objPrestNonEff);
            currentAssistito.getMancataVaccinazione().add(currentMancataVaccinazione);

        }
    }

    private VaccinazioniNonEffettuate.Assistito creaAssistito(String idAssistito,
                                                              it.mds.sdk.flusso.avn.non.som.residenti.tracciato.bean.output.prestazioni.nonsomministrate.ObjectFactory objPrestNonEff) {
        VaccinazioniNonEffettuate.Assistito assistito = objPrestNonEff.createVaccinazioniNonEffettuateAssistito();
        assistito.setIdAssistito(idAssistito);
        return assistito;
    }

    private VaccinazioniNonEffettuate.Assistito.MancataVaccinazione creaMancataVaccinazione(RecordDtoAvnNonSomResidenti r,
                                                                                            it.mds.sdk.flusso.avn.non.som.residenti.tracciato.bean.output.prestazioni.nonsomministrate.ObjectFactory objPrestNonEff) {
        VaccinazioniNonEffettuate.Assistito.MancataVaccinazione mancataVaccinazione = objPrestNonEff.createVaccinazioniNonEffettuateAssistitoMancataVaccinazione();

        mancataVaccinazione.setCodAntigene(r.getCodiceAntigene());
        mancataVaccinazione.setDose(r.getDose() != null ? BigInteger.valueOf(r.getDose()) : null);
        mancataVaccinazione.setMotivazione(r.getMotivazione());
        try {
            mancataVaccinazione.setDataNonEffettuazione(r.getDataNonEffettuazione() != null ? DatatypeFactory.newInstance().newXMLGregorianCalendar(r.getDataNonEffettuazione()) : null);
        } catch (DatatypeConfigurationException e) {
            log.error("Error convert xmlGregorianCalendar DataNonEffettuazione", e);
        }
        mancataVaccinazione.setTipoTrasmissione(r.getTipoTrasmissione());

        return mancataVaccinazione;
    }

    public VaccinazioniNonEffettuate creaVaccinazioniNonEffettuate(List<RecordDtoAvnNonSomResidenti> records, VaccinazioniNonEffettuate vaccinazioniNonEffettuate) {

        //Imposto gli attribute element
        String modalita = records.get(0).getModalita();
        String codiceRegione = records.get(0).getCodRegione();

        if (vaccinazioniNonEffettuate == null) {
            ObjectFactory objAnagVaccSomm = new ObjectFactory();
            vaccinazioniNonEffettuate = objAnagVaccSomm.createVaccinazioniNonEffettuate();
            vaccinazioniNonEffettuate.setCodiceRegione(codiceRegione);
            vaccinazioniNonEffettuate.setModalita(Modalita.fromValue(modalita));

            for (RecordDtoAvnNonSomResidenti r : records) {
                if (!r.getTipoTrasmissione().equalsIgnoreCase("NM")) {
                    creaPrestazioniNonEffettuateXml(r, vaccinazioniNonEffettuate, objAnagVaccSomm);
                }
            }

        }
        return vaccinazioniNonEffettuate;
    }

}
