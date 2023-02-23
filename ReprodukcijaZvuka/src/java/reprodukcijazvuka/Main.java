
package reprodukcijazvuka;

import entiteti.Pesma;
import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author Andjela
 */
public class Main {
    
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory conn;
    
    @Resource(lookup= "Red")
    private static Queue queue;
    
    private static JMSContext context;
    private static JMSProducer producer;
    private static JMSConsumer consumer;
    
    private static EntityManagerFactory emf;
    private static EntityManager em;
    
    public static void main(String[] args) {
        emf = Persistence.createEntityManagerFactory("ReprodukcijaZvukaPU");
        em = emf.createEntityManager();
        
        context = conn.createContext();
        producer = context.createProducer();
        
        consumer = context.createConsumer(queue, "tip = 'RZvuka'");
        boolean kraj = false;
        
        while(true) {
            try {
                // 0 - kraj rada; 1 - pustanje pesme; 2 - sve pesme 3 - pesma za Alarm
                Message mes = consumer.receive();
                int mod = mes.getIntProperty("mod");
                //System.out.println("Mod je: " + mod);
                
                switch(mod) {
                    case 0:{
                        kraj = true;
                        break;
                    }
                    case 1:{
                        pustiPesmu(mes,mod);
                        break;
                    }
                    case 2:{
                        posaljiPesme(mes);
                        break;
                    }
                    case 3:{
                        pustiZaAlarm(mes);
                        break;
                    }
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(kraj)break;
        }
        
        //emf.close();
    }
    
    private static void pustiPesmu(Message mes, int mod) {
        try {
            TextMessage txt = (TextMessage)mes;
            String pesma = txt.getText();
            String link = txt.getStringProperty("link");
            String korisnik = txt.getStringProperty("korisnik");
            //STIGLA PORUKA
            System.out.println("Stigla je pesma " + pesma + " od korisnika " + korisnik + ", link je: " + link + " a mod je " + mod);
            pusti(link);
            
            em.getTransaction().begin();
            Pesma p = new Pesma();
            p.setNaziv(pesma);
            p.setKorisnik(korisnik);
            em.persist(p);
            em.getTransaction().commit();
            
            TextMessage t = context.createTextMessage();
            t.setStringProperty("tip", "KUredjaj");
            t.setText("Pesma " + pesma + "je uspesno pustena!"); 
            producer.send(queue, t);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void pustiZaAlarm(Message mes) {
        try {
            System.out.println("Stigla je poruka za pustanje alarma");
            TextMessage txt = (TextMessage)mes;
            String pesma = txt.getText();
            pusti(pesma);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void posaljiPesme(Message mes){
        try {
            TextMessage txt = (TextMessage)mes;
            String korisnik = txt.getStringProperty("korisnik");
            System.out.println("Stigle poruka za izlistavanje pesama od: " + korisnik);
            TypedQuery<Pesma> q = em.createQuery("SELECT p FROM Pesma p WHERE p.korisnik = :kor ORDER BY p.id", Pesma.class);
            q.setParameter("kor", korisnik);
            List<Pesma> lista = q.getResultList();
            
            //pakovanje u string
            StringBuilder res = new StringBuilder();
            for(Pesma p: lista) {
                res.append(p.getNaziv()).append("\n");
            }
            
            TextMessage msg = context.createTextMessage();
            msg.setText(res.toString());
            msg.setStringProperty("tip", "KUredjaj");
            producer.send(queue, msg);
            
            //ispis poslate poruke
            System.out.println("Poslata je lista pesama");
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void pusti(String link){
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(link));
        } catch (Exception  ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    /*TypedQuery<Pesma> selectQuery = em.createQuery("SELECT p FROM Pesma p WHERE p.naziv = :pesmaNaziv AND p.korisnik = :kor", Pesma.class);
            selectQuery.setParameter("pesmaNaziv", pesma);
            selectQuery.setParameter("kor", korisnik);*/
            
            //List<Pesma> pesme = selectQuery.getResultList();
            /* if(pesme.isEmpty()){
                
            }*/
}


    
   

