
package com.ceasa.pushservice;

/** 
 * Representa a resposta individual de envio de uma mensagem para um aparelho, via push.
 * Possui os seguintes campos:
 * <p/>
 * <b>id</b> - id do destinatário da mensagem
 * <p/>
 * <b>status</b> - status do envio. pode ser <b>OK</b>, quando o envio é realizado com sucesso;
 * ou <b>FAIL</b>, quando há uma falha no envio.
 * <p/>
 * <b>message</b> - a mensagem de erro, quando houver
 * 
 * @author William
 */
public class MsgResponse {
    
    private String id;
    private String status;
    private String message;    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
