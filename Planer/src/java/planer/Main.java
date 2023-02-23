package planer;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import entiteti.Alarm;
import entiteti.Obaveza;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
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
    private static Queue queue;
    
    private static JMSContext context;
    private static JMSProducer producer;
    private static JMSConsumer consumer;
    
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static DatabaseReader dbReader;
    
    public static void main(String[] args) {
        emf = Persistence.createEntityManagerFactory("PlanerPU");
        em = emf.createEntityManager();
        
        context = conn.createContext();
        producer = context.createProducer();
        consumer = context.createConsumer(queue, "tip = 'Planer'");
        
        //baza za trazenje trenutne lokacije
        String dbLocation = "C:\\Users\\Andjela\\Downloads\\GeoLite2-City_20210914.tar\\GeoLite2-City_20210914\\GeoLite2-City.mmdb";
        File database = new File(dbLocation);
        try {
            dbReader = new DatabaseReader.Builder(database).build();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                        listaObaveza();
                        break;
                    }
                    case 2:{
                        dodajObavezu(mes);
                        break;
                    }
                    case 3:{
                        promeniObavezu(mes);
                        break;
                    }
                    case 4:{
                        obrisiObavezu(mes);
                        break;
                    }
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(kraj) break;
        }
    }
    
    private static void listaObaveza(){
        try {
            TypedQuery<Obaveza> q = em.createQuery("SELECT o FROM Obaveza AS o ORDER BY o.id", Obaveza.class);
            List<Obaveza> lista = q.getResultList();
            
            System.out.println("Stigla je poruka za izlistavanje obaveza");
            //pakovanje u string
            StringBuilder res = new StringBuilder();
            for(Obaveza o: lista)
                res.append(o.getId() + " " +  o.getStart() + " " + o.getDestinacija() + " " + o.getVreme() + " " + o.getOpis() + "\n");
            
            TextMessage msg = context.createTextMessage();
            msg.setText(res.toString());
            
            msg.setStringProperty("tip", "KUredjaj");
            
            producer.send(queue, msg);
            
            //ispis poslate poruke
            System.out.println();
            System.out.println("Poslata je lista obaveza");
            System.out.println(res.toString());
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void dodajObavezu(Message mes){
        try {
            ObjectMessage obj = (ObjectMessage) mes;
            Obaveza obaveza = (Obaveza) obj.getObject();
            
            System.out.println("Stigla je poruka za dodavanje obaveze");
            if(mes.getBooleanProperty("alarm")){
                long trajanjePuta = trajanjePuta(obaveza.getStart(), obaveza.getDestinacija());
                System.out.println("Trajanje puta je: " + trajanjePuta + " sekundi");
                //pravi se novi alarm za obavezu
                Alarm alarm = new Alarm();
                alarm.setH(0);
                alarm.setMin(0);
                Date cur = new Date();
                if(obaveza.getVreme().getTime() - (long)(trajanjePuta * 1000) < cur.getTime()){
                    System.out.println("Nije moguce stici na ovu obavezu");
                    alarm.setVreme(new Date(0));
                }
                else {
                    alarm.setVreme(new Date(obaveza.getVreme().getTime() - (long)(trajanjePuta * 1000)));
                }
    
                System.out.println("Slanje poruke za navijanje alarma");
                //slanje alarmu da navije alarm
                ObjectMessage msg = context.createObjectMessage(alarm);
                msg.setStringProperty("tip", "Alarm");
                msg.setIntProperty("mod", 4);
                producer.send(queue, msg);
                
                ObjectMessage o = (ObjectMessage) consumer.receive();
                alarm = (Alarm) o.getObject();
                obaveza.setIdA(alarm.getId());
            }
            
            //upisivanje u bazu
            em.getTransaction().begin();
            em.persist(obaveza);
            em.getTransaction().commit();
            
            } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static final String API_KEY = "AIzaSyBwp6V9T1BSY45KyMNFozLUH5d6nShtNYI";
    
    public static final GeoApiContext contextGoogleApi = new GeoApiContext.Builder().apiKey(API_KEY).build();

    private static long trajanjePuta(String city1, String city2){

        try {
            DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(contextGoogleApi);
            
            //odredjivanje trenutna lokacije
            if(city1.equals("x"))
                city1 = trenutnaLokacija();
            if(city2.equals("x"))
                city2 = trenutnaLokacija();
            System.out.println("Trenutna lokacija je:" + city1);
            System.out.println("Lokacija dogadjaja je: " + city2);
            
            DistanceMatrix trix = req.origins(city1)
                .destinations(city2)
                .mode(TravelMode.DRIVING)
                .language("en-EN")
                .await();

            return trix.rows[0].elements[0].duration.inSeconds;

            } catch (ApiException | IOException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
        return -100;
    }
    
    //internet api for finding address
    private static String getIP(){
        // Find public IP address
        String systemipaddress;
        try
        {
            URL url_name = new URL("http://bot.whatismyipaddress.com");
            
            BufferedReader sc =
                    new BufferedReader(new InputStreamReader(url_name.openStream()));
            
            // reads system IPAddress
            systemipaddress = sc.readLine().trim();
        }
        catch (IOException e)
        {
            systemipaddress = "Cannot Execute Properly";
        }
        
        return systemipaddress; 
    }
    //throws IOException, GeoIp2Exception
    private static String trenutnaLokacija()  {
        try {
            String ip = getIP();
            
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = dbReader.city(ipAddress);
            
            String cityName = response.getCity().getName();
            return response.getCity().getName();
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | GeoIp2Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Ne moze da se odredi trenutna lokacija";
    }
    
    private static void promeniObavezu(Message mes){
        try {
            //primarni kljuc obaveze koja se menja
            TextMessage text = (TextMessage) mes;
            int ind = text.getIntProperty("id");
            System.out.println("INDEKS JE " + ind);
            em.getTransaction().begin();
            Obaveza obaveza = em.find(Obaveza.class, ind);
            
            //vrati obavezu nazad
            ObjectMessage obj1 = context.createObjectMessage(obaveza);
            System.out.println("Salje se objekat nazad za obavezu " + obaveza.getOpis());
            obj1.setStringProperty("tip", "KUredjaj");
            producer.send(queue, obj1);
            
            //vracena obaveza, treba da se upise u bazu
            ObjectMessage obj2 = (ObjectMessage) consumer.receive();
            Obaveza ob = (Obaveza) obj2.getObject();
            
            obaveza.setStart(ob.getStart());
            obaveza.setDestinacija(ob.getDestinacija());
            obaveza.setVreme(ob.getVreme());
            obaveza.setOpis(ob.getOpis());
            
            
            TextMessage txt = context.createTextMessage();
            txt.setIntProperty("id", obaveza.getIdA());
            txt.setStringProperty("tip", "Alarm");
            txt.setIntProperty("mod", 6);
            producer.send(queue, txt);
            
            ObjectMessage o = (ObjectMessage) consumer.receive();
            Alarm alarm = (Alarm) o.getObject();
            //novo vreme za alarm
            long trajanjePuta = trajanjePuta(obaveza.getStart(), obaveza.getDestinacija());
            alarm.setVreme(new Date(obaveza.getVreme().getTime() - (long)(trajanjePuta * 1000)));
            System.out.println("Stigao alarm");
             
            ObjectMessage obj3 = context.createObjectMessage(alarm);
            obj3.setStringProperty("tip", "Alarm");
            obj3.setIntProperty("mod", 5);
            
            producer.send(queue, obj3);
            
            em.persist(obaveza);
            em.getTransaction().commit();
            
            TextMessage tm = (TextMessage)consumer.receive();
            System.out.println(tm.getText());
            
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void obrisiObavezu(Message mes){
        try {
            TextMessage obj = (TextMessage) mes;
            int ind = obj.getIntProperty("id");
            
            //em.getTransaction().begin();
            Obaveza obaveza = em.find(Obaveza.class, ind);
            //em.getTransaction().commit();
            
            //ako je navijen alarm, treba da se obrise
            if(obaveza.getIdA() != null){
                TextMessage txt = context.createTextMessage();
                txt.setIntProperty("id", obaveza.getIdA());
                txt.setStringProperty("tip", "Alarm");
                txt.setIntProperty("mod", 7);
                producer.send(queue, txt);
            }
            
            //brisanje same obaveze
            em.getTransaction().begin();
            em.remove(obaveza);
            em.getTransaction().commit();
            
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
