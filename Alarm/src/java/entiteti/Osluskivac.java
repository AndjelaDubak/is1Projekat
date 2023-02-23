package entiteti;

import alarm.Main;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.persistence.TypedQuery;

/**
 *
 * @author Andjela
 */
public class Osluskivac extends Thread {
    private int milisInAMinute = 60000;
    private long time = System.currentTimeMillis();
    private volatile boolean radi = true;

    Runnable update = new Runnable() {
        public void run() {
            //dohvatanje trenutnog vremena
            Date cur = new Date();
            //System.out.println("Sada je " + cur);
            
            //dohvatanje svih alarma
            TypedQuery<Alarm> q = Main.em.createQuery("SELECT a FROM Alarm AS a ORDER BY a.id", Alarm.class);
            
            Main.em.getTransaction().begin();
            List<Alarm> lista = q.getResultList();
            Main.em.getTransaction().commit();
            
            //provera za svaki alarm
            for(Alarm a: lista){
                try {
                    if(a.getVreme().getYear() != cur.getYear()) continue;
                    if(a.getVreme().getMonth()!= cur.getMonth()) continue;
                    if(a.getVreme().getDate()!= cur.getDate()) continue;
                    if(a.getVreme().getHours()!= cur.getHours()) continue;
                    if(a.getVreme().getMinutes()!= cur.getMinutes()) continue;
                    
                    System.out.println("Nadjen je alarm " + a.getId());
                    
                    //poruka za pustanje tona
                    String pesma = a.getTon();
                    TextMessage msg = Main.context.createTextMessage();
                    msg.setText(pesma);
                    msg.setStringProperty("tip", "RZvuka");
                    System.out.println("Salje se poruka za pustanje pesme za alarm ");
                    //mod za pustanje pesme je 3
                    msg.setIntProperty("mod", 3);
                    Main.producer.send(Main.queue, msg);
                    
                    //ako je inkrement nula nema sta da se azurira
                    if(a.getH() == 0 && a.getMin() == 0) continue;
                    
                    //update na sledeci inkrement
                    long mili = 0;
                    mili += a.getH() * 60*60000;
                    mili += a.getMin() *60000;
                    
                    Date novo = new Date(a.getVreme().getTime() + mili);
                    
                    Alarm al = Main.em.find(Alarm.class, a.getId());
                    
                    Main.em.getTransaction().begin();
                    
                    a.setVreme(novo);
                    Main.em.persist(a);
                    
                    Main.em.getTransaction().commit();
                } catch (JMSException ex) {
                    Logger.getLogger(Osluskivac.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                
            
        }
    };

    Timer timer = new Timer();

    public void prekini(){
        radi = false;
    }
    
    public void run(){
        timer.schedule(new TimerTask() {
            public void run() {
                update.run();
            }
        }, time % milisInAMinute, milisInAMinute);

        // This will update for the current minute, it will be updated again in at most one minute.
        //update.run();
        
        while(radi){
                
        }
        
        //stops task
        timer.cancel();
    }
}
