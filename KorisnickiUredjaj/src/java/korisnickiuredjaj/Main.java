/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korisnickiuredjaj;

import entiteti.Alarm;
import entiteti.Obaveza;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;

/**
 *
 * @author Andjela
 */

public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory conn;
    
    @Resource(lookup="Red")
    private static Queue queue;
    
    private static JMSContext context;
    private static JMSProducer producer;
    private static JMSConsumer consumer;
    private static Scanner input;
    
    public static void main(String[] args) {
        context = conn.createContext();
        producer = context.createProducer();
        consumer = context.createConsumer(queue, "tip = 'KUredjaj'");
        
        input = new Scanner(System.in);
        while(true) {
            System.out.println();
            System.out.println("Izabrati opciju: ");
            System.out.println("1) Uredjaj za reprodukciju zvuka ");
            System.out.println("2) Alarm");
            System.out.println("3) Planer");
            System.out.println("0) Zavrsetak rada");
            System.out.println();
            boolean kraj = false;
            int i = input.nextInt();
            switch(i) {
                case 0:{
                    //isprazniRed();
                    ugasi();
                    kraj = true;
                    break;
                }
                case 1:{
                    System.out.println("Izabrati opciju: ");
                    System.out.println("1) Reprodukcija zadate pesme ");
                    System.out.println("2) Prethodno reprodukovane pesme ");
                    System.out.println("0) Nazad ");
                    System.out.println();
                    
                    int opcija = input.nextInt();
                    switch(opcija){
                        case 1:{
                            reprodukcija();
                            break;
                        }
                        case 2:{
                            prethodnoReprodukovane();
                            break;
                        }
                    }
                    break;
                }
                case 2:{
                    System.out.println("Izaberite opciju: ");
                    System.out.println("1) Navijanje alarma za zadati trenutak ");
                    System.out.println("2) Navijanje periodicnog alarma  ");
                    System.out.println("3) Navijanje alarma za ponudjeni trenutak ");
                    System.out.println("4) Konfigurisanje zvona alarma ");
                    System.out.println("0) Povratak unazad");
                    System.out.println();
                    
                    int k = input.nextInt();
                    
                    switch(k){
                        case 0:{
                            break;
                        }
                        case 1:{
                            navijAlarmZaTrenutak(false);
                            break;
                        }
                        case 2:{
                            navijAlarmZaTrenutak(true);
                            break;
                        }
                        case 3:{
                            ponudjeniAlarm();
                            break;
                        }
                        case 4:{
                            konfiguracijaTona();
                            break;
                        }
                    }
                    break;
                }
                case 3:{
                    System.out.println("Izabrati opciju: ");
                    System.out.println("1) Izlistavanje obaveza ");
                    System.out.println("2) Dodavanje obaveze  ");
                    System.out.println("3) Promena obaveze ");
                    System.out.println("4) Brisanje obaveze ");
                    System.out.println("0) Povratak unazad");
                    System.out.println();
                    
                    int k = input.nextInt();
                    switch(k){
                        case 0:{
                            break;
                        }
                        case 1:{
                            izlistajObaveze();
                            break;
                        }
                        case 2:{
                            napraviObavezu();
                            break;
                        }
                        case 3:{
                            promenaObaveze();
                            break;
                        }
                        case 4: {
                            obrisiObavezu();
                            break;
                        }
                    }
                }
            }  
            if(kraj) break;
        }
    }
    
    //funkcija za praznjenje reda ako treba nekad
    private static void isprazniRed(){
        JMSConsumer cistac = context.createConsumer(queue);
    
        while(true){
            Message mes = cistac.receive();
            
            try {
                String prop = mes.getStringProperty("tip");
                int br = mes.getIntProperty("mod");
                
                System.out.println("tip - " + prop + " mod - " + br);
                
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void ugasi(){
        try {
            //mod za gasenje je 0
            Message m1 = context.createMapMessage();
            m1.setStringProperty("tip", "RZvuka");
            m1.setIntProperty("mod", 0);
            
            producer.send(queue, m1);
            
            Message m2 = context.createMapMessage();
            m2.setStringProperty("tip", "Alarm");
            m2.setIntProperty("mod", 0);
            
            producer.send(queue, m2);
            
            Message m3 = context.createMapMessage();
            m3.setStringProperty("tip", "Planer");
            m3.setIntProperty("mod", 0);
            
            producer.send(queue, m3);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void reprodukcija(){
        try {
            System.out.println();
            System.out.println("Unesite ime korisnika: ");
            input.nextLine();
            String imeKorisnika = input.nextLine();
            System.out.println();
            
            System.out.println("Unesite naziv zadate pesme: ");
            String pesma = input.nextLine();
            System.out.println();
            
            System.out.println("Unesite link zadate pesme: ");
            String link = input.nextLine();
            System.out.println();
            
            TextMessage msg = context.createTextMessage();
            msg.setText(pesma);
            
            msg.setStringProperty("tip", "RZvuka");
            
            msg.setIntProperty("mod", 1);
            msg.setStringProperty("korisnik", imeKorisnika);
            msg.setStringProperty("link", link);
            
            producer.send(queue, msg);
            
            //ispis poslate poruke
            System.out.println("Poslata je poruka sa pesmom " + pesma + ", korisnik je " + imeKorisnika + ", link je " + link + " i modom 1");
            
            TextMessage txt = (TextMessage) consumer.receive();
            System.out.println();
            System.out.println(txt.getText());    
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void prethodnoReprodukovane(){
        try {
            System.out.println();
            System.out.println("Unesite ime korisnika: ");
            input.nextLine();
            String imeKorisnika = input.nextLine();
            System.out.println();
            
            Message msg = context.createTextMessage();
            msg.setStringProperty("tip", "RZvuka");
            msg.setStringProperty("korisnik", imeKorisnika);
            
            //mod za listanje je 2
            msg.setIntProperty("mod", 2);
            producer.send(queue, msg);
            System.out.println("Poslat je zahtev za izlistavanje od korisnika: " + imeKorisnika);
            
            //prijem liste
            TextMessage txt = (TextMessage) consumer.receive();
            System.out.println();
            System.out.println("Lista prethodno pustenih pesama: ");
            System.out.println(txt.getText());   
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //navija obican ili periodicni alarm za zadati trenutak
    private static void navijAlarmZaTrenutak(boolean periodican){
        try {
            //perioda
            int add_h = 0;
            int add_min = 0;
            
            if(periodican){
                System.out.println("Uneti na koliko casova alarm treba da se ponavlja: ");
                add_h = input.nextInt();
                System.out.println("Uneti na koliko minuta alarm treba da se ponavlja: ");
                add_min = input.nextInt();
            }
            
            //ucitavanje vremena za zvono alarma
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            System.out.println("Uneti datum i vreme za zadati alarm po formatu yyyy-MM-dd hh:mm:ss");
            
            input.nextLine();
            String datumStr = input.nextLine();
            
            Date datum = format.parse(datumStr);
            
            //kreiranje poruke koja se salje
            Alarm alarm = new Alarm();
       
            alarm.setVreme(datum);
            alarm.setH(add_h);
            alarm.setMin(add_min);
            
            ObjectMessage msg = context.createObjectMessage(alarm);
            msg.setStringProperty("tip", "Alarm");
            //mod za navijanje alarma je 1
            msg.setIntProperty("mod", 1);
            
            producer.send(queue, msg);
            
            //ispis poslate poruke
            System.out.println("Salje se poruka za navijanje alarma sa vremenom " + datumStr);
            
            TextMessage txt = (TextMessage) consumer.receive();
            System.out.println(txt.getText());
            
        } catch (ParseException | JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    //navijanje alarma za ponudjeni trenutak
    private static void ponudjeniAlarm(){
        try {
            int add_h = 0;
            int add_min = 0;
            
            System.out.println("Da li zelite da se ponavlja (1 - da, 0 - ne)");
            int k = input.nextInt();
            if(k == 1){
                System.out.println("Uneti na koliko casova alarm treba da se ponavlja: ");
                add_h = input.nextInt();
                
                System.out.println("Uneti na koliko minuta alarm treba da se ponavlja: ");
                add_min = input.nextInt();
                
            }
            
            //trenutni
            Date cur = new Date();
            
            //5 vremena: 15 min, pola sata, sat, dva sata i 12h
            int kap = 5;
            
            Date [] datumi = new Date[kap];
            datumi[0] = new Date(cur.getTime() + (long)(15*60000));
            datumi[1] = new Date(cur.getTime() + (long)(30*60000));
            datumi[2] = new Date(cur.getTime() + (long)(60*60000));
            datumi[3] = new Date(cur.getTime() + (long)(120*60000));
            datumi[4] = new Date(cur.getTime() + (long)(720*60000));
 
            System.out.println("Izabrati jedan od trenutaka: ");
            for(int i = 0; i < kap; i++)
                System.out.println(i + "    " + datumi[i]);
            
            k = input.nextInt();
            Date datum = datumi[k];
            
            Alarm alarm = new Alarm();
            
            alarm.setVreme(datum);
            alarm.setH(add_h);
            alarm.setMin(add_min);
            
            ObjectMessage msg = context.createObjectMessage(alarm);
            msg.setStringProperty("tip", "Alarm");
            msg.setIntProperty("mod", 1);
            producer.send(queue, msg);
            System.out.println("Navija se alarm za " + datum);
            
            TextMessage txt = (TextMessage) consumer.receive();
            System.out.println(txt.getText());
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //konfiguracija tona alarma
    private static void konfiguracijaTona(){
        try {
            Message msg = context.createTextMessage();
            msg.setStringProperty("tip", "Alarm");
            
            msg.setIntProperty("mod", 2);
            producer.send(queue, msg);
            
            //prijem liste
            TextMessage txt = (TextMessage) consumer.receive();
            System.out.println("Izabrati jedan od alarma: (uneti id)");
            System.out.println("Id        Vreme                       Ton");
            System.out.println(txt.getText());
            
            //citanje izbora alarma i novog tona
            int k = input.nextInt();
            System.out.println("Uneti novi ton: ");
            input.nextLine();
            String noviTon = input.nextLine();
            
            //slanje poruke
            TextMessage t = context.createTextMessage(noviTon);
            // mod za nov ton je 3
            t.setIntProperty("mod", 3);
            t.setStringProperty("tip", "Alarm");
            t.setLongProperty("id", k);
            
            producer.send(queue, t);
            
            //prijem odgovora
            TextMessage x = (TextMessage) consumer.receive();
            System.out.println(x.getText());
            
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //izlistavanje obaveza
    private static void izlistajObaveze(){
        try {
            Message msg = context.createTextMessage();
            
            msg.setStringProperty("tip", "Planer");
            
            //mod za listanje je 1
            msg.setIntProperty("mod", 1);
            producer.send(queue, msg);
            //ispis poslate poruke
            System.out.println("Poslat je zahtev za izlistavanje obaveza");
            
            //prijem liste
            TextMessage txt = (TextMessage) consumer.receive();
            System.out.println();
            System.out.println("Spisak obaveza: ");
            System.out.println(txt.getText());
            
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //pravljenje obaveze
    private static void napraviObavezu(){
        try {
            //ucitavanje pocetka obaveze
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            System.out.println("Uneti datum i vreme po formatu yyyy-MM-dd hh:mm:ss");
            
            input.nextLine();
            String datumStr = input.nextLine();
            Date datum = format.parse(datumStr);
            
            System.out.println("Uneti pocetnu poziciju: (x za trenutnu lokaciju)");
            //input.nextLine();
            String start = input.nextLine();
            
            System.out.println("Uneti krajnju destinaciju: (x za trenutnu lokaciju)");
            //input.nextLine();
            String end = input.nextLine();
            
            System.out.println("Uneti opis obaveze: ");
            String opis = input.nextLine();       
            
            Obaveza obaveza = new Obaveza();
            obaveza.setVreme(datum);
            obaveza.setStart(start);
            obaveza.setDestinacija(end);
            obaveza.setOpis(opis);
            
            System.out.println("Da li zelite da se navije alarm? (1 - da, 0 - ne)" );
            int k = input.nextInt();
            boolean flag = false;
            if(k == 1) flag = true;
            
            System.out.println();
            System.out.println("Salje se poruka za pravljenje obaveze " + obaveza.getOpis() + " za destinaciju " + obaveza.getDestinacija() + " i vreme " + obaveza.getVreme());
            ObjectMessage msg = context.createObjectMessage(obaveza);
            msg.setStringProperty("tip", "Planer");
            //da li se navija alarm
            msg.setBooleanProperty("alarm", flag);
            
            //mod za dodavanje obaveze je 2
            msg.setIntProperty("mod", 2);
            
            producer.send(queue, msg);
            System.out.println("Obaveza je uspesno evidentirana. ");
            
        } catch (ParseException | JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void promenaObaveze(){
        try {
            izlistajObaveze();
            System.out.println("Izabrati jednu od obaveza: ");
            //System.out.println("OVDE NE TREBA DA DODJE DRUGI PUT");
            //posalji redni broj obaveze, planer treba da vrati objekat
            int k = input.nextInt();
            TextMessage ind = context.createTextMessage();
            ind.setStringProperty("tip", "Planer");
            //mod za promenu obaveze je 
            ind.setIntProperty("mod", 3);
            ind.setIntProperty("id", k);
            System.out.println("Salje se redni broj obaveze " + k);
            producer.send(queue, ind);
            
            //System.out.println("STIGAO PROBLEMATICAN OBJ");
            // objekat obaveze koja se menja
            ObjectMessage obj = (ObjectMessage) consumer.receive();
            Obaveza obaveza = (Obaveza) obj.getObject();
            System.out.println("Stigao objekat obaveze koja se menja: " + obaveza.getOpis());
            
            System.out.println("Izaberite opciju: ");
            System.out.println("1) Promena vremena pocetka obaveze ");
            System.out.println("2) Promena destinacije ");
            System.out.println("3) Promena pocetne pozicije ");
            System.out.println("4) Promena opisa obaveze ");
            
            k = input.nextInt();
            switch(k){
                case 1:{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    System.out.println("Uneti novi datum i vreme po formatu yyyy-MM-dd hh:mm:ss: ");
            
                    input.nextLine();
                    String datumStr = input.nextLine();
                    Date datum = format.parse(datumStr);
                    
                    obaveza.setVreme(datum);
                    
                    break;
                }
                case 2:{
                    System.out.println("Uneti novu destinaciju: ");
                    input.nextLine();
                    String novi= input.nextLine();
                    
                    obaveza.setDestinacija(novi);
                    
                    break;
                }
                case 3:{
                    System.out.println("Uneti novu startnu poziciju: ");
                    input.nextLine();
                    String novi= input.nextLine();
                    
                    obaveza.setStart(novi);
                    
                    break;
                }
                case 4:{
                    System.out.println("Uneti novi opis: ");
                    input.nextLine();
                    String opis= input.nextLine();
                    
                    obaveza.setOpis(opis);
                    break;
                }
                
            }
            
            //vrati izmenjeni objekat
            ObjectMessage objFinal = context.createObjectMessage(obaveza);
            objFinal.setStringProperty("tip", "Planer");
            producer.send(queue, objFinal);
            
        } catch (JMSException | ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void obrisiObavezu(){
        try {
            izlistajObaveze();
            System.out.println("Izabrati jednu od obaveza: ");
            
            //posalji redni broj obaveze koja treba da se obrise
            int k = input.nextInt();
            TextMessage ind = context.createTextMessage();
            ind.setIntProperty("id", k);
            ind.setStringProperty("tip", "Planer");
            ind.setIntProperty("mod", 4);
            producer.send(queue, ind);
            
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
