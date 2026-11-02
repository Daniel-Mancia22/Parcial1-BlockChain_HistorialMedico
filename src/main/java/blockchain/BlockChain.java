package blockchain;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

//Motor de la cadena de bloques. La lógica de minado y prueba de trabajo.
public class BlockChain {

    private ArrayList<Block> blockChain;
    private int complexity;
    private String proofOfWork;

    // Cifrado transparente: el cifrador guarda la llave de sesion y la sal permite el indice de DUI.
    // Se generan una vez por cadena, viven solo en memoria (no hay password de usuario ni clave hardcodeada).
    private String salCadenaHex;
    private transient Cifrado cifrador;

    public BlockChain(int iComplexity, String proofChar){
        this.blockChain = new ArrayList<>();
        this.complexity = iComplexity;
        this.proofOfWork = "";
        
        for (int i = 0; i < this.complexity; i++) {
            this.proofOfWork += proofChar;
        }
        this.salCadenaHex = generarSalHex();
        this.cifrador = new Cifrado(Cifrado.generarLlaveSesion());
    }

    private static String generarSalHex(){
        byte[] sal = new byte[16];
        
        new SecureRandom().nextBytes(sal);
        StringBuilder sb = new StringBuilder();
        
        for (byte b : sal){
            String h = Integer.toHexString(0xff & b);
            
            if (h.length() == 1) sb.append('0');
            sb.append(h);
        }
        return sb.toString();
    }

    /**
     * Indice para buscar por DUI sin guardar el DUI en claro:
     * SHA-256(DUI normalizado + sal de la cadena). Irreversible, pero
     * permite comparar el hash de consulta con el almacenado.
     */
    public String indiceDui(String pDui){
        if (pDui == null) return null;
        String normalizado = pDui.trim();
        
        return SHA256.generateHash(normalizado + this.salCadenaHex);
    }

    /** Cifra un campo de texto con la llave de sesion. Retorna Base64. */
    public String cifrarCampo(String plano) throws Exception{
        if (plano == null) return null;
        
        return this.cifrador.encriptar(plano);
    }

    /** Descifra un campo Base64 con la llave de sesion. */
    public String descifrarCampo(String blob) throws Exception{
        if (blob == null) return null;
        
        return this.cifrador.desencriptar(blob);
    }

    public Block getBlock(int index){
        return this.blockChain.get(index);
    }

    public Block getLastBlock(){
        return this.blockChain.get(this.blockChain.size() - 1);
    }

    public int size() {
        return this.blockChain.size();
    }

    // Primero se crea el bloque génesis y lo mina. Sin registro clínico inicial.
    public boolean createGenesis(){
        if (this.size() < 1){
            Block tmpBlock = new Block(0, "0000000000000000000000000000000000000000000000000000000000000000");
            this.blockChain.add(tmpBlock);
            this.mineBlock();
            return true;
        }
        return false;
    }

    // Segundo crea un nuevo bloque propuesto, enlazado al hash del último bloque.
    public void createBlock(){
        String prevHash = this.blockChain.get(this.blockChain.size() - 1).getHash();
        this.blockChain.add(new Block(this.blockChain.size(), prevHash));
    }

    /**
     * Recorre toda la cadena y devuelve el historial médico completo de un paciente, buscando por su DUI.
     * Registros nuevos (cifrados): compara el indice hash del DUI.
     * Registros viejos (encriptado=false, DUI en claro): compara directo.
     */
    public List<RegistroMedico> obtenerHistorial(String pDui){
        List<RegistroMedico> historial = new ArrayList<>();
        String idxBuscado = this.indiceDui(pDui == null ? "" : pDui);
        
        for (int i = 0; i < this.size(); i++){
            Block blk = this.getBlock(i);
            
            for (int j = 0; j < blk.countRegistros(); j++){
                RegistroMedico r = blk.getRegistro(j);
                
                if (r.isEncriptado() && r.getDuiIndex() != null){
                    if (r.getDuiIndex().equals(idxBuscado)) historial.add(r);
                } else{
                    String duiClaro = r.getPaciente() == null ? null : r.getPaciente().getDui();
                    if (duiClaro != null && duiClaro.equalsIgnoreCase(pDui)) historial.add(r);
                }
            }
        }
        return historial;
    }

    // Cuarto paso: prueba de trabajo — fuerza bruta del nonce hasta cumplir la dificultad.
    public void mineBlock(){
        String cad = this.blockChain.get(this.blockChain.size() - 1).toString();
        int nonce = 0;
        String sHash = "";
        
        while (true){
            sHash = this.generateHash(cad + Integer.toString(nonce));
            
            if (sHash.subSequence(0, complexity).equals(this.proofOfWork)){
                // Quinto paso: el bloque queda sellado y ya forma parte de la cadena
                // (estaba en el ArrayList desde el paso 2; register() lo certifica).
                this.blockChain.get(this.blockChain.size() - 1).register(nonce, sHash);
                break;
            }
            nonce++;
        }
    }

    private String generateHash(String pCad){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pCad.getBytes("UTF-8"));
            StringBuffer hexadecimalString = new StringBuffer();
            
            for (int i = 0; i < hash.length; i++){
                String hexadecimal = Integer.toHexString(0xff & hash[i]);
                if (hexadecimal.length() == 1) hexadecimalString.append('0');
                hexadecimalString.append(hexadecimal);
            }
            return hexadecimalString.toString();
        } catch (Exception ee){
            return null;
        }
    }

    /**
     * Filas listas para pintar en un JTable (visor de bloques):
     * columnas -> ID, Hash (corto), Hash anterior (corto), Nonce, Fecha, # Registros
     */
    public java.util.List<Object[]> obtenerFilasTabla(){
        java.util.List<Object[]> filas = new ArrayList<>();
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        for (int i = 0; i < this.size(); i++){
            Block b = this.getBlock(i);
            String hashCorto = (b.getHash() == null) ? "" : b.getHash().substring(0, 12) + "...";
            String prevCorto = (b.getPreviousHash() == null) ? "" :
                    b.getPreviousHash().substring(0, Math.min(12, b.getPreviousHash().length())) + "...";
            filas.add(new Object[]{
                b.getId(), hashCorto, prevCorto, b.getNonce(),
                fmt.format(new java.util.Date(b.getTimeStamp())), b.countRegistros()
            });
        }
        return filas;
    }
}
