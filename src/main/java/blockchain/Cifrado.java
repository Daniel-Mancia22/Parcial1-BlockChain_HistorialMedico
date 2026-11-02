package blockchain;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Cifrado{
    private SecretKeySpec llave;
    private Cipher oCifrado;    //encriptador
    private Cipher oDescifrado; //desencriptador

    private static final String TRANSFORMACION = "AES/ECB/PKCS5Padding";

    /**
     * Genera una llave AES aleatoria de sesion (transparente: sin password
     * del usuario y sin hardcodear). Se crea una vez por BlockChain.
     */
    public static SecretKey generarLlaveSesion(){
        try{
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            try{
                gen.init(256, new SecureRandom());
            } catch (Exception e){
                // Si JCE restringido, caer a 128 bits
                gen.init(128, new SecureRandom());
            }
            return gen.generateKey();
        } catch (Exception e){
            throw new RuntimeException("No se pudo generar la llave de sesion AES", e);
        }
    }

    /** Constructor para cifrado transparente con llave de sesion. */
    public Cifrado(SecretKey pLlave){
        try{
            if (pLlave == null) throw new IllegalArgumentException("Llave nula");
            byte[] raw = pLlave.getEncoded();
            if (raw == null) throw new IllegalArgumentException("Llave sin encoding");
            this.llave = new SecretKeySpec(raw, "AES");

            this.oCifrado = Cipher.getInstance(TRANSFORMACION);
            this.oCifrado.init(Cipher.ENCRYPT_MODE, this.llave);

            this.oDescifrado = Cipher.getInstance(TRANSFORMACION);
            this.oDescifrado.init(Cipher.DECRYPT_MODE, this.llave);
        } catch (Exception e){
            throw new RuntimeException("No se pudo inicializar el cifrador AES", e);
        }
    }

    public String encriptar(String pCadena) throws Exception {
        if (pCadena == null) return null;
        byte[] aBytes = pCadena.getBytes("UTF-8");
        byte[] aBytesEnc = this.oCifrado.doFinal(aBytes);
        return Base64.getEncoder().encodeToString(aBytesEnc);
    }

    public String desencriptar(String pCadena) throws Exception {
        if (pCadena == null) return null;
        byte[] aBytes = Base64.getDecoder().decode(pCadena);
        byte[] aBytesDec = this.oDescifrado.doFinal(aBytes);
        return new String(aBytesDec, "UTF-8");
    }
}
