package alarm;

import entiteti.Alarm;
import entiteti.Osluskivac;
import java.util.Date;
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
import javax.jms.ObjectMessage;
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
    
    @Resource(lookup = "Red")
    public static Queue queue;
    
    public static JMSContext context;
    public static JMSProducer producer;
    public static JMSConsumer consumer;
    
    private static EntityManagerFactory emf;
    public static EntityManager em;
    
    private static String ring;
    
    public static void main(String[] args) {
            emf = Persistence.createEntityManagerFactory("AlarmPU");
            em = emf.createEntityManager();
            
            context = conn.createContext();
            producer = context.createProducer();
            consumer = context.createConsumer(queue, "tip = 'Alarm'");
            ring = "https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze";
            //pokrentanje niti koja ide kroz sve alarme
            Osluskivac osluskivac = new Osluskivac();
            osluskivac.start();
            boolean kraj = false;
            while(true) {
                try {
                    Message mes = consumer.receive();
                    int mod = mes.getIntProperty("mod");
                    
                    switch(mod){
                        case 0:{
                            kraj = true;
                            break;
                        }
                        case 1:{
                            napraviAlarm(mes, mod);
                            break;
                        }
                        case 2:{
                            listaAlarma();
                            break;
                        }
                        case 3:{
                            noviTon(mes);
                            break;
                        }
                        //alarm koji navija obaveza
                        case 4:{
                            napraviAlarm(mes, mod);
                            break;
                        }
                        case 5:{
                            promeniAlarm(mes);
                            break;
                        }
                        case 6:{
                            nadjiAlarm(mes);
                            break;
                        }
                        case 7:{
                            obrisiAlarm(mes);
                            break;
                        }
                    }
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(kraj) break;
            } 
            
            //gasi nit koja slusa i ceka je
        osluskivac.prekini();
        try {
            osluskivac.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void listaAlarma() {
        try {
            TypedQuery<Alarm> q = em.createQuery("SELECT a FROM Alarm AS a ORDER BY a.id", Alarm.class);
            List<Alarm> lista = q.getResultList();
            //pakovanje u string
            StringBuilder res = new StringBuilder();
            for(Alarm a: lista)
                res.append(a.getId() + " " + a.getVreme() + " " + a.getTon() + "\n");
            
            TextMessage msg = context.createTextMessage();
            msg.setText(res.toString());
            
            msg.setStringProperty("tip", "KUredjaj");
            producer.send(queue, msg);
            System.out.println("Poslata je lista alarma: ");
            System.out.println(res.toString());
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void napraviAlarm(Message mes, int mod) {
        try {
            ObjectMessage obj = (ObjectMessage) mes;
            Alarm alarm = (Alarm) obj.getObject();
            
            //provera da li je poruka stigla
            System.out.println("Stigla je poruka za navijanje alarma sa vremenom " + alarm.getVreme());
            
            //postavljanje difoltne pesme
            if(alarm.getTon() == null) alarm.setTon(ring);
            
            //upisivanje u bazu
            em.getTransaction().begin(); 
            em.persist(alarm);
            em.getTransaction().commit();
            
            if(mod == 1){
                TextMessage t = context.createTextMessage();
                t.setStringProperty("tip", "KUredjaj");
                t.setText("Alarm je uspesno navijen!");
                producer.send(queue, t);
            }
            
            if(mod == 4){
                ObjectMessage o = context.createObjectMessage(alarm);
                //System.out.println("Stigla je poruka za navijanje alarma");
                o.setStringProperty("tip", "Planer");
                //vrati upisani alarm
                producer.send(queue, o);
            }
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //podesavanje novog tona alarma
    private static void noviTon(Message mes){
        try {
            TextMessage txt = (TextMessage) mes;
            int id = (int)txt.getLongProperty("id");
            
            System.out.println("Stigla je poruka za menjanje tona " + txt.getText());
            
            //update
            em.getTransaction().begin();   
            Alarm a = em.find(Alarm.class, id);
            a.setTon(txt.getText());
            em.persist(a);
            em.getTransaction().commit();
            
            TextMessage t = context.createTextMessage("Ton je promenjen na " + txt.getText());
            
            t.setStringProperty("tip", "KUredjaj");
            producer.send(queue, t);
            
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void nadjiAlarm(Message mes) {
        try {
            TextMessage txt = (TextMessage) mes;
            int id = (int)txt.getIntProperty("id");
            Alarm alarm = em.find(Alarm.class, id);
            
            ObjectMessage obj = context.createObjectMessage(alarm);
            obj.setStringProperty("tip", "Planer");
            producer.send(queue, obj);
            
            System.out.println();
            System.out.println("poslat id alarma");
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private static void promeniAlarm(Message mes) {
        try {
            ObjectMessage o = (ObjectMessage) mes;
            Alarm alarm = (Alarm) o.getObject();
            
            System.out.println("Stigla je poruka za menjanje vremena za alarm sa id: " + alarm.getId());
            
            em.getTransaction().begin();
            Alarm alarm2 = em.find(Alarm.class, alarm.getId());
            alarm2.setVreme(alarm.getVreme());
            em.persist(alarm2);
            em.getTransaction().commit();  
            
            TextMessage tx = context.createTextMessage("Promenjen je alarm za odredjenu obavezu");
            tx.setStringProperty("tip", "Planer");
            producer.send(queue, tx);
            
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    private static void obrisiAlarm(Message mes) {
        try {
            TextMessage txt = (TextMessage) mes;
            int id = (int)txt.getIntProperty("id");
            Alarm alarm = em.find(Alarm.class, id);
            em.getTransaction().begin();
            em.remove(alarm);
            em.getTransaction().commit();
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
