package blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Block implements Serializable {
    private int id;
    private int nonce;
    private long timeStamp;
    private String hash;          // Prueba de trabajo
    private String previousHash;
    private ArrayList<RegistroMedico> aRegistros;

    public Block(int pId, String pPrevHash) {
        this.id = pId;
        this.timeStamp = new Date().getTime();
        this.previousHash = pPrevHash;
        this.aRegistros = new ArrayList<>();
        this.nonce = -1;
        this.hash = null;
    }

    // Esto sella el bloque con el nonce y hash encontrados en el minado
    public boolean register(int pNonce, String pHash){
        if ((this.id > -1) && (this.nonce < 0) && (this.hash == null)){
            this.nonce = pNonce;
            this.hash = pHash;
            return true;
        }
        else return false;
    }

    /**
     * Agrega un registro cifrado de forma transparente con la llave de
     * sesion de la cadena. No pide password al usuario.
     */
    public void addRegistroCifrado(Paciente pPaciente, Medico pMedico, String pTipo,
                                   String pDescripcion, BlockChain bc) throws Exception{
        this.aRegistros.add(RegistroMedico.crearCifrado(
                this.aRegistros.size(), pPaciente, pMedico, pTipo, pDescripcion, bc
        ));
    }

    public RegistroMedico getRegistro(int pId){
        return this.aRegistros.get(pId);
    }

    public int countRegistros(){
        return this.aRegistros.size();
    }

    //Getter and Setter
    public int getId() { return id; }

    public int getNonce() { return nonce; }

    public long getTimeStamp() { return timeStamp; }

    public String getHash() { return hash; }

    public String getPreviousHash() { return previousHash; }

    @Override
    public String toString() {
        // Concatenación de campos del bloque
        // + toString() de cada registro que contiene. Esto es lo que se hashea.
        String sCad = Integer.toString(id) + Long.toString(timeStamp)
                + this.previousHash;
        
        for (int i = 0; i < this.aRegistros.size(); i++){
            sCad = sCad + this.aRegistros.get(i).toString();
        }
        return sCad;
    }
}
