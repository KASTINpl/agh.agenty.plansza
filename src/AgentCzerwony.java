
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main Agent: losowe proszuszanie siê po planszy w poszukiwaniu celu, po jego zobaczeniu wybranie najkrótszej mo¿liwej drogi. Rejestracja w DF lub streming wiadomo¶ci do pozosta³ych agentów po zobaczeniu celu.
 * 
 * @author KASTINpl
 */
public class AgentCzerwony extends Agent {

    public int Px = 9;
    public int Py = 9;
    public int x = -1; // random w setup()
    public int y = -1;
    public Belka[] b = {new Belka(new Pkt(3, 3), new Pkt(5, 3)),
        new Belka(new Pkt(7, 2), new Pkt(7, 4))};
    private Pkt cel = new Pkt(8, 8);                        // cel
    private List<Pkt> droga = new ArrayList<Pkt>();
    private boolean widze_cel = false;
    private int R = 2; // promien widzialnosci
    private Random r;

    @Override
    protected void setup() {

        r = new Random();
        x = r.nextInt(Px);
        y = r.nextInt(Py);

        addBehaviour(new TickerBehaviour(this, 500) {
            @Override
            protected void onTick() {
                Pkt tmp;
                droga.clear();

                if (!widze_cel) { // !wc
                    tmp = new Pkt(x - 1, y);
                    if (tmp.x >= 0 && !b[0].in(tmp) && !b[1].in(tmp) && y > 0 && y < Py) {
                        droga.add(tmp.kopia());
                    }

                    tmp = new Pkt(x, y - 1);
                    if (tmp.y >= 0 && !b[0].in(tmp) && !b[1].in(tmp) && x > 0 && x < Px) {
                        droga.add(tmp.kopia());
                    }

                    tmp = new Pkt(x, y + 1);
                    if (tmp.y <= Py && !b[0].in(tmp) && !b[1].in(tmp) && x > 0 && x < Px) {
                        droga.add(tmp.kopia());
                    }

                    tmp = new Pkt(x + 1, y);
                    if (tmp.x <= Px && !b[0].in(tmp) && !b[1].in(tmp) && y > 0 && y < Py) {
                        droga.add(tmp.kopia());
                    }
                    if (droga.size() < 1) {
                        System.out.println("Nie mam gdzie isc...");
                        return;
                    }
                    tmp = droga.get(r.nextInt(droga.size()));
                } else { // widze cel [wc]
                    tmp = new Pkt(x, y);
                    if (cel.exual(tmp)) {
                        return;
                    }
                    if (cel.x > tmp.x) {
                        ++tmp.x;
                    } else if (cel.x < tmp.x) {
                        --tmp.x;
                    } else if (cel.y > tmp.y) {
                        ++tmp.y;
                    } else if (cel.y < tmp.y) {
                        --tmp.y;
                    } else {/* no to problem...*/ }
                }//wc
                x = tmp.x;
                y = tmp.y;
                System.out.println("Agent " + getAID().getName() + " idzie do: " + tmp.x + " x " + tmp.y + ";");
            }
        });//TB

        addBehaviour(new CyclicBehaviour() { // wzrok
            @Override
            public void action() {
                if (!widze_cel) {
                    Pkt tmp = new Pkt(x, y);
                    if (tmp.in_r(cel, R)) { //in R
                        widze_cel = true;
                        String mess = "Agent " + getAID().getName() + " zobaczyl cel!";
                        System.out.println(mess);

                        DFregister("cel");
                        
                       AID[] sAgents = DFgetList("cel");
                       if (sAgents.length>0) {
                         System.out.println("Aktualnie zarejestrowanych agentow w DF: "+sAgents.length);
                       }
                       
                       /*
                        * wy¶lij wiadomo¶æ do agentów
                        * 
                       List<AID> aids = new ArrayList<AID>();
                       aids.add(new AID("A", AID.ISLOCALNAME));
                       aids.add(new AID("B", AID.ISLOCALNAME));
                       aids.add(new AID("B", AID.ISLOCALNAME));
                        
                       sendMessage(aids, "Wiadomo¶æ do wymienionych agentów")
                       */
                    }//in R
                }//widze_cel
            }//action
        }); //CB

        addBehaviour(new CyclicBehaviour() { // nasluchiwanie
            @Override
            public void action() {
                AID[] sAgents = DFgetList("cel");
                if (sAgents.length>0) {
                    // sa zarejestrowani agenci
                }
                /*
                 * wczytaj wiadomo¶ci
                 * 
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    System.out.println("Agent " + getAID().getName() + " dostal wiadomosc: " + msg.getContent());
                }*/
            }//action
        }); //CB
    }//setup

    /**
     * wy¶lij wiadomo¶æ do agentów z listy argumentów
     * 
     * @param AIDS lista agentów
     * @param mess wiadomo¶æ do wys³ania
     */
    private void sendMessage(List<AID> AIDS, String mess) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        for (AID a : AIDS) {
            msg.addReceiver(a);
        }

        msg.setContent(mess);
        send(msg);
    }
    
    /**
     * pobierz listê agentów od agenta DF
     * 
     * @param t typ wpisu do rejestru DF
     * @return tabela statyczna agnetów AID
     */
    private AID[] DFgetList(String t) {
        AID[] sellerAgents = null;
        
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(t);
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            sellerAgents = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                sellerAgents[i] = result[i].getName();
            }
        } catch (Exception e) {  }
        
        return sellerAgents;
    }//-

    /**
     * rejestruj agenta w rejestrze DF
     * @param t typ wpisu do rejestru
     */
    private void DFregister(String t) {
        // Register the book-selling service in the yellow pages 
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(t);
        sd.setName("jakies smieci");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (Exception e) {
        }

    }//--

    /**
     * wypisz siê z rejestru DF
     */
    @Override
    protected void takeDown() {
        // Deregister from the yellow pages 
        try {
            DFService.deregister(this);
        } catch (Exception e) {  }
    }//--
}//$