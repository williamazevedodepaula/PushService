package com.ceasa.pushservice;

import java.util.ArrayList;
import java.util.List;

/** 
 * Representa a estrutura de retorno do serviço de envio de mensagens
 * Possui os seguintes campos/propriedades:
 * <p/>
 * <b>status</b> - Status da requisição. Pode ser <b>OK</b>, quando a operação
 * é realizada com sucesso; ou <b>FAIL</b>, quando acontece algum erro durante a execução.
 * <p/>
 * <b>message</b> - Mensagem de erro, quando houver
 * <p/>
 * <b>responses</b> - Lista de {@link MsgResponse}, conde cada item representa uma resposta individual
 * dos envios realizados.
 * 
 * @author William
 */
public class SenderResponse     {
    
    private String status;
    private String message;
    private List<MsgResponse> responses;

    public SenderResponse(){
        responses = new ArrayList<>();
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

    public List<MsgResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<MsgResponse> responses) {
        this.responses = responses;
    }       
    
    public void addResponse(MsgResponse response){
        this.responses.add(response);
    }
}
